import { animate, type Transition } from "motion";
import { createMotionLane, motionTokens, trackMotion } from "@/lib/cojeev-motion/choreography";
import { getSettingsSnapshot, getFlowSettings, type FlowVariant } from "@/lib/cojeev-motion/settings";

/** Distinct travel response, including locally pinned presets. Positive dimensions
 * use the same timing but are bounded at paint so a reversal cannot invert a layer. */
export function flowTravelTransition(variant:FlowVariant,speed=1):Transition {
  const tuning:Record<FlowVariant,[number,number,number]>={glide:[420,38,.8],stretch:[330,30,.9],jelly:[235,20,1],comet:[370,31,.8],drop:[310,27,.9],rubber:[270,23,1],pebble:[230,27,1.1],ripple:[350,33,.9],halo:[285,31,1],off:[420,38,.8]};
  const [stiffness,damping,mass]=tuning[variant];
  return {type:"spring",stiffness:stiffness*speed*speed,damping:damping*speed,mass};
}

type Box = { x: number; y: number; w: number; h: number; r: string };

/** CSS retains paint; Motion is the sole owner of all travelling layer geometry. */
export function createFlowPainter(write: (name: string, value: string) => void) {
  const families = ["glide", "hov", "trail"] as const;
  const make = (prefix: string) => Object.fromEntries(["x", "y", "w", "h", "o"].map(key => [key,
    createMotionLane(0, value => write(`--${prefix}-${key}`, `${Number((key === "w" || key === "h" ? Math.max(0,value) : value).toFixed(3))}${key === "o" ? "" : "px"}`)),
  ])) as Record<"x" | "y" | "w" | "h" | "o", ReturnType<typeof createMotionLane>>;
  const lanes = { glide: make("glide"), hov: make("hov"), trail: make("trail") };
  const initialized = new Set<string>();
  function paint(box: Box, hover = false, immediate = false, variant=getFlowSettings().variant) {
    const snapshot = getSettingsSnapshot();
    const transition = flowTravelTransition(variant,snapshot.flow.speed);
    for (const prefix of hover ? ["hov"] as const : ["glide", "trail"] as const) {
      const instant = immediate || !initialized.has(prefix);
      const timing = prefix === "trail" ? { ...transition, delay: .045 / snapshot.flow.speed } : transition;
      for (const key of ["x", "y", "w", "h"] as const) {
        if (instant) lanes[prefix][key].jump(box[key]);
        else lanes[prefix][key].to(box[key], timing);
      }
      write(`--${prefix}-r`, box.r);
      if (instant) lanes[prefix].o.jump(1);
      else lanes[prefix].o.to(1, { duration: .12 / snapshot.flow.speed });
      initialized.add(prefix);
    }
  }
  return {
    paint,
    hide(hover: boolean, immediate = false) {
      for (const prefix of hover ? ["hov"] as const : ["glide", "trail"] as const) {
        // Transparent positioned layers still contribute native scroll overflow.
        // Release their footprint when the fade finishes; painting again cancels
        // this completion through the opacity lane's existing generation guard.
        const clearGeometry = () => {
          for (const key of ["x", "y", "w", "h"] as const) lanes[prefix][key].jump(0);
          initialized.delete(prefix);
        };
        if (immediate) { lanes[prefix].o.jump(0); clearGeometry(); }
        else lanes[prefix].o.to(0, { duration: motionTokens.duration.quick }, clearGeometry);
      }
    },
    // Selection travel and pointer preview have independent lifetimes. Stopping
    // a selection phase must not cancel the ghost's fade/geometry cleanup.
    stop(hover?: boolean) {
      const targets = hover === undefined ? families : hover ? ["hov"] as const : ["glide", "trail"] as const;
      for (const prefix of targets) Object.values(lanes[prefix]).forEach(lane => lane.stop());
    },
    dispose: () => { for (const prefix of families) Object.values(lanes[prefix]).forEach(lane => lane.dispose()); },
  };
}

/** A bounded release response. Each new gesture cancels the previous owner. */
export function animateFlowLanding(element: HTMLElement, variant: string, direction = "x") {
  const original=new Map(["transform","transform-origin"].map(name=>[name,{value:element.style.getPropertyValue(name),priority:element.style.getPropertyPriority(name)}]));
  const restore=()=>{for(const[name,old]of original){if(old.value)element.style.setProperty(name,old.value,old.priority);else element.style.removeProperty(name)}};
  const { intensity, speed } = getFlowSettings();
  const stretch = (variant === "jelly" ? .13 : variant === "drop" ? -.08 : .045) * Math.min(intensity, 2);
  const x = direction === "x" ? 1 + stretch : 1 - stretch * .55;
  const y = direction === "x" ? 1 - stretch * .55 : 1 + stretch;
  // Own the scalar and inline paint directly. Motion's DOM visual-element cache
  // can enqueue a transform render after stop(), overwriting a restored value.
  let disposed=false;
  const lane=createMotionLane(0,progress=>{
    if(disposed)return;
    const remaining=1-progress,base=original.get('transform')!;
    const prefix=base.value&&base.value!=='none'?`${base.value} `:'';
    element.style.setProperty('transform',`${prefix}scaleX(${1+(x-1)*remaining}) scaleY(${1+(y-1)*remaining})${variant==='pebble'?` skewX(${-2*intensity*remaining}deg)`:''}`,base.priority);
  });
  const transition = {
    ...motionTokens.spring.expressive,
    stiffness: motionTokens.spring.expressive.stiffness * speed * speed,
    damping: motionTokens.spring.expressive.damping * speed,
  };
  let removeAura = () => {};
  if (variant === "ripple" || variant === "halo") {
    const aura = document.createElement("span");
    Object.assign(aura.style, {
      position: "absolute", inset: "0", borderRadius: "inherit", pointerEvents: "none",
      boxShadow: variant === "ripple" ? "inset 0 0 0 1px var(--v-pink)" : "0 0 12px 4px var(--v-pink)",
    });
    element.append(aura);
    const bloom = animate(aura, { scale: [1, 1 + .3 * intensity], opacity: [.45, 0] }, {
      duration: .5 / speed, ease: [...motionTokens.ease.settle],
    });
    const stopAura = trackMotion(bloom);
    removeAura = () => { stopAura(); aura.remove(); };
    void bloom.finished.then(() => aura.remove(), () => aura.remove());
  }
  const cleanup=()=>{if(disposed)return;disposed=true;lane.dispose();removeAura();restore()};
  lane.jump(0);lane.to(1,transition,cleanup);
  return cleanup;
}
