"use client";

import * as React from "react";
import { useMotionValueEvent, useSpring } from "motion/react";
import { motionTokens, useChoreography } from "@/lib/cojeev-motion/choreography";

/** Small pointer-relative depth for opt-in surfaces. Native controls keep ownership. */
export function useDepth(ref: React.RefObject<HTMLElement | null>, enabled: boolean) {
  const { quiet } = useChoreography();
  const x = useSpring(0, motionTokens.spring.gentle);
  const y = useSpring(0, motionTokens.spring.gentle);
  const lift = useSpring(0, motionTokens.spring.responsive);
  const focused = React.useRef(false);
  const paint = React.useCallback(() => {
    const element = ref.current;
    if (!element) return;
    const px = x.get(), py = y.get();
    element.style.setProperty("--depth-x", `${px * 4}px`);
    element.style.setProperty("--depth-y", `${py * 4}px`);
    element.style.setProperty("--depth-axis-x", String(-py));
    element.style.setProperty("--depth-axis-y", String(px || .001));
    element.style.setProperty("--depth-angle", `${Math.hypot(px, py) * 2.5}deg`);
    element.style.setProperty("--depth-lift", `${lift.get() * -3}px`);
    element.style.setProperty("--depth-light-x", `${50 + px * 30}%`);
    element.style.setProperty("--depth-light-y", `${50 + py * 30}%`);
    element.style.setProperty("--depth-light", String(lift.get() * .3));
  }, [ref, x, y, lift]);
  useMotionValueEvent(x, "change", paint);
  useMotionValueEvent(y, "change", paint);
  useMotionValueEvent(lift, "change", paint);
  React.useEffect(() => {
    if (!enabled || quiet) { x.jump(0); y.jump(0); lift.jump(0); paint(); }
  }, [enabled, quiet, x, y, lift, paint]);
  const reset = () => { x.set(0); y.set(0); lift.set(focused.current && !quiet ? .6 : 0); };
  return {
    active: enabled && !quiet,
    onPointerMove(event: React.PointerEvent<HTMLElement>) {
      if (!enabled || quiet || event.pointerType === "touch") return;
      const rect = event.currentTarget.getBoundingClientRect();
      x.set(Math.max(-1, Math.min(1, (event.clientX - rect.left) / Math.max(1, rect.width) * 2 - 1)));
      y.set(Math.max(-1, Math.min(1, (event.clientY - rect.top) / Math.max(1, rect.height) * 2 - 1)));
      lift.set(1);
    },
    onPointerLeave: reset,
    onFocus() { focused.current = true; if (enabled && !quiet) lift.set(.6); },
    onBlur(event: React.FocusEvent<HTMLElement>) {
      if (!event.currentTarget.contains(event.relatedTarget)) { focused.current = false; reset(); }
    },
  };
}
