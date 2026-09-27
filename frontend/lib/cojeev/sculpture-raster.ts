import type { SculptureGeometry } from "@/lib/cojeev/sculpture-geometry";

export type SculptureGrid = { columns: number; rows: number; cellWidth: number; cellHeight: number; width: number; height: number };
export type SculptureFrame = { grid: SculptureGrid; coverage: Uint8Array; light: Float32Array; depth: Float32Array; sampling: number };
export const sculptureNumber = (value: number | undefined, fallback: number, min: number, max: number) => Number.isFinite(value) ? Math.max(min, Math.min(max, value!)) : fallback;
export function sculptureGrid(width: number, height: number, step = 2): SculptureGrid {
  width = sculptureNumber(width, 1, 1, 20000); height = sculptureNumber(height, 1, 1, 20000);
  step = Math.max(sculptureNumber(step, 2, 1.5, 12), width / 280, height / 220);
  const columns = Math.max(1, Math.floor(width / step)), rows = Math.max(1, Math.floor(height / step));
  return { columns, rows, cellWidth: width / columns, cellHeight: height / rows, width, height };
}

/** Original orthographic depth pass. The same surface supplies glyphs, print dots and engraved lines. */
export function rasterizeSculpture(mesh: SculptureGeometry, grid: SculptureGrid, yaw = .35, pitch = -.2, zoom = 1): SculptureFrame {
  const { columns, rows, cellWidth, cellHeight, width, height } = grid;
  const coverage = new Uint8Array(columns * rows), depth = new Float32Array(coverage.length).fill(-Infinity), light = new Float32Array(coverage.length);
  const projected = new Float32Array(mesh.positions.length / 3 * 4), cy = Math.cos(yaw), sy = Math.sin(yaw), cx = Math.cos(pitch), sx = Math.sin(pitch);
  const angle = -.14, cz = Math.cos(angle), sz = Math.sin(angle), scale = Math.min(width, height) * .39 * sculptureNumber(zoom, 1, .6, 1.15);
  function rotate(x: number, y: number, z: number) { const a = cy * x + sy * z, b = -sy * x + cy * z, c = cx * y - sx * b; return [cz * a - sz * c, sz * a + cz * c, sx * y + cx * b]; }
  for (let i = 0, out = 0; i < mesh.positions.length; i += 3, out += 4) {
    const p = rotate(mesh.positions[i], mesh.positions[i + 1], mesh.positions[i + 2]), n = rotate(mesh.normals[i], mesh.normals[i + 1], mesh.normals[i + 2]);
    projected[out] = columns / 2 + p[0] * scale / cellWidth; projected[out + 1] = rows / 2 - p[1] * scale / cellHeight; projected[out + 2] = p[2];
    projected[out + 3] = Math.min(1, .12 + .8 * Math.max(0, n[0] * -.42 + n[1] * .58 + n[2] * .69) + .12 * Math.abs(n[2]));
  }
  // Vertex/triangle limits alone cannot bound overlapping triangle work. Estimate
  // clipped coverage first, then lower sampling before any expensive raster pass.
  let work = 0;
  for (let i = 0; i < mesh.triangles.length; i += 3) {
    const a = mesh.triangles[i] * 4, b = mesh.triangles[i + 1] * 4, c = mesh.triangles[i + 2] * 4;
    const left = Math.max(0, Math.floor(Math.min(projected[a], projected[b], projected[c]))), right = Math.min(columns - 1, Math.ceil(Math.max(projected[a], projected[b], projected[c])));
    const top = Math.max(0, Math.floor(Math.min(projected[a + 1], projected[b + 1], projected[c + 1]))), bottom = Math.min(rows - 1, Math.ceil(Math.max(projected[a + 1], projected[b + 1], projected[c + 1])));
    work += Math.max(0, right - left + 1) * Math.max(0, bottom - top + 1);
  }
  if (work > 350000 && (columns > 1 || rows > 1)) {
    const factor = Math.sqrt(work / 350000) * 1.15, lowColumns = Math.max(1, Math.floor(columns / factor)), lowRows = Math.max(1, Math.floor(rows / factor));
    const low = rasterizeSculpture(mesh, { ...grid, columns: lowColumns, rows: lowRows, cellWidth: width / lowColumns, cellHeight: height / lowRows }, yaw, pitch, zoom);
    for (let y = 0; y < rows; y++) for (let x = 0; x < columns; x++) {
      const at = Math.min(lowRows - 1, Math.floor(y / rows * lowRows)) * lowColumns + Math.min(lowColumns - 1, Math.floor(x / columns * lowColumns)), i = y * columns + x;
      coverage[i] = low.coverage[at]; light[i] = low.light[at]; depth[i] = low.depth[at];
    }
    return { grid, coverage, depth, light, sampling: Math.max(columns / lowColumns, rows / lowRows) * low.sampling };
  }
  for (let i = 0; i < mesh.triangles.length; i += 3) {
    const a = mesh.triangles[i] * 4, b = mesh.triangles[i + 1] * 4, c = mesh.triangles[i + 2] * 4;
    const ax = projected[a], ay = projected[a + 1], bx = projected[b], by = projected[b + 1], cxp = projected[c], cyp = projected[c + 1];
    const denominator = (by - cyp) * (ax - cxp) + (cxp - bx) * (ay - cyp);
    if (Math.abs(denominator) < 1e-7) continue;
    const left = Math.max(0, Math.floor(Math.min(ax, bx, cxp))), right = Math.min(columns - 1, Math.ceil(Math.max(ax, bx, cxp)));
    const top = Math.max(0, Math.floor(Math.min(ay, by, cyp))), bottom = Math.min(rows - 1, Math.ceil(Math.max(ay, by, cyp)));
    for (let y = top; y <= bottom; y++) for (let x = left; x <= right; x++) {
      const wa = ((by - cyp) * (x + .5 - cxp) + (cxp - bx) * (y + .5 - cyp)) / denominator, wb = ((cyp - ay) * (x + .5 - cxp) + (ax - cxp) * (y + .5 - cyp)) / denominator, wc = 1 - wa - wb;
      if (wa < -.001 || wb < -.001 || wc < -.001) continue;
      const z = wa * projected[a + 2] + wb * projected[b + 2] + wc * projected[c + 2], index = y * columns + x;
      if (z <= depth[index]) continue;
      depth[index] = z; coverage[index] = 1; light[index] = wa * projected[a + 3] + wb * projected[b + 3] + wc * projected[c + 3];
    }
  }
  return { grid, coverage, depth, light, sampling: 1 };
}

