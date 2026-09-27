"use client";

import * as React from "react";
import { AnimatePresence, motion, useIsPresent, type HTMLMotionProps, type Target, type TargetAndTransition } from "motion/react";
import { Slot } from "@radix-ui/react-slot";
import { cn } from "@/lib/utils";
import { motionTokens, useChoreography } from "@/lib/cojeev-motion/choreography";

const shown = { opacity: 1, "--presence-x": "0px", "--presence-y": "0px", "--presence-scale": 1 };
export const presencePresets = {
  fade: { initial: { ...shown, opacity: 0 }, animate: shown, exit: { ...shown, opacity: 0 } },
  rise: { initial: { ...shown, opacity: 0, "--presence-y": "10px" }, animate: shown, exit: { ...shown, opacity: 0, "--presence-y": "-6px" } },
  slide: { initial: { ...shown, opacity: 0, "--presence-x": "16px" }, animate: shown, exit: { ...shown, opacity: 0, "--presence-x": "-10px" } },
  scale: { initial: { ...shown, opacity: 0, "--presence-scale": .96 }, animate: shown, exit: { ...shown, opacity: 0, "--presence-scale": .985 } },
  settle: { initial: { ...shown, opacity: 0, "--presence-y": "6px", "--presence-scale": 1.04 }, animate: shown, exit: { ...shown, opacity: 0, "--presence-y": "-5px", "--presence-scale": .94 } },
  soften: { initial: { ...shown, opacity: 0, "--presence-y": "8px", "--presence-blur": "5px" }, animate: { ...shown, "--presence-blur": "0px" }, exit: { ...shown, opacity: 0, "--presence-y": "-10px", "--presence-blur": "6px" } },
  focus: { initial: { ...shown, opacity: 0, "--presence-y": "10px", "--presence-scale": 1.015, "--presence-blur": "9px" }, animate: { ...shown, "--presence-blur": "0px" }, exit: { ...shown, opacity: 0, "--presence-y": "-8px", "--presence-blur": "7px" } },
  mask: {
    initial: { ...shown, opacity: 0, clipPath: "inset(0% 0% 100% 0% round 16px)" },
    animate: { ...shown, clipPath: "inset(0% 0% 0% 0% round 16px)" },
    exit: { ...shown, opacity: 0, clipPath: "inset(0% 0% 100% 0% round 16px)" },
  },
} satisfies Record<string, { initial: Target; animate: Target; exit: Target }>;
export type PresencePreset = keyof typeof presencePresets;

export type MotionPresenceProps = React.ComponentProps<typeof AnimatePresence>;
/** Keep this boundary mounted; each conditional direct child needs a stable key. */
export function MotionPresence({ initial = false, ...props }: MotionPresenceProps) {
  const { quiet } = useChoreography();
  return <AnimatePresence initial={quiet ? false : initial} {...props} />;
}

const MotionSlot = motion.create(Slot);
export type MotionSurfaceProps = HTMLMotionProps<"div"> & {
  "data-slot"?: string;
  /** Merge onto exactly one ref-capable child, preserving its native element. */
  asChild?: boolean;
  preset?: PresencePreset;
  /** Seconds; use motionTokens.stagger for a coordinated sibling sequence. */
  delay?: number;
  /** Seconds before exit; cap sibling staggering yourself to a short total window. */
  exitDelay?: number;
  /** Seconds. Soft-focus and settling presets use a gentler 420ms default. */
  exitDuration?: number;
};

/** Defines lifecycle states. An external MotionPresence retains actual removals. */
export function MotionSurface({
  asChild = false, preset = "rise", delay = 0, exitDelay = 0, exitDuration, className,
  initial, animate, exit, transition: suppliedTransition, ...props
}: MotionSurfaceProps) {
  const { quiet, transition } = useChoreography();
  const isPresent = useIsPresent();
  const states = presencePresets[preset];
  const Element = asChild ? MotionSlot : motion.div;
  const entrance = { ...transition, delay: Math.max(0, Number.isFinite(delay) ? delay : 0), ...suppliedTransition };
  const soft = ["soften", "focus", "settle"].includes(preset);
  const exitTime = exitDuration === undefined || !Number.isFinite(exitDuration) ? soft ? .42 : motionTokens.duration.exit : Math.min(2, Math.max(0, exitDuration));
  const exitState: TargetAndTransition = { ...states.exit, transition: { duration: exitTime, delay: Number.isFinite(exitDelay) ? Math.min(.6, Math.max(0, exitDelay)) : 0, ease: [...(soft ? motionTokens.ease.settle : motionTokens.ease.exit)] } };
  const children = !isPresent && asChild && React.isValidElement(props.children)
    ? React.cloneElement(props.children as React.ReactElement<React.HTMLAttributes<HTMLElement>>, { inert: true, "aria-hidden": true })
    : props.children;
  return <Element
    {...props}
    {...(asChild ? {} : { "data-slot": props["data-slot"] ?? "motion-surface" })}
    data-motion-surface={preset}
    data-motion-quiet={quiet ? "true" : undefined}
    data-motion-exiting={!isPresent ? "true" : undefined}
    inert={!isPresent || props.inert || undefined}
    aria-hidden={!isPresent ? true : props["aria-hidden"]}
    className={cn("v-motion-surface", className)}
    initial={quiet ? false : initial ?? states.initial}
    animate={quiet ? states.animate : animate ?? states.animate}
    exit={quiet ? { ...states.animate, transition: { duration: 0, delay: 0 } } : exit ?? exitState}
    transition={quiet ? { duration: 0, delay: 0 } : entrance}
  >{children}</Element>;
}
