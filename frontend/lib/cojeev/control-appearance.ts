import type { CSSProperties } from "react";

export type ControlRadius = "square" | "soft" | "round" | "pill";
export type FieldAppearance = "contour" | "editorial" | "inset";
export type ControlAppearanceProps = { radius?: ControlRadius; appearance?: FieldAppearance };

/** Omission inherits the nearest owner's corners; no preference state is created. */
export function controlRadiusStyle(radius?: ControlRadius): CSSProperties | undefined {
  if (!radius) return undefined;
  return { "--v-control-radius": { square: "0px", soft: "8px", round: "20px", pill: "999px" }[radius] } as CSSProperties;
}
