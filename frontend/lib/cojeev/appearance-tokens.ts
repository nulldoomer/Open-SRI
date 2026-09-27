export const palettes = [
  { id: "paper", name: "Paper", description: "Warm paper, crisp ink", light: ["#F8F6F1", "#FFFFFF", "#EEE9E1"], dark: ["#15171A", "#20242A", "#292F37"], accents: ["#F0A4CC", "#A8BC75", "#95BAE8", "#F1D369"] },
  { id: "tide", name: "Tide", description: "Clear blue, sea glass", light: ["#F1F6F9", "#FFFFFF", "#DFE9F0"], dark: ["#101B24", "#192A37", "#233947"], accents: ["#DAA8D5", "#8BC3B2", "#82BCEB", "#EFCD79"] },
  { id: "grove", name: "Grove", description: "Leaf greens, quiet light", light: ["#F4F6ED", "#FCFEF8", "#E4EADA"], dark: ["#141C15", "#213026", "#2D3D30"], accents: ["#E4A8B2", "#B1CA78", "#A5C7D9", "#E7CE85"] },
  { id: "clay", name: "Clay", description: "Terracotta, golden warmth", light: ["#FBF3EB", "#FFFCF7", "#EFDFD2"], dark: ["#211814", "#30231F", "#3F3028"], accents: ["#EDA992", "#B7C281", "#A4BCCF", "#F1CB7A"] },
  { id: "orchid", name: "Orchid", description: "Lavender, soft violet", light: ["#F7F3FA", "#FFFCFF", "#E9E0F0"], dark: ["#1B1521", "#2A2133", "#382C43"], accents: ["#D6ADEE", "#B7C993", "#A6BCEC", "#F0D18C"] },
  { id: "graphite", name: "Graphite", description: "Cool neutrals, clean focus", light: ["#F4F5F6", "#FFFFFF", "#E5E8EB"], dark: ["#111418", "#20252C", "#2C333D"], accents: ["#E9ACC5", "#B4C3A0", "#ABBEDC", "#E7CF98"] },
] as const;
export type PaletteName = typeof palettes[number]["id"];
export type AppearanceSettings = { palette: PaletteName; contrast: number };
export const defaultAppearance: AppearanceSettings = Object.freeze({ palette: "paper", contrast: 60 });

export function normalizeAppearance(value: unknown): AppearanceSettings {
  const input = value && typeof value === "object" ? value as Partial<AppearanceSettings> : {};
  return {
    palette: palettes.some(p => p.id === input.palette) ? input.palette! : defaultAppearance.palette,
    contrast: typeof input.contrast === "number" && Number.isFinite(input.contrast) ? Math.max(0, Math.min(100, input.contrast)) : defaultAppearance.contrast,
  };
}
function rgb(hex: string): number[] { return [1, 3, 5].map(i => parseInt(hex.slice(i, i + 2), 16)); }
export function mixColor(a: string, b: string, amount: number): string {
  const av = rgb(a), bv = rgb(b);
  return "#" + av.map((v, i) => Math.round(v + (bv[i] - v) * Math.max(0, Math.min(1, amount))).toString(16).padStart(2, "0")).join("");
}
export function luminance(hex: string): number {
  const channels = rgb(hex).map(v => { const c = v / 255; return c <= .04045 ? c / 12.92 : ((c + .055) / 1.055) ** 2.4; });
  return channels[0] * .2126 + channels[1] * .7152 + channels[2] * .0722;
}
export function contrastRatio(a: string, b: string): number {
  const x = luminance(a), y = luminance(b);
  return (Math.max(x, y) + .05) / (Math.min(x, y) + .05);
}
/** Nearest blend toward the mode's ink that clears every supplied surface. */
export function readableInk(seed: string, surfaces: readonly string[], target: number, dark: boolean): string {
  const end = dark ? "#FFFFFF" : "#080A0D";
  if (surfaces.every(bg => contrastRatio(seed, bg) >= target)) return seed;
  let low = 0, high = 1;
  for (let i = 0; i < 18; i++) {
    const mid = (low + high) / 2;
    if (surfaces.every(bg => contrastRatio(mixColor(seed, end, mid), bg) >= target)) high = mid;
    else low = mid;
  }
  return mixColor(seed, end, high);
}

