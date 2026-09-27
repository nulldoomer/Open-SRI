export type TextRibbonShape = "loop" | "circle" | "wave" | "arch" | "line" | "figure-eight";

/** Original paths in one 640 × 280 coordinate system, with room for glyph ascenders. */
export function textRibbonPath(shape: TextRibbonShape, curvature = 1) {
  const bend = Number.isFinite(curvature) ? Math.max(0, Math.min(1, curvature)) : 1;
  if (shape === "circle") return "M320 52A88 88 0 1 1 320 228A88 88 0 1 1 320 52Z";
  if (shape === "loop") return "M80 150C80 56 560 56 560 150C560 244 80 244 80 150Z";
  if (shape === "arch") return `M24 236C104 ${236 - 198 * bend} 536 ${236 - 198 * bend} 616 236`;
  if (shape === "line") return "M16 152H624";
  if (shape === "figure-eight") return "M320 146C390 54 564 52 564 146C564 240 390 238 320 146C250 54 76 52 76 146C76 240 250 238 320 146Z";
  return `M16 162C110 ${162 - 102 * bend} 212 ${162 - 94 * bend} 320 162S524 ${162 + 96 * bend} 624 ${162 - 20 * bend}`;
}

/** Direct manipulation uses the same wrapping as playback, in SVG coordinates. */
export function moveRibbonPhase(phase: number, distance: number, unitLength: number) {
  if (![phase, distance, unitLength].every(Number.isFinite) || unitLength <= 0) return 0;
  return (((phase + distance) % unitLength) + unitLength) % unitLength;
}

export function ribbonCopies(pathLength: number, unitLength: number) {
  if (!Number.isFinite(pathLength) || !Number.isFinite(unitLength) || pathLength <= 0 || unitLength <= 0) return 1;
  return Math.min(200, Math.max(2, Math.ceil(pathLength / unitLength) + 2));
}

/** Closed paths need a whole number of phrases, or their seam collides mid-word. */
export function ribbonPeriod(pathLength: number, measuredUnit: number, closed: boolean) {
  if (!closed || pathLength <= 0 || measuredUnit <= 0 || !Number.isFinite(pathLength + measuredUnit)) return measuredUnit;
  // A long phrase passes through at its natural scale; fitting the whole phrase
  // into a single lap would make editable/user-provided text unreadably narrow.
  if (measuredUnit > pathLength) return measuredUnit;
  return pathLength / Math.max(1, Math.round(pathLength / measuredUnit));
}

/** One phrase's phase; wrapping it never changes the visible repeated sequence. */
export function advanceRibbonPhase(phase: number, seconds: number, speed: number, unitLength: number) {
  if (![phase, seconds, speed, unitLength].every(Number.isFinite) || unitLength <= 0) return 0;
  const distance = phase + Math.max(0, Math.min(seconds, .05)) * speed;
  return moveRibbonPhase(0, distance, unitLength);
}
