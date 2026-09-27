"use client";
import * as React from "react";
import { animate, useMotionValue, type MotionValue } from "motion/react";
import { motionTokens, trackMotion, useChoreography } from "@/lib/cojeev-motion/choreography";
/** Animate a value change, then settle its contour. There is no idle oscillator. */
export function useOrganicValue(value: number, quiet = false) {
  const { quiet: globalQuiet, transition } = useChoreography(),
    position = useMotionValue(value),
    energy = useMotionValue(0),
    previous = React.useRef(value);
  React.useLayoutEffect(() => {
    const delta = value - previous.current;
    previous.current = value;
    if (quiet || globalQuiet) {
      position.jump(value);
      energy.jump(0);
      return;
    }
    const move = animate(position, value, transition),
      releaseMove = trackMotion(move);
    if (delta) energy.set(Math.max(-1, Math.min(1, delta * 5)));
    const settle = animate(energy, 0, {
        ...motionTokens.spring.responsive,
        restDelta: 0.005,
        restSpeed: 0.05,
      }),
      releaseSettle = trackMotion(settle);
    return () => {
      releaseMove();
      releaseSettle();
    };
  }, [value, quiet, globalQuiet, transition, position, energy]);
  return { position, energy };
}
/** SVG geometry follows its actual box without React renders during a drag. */
export function usePaintSize(
  ref: React.RefObject<SVGSVGElement | null>,
  initialWidth = 100,
  initialHeight = 20,
) {
  const width = useMotionValue(initialWidth),
    height = useMotionValue(initialHeight);
  React.useLayoutEffect(() => {
    const element = ref.current;
    if (!element) return;
    const measure = () => {
      const box = element.getBoundingClientRect();
      width.set(box.width);
      height.set(box.height);
    };
    // Derived MotionValues subscribe during layout effects. Measuring in this
    // earlier hook synchronously can leave their first drawing at100x20 forever.
    // The first frame and ResizeObserver both run after those subscriptions exist.
    const frame = requestAnimationFrame(measure);
    const observer = new ResizeObserver(measure);
    observer.observe(element);
    return () => {
      cancelAnimationFrame(frame);
      observer.disconnect();
    };
  }, [ref, width, height]);
  return { width, height };
}
export type OrganicValue = {
  position: MotionValue<number>;
  energy: MotionValue<number>;
};
