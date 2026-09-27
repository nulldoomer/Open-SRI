export type RelayOrder = "first" | "last" | "center" | "edges";

export function relayIndex(value: number, count: number) {
  return count ? Math.min(count - 1, Math.max(0, Number.isFinite(value) ? Math.floor(value) : 0)) : 0;
}

/** Rank in logical reading order; center and edges are symmetric, including odd counts. */
export function relayRank(index: number, count: number, order: RelayOrder) {
  const middle = (count - 1) / 2;
  if (order === "last") return count - index - 1;
  if (order === "center") return Math.abs(index - middle);
  if (order === "edges") return middle - Math.abs(index - middle);
  return index;
}

export function relayTiming(duration: number, stagger: number, count: number) {
  const milliseconds = Number.isFinite(duration) ? Math.max(0, Math.min(1600, duration)) : 650;
  const requested = Number.isFinite(stagger) ? Math.max(0, Math.min(120, stagger)) : 24;
  const step = Math.min(requested, 500 / Math.max(1, count - 1));
  return { duration: milliseconds / 1000, step: step / 1000, window: milliseconds + step * Math.max(0, count - 1) };
}
