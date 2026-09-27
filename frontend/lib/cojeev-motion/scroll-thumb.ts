export function scrollThumbPath(engagement: number, bias: number, velocity = 0, pressure = 0) {
  const active = Math.max(0, Math.min(1, Number.isFinite(engagement) ? engagement : 0));
  const speed = Math.max(-1, Math.min(1, Number.isFinite(velocity) ? velocity : 0));
  const pressed = Math.max(0, Math.min(1, Number.isFinite(pressure) ? pressure : 0));
  const bend = Math.max(-1, Math.min(1, Number.isFinite(bias) ? bias : 0)) * active;
  const half = 5.4 + active * 1.6 + pressed * .8;
  const upper = half - speed * 1.15, lower = half + speed * 1.15;
  const waist = half - pressed * 2.2;
  const lean = bend * .8;
  const f = (n: number) => Number(n.toFixed(3));
  return `M 10 1 C ${f(10 + upper * .7)} 1 ${f(10 + upper)} 5 ${f(10 + upper)} 14 C ${f(10 + upper)} 28 ${f(10 + waist + lean)} 36 ${f(10 + waist + lean)} 50 C ${f(10 + waist + lean)} 66 ${f(10 + lower)} 75 ${f(10 + lower)} 87 C ${f(10 + lower)} 95 ${f(10 + lower * .65)} 99 10 99 C ${f(10 - lower * .7)} 99 ${f(10 - lower)} 95 ${f(10 - lower)} 86 C ${f(10 - lower)} 72 ${f(10 - waist + lean)} 64 ${f(10 - waist + lean)} 50 C ${f(10 - waist + lean)} 34 ${f(10 - upper)} 25 ${f(10 - upper)} 13 C ${f(10 - upper)} 5 ${f(10 - upper * .65)} 1 10 1 Z`;
}

/** Signed native pixels/ms become a bounded contour impulse, never scroll momentum. */
export function scrollVelocity(delta:number,elapsed:number) {
  if(!Number.isFinite(delta)||!Number.isFinite(elapsed)||elapsed<=0)return 0;
  return Math.tanh(delta/Math.max(8,elapsed)*.85);
}

/** The overlay follows document scroll; it never creates a second scroll owner. */
export function pageScrollGeometry(scrollSize: number, viewportSize: number, trackSize: number, scrollTop: number) {
  const safe = (value: number) => Number.isFinite(value) ? Math.max(0, value) : 0;
  const content = safe(scrollSize), viewport = safe(viewportSize), track = safe(trackSize);
  const maxScroll = Math.max(0, content - viewport);
  const thumbSize = maxScroll === 0 ? track : Math.min(track, Math.max(36, track * viewport / Math.max(1, content)));
  const travel = track - thumbSize;
  const thumbOffset = maxScroll === 0 ? 0 : Math.min(maxScroll, safe(scrollTop)) / maxScroll * travel;
  return { maxScroll, thumbSize, thumbOffset, travel };
}

type ScrollRoot = Pick<Element, "getAttribute" | "setAttribute" | "removeAttribute">;

/** Inline this in the document head so the native scrollbar is never painted and then
 *  swapped once the overlay mounts. It is deliberately bounded: if the bundle never
 *  arrives, or never mounts a page scrollbar, the marker clears itself and the native
 *  scrollbar comes back. Without scripting it never runs, so nothing is hidden. */
export const pageScrollbarPendingAttribute = "data-page-scrollbar-pending";
export const pageScrollbarPendingTimeout = 5000;
export const pageScrollbarBootstrap = `(function(){var r=document.documentElement;r.setAttribute("${pageScrollbarPendingAttribute}","");setTimeout(function(){r.removeAttribute("${pageScrollbarPendingAttribute}")},${pageScrollbarPendingTimeout})})()`;

const pageScrollRoots = new WeakMap<ScrollRoot, { users: number; original: string | null }>();
/** StrictMode-safe ownership of the native scrollbar override. */
export function acquirePageScrollbar(root: ScrollRoot) {
  const state = pageScrollRoots.get(root) ?? { users: 0, original: root.getAttribute("data-page-scrollbar") };
  // Ownership supersedes the bootstrap's pending marker, and dropping it here is what
  // lets an unmount inside the bootstrap window hand the native scrollbar straight back.
  if (state.users++ === 0) { pageScrollRoots.set(root, state); root.removeAttribute(pageScrollbarPendingAttribute); root.setAttribute("data-page-scrollbar", "mounted"); }
  let released = false;
  return () => {
    if (released) return;
    released = true;
    if (--state.users === 0) {
      if (state.original === null) root.removeAttribute("data-page-scrollbar");
      else root.setAttribute("data-page-scrollbar", state.original);
      pageScrollRoots.delete(root);
    }
  };
}

const elementScrollRoots = new WeakMap<ScrollRoot, { users: number; original: string | null }>();
/** A native editor remains the owner; remove only its scrollbar while adapted. */
export function acquireElementScrollbar(root: ScrollRoot) {
  const state = elementScrollRoots.get(root) ?? { users: 0, original: root.getAttribute("data-element-scrollbar") };
  if (state.users++ === 0) { elementScrollRoots.set(root, state); root.setAttribute("data-element-scrollbar", "mounted"); }
  let released = false;
  return () => {
    if (released) return;
    released = true;
    if (--state.users === 0) {
      if (state.original === null) root.removeAttribute("data-element-scrollbar");
      else root.setAttribute("data-element-scrollbar", state.original);
      elementScrollRoots.delete(root);
    }
  };
}
