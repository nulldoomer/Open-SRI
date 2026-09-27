export type Point = { x: number; y: number };
export type SwarmPoint = Point & { vx: number; vy: number };
export type Ripple = Point & { time: number };
export const boundedCount = (n: number, fallback: number, max: number) => Math.max(1, Math.min(max, Math.round(Number.isFinite(n) ? n : fallback)));
export const boundedNumber = (n: number, fallback: number, min: number, max: number) => Math.max(min, Math.min(max, Number.isFinite(n) ? n : fallback));
export function makeSwarm(count: number): SwarmPoint[] {
  return Array.from({ length: boundedCount(count, 18, 64) }, (_, i) => ({ x: Math.cos(i * 2.4) * 25, y: Math.sin(i * 2.4) * 25, vx: 0, vy: 0 }));
}
/** Bounded semi-implicit springs; a long background-tab delta cannot explode the simulation. */
export function stepSwarm(points: SwarmPoint[], target: Point, milliseconds: number, time: number, spread: number) {
  const dt = boundedNumber(milliseconds / 1000, .016, 0, .033);
  points.forEach((p, i) => {
    const angle = i * 2.39996 + time * (.55 + i % 3 * .09);
    const radius = boundedNumber(spread, 28, 0, 160) * (.3 + (i % 7) / 9);
    const x = target.x + Math.cos(angle) * radius, y = target.y + Math.sin(angle) * radius;
    p.vx = (p.vx + (x - p.x) * 48 * dt) * Math.exp(-7 * dt);
    p.vy = (p.vy + (y - p.y) * 48 * dt) * Math.exp(-7 * dt);
    p.x += p.vx * dt; p.y += p.vy * dt;
  });
}
export function orbitPoint(progress: number, width: number, height: number): Point {
  const angle = ((progress % 1 + 1) % 1) * Math.PI * 2;
  return { x: width / 2 + Math.cos(angle) * Math.max(0, width / 2 - 48), y: height / 2 + Math.sin(angle) * Math.max(0, height / 2 - 42) };
}
export function rippleOffset(x: number, y: number, waves: Ripple[], time: number): Point {
  let dx = 0, dy = 0;
  for (const wave of waves) {
    const age = time - wave.time;
    if (age < 0 || age > 1.6) continue;
    const distance = Math.hypot(x - wave.x, y - wave.y), envelope = Math.exp(-Math.pow((distance - age * 230) / 65, 2));
    const amount = Math.sin(distance * .085 - age * 16) * envelope * (1 - age / 1.6) * 12;
    dx += (x - wave.x) / Math.max(1, distance) * amount; dy += (y - wave.y) / Math.max(1, distance) * amount;
  }
  return { x: dx, y: dy };
}
