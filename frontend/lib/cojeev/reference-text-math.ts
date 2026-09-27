export const bounded = (value: number, min: number, max: number, fallback: number) => Number.isFinite(value) ? Math.max(min, Math.min(max, value)) : fallback;
export const graphemes = (text: string) => Array.from(new Intl.Segmenter(undefined, { granularity: "grapheme" }).segment(text), item => item.segment);
export function proximityWeight(distance: number, radius: number, from: number, to: number) {
  const p = 1 - bounded(distance / bounded(radius, 1, 1000, 120), 0, 1, 1);
  return from + (to - from) * p * p * (3 - 2 * p);
}
export function scrollWordProgress(progress: number, index: number, count: number) {
  return bounded(progress * (Math.max(1, count) + 2) - index, 0, 1, 1);
}
export function caretFrame(from: string, to: string, progress: number) {
  const p = bounded(progress, 0, 1, 1), chars = graphemes(to);
  if (p < .25) return { text: from, phase: "holding", cover: 0 };
  if (p < .42) return { text: from, phase: "covering", cover: (p - .25) / .17 };
  if (p < .55) return { text: "", phase: "collapsing", cover: 1 - (p - .42) / .13 };
  return { text: chars.slice(0, Math.ceil(chars.length * Math.pow((p - .55) / .45, .7))).join(""), phase: p === 1 ? "complete" : "typing", cover: 0 };
}
export type FallingBody = { x: number; y: number; vx: number; vy: number; w: number; h: number; angle: number };
export function fallStep(body: FallingBody, width: number, height: number, delta: number, gravity: number): FallingBody {
  const dt = bounded(delta, 0, 32, 16) / 1000;
  let vx = body.vx, vy = body.vy + bounded(gravity, 0, 2400, 900) * dt;
  let x = body.x + vx * dt, y = body.y + vy * dt;
  const right = Math.max(0, width - body.w), floor = Math.max(0, height - body.h);
  if (x < 0 || x > right) { x = bounded(x, 0, right, 0); vx *= -.55; }
  if (y > floor) { y = floor; vy = Math.abs(vy) < 35 ? 0 : -vy * .36; vx *= .85; }
  y = Math.max(0, y);
  return {...body, x, y, vx, vy, angle: bounded(body.angle + vx * dt * .015, -12, 12, 0)};
}
