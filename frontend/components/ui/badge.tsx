"use client";

import * as React from "react";
import { useMorph } from "@/lib/cojeev-motion/use-morph";
import { cva, type VariantProps } from "class-variance-authority";
import { cn } from "@/lib/utils";
import {
  controlRadiusStyle,
  type ControlRadius,
} from "@/lib/cojeev/control-appearance";

const badgeVariants = cva(
  "v-badge inline-flex items-center justify-center gap-[6px] rounded-[var(--r-pill)] leading-none whitespace-nowrap bg-[var(--v-beige)] text-[color:var(--v-text)] tracking-[.005em]",
  {
    variants: {
      variant: {
        default: "",
        pink: "-pink bg-[var(--v-pink)] text-[color:var(--v-on-accent)] [--muted-foreground:var(--muted-foreground-ink)]",
        yellow:
          "-yellow bg-[var(--status-warn-bg)] text-[color:var(--status-warn-ink)] [--muted-foreground:var(--muted-foreground-ink)]",
        olive:
          "-olive bg-[var(--status-ok-bg)] text-[color:var(--status-ok-ink)] [--muted-foreground:var(--muted-foreground-ink)]",
        blue: "-blue bg-[var(--status-info-bg)] text-[color:var(--status-info-ink)] [--muted-foreground:var(--muted-foreground-ink)]",
        ink: "-ink bg-[var(--v-ink)] text-[color:var(--v-on-ink)]",
        cream: "-cream bg-[var(--v-canvas)]",
        "pink-soft":
          "-pink-soft bg-[var(--v-pink-soft)] [--muted-foreground:var(--muted-foreground-tinted)]",
        "yellow-soft":
          "-yellow-soft bg-[var(--v-yellow-soft)] [--muted-foreground:var(--muted-foreground-tinted)]",
        "olive-soft":
          "-olive-soft bg-[var(--v-olive-soft)] [--muted-foreground:var(--muted-foreground-tinted)]",
        "blue-soft":
          "-blue-soft bg-[var(--v-blue-soft)] [--muted-foreground:var(--muted-foreground-tinted)]",
        danger:
          "-danger bg-[var(--status-danger-bg)] text-[color:var(--status-danger-ink)]",
        pending: "-pending bg-[var(--v-ink)] text-[color:var(--v-on-ink)]",
        count:
          "-count min-w-[22px] bg-[var(--v-ink)] text-[color:var(--v-on-ink)] tabular-nums",
        dashed:
          "-dashed bg-[var(--v-canvas)] border border-dashed border-[var(--v-edge)]",
        caps: "-caps uppercase tracking-[.08em]",
        test: "-test bg-[var(--v-canvas)] border-[1.5px] border-solid border-[var(--v-ink)] tracking-[.03em]",
        live: "-live bg-[var(--v-canvas)] [box-shadow:inset_0_0_0_1px_var(--v-edge)]",
      },
      size: { default: "", sm: "-sm", lg: "-lg" },
    },
    defaultVariants: { variant: "default", size: "default" },
  },
);
// Later reference rules deliberately override sizes for count/caps/dashed/test.
const badgeDimensions = cva("", {
  variants: {
    dimension: {
      default: "h-[26px] px-[11px] py-0 text-[12.5px] font-medium",
      sm: "h-[20px] px-[8px] py-0 text-[11px] font-semibold",
      lg: "h-[32px] px-[14px] py-0 text-[13.5px] font-medium",
      count: "h-[22px] px-[7px] py-0 text-[11px] font-semibold",
      caps: "h-[26px] px-[11px] py-0 text-[10.5px] font-semibold",
    },
  },
});

type BadgeProps = React.ComponentProps<"span"> &
  VariantProps<typeof badgeVariants> & {
    appearance?: "stamp" | "tag" | "counter";
    radius?: ControlRadius;
  };
const badgeFaces: Record<string, string> = {
  default: "var(--v-beige)",
  pink: "var(--v-pink)",
  yellow: "var(--status-warn-bg)",
  olive: "var(--status-ok-bg)",
  blue: "var(--status-info-bg)",
  ink: "var(--v-ink)",
  cream: "var(--v-canvas)",
  "pink-soft": "var(--v-pink-soft)",
  "yellow-soft": "var(--v-yellow-soft)",
  "olive-soft": "var(--v-olive-soft)",
  "blue-soft": "var(--v-blue-soft)",
  danger: "var(--status-danger-bg)",
  pending: "var(--v-ink)",
  count: "var(--v-ink)",
  dashed: "var(--v-canvas)",
  caps: "var(--v-beige)",
  test: "var(--v-canvas)",
  live: "var(--v-canvas)",
};
function Badge({
  ref: externalRef,
  className,
  variant = "default",
  size = "default",
  appearance,
  radius,
  style,
  ...props
}: BadgeProps) {
  const morphRef = useMorph<HTMLSpanElement>("pills", externalRef);
  const dimension =
    variant === "count" || variant === "caps" ? variant : (size ?? "default");
  return (
    <span
      ref={morphRef}
      data-slot="badge"
      data-part="root"
      data-state="rest"
      data-appearance={appearance}
      data-morph={appearance ? "both" : undefined}
      data-r={appearance ? "css" : undefined}
      data-dash={appearance === "stamp" ? "2 3" : undefined}
      style={{
        ...((appearance
          ? { "--badge-face": badgeFaces[variant ?? "default"] }
          : {}) as React.CSSProperties),
        ...style,
        ...controlRadiusStyle(radius),
      }}
      className={cn(
        badgeVariants({ variant, size }),
        badgeDimensions({ dimension }),
        (variant === "dashed" || variant === "test") && "h-[26px] px-[11px]",
        (variant === "test" || variant === "danger") && "font-semibold",
        "leading-none",
        className,
      )}
      {...props}
    />
  );
}
function BadgeIndicator({ className, ...props }: React.ComponentProps<"span">) {
  return (
    <span
      data-slot="badge-indicator"
      data-part="indicator"
      aria-hidden="true"
      className={cn(
        "v-dot size-[7px] [border-radius:50%] flex-none m-0 bg-current",
        className,
      )}
      {...props}
    />
  );
}
export { Badge, BadgeIndicator, badgeVariants };
export type { BadgeProps };
