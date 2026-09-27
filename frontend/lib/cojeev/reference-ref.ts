"use client";
import * as React from "react";
import { assignMotionRef } from "@/lib/cojeev-motion/refs";

/** Preserve caller refs while the effect observes the same DOM element. */
export function useReferenceRef<T>(hostRef: React.RefObject<T | null>, forwardedRef: React.Ref<T> | undefined) {
  return React.useCallback((node: T | null) => {
    hostRef.current = node;
    const cleanup = assignMotionRef(forwardedRef, node);
    return () => { hostRef.current = null; cleanup(); };
  }, [hostRef, forwardedRef]);
}
