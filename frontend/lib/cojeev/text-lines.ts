export type TextLine = { text: string; start: number; end: number };
export type MeasuredGrapheme = { start: number; end: number; top: number };
export const TEXT_LINE_LIMIT = 4000;

/** Preserve Unicode and explicit empty lines; logical order also works for RTL. */
export function groupTextLines(text: string, units: readonly MeasuredGrapheme[], tolerance = 2): TextLine[] {
  const lines: TextLine[] = [];
  let start = 0;
  let top: number | undefined;
  for (const unit of units) {
    const content = text.slice(unit.start, unit.end);
    if (/\r?\n/.test(content)) {
      lines.push({ text: text.slice(start, unit.start), start, end: unit.start });
      start = unit.end;
      top = undefined;
    } else {
      if (top !== undefined && Math.abs(unit.top - top) > tolerance) {
        lines.push({ text: text.slice(start, unit.start), start, end: unit.start });
        start = unit.start;
      }
      top = unit.top;
    }
  }
  if (start < text.length || lines.length) lines.push({ text: text.slice(start), start, end: text.length });
  return lines;
}

/** Measure a plain text node, leaving shaping and wrapping to the browser. */
export function measureTextLines(element: HTMLElement, text: string): TextLine[] | null {
  const node = element.firstChild;
  if (!text) return [];
  if (!node || node.nodeType !== Node.TEXT_NODE) return null;
  const segmenter = new Intl.Segmenter(undefined, { granularity: "grapheme" });
  const units: MeasuredGrapheme[] = [];
  const range = document.createRange();
  const style = getComputedStyle(element);
  const lineHeight = parseFloat(style.lineHeight) || parseFloat(style.fontSize) * 1.2;
  for (const { segment, index } of segmenter.segment(text)) {
    // A bounded heading effect. Long prose keeps its natural, readable layout.
    if (units.length >= TEXT_LINE_LIMIT) return null;
    range.setStart(node, index);
    range.setEnd(node, index + segment.length);
    // WebKit includes a zero-width caret on the previous row for a glyph
    // immediately after an empty RTL line. The painted glyph owns the row.
    const rect = Array.from(range.getClientRects()).sort((a, b) => b.width * b.height - a.width * a.height)[0] ?? range.getBoundingClientRect();
    // Fallback fonts (especially emoji) have different ascenders on the same
    // baseline. Compare row centers with a line-height-relative tolerance.
    units.push({ start: index, end: index + segment.length, top: rect.top + rect.height / 2 });
  }
  return groupTextLines(text, units, Math.max(4, lineHeight * .45));
}
