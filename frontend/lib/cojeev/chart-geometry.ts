export type ChartColor = "pink" | "blue" | "olive" | "yellow" | "ink";
export type ChartSeries = { key: string; label: string; color?: ChartColor };
export type ChartPoint = {
  label: string;
  [key: string]: string | number | null | undefined;
};
export type ChartSlice = {
  label: string;
  value: number | null;
  color?: ChartColor;
};
export type PlotPoint = { x: number; y: number };
export type Curve = "linear" | "smooth" | "step";
export const chartColors: ChartColor[] = [
  "pink",
  "blue",
  "olive",
  "yellow",
  "ink",
];
export const chartColor = (color: ChartColor | undefined, index = 0) =>
  `var(--v-${color ?? chartColors[index % chartColors.length]})`;
export const finiteValue = (value: unknown): number | null =>
  typeof value === "number" && Number.isFinite(value) ? value : null;
export const clamp = (value: number, low: number, high: number) =>
  Math.max(low, Math.min(high, Number.isFinite(value) ? value : low));
const n = (value: number) =>
  Number((Number.isFinite(value) ? value : 0).toFixed(3));

/** Missing/non-finite observations stay missing; they never become zero in the table. */
export function cleanChartData(data: ChartPoint[], series: ChartSeries[]) {
  return data.map((point) => ({
    label: String(point.label),
    values: series.map((item) => finiteValue(point[item.key])),
  }));
}
export function chartDomain(
  data: ChartPoint[],
  series: ChartSeries[],
  stacked = false,
): [number, number] {
  let low = 0,
    high = 0;
  for (const point of cleanChartData(data, series)) {
    if (stacked) {
      low = Math.min(
        low,
        point.values.reduce<number>(
          (sum, value) => sum + Math.min(0, value ?? 0),
          0,
        ),
      );
      high = Math.max(
        high,
        point.values.reduce<number>(
          (sum, value) => sum + Math.max(0, value ?? 0),
          0,
        ),
      );
    } else
      for (const value of point.values) {
        low = Math.min(low, value ?? 0);
        high = Math.max(high, value ?? 0);
      }
  }
  // A zero-only set still has a stable, finite axis and inspectable observations.
  if (low === high) return [0, 1];
  const padding = (high - low) * 0.08;
  return [low < 0 ? low - padding : 0, high > 0 ? high + padding : 0];
}
export function scaleValue(
  value: number,
  domain: [number, number],
  start: number,
  end: number,
) {
  const span = domain[1] - domain[0];
  return span > 0 && Number.isFinite(span)
    ? start + ((value - domain[0]) / span) * (end - start)
    : start;
}
export function stackChartData(data: ChartPoint[], series: ChartSeries[]) {
  return cleanChartData(data, series).map((point) => {
    let positive = 0,
      negative = 0;
    return point.values.map((value) => {
      if (value === null) return null;
      const start = value >= 0 ? positive : negative;
      const end = start + value;
      if (value >= 0) positive = end;
      else negative = end;
      return { start, end, value };
    });
  });
}
export function linePath(
  points: (PlotPoint | null)[],
  curve: Curve = "linear",
) {
  let previous: PlotPoint | null = null,
    path = "";
  for (const point of points) {
    if (!point) {
      previous = null;
      continue;
    }
    if (!previous) path += `M${n(point.x)},${n(point.y)}`;
    else if (curve === "step") path += `H${n(point.x)}V${n(point.y)}`;
    else if (curve === "smooth") {
      const mid = (previous.x + point.x) / 2;
      path += `C${n(mid)},${n(previous.y)} ${n(mid)},${n(point.y)} ${n(point.x)},${n(point.y)}`;
    } else path += `L${n(point.x)},${n(point.y)}`;
    previous = point;
  }
  return path;
}
export function areaPath(
  top: (PlotPoint | null)[],
  bottom: (PlotPoint | null)[],
  curve: Curve = "linear",
) {
  let path = "",
    start = 0;
  while (start < top.length) {
    while (start < top.length && (!top[start] || !bottom[start])) start++;
    let end = start;
    while (end < top.length && top[end] && bottom[end]) end++;
    if (end > start)
      path +=
        linePath(top.slice(start, end), curve) +
        linePath(bottom.slice(start, end).reverse(), curve).replace(/^M/, "L") +
        "Z";
    start = end + 1;
  }
  return path;
}
export function polarPoint(
  cx: number,
  cy: number,
  radius: number,
  angle: number,
): PlotPoint {
  return { x: cx + Math.cos(angle) * radius, y: cy + Math.sin(angle) * radius };
}
/** Two arcs also make exact full circles well-defined in SVG. */
export function arcPath(
  cx: number,
  cy: number,
  outer: number,
  inner: number,
  start: number,
  end: number,
) {
  const sweep = clamp(end - start, 0, Math.PI * 2);
  if (sweep <= 0 || outer <= 0) return "";
  const mid = start + sweep / 2,
    finish = start + sweep;
  const a = polarPoint(cx, cy, outer, start),
    b = polarPoint(cx, cy, outer, mid),
    c = polarPoint(cx, cy, outer, finish);
  const outerPath = `M${n(a.x)},${n(a.y)}A${n(outer)},${n(outer)} 0 0 1 ${n(b.x)},${n(b.y)}A${n(outer)},${n(outer)} 0 0 1 ${n(c.x)},${n(c.y)}`;
  if (inner <= 0) return `${outerPath}L${n(cx)},${n(cy)}Z`;
  const d = polarPoint(cx, cy, inner, finish),
    e = polarPoint(cx, cy, inner, mid),
    f = polarPoint(cx, cy, inner, start);
  return `${outerPath}L${n(d.x)},${n(d.y)}A${n(inner)},${n(inner)} 0 0 0 ${n(e.x)},${n(e.y)}A${n(inner)},${n(inner)} 0 0 0 ${n(f.x)},${n(f.y)}Z`;
}
export function pieAngles(data: ChartSlice[]) {
  const values = data.map((item) => Math.max(0, finiteValue(item.value) ?? 0));
  const total = values.reduce((sum, value) => sum + value, 0);
  let angle = -Math.PI / 2;
  return values.map((value, index) => {
    const start = angle;
    angle += total > 0 ? (value / total) * Math.PI * 2 : 0;
    return { index, value, start, end: angle, total };
  });
}
export function radarPath(points: (PlotPoint | null)[], rounded = false) {
  if (!points.length) return "";
  const missing = points.indexOf(null);
  // Begin just after a missing axis so a valid run can cross the final/first axis.
  // Open runs have no fill and never imply an observation at the origin.
  if (missing !== -1)
    return linePath([
      ...points.slice(missing + 1),
      ...points.slice(0, missing + 1),
    ]);
  const observed = points.filter((point): point is PlotPoint => point !== null);
  if (!rounded) return linePath([...observed, observed[0]]) + "Z";
  const middle = (a: PlotPoint, b: PlotPoint) => ({
    x: (a.x + b.x) / 2,
    y: (a.y + b.y) / 2,
  });
  const start = middle(observed.at(-1)!, observed[0]);
  let result = `M${n(start.x)},${n(start.y)}`;
  observed.forEach((point, index) => {
    const end = middle(point, observed[(index + 1) % observed.length]);
    result += `Q${n(point.x)},${n(point.y)} ${n(end.x)},${n(end.y)}`;
  });
  return result + "Z";
}
export function tooltipPosition(
  x: number,
  y: number,
  width: number,
  height: number,
  tipWidth: number,
  tipHeight: number,
) {
  const left = x + 16 + tipWidth <= width - 8 ? x + 16 : x - tipWidth - 16;
  const top = y + 12 + tipHeight <= height - 8 ? y + 12 : y - tipHeight - 12;
  return {
    x: clamp(left, 8, Math.max(8, width - tipWidth - 8)),
    y: clamp(top, 8, Math.max(8, height - tipHeight - 8)),
  };
}
