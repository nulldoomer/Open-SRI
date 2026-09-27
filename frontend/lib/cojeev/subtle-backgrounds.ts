export type PatternBackgroundVariant = "dots" | "grid" | "contours" | "weave" | "pebbles" | "sunwash" | "folds" | "sprouts" | "none";

export const subtleBackgrounds: Array<{ value: Exclude<PatternBackgroundVariant, "none">; label: string; description: string }> = [
  { value: "dots", label: "Dots", description: "A quiet field of points." },
  { value: "grid", label: "Grid", description: "Light rules for gentle structure." },
  { value: "contours", label: "Contours", description: "Nested organic linework." },
  { value: "weave", label: "Weave", description: "Short interlaced strokes." },
  { value: "pebbles", label: "Pebbles", description: "Sparse hand-drawn outlines." },
  { value: "sunwash", label: "Sunwash", description: "A broad, soft paper wash." },
  { value: "folds", label: "Folds", description: "Low-contrast faceted paper." },
  { value: "sprouts", label: "Sprouts", description: "Paired seed leaves in open space." },
];
