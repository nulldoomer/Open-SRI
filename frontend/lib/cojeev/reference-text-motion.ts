"use client";
import * as React from "react";
import { useMotionVisibility } from "@/lib/cojeev-motion/use-motion-visibility";
export function useReferenceText(paused = false) {
  const host = React.useRef<HTMLDivElement>(null);
  const { enabled, inView } = useMotionVisibility(host);
  return { host, enabled, inView, running: enabled && inView && !paused };
}
/** A single bounded clock. Interruption freezes elapsed time; cleanup cancels its frame. */
export function useReferenceClock(running: boolean, draw: (elapsed: number, delta: number) => boolean | void, replayKey: string | number = 0) {
  const elapsed = React.useRef(0), callback = React.useRef(draw);
  React.useLayoutEffect(() => { callback.current = draw; });
  React.useEffect(() => { elapsed.current = 0; }, [replayKey]);
  React.useEffect(() => {
    if (!running) return;
    let frame = 0, previous = 0;
    const tick = (now: number) => {
      const delta = previous ? Math.min(32, now - previous) : 0;
      previous = now; elapsed.current += delta;
      if (callback.current(elapsed.current, delta) !== false) frame = requestAnimationFrame(tick);
    };
    frame = requestAnimationFrame(tick);
    return () => cancelAnimationFrame(frame);
  }, [running, replayKey]);
}
export type ReferenceTextSize = "sm" | "default" | "lg";
export type ReferenceTextTone = "ink" | "pink" | "olive" | "blue" | "yellow";
