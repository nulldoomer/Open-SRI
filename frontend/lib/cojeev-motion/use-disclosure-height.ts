"use client";

import * as React from "react";
import {
  createMotionLane,
  motionTokens,
  useChoreography,
} from "@/lib/cojeev-motion/choreography";

/** One interruptible height owner for Radix disclosures; closed descendants are inert. */
export function useDisclosureHeight(
  ref: React.RefObject<HTMLDivElement | null>,
  animationName?: string,
) {
  const { quiet } = useChoreography();
  const quietRef = React.useRef(quiet);
  const settle = React.useRef<(() => void) | null>(null);
  React.useLayoutEffect(() => {
    quietRef.current = quiet;
    if (quiet) settle.current?.();
  }, [quiet]);
  React.useLayoutEffect(() => {
    const content = ref.current;
    if (!content) return;
    const originalHeight = content.style.height;
    const originalHidden = content.getAttribute("aria-hidden");
    const originalInert = content.inert;
    const lane = createMotionLane(
      content.getBoundingClientRect().height,
      (height) => {
        content.style.height = `${Math.max(0, height)}px`;
      },
    );
    let observed: Element | null = null;
    let initialized = false;
    const sync = (animate = true) => {
      const open = content.dataset.state === "open";
      content.inert = !open || originalInert;
      if (!open) content.setAttribute("aria-hidden", "true");
      else if (originalHidden === null) content.removeAttribute("aria-hidden");
      else content.setAttribute("aria-hidden", originalHidden);
      content.style.setProperty(
        "--disclosure-exit-duration",
        quietRef.current ? "0s" : `${motionTokens.duration.exit + 0.04}s`,
      );
      const target = open
        ? (content.querySelector<HTMLElement>("[data-disclosure-inner]")
            ?.offsetHeight ?? 0)
        : 0;
      if (!animate || quietRef.current) lane.jump(target);
      else
        lane.to(target, {
          duration: open
            ? motionTokens.duration.enter
            : motionTokens.duration.exit,
          ease: [...(open ? motionTokens.ease.enter : motionTokens.ease.exit)],
        });
    };
    const resize = new ResizeObserver(() => sync(initialized));
    const observeInner = () => {
      const inner = content.querySelector("[data-disclosure-inner]");
      if (inner === observed) return;
      if (observed) resize.unobserve(observed);
      observed = inner;
      if (inner) resize.observe(inner);
    };
    const changes = new MutationObserver(() => {
      observeInner();
      sync();
    });
    changes.observe(content, {
      attributes: true,
      attributeFilter: ["data-state"],
      childList: true,
    });
    observeInner();
    sync(false);
    initialized = true;
    // Radix suppresses authored animation while measuring the first open frame.
    content.style.animationName = animationName ?? "";
    settle.current = () => sync(false);
    return () => {
      settle.current = null;
      changes.disconnect();
      resize.disconnect();
      lane.dispose();
      content.style.height = originalHeight;
      content.style.removeProperty("--disclosure-exit-duration");
      content.inert = originalInert;
      if (originalHidden === null) content.removeAttribute("aria-hidden");
      else content.setAttribute("aria-hidden", originalHidden);
    };
  }, [ref, animationName]);
}
