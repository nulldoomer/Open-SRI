export type NumberPart = { key: string; text: string; digit?: number; glyphs?: string[] };

/** Keep Intl's punctuation and bidi literals intact; only decimal digits become wheels. */
export function numberParts(formatter: Intl.NumberFormat, value: number): NumberPart[] {
  const resolved = formatter.resolvedOptions();
  const digits = new Intl.NumberFormat(resolved.locale, { useGrouping: false, numberingSystem: resolved.numberingSystem });
  const glyphs = Array.from({ length: 10 }, (_, digit) => digits.format(digit));
  const parts = formatter.formatToParts(value);
  const integerLength = parts.filter(part => part.type === "integer").reduce((length, part) => length + Array.from(part.value).length, 0);
  let integer = integerLength, fraction = 0;
  const occurrences: Record<string, number> = {};
  return parts.flatMap(part => {
    if (part.type === "integer" || part.type === "fraction") return Array.from(part.value).map(text => {
      const digit = glyphs.indexOf(text);
      const key = part.type === "integer" ? `integer:${--integer}` : `fraction:${fraction++}`;
      return { key, text, ...(digit < 0 ? {} : { digit, glyphs }) };
    });
    const occurrence = occurrences[part.type] ?? 0;
    occurrences[part.type] = occurrence + 1;
    return [{ key: `${part.type}:${occurrence}`, text: part.value }];
  });
}

/** Choose the next matching digit without resetting an interrupted fractional position. */
export function rollingTarget(current: number, digit: number, direction: number) {
  const cycle = Math.floor(current / 10) * 10;
  let target = cycle + digit;
  if (direction >= 0 && target < current - 1e-7) target += 10;
  if (direction < 0 && target > current + 1e-7) target -= 10;
  return target;
}

export function mixNumber(from: number, to: number, progress: number) {
  if (progress >= 1) return to;
  if (progress <= 0) return from;
  return Math.sign(from) === Math.sign(to) ? from + (to - from) * progress : from * (1 - progress) + to * progress;
}

/** Base-aware decimal stepping, with a bounded precision that avoids visible floating point tails. */
export function stepNumber(value: number | null, direction: 1 | -1, step = 1, min?: number, max?: number) {
  const amount = Number.isFinite(step) && step > 0 ? step : 1;
  const lower = Number.isFinite(min) ? min! : -Infinity;
  const upper = Number.isFinite(max) ? Math.max(lower, max!) : Infinity;
  const base = Number.isFinite(lower) ? lower : 0;
  if (value === null || !Number.isFinite(value)) return Math.min(upper, Math.max(lower, Number.isFinite(lower) ? lower : Number.isFinite(upper) && upper < 0 ? upper : 0));
  const position = (value - base) / amount;
  const nearest = Math.round(position);
  const aligned = Math.abs(position - nearest) < 1e-9;
  const next = aligned ? nearest + direction : direction > 0 ? Math.ceil(position) : Math.floor(position);
  const result = base + next * amount;
  const decimals = (number: number) => { const [mantissa, exponent = "0"] = String(number).split("e"); return Math.max(0, (mantissa.split(".")[1]?.length ?? 0) - Number(exponent)); };
  const precision = Math.max(decimals(base), decimals(amount));
  // Preserve all integer digits; only remove fractional arithmetic tails at the step's precision.
  const clean = Number.isFinite(result) ? Number(precision <= 100 ? result.toFixed(precision) : result.toPrecision(15)) : value;
  return Math.min(upper, Math.max(lower, clean));
}

/** Parse editable decimal text in the chosen numbering system; punctuation remains data until commit. */
export function parseNumberInput(text: string, locale = "en-US"): number | null | undefined {
  if (!text.trim()) return null;
  const formatter = new Intl.NumberFormat(locale);
  const parts = formatter.formatToParts(-12345.6);
  const decimal = parts.find(part => part.type === "decimal")?.value ?? ".";
  const group = parts.find(part => part.type === "group")?.value;
  const minus = parts.find(part => part.type === "minusSign")?.value ?? "-";
  let normalized = text.trim().replace(/[\u061c\u200e\u200f]/g, "");
  if (group) normalized = normalized.split(group).join("");
  normalized = normalized.split(decimal).join(".").split(minus).join("-");
  const digits = new Intl.NumberFormat(locale, { useGrouping: false });
  for (let digit = 0; digit < 10; digit++) normalized = normalized.split(digits.format(digit)).join(String(digit));
  if (!/^[+-]?(?:\d+(?:\.\d*)?|\.\d+)(?:e[+-]?\d+)?$/i.test(normalized)) return undefined;
  const value = Number(normalized);
  return Number.isFinite(value) ? value : undefined;
}

/** Editable text preserves the number's full significant precision, including scientific notation. */
export function numberInputText(value: number, locale = "en-US") {
  const formatter = new Intl.NumberFormat(locale, { useGrouping: false });
  const parts = formatter.formatToParts(-1.5);
  const decimal = parts.find(part => part.type === "decimal")?.value ?? ".";
  const minus = parts.find(part => part.type === "minusSign")?.value ?? "-";
  return (Object.is(value, -0) ? "-0" : String(value)).replace(/[0-9.-]/g, character => character === "." ? decimal : character === "-" ? minus : formatter.format(Number(character)));
}
