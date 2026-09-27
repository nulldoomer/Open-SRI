type Point = readonly [number, number];
const tau = Math.PI * 2;
export function shapeContour(sample: (angle: number) => Point) {
  const points = Array.from({ length: 96 }, (_, i) => sample(i / 96 * tau));
  const f = (n: number) => Number(n.toFixed(3));
  let path = `M${f(points[0][0])} ${f(points[0][1])}`;
  for (let i = 0; i < points.length; i++) {
    const before = points[(i + 95) % 96], a = points[i], b = points[(i + 1) % 96], after = points[(i + 2) % 96];
    path += `C${f(a[0] + (b[0] - before[0]) / 6)} ${f(a[1] + (b[1] - before[1]) / 6)} ${f(b[0] - (after[0] - a[0]) / 6)} ${f(b[1] - (after[1] - a[1]) / 6)} ${f(b[0])} ${f(b[1])}`;
  }
  return path + "Z";
}
const radial = (radius: (angle: number) => number, sx = 1, sy = 1) => shapeContour(t => [50 + radius(t) * Math.cos(t) * sx, 50 + radius(t) * Math.sin(t) * sy]);
export const signatureShapePaths = {
  "daisy-12": radial(t => 35 + 10 * Math.cos(12 * t)),
  "petal-7": radial(t => 32 + 14 * Math.cos(7 * t)),
  "aster-9": radial(t => 29 + 16 * Math.pow((1 + Math.cos(9 * t)) / 2, 1.6)),
  "sunburst-24": radial(t => 39 + 7 * Math.cos(24 * t)),
  "clover-soft": radial(t => 35 + 10 * Math.cos(4 * t)),
  "cloud-3": radial(t => 37 + 7 * Math.cos(3 * t - .8), 1, .82),
  "pebble-soft": radial(t => 38 + 5 * Math.cos(3 * t + .6) + 2 * Math.sin(5 * t)),
  "pebble-tall": radial(t => 38 + 5 * Math.cos(3 * t + .2) + 2 * Math.sin(2 * t), .68, 1),
  "ribbon-soft": radial(t => 29 + 15 * Math.cos(2 * t) + 2 * Math.sin(5 * t), 1, .82),
  "scalloped-square": radial(t => (36 / Math.pow(Math.pow(Math.abs(Math.cos(t)), 6) + Math.pow(Math.abs(Math.sin(t)), 6), 1 / 6)) + 2.2 * Math.cos(16 * t)),
  "cushion": radial(t => 37 - 5 * Math.cos(4 * t) + 1.5 * Math.cos(8 * t)),
  "seed-wing": radial(t => 34 + 8 * Math.cos(3 * t) + 4 * Math.sin(2 * t)),
} as const;
export type SignatureShapeName = keyof typeof signatureShapePaths;
