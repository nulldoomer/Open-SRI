"use client";
import * as React from "react";
import { useMotionVisibility } from "@/lib/cojeev-motion/use-motion-visibility";
import { galleryClamp, galleryIndex, stepGalleryTransition } from "@/lib/cojeev/reference-gallery-geometry";
export function useGallerySelection(count: number, initial = 0, value?: number, onChange?: (index: number) => void) {
 const [local,setLocal]=React.useState(initial);
 const selected=galleryIndex(value ?? local,count);
 const select=(index:number)=> {const next=galleryIndex(index,count); if(next < 0 || next===selected) return; if(value===undefined)setLocal(next); onChange?.(next);};
 return {selected,select};
}
/** Pausing a content transition completes its target so no content remains obscured. */
export function useGalleryTransition(active: boolean, duration = 1100, paused = false) {
 const host=React.useRef<HTMLDivElement>(null);
 const {enabled,inView}=useMotionVisibility(host);
 const [progress,setProgress]=React.useState(active ? 1 : 0);
 const position=React.useRef(progress);
 const canMove=enabled && inView && !paused;
 const target=active ? 1 : 0;
 const boundedDuration=Number.isFinite(duration) ? galleryClamp(duration,0,3000) : 0;
 React.useEffect(()=> {
  if(!canMove || !boundedDuration){position.current=target;
   // eslint-disable-next-line react-hooks/set-state-in-effect -- An external stillness boundary must settle the visible content synchronously.
   setProgress(target);return;}
  let frame=0,last=performance.now();
  const tick=(now:number)=> {position.current=stepGalleryTransition(position.current,target,Math.min(now-last,64),boundedDuration);last=now;setProgress(position.current);if(position.current!==target)frame=requestAnimationFrame(tick);};
  if(position.current!==target)frame=requestAnimationFrame(tick);
  return ()=>cancelAnimationFrame(frame);
 },[target,canMove,boundedDuration]);
 return {host,progress:canMove ? progress : target,canMove};
}
export function useGalleryRef<T>(hostRef: React.RefObject<T | null>, forwardedRef: React.Ref<T> | undefined): React.RefCallback<T> {
 // eslint-disable-next-line react-hooks/immutability -- A React callback ref assigns caller and internal ref objects during commit, never during render.
 return React.useCallback((node:T|null)=> {hostRef.current=node;if(typeof forwardedRef==="function")return forwardedRef(node);if(forwardedRef)forwardedRef.current=node;},[hostRef,forwardedRef]);
}
export type GalleryTransitionProps=Omit<React.ComponentProps<"div">,"children"> & {first:React.ReactNode;second:React.ReactNode;active:boolean;duration?:number;paused?:boolean};
