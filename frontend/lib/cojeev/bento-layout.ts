export type BentoTile = {
  id: string;
  x: number;
  y: number;
  width: number;
  height: number;
  label: string;
};
export type BentoLayout = {
  columns: number;
  rows: number;
  tiles: BentoTile[];
  seed: number;
};
export type BentoVariant = "classic" | "interlock";
export type BentoTemplate = "Editorial" | "Dashboard" | "Gallery" | "Showcase";
export type BentoEdit = { layout: BentoLayout; error?: string };
export type BentoSide = "left" | "right" | "top" | "bottom";
export const bentoTemplates: BentoTemplate[] = [
  "Editorial",
  "Dashboard",
  "Gallery",
  "Showcase",
];
const defaults = [
  "Make room for ideas",
  "In progress",
  "A little perspective",
  "Things worth keeping",
  "Next chapter",
  "Stay curious",
].map((label, i) => ({ id: `tile-${i + 1}`, label }));

export function validateBento(layout: BentoLayout): string | null {
  if (
    ![layout.columns, layout.rows].every(
      (n) => Number.isInteger(n) && n >= 2 && n <= 8,
    ) ||
    !Number.isSafeInteger(layout.seed)
  )
    return "Use 2–8 whole rows and columns, with an integer seed.";
  const cells = new Set<string>(),
    ids = new Set<string>();
  for (const tile of layout.tiles) {
    if (!tile.id || ids.has(tile.id) || typeof tile.label !== "string")
      return "Every tile needs a unique identity and text label.";
    ids.add(tile.id);
    if (
      ![tile.x, tile.y, tile.width, tile.height].every(Number.isInteger) ||
      tile.x < 0 ||
      tile.y < 0 ||
      tile.width < 1 ||
      tile.height < 1 ||
      tile.x + tile.width > layout.columns ||
      tile.y + tile.height > layout.rows
    )
      return "Tiles must stay inside the grid and occupy at least one cell.";
    for (let y = tile.y; y < tile.y + tile.height; y++)
      for (let x = tile.x; x < tile.x + tile.width; x++) {
        const cell = `${x},${y}`;
        if (cells.has(cell)) return "Tiles cannot overlap.";
        cells.add(cell);
      }
  }
  return cells.size === layout.columns * layout.rows
    ? null
    : "Tiles must cover the whole grid.";
}
function random(seed: number) {
  let value = seed >>> 0;
  return () => {
    value = (Math.imul(value, 1664525) + 1013904223) >>> 0;
    return value / 4294967296;
  };
}
export function generateBento(
  columns = 6,
  rows = 4,
  seed = 17,
  content: Pick<BentoTile, "id" | "label">[] = defaults,
  template: BentoTemplate = "Editorial",
): BentoLayout {
  if (
    ![columns, rows].every((n) => Number.isInteger(n) && n >= 2 && n <= 8) ||
    !Number.isSafeInteger(seed)
  )
    throw new Error("Use 2–8 whole rows and columns.");
  if (!content.length || content.length > columns * rows)
    throw new Error(
      "This grid cannot retain every tile. Increase its dimensions.",
    );
  const next = random(seed + bentoTemplates.indexOf(template) * 7919);
  const rectangles = [{ x: 0, y: 0, width: columns, height: rows }];
  while (rectangles.length < content.length) {
    const candidates = rectangles
      .map((tile, index) => ({
        tile,
        index,
        score:
          tile.width *
          tile.height *
          (template === "Gallery" ? 1 : 0.65 + next() * 0.7),
      }))
      .filter(({ tile }) => tile.width > 1 || tile.height > 1)
      .sort((a, b) => b.score - a.score);
    const { tile, index } = candidates[0];
    const vertical =
      tile.height === 1 ||
      (tile.width > 1 &&
        (template === "Dashboard"
          ? tile.width >= tile.height
          : next() < tile.width / (tile.width + tile.height)));
    const length = vertical ? tile.width : tile.height;
    const cut =
      template === "Gallery"
        ? Math.floor(length / 2)
        : 1 + Math.floor(next() * (length - 1));
    rectangles.splice(
      index,
      1,
      ...(vertical
        ? [
            { ...tile, width: cut },
            { ...tile, x: tile.x + cut, width: tile.width - cut },
          ]
        : [
            { ...tile, height: cut },
            { ...tile, y: tile.y + cut, height: tile.height - cut },
          ]),
    );
  }
  rectangles.sort((a, b) => a.y - b.y || a.x - b.x);
  const layout = {
    columns,
    rows,
    seed,
    tiles: rectangles.map((tile, i) => ({
      ...tile,
      id: content[i].id,
      label: content[i].label,
    })),
  };
  const error = validateBento(layout);
  if (error) throw new Error(error);
  return layout;
}
export function swapBentoTiles(
  layout: BentoLayout,
  first: string,
  second: string,
): BentoLayout {
  const a = layout.tiles.find((t) => t.id === first),
    b = layout.tiles.find((t) => t.id === second);
  if (!a || !b) return layout;
  return {
    ...layout,
    tiles: layout.tiles.map((t) =>
      t === a
        ? { ...t, id: b.id, label: b.label }
        : t === b
          ? { ...t, id: a.id, label: a.label }
          : t,
    ),
  };
}
/** Expand along touching edge intervals until the entire connected seam is owned. */
export function resizeBentoSeam(
  layout: BentoLayout,
  id: string,
  side: BentoSide,
  position: number,
): BentoEdit {
  const tile = layout.tiles.find((t) => t.id === id);
  if (!tile) return { layout, error: "Select a tile first." };
  const vertical = side === "left" || side === "right";
  const coordinate = vertical
    ? tile.x + (side === "right" ? tile.width : 0)
    : tile.y + (side === "bottom" ? tile.height : 0);
  const limit = vertical ? layout.columns : layout.rows;
  if (
    !Number.isInteger(position) ||
    position <= 0 ||
    position >= limit ||
    coordinate === 0 ||
    coordinate === limit
  )
    return {
      layout,
      error:
        "The outside edge stays fixed; every neighbor needs at least one cell.",
    };
  let start = vertical ? tile.y : tile.x,
    end = start + (vertical ? tile.height : tile.width);
  const edges = layout.tiles.filter((t) =>
    vertical
      ? t.x === coordinate || t.x + t.width === coordinate
      : t.y === coordinate || t.y + t.height === coordinate,
  );
  let changed = true;
  while (changed) {
    changed = false;
    for (const t of edges) {
      const a = vertical ? t.y : t.x,
        b = a + (vertical ? t.height : t.width);
      if (a < end && b > start) {
        const s = Math.min(start, a),
          e = Math.max(end, b);
        if (s !== start || e !== end) {
          start = s;
          end = e;
          changed = true;
        }
      }
    }
  }
  const delta = position - coordinate;
  const result = {
    ...layout,
    tiles: layout.tiles.map((t) => {
      const a = vertical ? t.y : t.x,
        b = a + (vertical ? t.height : t.width);
      if (a >= end || b <= start) return t;
      if (vertical) {
        if (t.x === coordinate)
          return { ...t, x: position, width: t.width - delta };
        if (t.x + t.width === coordinate)
          return { ...t, width: t.width + delta };
      } else {
        if (t.y === coordinate)
          return { ...t, y: position, height: t.height - delta };
        if (t.y + t.height === coordinate)
          return { ...t, height: t.height + delta };
      }
      return t;
    }),
  };
  const error = validateBento(result);
  return error
    ? {
        layout,
        error:
          "That seam cannot move further: every neighbor needs at least one cell.",
      }
    : { layout: result };
}
type Point = [number, number];
/** Canonical unit cubic. Reverse traversal reuses the exact same controls. */
export function bentoEdge(
  x1: number,
  y1: number,
  x2: number,
  y2: number,
  seed: number,
  outer: boolean,
): [Point, Point, Point, Point] {
  if (x1 > x2 || y1 > y2) {
    const points = bentoEdge(x2, y2, x1, y1, seed, outer);
    return [points[3], points[2], points[1], points[0]];
  }
  const dx = x2 - x1,
    dy = y2 - y1,
    amplitude = outer
      ? 0
      : (random(seed + x1 * 73856093 + y1 * 19349663 + (dx ? 83492791 : 0))() -
          0.5) *
        0.42;
  return [
    [x1, y1],
    [x1 + dx / 3 + dy * amplitude, y1 + dy / 3 + dx * amplitude],
    [x1 + (dx * 2) / 3 + dy * amplitude, y1 + (dy * 2) / 3 + dx * amplitude],
    [x2, y2],
  ];
}
export function bentoTilePath(tile: BentoTile, layout: BentoLayout): string {
  const { x, y, width: w, height: h } = tile;
  let path = `M ${x} ${y}`;
  const append = (a: number, b: number, c: number, d: number) => {
    const p = bentoEdge(
      a,
      b,
      c,
      d,
      layout.seed,
      (a === c && (a === 0 || a === layout.columns)) ||
        (b === d && (b === 0 || b === layout.rows)),
    );
    path += ` C ${p[1].join(" ")} ${p[2].join(" ")} ${p[3].join(" ")}`;
  };
  for (let i = 0; i < w; i++) append(x + i, y, x + i + 1, y);
  for (let i = 0; i < h; i++) append(x + w, y + i, x + w, y + i + 1);
  for (let i = w; i > 0; i--) append(x + i, y + h, x + i - 1, y + h);
  for (let i = h; i > 0; i--) append(x, y + i, x, y + i - 1);
  return path + " Z";
}
export function exportBento(
  layout: BentoLayout,
  variant: BentoVariant,
): string {
  const error = validateBento(layout);
  if (error) throw new Error(error);
  return `import { BentoGrid } from "@/components/ui/bento-grid";\n\nconst layout = ${JSON.stringify(layout, null, 2).replace(/</g, "\\u003c")};\n\nexport default function MyBento() {\n  return <BentoGrid layout={layout} variant="${variant}" />;\n}\n`;
}
