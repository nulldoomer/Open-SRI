import { rasterizeSculpture } from "@/lib/cojeev/sculpture-raster";
/** Original organic geometry and a small, dependency-free character-cell rasterizer. */
export type GlyphForm = "seed" | "bloom" | "pebble";
export type GlyphTone = "ink" | "rose" | "moss" | "sky";
export const GLYPH_RAMP = " .,:;=+*#%@";
type Vector = [number, number, number];
export type GlyphMesh = { positions: Float32Array; normals: Float32Array; triangles: Uint16Array };
export type GlyphGrid = { columns: number; rows: number; cellWidth: number; cellHeight: number; width: number; height: number };
const bounded = (value: number | undefined, fallback: number, min: number, max: number) => Number.isFinite(value) ? Math.max(min, Math.min(max, value!)) : fallback;

export function normalizeGlyphOptions({ speed, cellSize, turn }: { speed?: number; cellSize?: number; turn?: number } = {}) {
  return { speed: bounded(speed, 1, 0, 3), cellSize: bounded(cellSize, 10, 7, 20), turn: bounded(turn, 0, -180, 180) };
}

export function glyphGrid(width: number, height: number, cellSize: number): GlyphGrid {
  width = bounded(width, 1, 1, 20000); height = bounded(height, 1, 1, 20000);
  const size = Math.max(bounded(cellSize, 10, 7, 20), width / (140 * .62), height / 90);
  const columns = Math.max(1, Math.min(140, Math.floor(width / (size * .62))));
  const rows = Math.max(1, Math.min(90, Math.floor(height / size)));
  return { columns, rows, cellWidth: width / columns, cellHeight: height / rows, width, height };
}

function surface(form: GlyphForm, longitude: number, latitude: number): Vector {
  const ring = Math.sin(latitude), height = Math.cos(latitude);
  const x = Math.cos(longitude), z = Math.sin(longitude);
  if (form === "seed") return [.69 * ring * x * (1 + .2 * height), 1.08 * height, .5 * ring * z * (1 - .18 * height)];
  if (form === "bloom") {
    // A soft five-lobed cushion, with a shallow dimple at the center of each face.
    const radius = .83 + .19 * Math.cos(5 * longitude);
    return [radius * ring * x, radius * ring * z, .44 * height * (1 - .28 * Math.exp(-ring * ring * 12))];
  }
  const radius = 1 + .09 * Math.cos(3 * longitude + .3) * ring;
  return [.94 * ring * x * radius, .79 * height + .05 * ring * Math.sin(2 * longitude), .66 * ring * z * radius];
}

function unit(x: number, y: number, z: number): Vector {
  const length = Math.hypot(x, y, z) || 1;
  return [x / length, y / length, z / length];
}

export function createGlyphMesh(form: GlyphForm): GlyphMesh {
  const around = 64, vertical = 32;
  const positions = new Float32Array((around + 1) * (vertical + 1) * 3);
  const normals = new Float32Array(positions.length);
  const triangles: number[] = [];
  for (let row = 0; row <= vertical; row++) for (let column = 0; column <= around; column++) {
    const longitude = column / around * Math.PI * 2, latitude = row / vertical * Math.PI;
    const index = (row * (around + 1) + column) * 3;
    const point = surface(form, longitude, latitude);
    positions.set(point, index);
    const u0 = surface(form, longitude - .001, latitude), u1 = surface(form, longitude + .001, latitude);
    const v0 = surface(form, longitude, latitude - .001), v1 = surface(form, longitude, latitude + .001);
    const u = u1.map((value, i) => value - u0[i]), v = v1.map((value, i) => value - v0[i]);
    let normal = unit(u[1] * v[2] - u[2] * v[1], u[2] * v[0] - u[0] * v[2], u[0] * v[1] - u[1] * v[0]);
    if (row === 0 || row === vertical) normal = unit(...point);
    if (normal.reduce((sum, value, i) => sum + value * point[i], 0) < 0) normal = normal.map(value => -value) as Vector;
    normals.set(normal, index);
    if (row < vertical && column < around) {
      const a = row * (around + 1) + column, b = a + around + 1;
      triangles.push(a, b, a + 1, a + 1, b, b + 1);
    }
  }
  return { positions, normals, triangles: new Uint16Array(triangles) };
}

/** Compatibility density-grid API backed by the shared depth-tested surface. */
export function rasterizeGlyphs(mesh: GlyphMesh, grid: GlyphGrid, yaw = .35, pitch = -.2): Uint8Array {
  const frame = rasterizeSculpture(mesh, grid, yaw, pitch), cells = new Uint8Array(frame.coverage.length);
  for (let i = 0; i < cells.length; i++) if (frame.coverage[i]) cells[i] = Math.max(1, Math.min(GLYPH_RAMP.length - 1, Math.round(frame.light[i] * (GLYPH_RAMP.length - 2)) + 1));
  return cells;
}

export function glyphText(cells: Uint8Array, columns: number): string {
  const lines: string[] = [];
  for (let i = 0; i < cells.length; i += columns) lines.push(Array.from(cells.subarray(i, i + columns), value => GLYPH_RAMP[value]).join(""));
  return lines.join("\n");
}