export type DitherPattern = "ordered" | "halftone" | "diffusion" | "stipple";
export type InkTreatment = "hatch" | "crosshatch" | "contour";
export type PrintOptions = { kind: "dither" | "ink"; pattern?: DitherPattern; treatment?: InkTreatment; markSize?: number; density?: number; angle?: number; relief?: number };
const bayer = [0, 8, 2, 10, 12, 4, 14, 6, 3, 11, 1, 9, 15, 7, 13, 5];
const noise = (x: number, y: number) => { const n = Math.sin(x * 127.1 + y * 311.7) * 43758.5453; return n - Math.floor(n); };
const fract = (value: number) => value - Math.floor(value);
export function sculpturePrint(frame: SculptureFrame, options: PrintOptions): Uint8Array {
  const { grid, coverage, light, depth } = frame, { columns, rows, cellWidth, cellHeight } = grid, marks = new Uint8Array(coverage.length);
  const density = sculptureNumber(options.density, 1, .5, 1.6), spacing = sculptureNumber(options.markSize, 8, 4, 16), angle = sculptureNumber(options.angle, -25, -180, 180) * Math.PI / 180, relief = sculptureNumber(options.relief, .55, 0, 1);
  const errors = options.pattern === "diffusion" ? new Float32Array(coverage.length) : undefined;
  for (let y = 0; y < rows; y++) for (let x = 0; x < columns; x++) {
    const i = y * columns + x;
    if (!coverage[i]) continue;
    const shade = Math.max(.06, Math.min(.98, (.18 + (1 - light[i]) * .85) * density));
    if (options.kind === "dither") {
      if (errors) {
        const value = shade + errors[i], hit = value >= .5 ? 1 : 0, error = value - hit;
        marks[i] = hit * 255;
        if (x + 1 < columns) errors[i + 1] += error * 7 / 16;
        if (y + 1 < rows) { if (x) errors[i + columns - 1] += error * 3 / 16; errors[i + columns] += error * 5 / 16; if (x + 1 < columns) errors[i + columns + 1] += error / 16; }
      } else {
        const threshold = options.pattern === "stipple" ? noise(x, y) : options.pattern === "halftone" ? Math.hypot(fract(x / 5) - .5, fract(y / 5) - .5) * 1.42 : (bayer[(y % 4) * 4 + x % 4] + .5) / 16;
        marks[i] = shade > threshold ? 255 : 0;
      }
    } else {
      const px = (x + .5) * cellWidth, py = (y + .5) * cellHeight, u = px * Math.cos(angle) + py * Math.sin(angle), v = -px * Math.sin(angle) + py * Math.cos(angle);
      const bend = depth[i] * spacing * 2.2 * relief, wobble = Math.sin(u / 37) * .1 * spacing;
      const line = Math.abs(fract((v + bend + wobble) / spacing) - .5), cross = Math.abs(fract((u - bend * .7) / (spacing * 1.2)) - .5);
      const contour = Math.abs(fract(depth[i] * (96 / spacing)) - .5);
      const edge = x === 0 || y === 0 || x === columns - 1 || y === rows - 1 || !coverage[i - 1] || !coverage[i + 1] || !coverage[i - columns] || !coverage[i + columns];
      const broken = shade > .5 || noise(Math.floor(u / 12), Math.floor((v + bend) / spacing)) < shade + .35;
      const hit = options.treatment === "contour" ? contour < .055 + shade * .08 || edge : (line < .05 + shade * .33 && broken) || (options.treatment === "crosshatch" && shade > .42 && cross < (shade - .38) * .32);
      marks[i] = hit && noise(x, y) > .035 ? 255 : 0;
    }
  }
  return marks;
}

