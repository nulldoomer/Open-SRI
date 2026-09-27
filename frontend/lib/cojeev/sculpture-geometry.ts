export type SculptureGeometryInput = {
  positions: ArrayLike<number>;
  triangles?: ArrayLike<number>;
  normals?: ArrayLike<number>;
};
export type SculptureGeometry = { positions: Float32Array; triangles: Uint16Array; normals: Float32Array };
export const SCULPTURE_LIMITS = { vertices: 16000, triangles: 20000, fileBytes: 8 * 1024 * 1024, imagePixels: 4000000, svgBytes: 128000 } as const;

export function sculptureGeometry(input: SculptureGeometryInput): SculptureGeometry {
  const length = input?.positions?.length ?? 0;
  if (!Number.isInteger(length) || length < 9 || length % 3 || length > SCULPTURE_LIMITS.vertices * 3) throw new Error("Use 3–16,000 vertices, with three coordinates per vertex.");
  const positions = Float32Array.from(input.positions);
  if (!positions.every(Number.isFinite)) throw new Error("The geometry contains a non-finite coordinate.");
  const count = length / 3, source = input.triangles ?? Array.from({ length: count }, (_, index) => index);
  if (!source.length || source.length % 3 || source.length > SCULPTURE_LIMITS.triangles * 3) throw new Error("Use a triangle mesh with at most 20,000 triangles.");
  const triangles = new Uint16Array(source.length);
  for (let i = 0; i < source.length; i++) {
    const value = source[i];
    if (!Number.isInteger(value) || value < 0 || value >= count) throw new Error("A triangle refers to a missing vertex.");
    triangles[i] = value;
  }
  const min = [Infinity, Infinity, Infinity], max = [-Infinity, -Infinity, -Infinity];
  for (let i = 0; i < length; i++) { const axis = i % 3; min[axis] = Math.min(min[axis], positions[i]); max[axis] = Math.max(max[axis], positions[i]); }
  const center = min.map((value, i) => (value + max[i]) / 2);
  let radius = 0;
  for (let i = 0; i < length; i += 3) radius = Math.max(radius, Math.hypot(positions[i] - center[0], positions[i + 1] - center[1], positions[i + 2] - center[2]));
  if (!Number.isFinite(radius) || radius < 1e-9) throw new Error("The geometry has no visible extent.");
  for (let i = 0; i < length; i++) positions[i] = (positions[i] - center[i % 3]) / radius;
  const normals = new Float32Array(length);
  if (input.normals) {
    if (input.normals.length !== length) throw new Error("Provide one normal for every vertex.");
    normals.set(Array.from(input.normals));
    if (!normals.every(Number.isFinite)) throw new Error("The geometry contains a non-finite normal.");
  } else {
    for (let i = 0; i < triangles.length; i += 3) {
      const a = triangles[i] * 3, b = triangles[i + 1] * 3, c = triangles[i + 2] * 3;
      const ux = positions[b] - positions[a], uy = positions[b + 1] - positions[a + 1], uz = positions[b + 2] - positions[a + 2];
      const vx = positions[c] - positions[a], vy = positions[c + 1] - positions[a + 1], vz = positions[c + 2] - positions[a + 2];
      const nx = uy * vz - uz * vy, ny = uz * vx - ux * vz, nz = ux * vy - uy * vx;
      for (const target of [a, b, c]) { normals[target] += nx; normals[target + 1] += ny; normals[target + 2] += nz; }
    }
  }
  for (let i = 0; i < length; i += 3) {
    const size = Math.hypot(normals[i], normals[i + 1], normals[i + 2]);
    if (size < 1e-8) normals[i + 2] = 1;
    else { normals[i] /= size; normals[i + 1] /= size; normals[i + 2] /= size; }
  }
  return { positions, triangles, normals };
}

/** A sampled luminance relief with transparent cells removed. No image is uploaded. */
export function sculptureFromPixels(image: { width: number; height: number; data: ArrayLike<number> }, relief = .22): SculptureGeometry {
  const { width, height, data } = image;
  if (!Number.isInteger(width) || !Number.isInteger(height) || width < 2 || height < 2 || width * height > SCULPTURE_LIMITS.imagePixels || data.length !== width * height * 4) throw new Error("Use an RGBA image from 2×2 up to 4 million pixels.");
  const columns = Math.min(72, width), rows = Math.min(72, height), positions: number[] = [], alpha: boolean[] = [], triangles: number[] = [];
  const aspect = width / height, depth = Number.isFinite(relief) ? Math.max(0, Math.min(.6, relief)) : .22;
  for (let y = 0; y < rows; y++) for (let x = 0; x < columns; x++) {
    const px = Math.round(x / (columns - 1) * (width - 1)), py = Math.round(y / (rows - 1) * (height - 1)), at = (py * width + px) * 4;
    const light = (data[at] * .2126 + data[at + 1] * .7152 + data[at + 2] * .0722) / 255;
    positions.push((x / (columns - 1) * 2 - 1) * Math.min(aspect, 1), (1 - y / (rows - 1) * 2) / Math.max(aspect, 1), (light - .5) * depth);
    alpha.push(data[at + 3] >= 128);
  }
  for (let y = 0; y < rows - 1; y++) for (let x = 0; x < columns - 1; x++) {
    const a = y * columns + x, b = a + 1, c = a + columns, d = c + 1;
    if (alpha[a] && alpha[b] && alpha[c]) triangles.push(a, c, b);
    if (alpha[b] && alpha[c] && alpha[d]) triangles.push(b, c, d);
  }
  if (!triangles.length) throw new Error("The image is transparent or too small to form a surface. Choose an opaque image or a larger silhouette.");
  return sculptureGeometry({ positions, triangles });
}
