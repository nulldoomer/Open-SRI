"use client";

import * as React from "react";
import { animate, frameSteps, motionValue, type AnimationPlaybackControls, type Transition } from "motion";
import { getMotionTime, isMotionClockFrozen, registerMotionClock } from "@/lib/cojeev-motion/clock";
import { getSettingsSnapshot, getServerSettingsSnapshot, subscribeSettings, type SettingsSnapshot } from "@/lib/cojeev-motion/settings";
import { useReducedMotion } from "@/lib/cojeev-motion/use-reduced-motion";

/** The shared rhythm. Durations and stagger are seconds, matching Motion's API. */
export const motionTokens = {
  spring: {
    responsive: { type: "spring", stiffness: 360, damping: 32, mass: .85 },
    expressive: { type: "spring", stiffness: 260, damping: 22, mass: 1 },
    gentle: { type: "spring", stiffness: 180, damping: 28, mass: 1 },
  },
  ease: { enter: [.2, .8, .2, 1], exit: [.4, 0, 1, 1], settle: [.2, .65, .25, 1] },
  duration: { quick: .16, enter: .32, exit: .18 },
  stagger: .035,
} as const;

export function resolveChoreography(snapshot: SettingsSnapshot, reduced: boolean): { quiet: boolean; transition: Transition } {
  const quiet = reduced || snapshot.motion.mode === "off" || snapshot.flow.variant === "off";
  if (quiet) return { quiet, transition: { duration: 0, delay: 0 } };
  const speed = snapshot.flow.speed;
  const spring = ["jelly", "rubber", "drop", "pebble"].includes(snapshot.flow.variant)
    ? motionTokens.spring.expressive : motionTokens.spring.responsive;
  return { quiet, transition: { ...spring, stiffness: spring.stiffness * speed * speed, damping: spring.damping * speed } };
}

/** Reactive settings subscription for reusable React surfaces. */
export function useChoreography() {
  const snapshot = React.useSyncExternalStore(subscribeSettings, getSettingsSnapshot, getServerSettingsSnapshot);
  const reduced = useReducedMotion();
  return React.useMemo(() => resolveChoreography(snapshot, reduced), [snapshot, reduced]);
}

/** Connect Motion's JS animations to the existing deterministic document clock. */
export function trackMotion(controls: AnimationPlaybackControls) {
  let origin = getMotionTime() - controls.time * 1000;
  let frozen = isMotionClockFrozen();
  if (frozen) controls.pause();
  const release = registerMotionClock(time => {
    if (time === null) {
      if (frozen) controls.play();
      frozen = false;
      return;
    }
    if (!frozen) {
      origin = time - controls.time * 1000;
      controls.pause();
      frozen = true;
    }
    controls.time = Math.max(0, time - origin) / 1000;
    if (controls.time >= controls.duration) controls.complete();
    // A seek schedules Motion's update phase. Flush it at this explicit sample;
    // normal playback stays entirely on Motion's own animation frame loop.
    frameSteps.update.process({ delta: 0, timestamp: performance.now(), isProcessing: false });
  });
  void controls.finished.then(release, release);
  return () => { release(); controls.stop(); };
}

/** One interruptible spring per scalar; retargets inherit the current value and velocity. */
export function createMotionLane(initial: number, render: (value: number) => void) {
  const value = motionValue(initial);
  const unsubscribe = value.on("change", render);
  let cancel = () => {};
  let generation = 0;
  const stop = () => { generation++; cancel(); cancel = () => {}; value.stop(); };
  const jump = (target: number) => { stop(); value.jump(target); render(target); };
  return {
    get: () => value.get(),
    jump,
    to(target: number, transition: Transition, complete?: () => void) {
      stop();
      if (transition.duration === 0) { jump(target); complete?.(); return; }
      const owner = generation;
      const controls = animate(value, target, {
        ...transition,
        autoplay: !isMotionClockFrozen(),
        onComplete: () => { if (generation === owner) complete?.(); },
      });
      cancel = trackMotion(controls);
    },
    stop,
    dispose: () => { stop(); unsubscribe(); value.destroy(); },
  };
}