export function appearanceTokens(settings: AppearanceSettings, mode: "light" | "dark"): Record<string, string> {
  const normalized = normalizeAppearance(settings);
  const palette = palettes.find(p => p.id === normalized.palette)!;
  const dark = mode === "dark", amount = normalized.contrast / 100;
  const [canvas, paper, inset] = palette[mode];
  const surfaces = [canvas, paper, inset];
  const seed = dark ? "#7E858E" : "#727781";
  const text = readableInk(seed, surfaces, 8 + amount * 7, dark);
  const secondary = readableInk(seed, surfaces, 5 + amount * 4, dark);
  const muted = readableInk(seed, surfaces, 4.6 + amount * 2.4, dark);
  const edge = readableInk(inset, surfaces, 3.1 + amount * 1.8, dark);
  const line = readableInk(inset, [canvas, paper], 1.45 + amount * .9, dark);
  const [pink, olive, blue, yellow] = palette.accents;
  const tint = (accent: string, weight = dark ? .12 : .13) => mixColor(paper, accent, weight);
  const selection = tint(pink, dark ? .17 : .24);
  const selectionInk = readableInk(text, [selection], 7 + amount * 3, dark);
  const accentInk = "#14171B";
  const tokens: Record<string, string> = {
    "--v-canvas": canvas, "--v-paper": paper, "--v-beige": inset,
    "--v-beige-2": mixColor(canvas, inset, .55), "--v-cream-pill": paper,
    "--v-text": text, "--v-text-2": secondary, "--v-text-3": muted,
    "--v-ink": text, "--v-ink-soft": secondary, "--v-on-ink": canvas,
    "--v-border": line, "--v-edge": edge, "--v-on-accent": accentInk,
    "--v-brand": readableInk(pink, surfaces, 4.6, dark),
    "--v-accent-ink": readableInk(pink, surfaces, 4.6 + amount * 2, dark),
    "--v-olive-ink": readableInk(olive, surfaces, 4.6 + amount * 2, dark),
    "--v-disabled-face": inset, "--v-disabled-ink": muted, "--v-disabled-edge": edge,
    "--v-disabled-fill": inset, "--btn-disabled-ink": muted, "--unavail-ink": muted,
    "--alert-ink": secondary, "--nav-group-ink": muted,
    "--v-skel-face": inset, "--v-skel-sheen": paper, "--v-skel-edge": line,
    "--sel-bg": selection, "--sel-ink": selectionInk,
    "--sel-edge": readableInk(pink, [...surfaces, selection], 3.1, dark),
    "--switch-knob": paper, "--switch-knob-on": accentInk,
    "--v-structure": "#14171B", "--on-structure": "#F9FAFC", "--structure-text": "#E2E6ED",
    "--structure-quiet": "#242931", "--structure-line": "#565D68", "--sidebar-muted": "#B4BDCA",
    "--surface-quiet": paper, "--surface-work": tint(blue), "--surface-automation": tint(yellow),
    "--surface-memory": tint(olive), "--surface-library": tint(pink), "--surface-ai": tint(blue), "--surface-alert": tint("#E88B7A"),
    "--v-danger": "#D7473C", "--v-danger-fill": "#B92D28", "--v-danger-ink": readableInk("#DB5146", surfaces, 4.6, dark),
    "--v-danger-soft": tint("#D7473C"), "--v-scrim": dark ? "rgba(0,0,0,.66)" : "rgba(19,24,32,.46)",
    "--glide-bg": "#14171B", "--glide-fg": "#F9FAFC",
    "--card": "var(--v-paper)", "--popover": "var(--v-paper)",
    "--accent-foreground": "var(--v-on-accent)", "--destructive": "var(--v-danger-fill)",
  };
  ["pink", "olive", "blue", "yellow"].forEach((tone, i) => {
    const color = palette.accents[i];
    tokens[`--v-${tone}`] = color;
    tokens[`--v-${tone}-deep`] = mixColor(color, "#14171B", .12);
    tokens[`--v-${tone}-soft`] = tint(color, dark ? .15 : .24);
  });
  ["ok", "warn", "danger", "info", "pending"].forEach((state, i) => {
    const color = [olive, yellow, "#D7473C", blue, secondary][i];
    const bg = tint(color);
    tokens[`--status-${state}-bg`] = bg;
    tokens[`--status-${state}-ink`] = readableInk(color, [bg], 5 + amount * 2, dark);
  });
  return tokens;
}