export type GlyphSet = "density" | "digits" | "letters";
export const SCULPTURE_GLYPHS = { density: " .,:;=+*#%@", digits: " .1273456890", letters: " .iltrcoEXMW" } as const;
/** Edge cells use a local shape gradient, independently of the interior density ramp. */
export function sculptureCharacters(frame: SculptureFrame, set: GlyphSet = "density", edgeMatching = true): string[] {
  const { coverage, light, grid: { columns, rows } } = frame, ramp = SCULPTURE_GLYPHS[set], out = new Array<string>(coverage.length).fill(" ");
  for (let y = 0; y < rows; y++) for (let x = 0; x < columns; x++) {
    const i = y * columns + x;
    if (!coverage[i]) continue;
    out[i] = ramp[Math.max(1, Math.min(ramp.length - 1, Math.round(light[i] * (ramp.length - 2)) + 1))];
    if (!edgeMatching || set !== "density") continue;
    const sample = (dx: number, dy: number) => x + dx >= 0 && x + dx < columns && y + dy >= 0 && y + dy < rows ? coverage[i + dy * columns + dx] : 0;
    const gx = sample(1, -1) + 2 * sample(1, 0) + sample(1, 1) - sample(-1, -1) - 2 * sample(-1, 0) - sample(-1, 1);
    const gy = sample(-1, 1) + 2 * sample(0, 1) + sample(1, 1) - sample(-1, -1) - 2 * sample(0, -1) - sample(1, -1);
    if (Math.abs(gx) + Math.abs(gy) >= 3) out[i] = Math.abs(gx) > Math.abs(gy) * 1.7 ? "|" : Math.abs(gy) > Math.abs(gx) * 1.7 ? "_" : gx * gy > 0 ? "/" : "\\";
  }
  return out;
}

/** Compact opaque-cell path for a real, geometry-derived server/no-canvas fallback. */
export function sculpturePrintPath(marks: Uint8Array, columns: number) {
  let path = "";
  for (let i = 0; i < marks.length;) {
    if (!marks[i]) { i++; continue; }
    const x = i % columns, y = Math.floor(i / columns); let length = 1;
    while (x + length < columns && marks[i + length]) length++;
    path += `M${x} ${y}h${length}v1h-${length}z`; i += length;
  }
  return path;
}
