"use client";
import * as React from "react";
import { motion } from "motion/react";
import { Icon, createIconMotionPainter, getIconDirection, type IconProps } from "@/components/ui/icon";
import { createMotionLane, useChoreography } from "@/lib/cojeev-motion/choreography";
import { useMotionVisibility } from "@/lib/cojeev-motion/use-motion-visibility";

export type IconMotion = "auto" | "tremor" | "draw" | "spin" | "bounce" | "validation" | "pulse" | "none";
export type IconMotionEase="gentle"|"settle"|"linear"|readonly [number,number,number,number];
export type AnimatedIconProps = Omit<IconProps,"draw"|"feedback"> & { preset?:IconMotion; active?:boolean; amplitude?:number; duration?:number; ease?:IconMotionEase };
export function AnimatedIcon({name,preset="auto",active,amplitude=1,duration,ease="gentle",className,size="default",children,...props}:AnimatedIconProps) {
  const host=React.useRef<HTMLSpanElement>(null);
  const {quiet}=useChoreography();
  const {enabled,inView}=useMotionVisibility(host);
  const [hovered,setHovered]=React.useState(false);
  const [focused,setFocused]=React.useState(false);
  const [allowed,setAllowed]=React.useState(false);
  const [replay,setReplay]=React.useState(0);
  const [replayActive,setReplayActive]=React.useState(false);
  const seconds=duration!==undefined&&Number.isFinite(duration)?Math.max(.08,Math.min(duration,10)):undefined;
  React.useEffect(()=>{
    if(!replayActive)return;
    const timer=setTimeout(()=>setReplayActive(false),(seconds??.55)*1000);
    return()=>clearTimeout(timer);
  },[replay,replayActive,seconds]);
  React.useEffect(()=>{
    const node=host.current?.closest<HTMLElement>('button,a[href],summary,[role=button],[role=menuitem],[role=menuitemcheckbox],[role=menuitemradio],[role=option],[role=tab],[role=checkbox],[role=radio],[role=switch],label')??host.current;
    if(!node)return;
    const associated=node instanceof HTMLLabelElement?node.control:node;
    const eligible=()=>!node.closest('[inert],[hidden],[data-motion="off"],[data-flow="off"]')&&!node.matches(':disabled,[disabled],[aria-disabled="true"],[data-disabled]:not([data-disabled="false"])')&&!associated?.matches(':disabled,[disabled],[aria-disabled="true"],[data-disabled]:not([data-disabled="false"])');
    const sync=()=>{const next=eligible();setAllowed(next);if(!next){setHovered(false);setFocused(false);setReplayActive(false)}};
    sync();
    const startReplay=()=>{
      if(!eligible())return;
      setReplay(value=>value+1);setReplayActive(true);
    };
    const enter=(event:PointerEvent)=>{if(event.pointerType!=="touch")setHovered(eligible())},leave=()=>setHovered(false);
    const focus=()=>setFocused(eligible()),blur=(event:FocusEvent)=>{if(!node.contains(event.relatedTarget as Node|null))setFocused(false)};
    let pendingClick:"pointer"|"keyboard"|null=null;
    let releaseTimer:ReturnType<typeof setTimeout>|null=null;
    const queueRelease=()=>{if(releaseTimer!==null)clearTimeout(releaseTimer);releaseTimer=setTimeout(()=>{pendingClick=null;releaseTimer=null},0)};
    const pointerdown=(event:PointerEvent)=>{if(event.button!==0)return;if(releaseTimer!==null)clearTimeout(releaseTimer);pendingClick="pointer";startReplay()};
    const pointerup=()=>{if(pendingClick==="pointer")queueRelease()};
    const pointercancel=()=>{if(pendingClick==="pointer")pendingClick=null};
    const keydown=(event:KeyboardEvent)=>{if(event.repeat||!(event.key==="Enter"||event.key===" "))return;if(releaseTimer!==null)clearTimeout(releaseTimer);pendingClick="keyboard";startReplay()};
    const keyup=(event:KeyboardEvent)=>{if((event.key==="Enter"||event.key===" ")&&pendingClick==="keyboard")queueRelease()};
    const click=()=>{if(pendingClick){pendingClick=null;if(releaseTimer!==null){clearTimeout(releaseTimer);releaseTimer=null}return}startReplay()};
    node.addEventListener("pointerenter",enter);node.addEventListener("pointerleave",leave);node.addEventListener("focusin",focus);node.addEventListener("focusout",blur);node.addEventListener("pointerdown",pointerdown);node.addEventListener("keydown",keydown);node.addEventListener("keyup",keyup);node.addEventListener("click",click);
    window.addEventListener("pointerup",pointerup);window.addEventListener("pointercancel",pointercancel);window.addEventListener("blur",queueRelease);
    const attributes=new MutationObserver(sync);
    const observe=(element:Element)=>attributes.observe(element,{attributes:true,attributeFilter:["disabled","aria-disabled","data-disabled","inert","hidden","data-motion","data-flow"]});
    for(let ancestor:Element|null=node;ancestor;ancestor=ancestor.parentElement)observe(ancestor);
    if(associated&&associated!==node)observe(associated);
    return ()=>{node.removeEventListener("pointerenter",enter);node.removeEventListener("pointerleave",leave);node.removeEventListener("focusin",focus);node.removeEventListener("focusout",blur);node.removeEventListener("pointerdown",pointerdown);node.removeEventListener("keydown",keydown);node.removeEventListener("keyup",keyup);node.removeEventListener("click",click);window.removeEventListener("pointerup",pointerup);window.removeEventListener("pointercancel",pointercancel);window.removeEventListener("blur",queueRelease);if(releaseTimer!==null)clearTimeout(releaseTimer);attributes.disconnect()};
  },[]);
  const amount=Number.isFinite(amplitude)?Math.max(0,Math.min(amplitude,3)):1;
  const permitted=allowed&&!quiet&&enabled&&inView&&amount>0;
  const running=permitted&&(active??(hovered||focused||replayActive));
  const intent=preset!=="auto"?preset:"semantic";
  React.useEffect(()=>{
    if(intent==="none"||!running)return;
    const svg=host.current?.querySelector<SVGSVGElement>("[data-slot=icon]");if(!svg)return;
    const direction=getIconDirection(name);
    const [dx,dy]=direction[0]||direction[1]?direction:[1,0];
    const painter=createIconMotionPainter(svg,name,amount,intent==="semantic"?undefined:p=>{
      const pulse=Math.sin(Math.PI*p)*amount;
      if(intent==="draw")return {glyph:{draw:p}};
      if(intent==="spin")return {glyph:{transform:`rotate(${360*p} 12 12)`}};
      if(intent==="tremor")return {glyph:{transform:`rotate(${Math.sin(p*Math.PI*5)*(1-p)*9*amount} 12 12)`}};
      if(intent==="bounce")return {glyph:{transform:`translate(${3*pulse*dx} ${3*pulse*dy})`}};
      const scale=intent==="validation"?1-.15*Math.sin(p*Math.PI*2)*(1-p)*amount:1+.12*pulse;
      return {glyph:{transform:`translate(12 12) scale(${scale}) translate(-12 -12)`}};
    });
    const lane=createMotionLane(0,painter.paint);
    const looping=intent==="spin"||(intent==="semantic"&&(name==="loader"||name==="loader-circle"));
    lane.jump(0);
    const timing=looping?"linear":ease==="gentle"?([.22,.72,.22,1] as const):ease==="settle"?([.2,.8,.2,1] as const):ease;
    lane.to(1,{duration:seconds??(looping?1.35:.55),ease:timing,repeat:looping?Infinity:0},painter.restore);
    return ()=>{lane.dispose();painter.restore()};
  },[intent,running,name,amount,seconds,ease,active,replay]);
  return <span ref={host} data-slot="animated-icon" data-preset={intent} data-icon-replay={replay||undefined} data-animated={(running&&intent!=="none")||undefined} className="v-animated-icon">
    <span><Icon name={name} size={size} className={className} {...props} feedbackDuration={duration} feedbackEase={ease} feedback={false}>{children}</Icon></span>
  </span>;
}


