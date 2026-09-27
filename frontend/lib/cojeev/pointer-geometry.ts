export type GuidedPointerPoint = {
  id: string;
  /** Normalized position within the available stage area, from zero to one. */
  x: number;
  y: number;
  click?: boolean;
  pressed?: boolean;
};

/** Keep the first valid occurrence of each ID without mutating caller data. */
export function normalizePointerPoints(points: readonly GuidedPointerPoint[]): GuidedPointerPoint[] {
  const seen = new Set<string>();
  return points.flatMap(point => {
    if (!point.id.trim() || seen.has(point.id) || !Number.isFinite(point.x) || !Number.isFinite(point.y)) return [];
    seen.add(point.id);
    return [{ ...point, x: Math.min(1, Math.max(0, point.x)), y: Math.min(1, Math.max(0, point.y)) }];
  });
}

/** Omitted selection uses the first valid point; an unknown selection stays empty. */
export function resolvePointerPoint(points: readonly GuidedPointerPoint[], activeId?: string) {
  const normalized = normalizePointerPoints(points);
  return activeId === undefined ? normalized[0] : normalized.find(point => point.id === activeId);
}
