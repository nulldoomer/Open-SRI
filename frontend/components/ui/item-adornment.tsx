"use client";

import * as React from "react";
import { cn } from "@/lib/utils";
import { signatureShapePaths, type SignatureShapeName } from "@/lib/cojeev/signature-shapes";
import { Icon, Disk } from "@/components/ui/icon";
import { ShapeMorph } from "@/components/ui/shape";
import { AnimatedIcon, type IconMotion } from "@/components/ui/animated-icon";

export type ItemAdornmentColor = "pink" | "yellow" | "olive" | "blue";
export type ItemAdornmentOptions = {
  shape?: SignatureShapeName;
  /** A palette name or CSS color. Supply foreground for a custom dark color. */
  color?: ItemAdornmentColor | (string & {});
  foreground?: string;
  /** An Icon name, custom decorative node, or false for the silhouette alone. */
  icon?: string | React.ReactNode;
  /** Hide the glyph independently of its background silhouette. */
  showIcon?: boolean;
  /** Hide the silhouette while keeping the glyph in the row's foreground. */
  showBackground?: boolean;
  effect?: IconMotion;
};
export type ItemAdornmentValue = "auto" | "none" | false | ItemAdornmentOptions | React.ReactElement;
export type ItemAdornmentItemProps = {
  adornment?: ItemAdornmentValue;
  /** Keeps the visual identity stable when a label changes or is translated. */
  adornmentId?: string;
};

const signatureShapeNames = Object.keys(signatureShapePaths) as SignatureShapeName[];
const colors: ItemAdornmentColor[] = ["pink", "blue", "yellow", "olive"];
const glyphs = ["sparkles", "flower", "leaf", "star", "circle-plus", "zap"];
const namedGlyphs: [RegExp, string][] = [
  [/github/i, "github"], [/setting|preference/i, "settings"], [/save/i, "save"],
  [/copy|duplicate/i, "copy"], [/delete|remove|trash/i, "trash-2"], [/share/i, "share-2"],
  [/download|export/i, "download"], [/upload|import/i, "upload"], [/file|document/i, "file-text"],
  [/folder|project/i, "folder"], [/home|overview/i, "house"], [/search|find/i, "search"],
  [/calendar|date/i, "calendar"], [/user|profile|account/i, "user"], [/help|support/i, "circle-help"],
];

/** A pure identity mapping: independent of order, filtering, and hydration. */
export function resolveItemAdornment(identity: string) {
  let hash = 2166136261;
  for (const character of identity) hash = Math.imul(hash ^ character.codePointAt(0)!, 16777619) >>> 0;
  return {
    shape: signatureShapeNames[hash % signatureShapeNames.length],
    color: colors[(hash >>> 8) % colors.length],
    icon: namedGlyphs.find(([pattern]) => pattern.test(identity))?.[1] ?? glyphs[(hash >>> 16) % glyphs.length],
  };
}

export function itemText(children: React.ReactNode): string {
  return React.Children.toArray(children).map(child =>
    typeof child === "string" || typeof child === "number" ? String(child) :
    React.isValidElement<{ children?: React.ReactNode }>(child) ? itemText(child.props.children) : "",
  ).join(" ").trim();
}

/** Menus use one identity layer: a glyph by default, or an explicit silhouette. */
export function menuAdornment(value?: ItemAdornmentValue): ItemAdornmentValue {
  if (value === false || value === "none" || React.isValidElement(value)) return value;
  const options = typeof value === "object" ? value : {};
  const silhouette = options.showBackground === true || options.showIcon === false || options.icon === false || options.icon === null;
  return silhouette ? { ...options, showIcon: false } : { ...options, showBackground: false };
}

export type ItemAdornmentProps = Omit<React.ComponentProps<"span">, "children"> & {
  identity: string;
  value?: ItemAdornmentValue;
  size?: "sm" | "default" | "lg";
};
export function ItemAdornment({ identity, value = "auto", size = "default", className, style, ...props }: ItemAdornmentProps) {
  if (value === false || value === "none") return null;
  if (React.isValidElement(value)) return <span {...props} data-slot="item-adornment" data-custom="" aria-hidden="true" style={style} className={cn("v-item-adornment", className)} data-size={size}>{value}</span>;
  const options = typeof value === "object" ? value : {};
  const resolved = { ...resolveItemAdornment(identity), ...options };
  const showIcon = resolved.showIcon !== false && resolved.icon !== false && resolved.icon != null;
  const showBackground = resolved.showBackground !== false;
  if (!showIcon && !showBackground) return null;
  const color = colors.includes(resolved.color as ItemAdornmentColor) ? `var(--v-${resolved.color})` : resolved.color;
  return <span {...props} data-slot="item-adornment" data-shape={resolved.shape} data-color={resolved.color} data-size={size} data-background={showBackground || undefined} data-icon={showIcon || undefined} aria-hidden="true" className={cn("v-item-adornment", className)} style={{ "--adornment-color": color, "--adornment-foreground": resolved.foreground ?? (showBackground ? "var(--v-on-accent)" : "currentColor"), ...style } as React.CSSProperties}>
    {showBackground && <ShapeMorph name={resolved.shape} className="v-item-adornment__shape" />}
    {showIcon && <span className="v-item-adornment__icon">{typeof resolved.icon === "string" ? <AnimatedIcon name={resolved.icon} preset={resolved.effect ?? "auto"} size="sm" /> : resolved.icon}</span>}
  </span>;
}

function hasItemVisual(children: React.ReactNode): boolean {
  return React.Children.toArray(children).some(child => React.isValidElement<{ children?: React.ReactNode }>(child) &&
    (child.type === Icon || child.type === Disk || child.type === AnimatedIcon || child.type === ItemAdornment ||
      (child.type === React.Fragment && hasItemVisual(child.props.children))));
}

/** Decorate the native child itself when Radix's asChild composition is used. */
export function adornItem(children: React.ReactNode, adornment: ItemAdornmentValue | undefined, identity: string, asChild?: boolean, trailing?: React.ReactNode): React.ReactNode {
  if (asChild && React.isValidElement<{ children?: React.ReactNode }>(children)) {
    return React.cloneElement(children, { children: adornItem(children.props.children, adornment, identity, false, trailing) });
  }
  // Existing explicitly composed icons remain the consumer's custom adornment.
  const hasVisual = hasItemVisual(children);
  return <>{(!hasVisual || adornment !== undefined) && <ItemAdornment identity={identity || itemText(children)} value={adornment} />}{children}{trailing}</>;
}

/** Keep native-child and precomposed-icon handling while simplifying menu rows. */
export function adornMenuItem(children: React.ReactNode, adornment: ItemAdornmentValue | undefined, identity: string, asChild?: boolean, trailing?: React.ReactNode): React.ReactNode {
  if (asChild && React.isValidElement<{ children?: React.ReactNode }>(children)) {
    return React.cloneElement(children, { children: adornMenuItem(children.props.children, adornment, identity, false, trailing) });
  }
  return adornItem(children, adornment === undefined && hasItemVisual(children) ? false : menuAdornment(adornment), identity, false, trailing);
}
