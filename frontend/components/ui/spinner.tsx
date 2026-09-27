"use client";
import * as React from "react";
import { cva, type VariantProps } from "class-variance-authority";
import { motion } from "motion/react";
import { motionTokens } from "@/lib/cojeev-motion/choreography";
import { useMotionVisibility } from "@/lib/cojeev-motion/use-motion-visibility";
import { assignMotionRef } from "@/lib/cojeev-motion/refs";
import { cn } from "@/lib/utils";

const SpinnerVariants = cva(
  "v-pulse [display:inline-grid] [place-items:center] [width:var(--pulse,34px)] [height:var(--pulse,34px)] [position:relative]",
  {
    variants: {
      variant: { default: "", point: "-point" },
      size: { default: "" },
    },
    defaultVariants: { variant: "default", size: "default" },
  },
);
function seedPath(radius: (angle: number) => number) {
  return (
    "M" +
    Array.from({ length: 64 }, (_, i) => {
      const angle = (i / 64) * Math.PI * 2;
      const r = radius(angle);
      return `${(50 + r * Math.cos(angle)).toFixed(2)} ${(50 + r * Math.sin(angle)).toFixed(2)}`;
    }).join("L") +
    "Z"
  );
}
const pebble = seedPath(
  (t) => 41 + 2.4 * Math.cos(3 * t + 0.6) + 1.4 * Math.sin(5 * t),
);
const star = seedPath(
  (t) => 21 + 23 * Math.pow(Math.abs(Math.cos(2 * t)), 1.9),
);
const puff = seedPath((t) => 36 + 6 * Math.abs(Math.cos(4 * t)));
const point = seedPath(
  (t) => 12 + 32 * Math.pow(Math.abs(Math.cos(2 * t)), 2.4),
);
const pointSoft = seedPath(
  (t) => 16 + 28 * Math.pow(Math.abs(Math.cos(2 * t)), 2.2),
);
export type SpinnerAppearance = "bloom" | "orbit" | "relay";
export type SpinnerProps = React.ComponentProps<"span"> &
  VariantProps<typeof SpinnerVariants> & {
    label?: string;
    appearance?: SpinnerAppearance;
    paused?: boolean;
  };
export function Spinner({
  ref,
  className,
  variant,
  size,
  label,
  children,
  appearance = "bloom",
  paused = false,
  ...props
}: SpinnerProps) {
  const host = React.useRef<HTMLSpanElement>(null);
  const { enabled, inView } = useMotionVisibility(host);
  const active =
    enabled &&
    inView &&
    !paused &&
    !className?.split(/\s+/).includes("-paused");
  const attach = React.useCallback(
    (element: HTMLSpanElement | null) => {
      host.current = element;
      const release = assignMotionRef(ref, element);
      return () => {
        host.current = null;
        release();
      };
    },
    [ref],
  );
  const pointed = variant === "point",
    still = pointed ? point : star;
  const caption =
    label ??
    ((props as Record<string, unknown>)["data-label"] as string | undefined);
  return (
    <span
      ref={attach}
      role="status"
      aria-label={caption || "Working"}
      data-slot="spinner"
      data-part="root"
      data-appearance={appearance}
      data-paused={paused || undefined}
      data-animated={active || undefined}
      className={cn(SpinnerVariants({ variant, size }), className)}
      {...props}
    >
      <svg viewBox="0 0 100 100" aria-hidden="true">
        {appearance === "bloom" ? (
          <>
            <motion.g
              initial={{ rotate: 0 }}
              animate={{ rotate: active ? [0, 360] : 0 }}
              transition={
                active
                  ? { duration: 16, repeat: Infinity, ease: "linear" }
                  : { duration: 0 }
              }
              style={{ transformOrigin: "50% 50%", transformBox: "view-box" }}
            >
              <motion.path
                className="seed"
                initial={{ d: still }}
                d={still}
                animate={{
                  d: active
                    ? pointed
                      ? [point, pointSoft, point]
                      : [pebble, star, star, puff, pebble]
                    : still,
                }}
                transition={
                  active
                    ? {
                        duration: pointed ? 2.8 : 4.2,
                        repeat: Infinity,
                        times: pointed ? [0, 0.5, 1] : [0, 0.3, 0.45, 0.75, 1],
                        ease: [...motionTokens.ease.settle],
                      }
                    : { duration: 0 }
                }
              />
            </motion.g>
            <motion.circle
              className="core"
              cx="50"
              cy="50"
              r="5"
              initial={{ scale: 1 }}
              animate={{ scale: active ? [1, 0.6, 1] : 1 }}
              transition={
                active
                  ? { duration: 2.1, repeat: Infinity, ease: "easeInOut" }
                  : { duration: 0 }
              }
              style={{ transformOrigin: "50% 50%", transformBox: "view-box" }}
            />
          </>
        ) : appearance === "orbit" ? (
          <>
            <circle className="v-pulse__orbit" cx="50" cy="50" r="33" />
            <motion.g
              initial={{ rotate: 0 }}
              animate={{ rotate: active ? [0, 360] : 0 }}
              transition={
                active
                  ? { duration: 3.6, repeat: Infinity, ease: "linear" }
                  : { duration: 0 }
              }
              style={{ transformOrigin: "50% 50%", transformBox: "view-box" }}
            >
              {[0, 1, 2].map((i) => (
                <g key={i} transform={`rotate(${i * 120} 50 50)`}>
                  <ellipse
                    className={`v-pulse__mark v-pulse__mark--${i}`}
                    cx="50"
                    cy="17"
                    rx={9 - i * 2}
                    ry={12 - i * 2}
                  />
                </g>
              ))}
            </motion.g>
            <path
              className="v-pulse__hub"
              d="M50 39Q51 49 61 50Q51 51 50 61Q49 51 39 50Q49 49 50 39Z"
            />
          </>
        ) : (
          <>
            <path className="v-pulse__orbit" d="M15 58H85" />
            {[0, 1, 2].map((i) => (
              <motion.rect
                key={i}
                className={`v-pulse__mark v-pulse__mark--${i}`}
                x={9 + i * 30}
                y="39"
                width="22"
                height="28"
                rx="10"
                initial={{ y: 0, rx: 10, opacity: 1 }}
                animate={{
                  y: active ? [0, -15, 0] : 0,
                  rx: active ? [10, 5, 10] : 10,
                  opacity: active ? [0.45, 1, 0.45] : 1,
                }}
                transition={
                  active
                    ? {
                        duration: 1.8,
                        repeat: Infinity,
                        delay: i * 0.22,
                        ease: [...motionTokens.ease.settle],
                      }
                    : { duration: 0 }
                }
              />
            ))}
          </>
        )}
      </svg>
      {caption && <SpinnerLabel>{caption}</SpinnerLabel>}
      {children}
    </span>
  );
}

const SpinnerLabelVariants = cva(
  "v-pulse__label [font-weight:500] [color:var(--v-text-2)] [opacity:1] [position:absolute] [inset:auto_auto_-20px_50%] [transform:translateX(-50%)] [font-size:11px] [white-space:nowrap]",
  {
    variants: { variant: { default: "" }, size: { default: "" } },
    defaultVariants: { variant: "default", size: "default" },
  },
);
export type SpinnerLabelProps = React.ComponentProps<"span"> &
  VariantProps<typeof SpinnerLabelVariants>;
export function SpinnerLabel({
  className,
  variant,
  size,
  ...props
}: SpinnerLabelProps) {
  return (
    <span
      data-slot="spinner-label"
      data-part="label"
      className={cn(SpinnerLabelVariants({ variant, size }), className)}
      {...props}
    />
  );
}