export type StateChevronProps = Omit<React.ComponentProps<"span">,"children"> & {
  /** Omit to follow the closest native trigger's data-state/aria-expanded. */
  open?: boolean;
  direction?: "down" | "right";
};
/** One real state cue; quiet motion still updates the direction immediately. */
export function StateChevron({ open, direction="down", className, ref, ...props }: StateChevronProps) {
  const host = React.useRef<HTMLSpanElement | null>(null);
  const [nativeOpen, setNativeOpen] = React.useState(false);
  const { quiet, transition } = useChoreography();
  const attach = React.useCallback((node: HTMLSpanElement | null) => {
    host.current = node;
    if (typeof ref === "function") return ref(node);
    if (ref) ref.current = node;
  }, [ref]);
  React.useEffect(() => {
    if (open !== undefined) return;
    const trigger = host.current?.parentElement?.closest("[aria-expanded],[data-state]");
    if (!trigger) return;
    const sync = () => setNativeOpen(trigger.getAttribute("aria-expanded") === "true" || trigger.getAttribute("data-state") === "open");
    sync();
    const observer = new MutationObserver(sync);
    observer.observe(trigger, { attributes:true, attributeFilter:["data-state","aria-expanded"] });
    return () => observer.disconnect();
  }, [open]);
  const expanded = open ?? nativeOpen;
  return <span {...props} ref={attach} data-slot="state-chevron" data-open={expanded} aria-hidden="true" className={["v-state-chevron",className].filter(Boolean).join(" ")}>
    <motion.span initial={false} animate={{rotate:expanded?(direction==="right"?90:180):0}} transition={quiet?{duration:0}:transition}><Icon name={direction==="right"?"chevron-right":"chevron-down"} size="sm" feedback={false} /></motion.span>
  </span>;
}
