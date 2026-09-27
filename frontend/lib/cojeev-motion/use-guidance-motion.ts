"use client";

import * as React from "react";
import { useChoreography } from "@/lib/cojeev-motion/choreography";
import { useMotionVisibility } from "@/lib/cojeev-motion/use-motion-visibility";

/** Decorative cues share the same global, local and visibility stillness boundary. */
export function useGuidanceMotion(ref: React.RefObject<HTMLElement | null>) {
  const { quiet } = useChoreography();
  const visibility = useMotionVisibility(ref);
  const subscribe = React.useCallback((listener: () => void) => {
    const observer = new MutationObserver(listener);
    for (let element = ref.current; element; element = element.parentElement) {
      observer.observe(element, { attributes: true, attributeFilter: ["data-flow", "data-no-glide"] });
    }
    return () => observer.disconnect();
  }, [ref]);
  const snapshot = React.useCallback(() => Boolean(ref.current?.closest('[data-flow="off"],[data-no-glide]')), [ref]);
  const localQuiet = React.useSyncExternalStore(subscribe, snapshot, () => false);
  return { ...visibility, quiet: quiet || localQuiet };
}
