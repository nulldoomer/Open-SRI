"use client"

import * as React from "react"
import { rim, path as serializePath } from "@/lib/cojeev-motion/geometry"
import { fromShape, SHAPES } from "@/lib/cojeev-motion/shapes"
import { bodyPadding, createBody, rewindBody, stepBody, type Body } from "@/lib/cojeev-motion/body"
import { getMotionSettings, getMorphProfile, subscribeMotion, type Category } from "@/lib/cojeev-motion/settings"

import { resolveMorphHost, resolveMorphTier, registerMorphHost } from "@/lib/cojeev-motion/category"
import { createMorphOverlays, morphColor } from "@/lib/cojeev-motion/morph-paint"
import { motionClock, registerMotionClock } from "@/lib/cojeev-motion/clock"
export { motionClock } from "@/lib/cojeev-motion/clock"
export { enableMorph, disableMorph } from "@/lib/cojeev-motion/category"

const instances=new Set<{measure:(t:number)=>void;invalidate:()=>void;frame:(t:number,dt:number)=>boolean;rewind:()=>void;reseed:()=>void;refresh:()=>void}>()
let clock:number|null=null,previous:number|null=null,raf=0,seed:number|null=null
const pointer={x:-1e4,y:-1e4}
function stop(){if(raf)cancelAnimationFrame(raf);raf=0}
function frame(t:number){const dt=previous===null?0:Math.min(.05,Math.max(0,(t-previous)/1000));previous=t;let active=false;instances.forEach(b=>b.measure(t));instances.forEach(b=>{active=b.frame(t,dt)||active});return active}
function wake(){if(clock!==null||raf||!instances.size||document.hidden)return;raf=requestAnimationFrame(t=>{raf=0;if(frame(t))wake()})}
/** Deterministic milliseconds on the document time origin; null resumes rAF. */
registerMotionClock(t=>{stop();if(t===null){clock=null;previous=performance.now();wake()}else{if(clock===null)previous=t;clock=t;frame(t)}})
export const morphClock=motionClock
export function rewindMorph(){pointer.x=pointer.y=-1e4;instances.forEach(b=>b.rewind());previous=clock??performance.now();wake()}
export function seedMorph(n:number|null){seed=n;instances.forEach(b=>b.reseed());rewindMorph()}
function bodySeed(el:HTMLElement){const raw=el.ownerDocument.documentElement.dataset.seed;const n=seed??(raw!=null&&raw!==""&&Number.isFinite(+raw)?+raw:null);if(n===null)return Math.random()*100;
 let key="",node:Element|null=el;while(node&&node!==el.ownerDocument.documentElement){let i=0,s=node.previousElementSibling;while(s){if(!s.matches("svg.v-morph,#v-morph-defs,.v-glide__pill,.v-glide__hover,.v-glide__trail"))i++;s=s.previousElementSibling}key=node.tagName+i+"/"+key;node=node.parentElement}
 let h=2166136261;for(const c of key+"#"+n){h^=c.charCodeAt(0);h=Math.imul(h,16777619)>>>0}return h%100000/1000
}
const ns="http://www.w3.org/2000/svg"
function svgNode(tag:string,attrs:Record<string,string>={}){const el=document.createElementNS(ns,tag);for(const [k,v]of Object.entries(attrs))el.setAttribute(k,v);return el}
const clear=(v:string)=>!v||v==="transparent"||v==="rgba(0, 0, 0, 0)"
function surfaceFill(el:HTMLElement){let bg="";for(let p=el.parentElement;p;p=p.parentElement){bg=getComputedStyle(p).backgroundColor;if(!clear(bg))break}const m=/rgba?\(\s*(\d+)[,\s]+(\d+)[,\s]+(\d+)/.exec(bg);const rgb=m?m.slice(1).map(n=>{const v=+n/255;return v<=.03928?v/12.92:((v+.055)/1.055)**2.4}):[1,1,1];return .2126*rgb[0]+.7152*rgb[1]+.0722*rgb[2]<.18?"rgba(251,244,230,.14)":"var(--v-beige)"}
// One browser subscription set, acquired by mounted hooks and released with the last host.
let environmentUsers=0,disposeEnvironment=()=>{}
function acquireEnvironment(){
 if(environmentUsers++===0){
  const ac=new AbortController(),opts={signal:ac.signal}
  const refresh=()=>instances.forEach(b=>b.refresh())
  const theme=new MutationObserver(refresh);theme.observe(document.documentElement,{attributes:true,attributeFilter:['data-mode','data-skin']})
  window.addEventListener('v-theme',refresh,opts);window.addEventListener('v-palette',refresh,opts)
  document.addEventListener('pointermove',e=>{pointer.x=e.clientX;pointer.y=e.clientY;wake()},{...opts,passive:true})
  document.addEventListener('pointerleave',()=>{pointer.x=pointer.y=-1e4;wake()},opts)
  const layout=()=>{instances.forEach(b=>b.invalidate());wake()}
  window.addEventListener('resize',layout,opts);window.addEventListener('scroll',layout,{...opts,capture:true,passive:true})
  document.addEventListener('visibilitychange',()=>{if(document.hidden)stop();else{previous=performance.now();wake()}},opts)
  disposeEnvironment=()=>{ac.abort();theme.disconnect();stop();previous=null}
 }
 return ()=>{if(--environmentUsers===0)disposeEnvironment()}
}
/** Component-owned attachment. No document scanning, global API, or provider required. */
export function useMorph<T extends HTMLElement>(category:Category,externalRef?:React.Ref<T>){
 const [host,setHost]=React.useState<T|null>(null)
 const syncHost=React.useRef<()=>void>(()=>{})
 const ref=React.useCallback((node:T|null)=>{setHost(node);if(typeof externalRef==="function"){const cleanup=externalRef(node);return ()=>{setHost(null);if(typeof cleanup==="function")cleanup();else externalRef(null)}}if(externalRef)externalRef.current=node;return ()=>{setHost(null);if(externalRef)externalRef.current=null}},[externalRef])
 React.useLayoutEffect(()=>{
  if(!host)return
  const el=host,ac=new AbortController(),opts={signal:ac.signal}
  const mq=matchMedia("(prefers-reduced-motion: reduce)")
  let destroyBody:()=>void=()=>{},retuneProfile:()=>void=()=>attach()
  let retainedBody:Body|undefined,repairBody=()=>{},signature='',autoMode:string|undefined,lastPaint:{fill:string;stroke:string}|null=null
  // Internal aria/class retunes rebuild decoration, but do not re-tag the host.
  // The source keeps its finite CSS radius captured at autoTag (morph.js:298)
  // until automatic decoration is removed, even if responsive dimensions shrink.
  let automaticRadius:number|undefined
  let cornerSignature=''
  const visualSignature=()=>JSON.stringify([
   getComputedStyle(el).borderTopLeftRadius,
   el.className.split(/\s+/).filter(c=>!['v-morph-host','v-morph-live','v-morph-rel'].includes(c)).join(' '),
   ...['motion','tier','reach','inside','amp','lobes','depth','asym','spread','r','shape','sw','dash','colors'].map(k=>el.dataset[k]),
   el.dataset.morph===autoMode?'':el.dataset.morph,el.style.getPropertyValue('--mfill'),el.style.getPropertyValue('--mstroke'),
   ...['aria-selected','aria-current','aria-pressed','aria-checked','aria-expanded','disabled','aria-disabled','aria-busy','data-state','data-highlighted'].map(name=>el.getAttribute(name)),
  ])
  function attach(){
   const nextCorner=getComputedStyle(el).borderTopLeftRadius
   if(nextCorner!==cornerSignature){automaticRadius=undefined;cornerSignature=nextCorner}
   const overrides=new Map<string,string>()
   if(lastPaint)for(const key of ['fill','stroke'] as const){const value=el.style.getPropertyValue('--m'+key);if(value!==lastPaint[key])overrides.set('--m'+key,value)}
   destroyBody();destroyBody=()=>{};lastPaint=null
   for(const [key,value]of overrides){if(value)el.style.setProperty(key,value);else el.style.removeProperty(key)}
   repairBody=()=>{};retuneProfile=()=>attach();autoMode=undefined
   const settings=getMotionSettings(),profile=getMorphProfile(),resolved=resolveMorphHost(el,category,settings)
   if(!resolved){automaticRadius=undefined;return}
   const {mode,tierName,explicit}=resolved,cs=getComputedStyle(el)
   const resolvedTier=resolveMorphTier(el,tierName,profile);if(!resolvedTier){automaticRadius=undefined;return};const tier={...resolvedTier}
   // A selection group owns one travelling body. Its controls and container
   // remain still, even when an authored profile enables breathing or press.
   const selectionGroup=el.closest<HTMLElement>('[data-flow-owned].v-glide')
   const groupBody=!!selectionGroup&&selectionGroup.dataset.flowKind!=='fill'&&selectionGroup.matches('.v-tabs,.v-seg,.v-pager,.v-iradios,[role="tablist"],[data-slot="toggle-group"]')
   const cfg={...profile.cfg};if(settings.mode==="off"||mq.matches||groupBody)for(const k of ["rest","reach","merge","hold","jiggleOn","press"] as const)cfg[k]=false
   const b=retainedBody??createBody(tier,tierName,bodySeed(el));retainedBody=b
   b.tier=tier;b.tierName=tierName;b.w=b.h=0;b.focus=el===el.ownerDocument.activeElement||el.contains(el.ownerDocument.activeElement)
   if(mq.matches||settings.mode==="off"||groupBody)rewindBody(b)
   const old={fill:el.style.getPropertyValue('--mfill'),stroke:el.style.getPropertyValue('--mstroke'),pad:el.style.getPropertyValue('--mpad'),transform:el.style.transform}
   const svg=svgNode('svg',{class:'v-morph','aria-hidden':'true','shape-rendering':'geometricPrecision'})
   // Hidden hosts cannot be measured yet. Keep their new layer at zero size
   // until measure() replaces it, avoiding SVG's 300px intrinsic first frame.
   svg.style.cssText="position:absolute;width:0;height:0;pointer-events:none"
   const path=svgNode('path',{'data-morph-body':'',fill:mode!=='stroke'?'var(--mfill,var(--v-beige))':'none'})
   if(mode!=='fill'){path.setAttribute('stroke','var(--mstroke,transparent)');path.setAttribute('stroke-width',el.dataset.sw||(el.matches('.v-badge.-test')?'1.5':'1'));path.setAttribute('stroke-linejoin','round');const dash=el.dataset.dash||(el.matches('.v-badge.-dashed')?'3 3':'');if(dash)path.setAttribute('stroke-dasharray',dash)}
   const echo=svgNode('path',{'data-morph-echo':'',fill:'none',stroke:'var(--mstroke,var(--mfill,transparent))','stroke-width':'1.5','vector-effect':'non-scaling-stroke'}),dots=svgNode('g',{fill:'var(--mfill,transparent)'})
   echo.style.display=dots.style.display='none';svg.append(path,echo,dots)
   const overlays=el.matches('.v-alive,.combo')?createMorphOverlays(svg):null
   const colors=el.dataset.colors?.split(',').map(color=>color.trim()).filter(color=>/^#[0-9a-f]{6}$/i.test(color))??[]
   const staticBody=mq.matches||settings.mode==='off'||groupBody
   // Quiet bodies keep their authored CSS fill, including live theme/palette changes.
   // Only moving bodies opt out of that paint rule for animated color attributes.
   if(colors.length&&!staticBody)path.setAttribute('data-morph-colors','')

   if(!explicit){el.dataset.morph=mode;el.dataset.autoMorph=category;autoMode=mode}
   el.classList.add('v-morph-host','v-morph-live');const relative=cs.position==='static';if(relative)el.classList.add('v-morph-rel')
   const focused=el.ownerDocument.activeElement;el.prepend(svg);if(focused instanceof HTMLElement&&el.contains(focused)&&el.ownerDocument.activeElement!==focused)focused.focus({preventScroll:true})
   const repaint=()=>{
    // Explicit bodies take their paint from the author's live CSS variables.
    // Only the automatic category adapter captures the host background. Copying
    // inherited paint into an explicit body breaks its parent/theme inheritance.
    if(!explicit&&mode!=='fill')el.style.setProperty('--mstroke',old.stroke||(el.matches('.v-badge.-test')?'var(--v-ink)':el.matches('.v-badge.-dashed')?'var(--v-text-2)':'var(--v-border)'))
    if(!explicit&&mode!=='stroke'){
     // Sample the authored CSS paint without detaching the SVG or changing the
     // host's live class. Detachment during pointer crossings invalidates browser
     // hit testing and stacking; the decorative node must remain mounted.
     // Paint-only :has(>svg.v-morph) rules are suspended by its class, not its DOM.
     // Capture the authored endpoint, not an in-flight background transition.
     // Otherwise busy/selected state changes can freeze the previous paint into
     // --mfill after the host's CSS transition has already finished.
     const transition=el.style.getPropertyValue('transition-property'),transitionPriority=el.style.getPropertyPriority('transition-property')
     el.style.setProperty('transition-property','none','important')
     svg.classList.remove('v-morph')
     el.style.removeProperty('--mfill')
     const paint=getComputedStyle(el),bg=paint.backgroundColor,cssFill=paint.getPropertyValue('--mfill').trim()
     // A transparent CSS fill is meaningful: selected controls reveal the
     // travelling flow layer underneath their own morph body.
     const fill=!clear(bg)?bg:old.fill||cssFill||surfaceFill(el)
     svg.classList.add('v-morph')
     el.style.setProperty('--mfill',fill)
     if(transition)el.style.setProperty('transition-property',transition,transitionPriority)
     else el.style.removeProperty('transition-property')
    }
    lastPaint={fill:el.style.getPropertyValue('--mfill'),stroke:el.style.getPropertyValue('--mstroke')};signature=visualSignature()
   }
   let rectValid=false,rAt=0,resize=true
   let lastD='',dirty=true
   repairBody=()=>{
    if(svg.parentNode!==el){el.prepend(svg);dirty=true}
    el.classList.add('v-morph-host','v-morph-live');if(relative)el.classList.add('v-morph-rel')
    if(!explicit&&!el.dataset.morph)el.dataset.morph=mode
   }
   function measure(){const R=el.getBoundingClientRect();b.R=R;const w=Math.round(R.width),h=Math.round(R.height);if(!w||!h)return;if(w===b.w&&h===b.h)return;b.w=w;b.h=h;const authored=el.dataset.r;const corner=getComputedStyle(el).borderTopLeftRadius,original=parseFloat(corner);
    // Explicit bodies retain their original capsule default. Opt-in CSS corners
    // let a component's radius control change the painted contour as well.
    const cssRadius=Number.isFinite(original)?Math.min(Math.min(w,h)/2,corner.endsWith('%')?Math.min(w,h)*original/100:original):Math.min(w,h)/2;
    const radius=authored==='css'?cssRadius:authored?+authored:explicit?Math.min(w,h)/2:Number.isFinite(original)&&original<200?(automaticRadius??=Math.min(original,Math.min(el.offsetWidth,el.offsetHeight)/2)):Math.min(w,h)/2;b.base=el.dataset.shape&&SHAPES[el.dataset.shape]?fromShape(el.dataset.shape,w,h,Math.max(1,cfg.quality)*.7):rim(w,h,radius,Math.max(1,cfg.quality));const pad=bodyPadding(tier,cfg,w,h);svg.setAttribute('viewBox',`${-pad} ${-pad} ${w+2*pad} ${h+2*pad}`);svg.style.cssText=`position:absolute;left:${-pad}px;top:${-pad}px;width:${w+2*pad}px;height:${h+2*pad}px;pointer-events:none;overflow:visible;z-index:-1`;el.style.setProperty('--mpad',pad+'px');dirty=true}
   // Match the source's read pass before any body writes its press transform.
   // A release keeps the last pressed rect until it is stale or interaction
   // demands another read; reading every frame changes the pressure axis.
   function premeasure(t:number){
    if(!el.isConnected){automaticRadius=undefined;rectValid=false;return}
    const R=b.R,margin=tier.R+40
    const near=rectValid&&pointer.x>-1e3&&pointer.x>R.left-margin&&pointer.x<R.right+margin&&pointer.y>R.top-margin&&pointer.y<R.bottom+margin
    if(!rectValid||t-rAt>250||near||b.press.to||b.lobe.x>.02||b.jiggle>0){b.R=el.getBoundingClientRect();rAt=t;rectValid=true}
    resize=Math.round(b.R.width)!==b.w||Math.round(b.R.height)!==b.h
   }
   const invalidate=()=>{rectValid=false;dirty=true}
   const instance={measure:premeasure,invalidate,frame:(t:number,dt:number)=>{if(!el.isConnected)return false;if(resize){resize=false;b.lob=null;dirty=true;lastD='';measure()}if(!b.w||!b.h||!b.R.width||!b.R.height)return false;const out=stepBody(b,pointer,dt,(staticBody?0:t/1000),cfg,staticBody,!dirty&&!colors.length&&tierName!=='spinner');if(out.d!==lastD){path.setAttribute('d',out.d);lastD=out.d}if(cfg.echo){const m=cfg.echoScale,ox=cfg.echoOff*Math.cos(b.seed),oy=cfg.echoOff*Math.sin(b.seed);echo.setAttribute('d',serializePath(out.points.map(([x,y])=>[b.w/2+(x-b.w/2)*m+ox,b.h/2+(y-b.h/2)*m+oy]),!!b.base.poly));echo.style.display=''}else echo.style.display='none';if(cfg.dots){while(dots.childElementCount>cfg.dots)dots.lastElementChild?.remove();while(dots.childElementCount<cfg.dots)dots.append(svgNode('circle',{r:(2+((dots.childElementCount*7+b.seed)%3)).toFixed(1)}));for(let i=0;i<cfg.dots;i++){const j=Math.floor((i/cfg.dots)*b.base.length+b.seed*3)%b.base.length,q=b.base[j],dot=dots.children[i];dot.setAttribute('cx',(out.points[j][0]+q[2]*(8+i*3)).toFixed(2));dot.setAttribute('cy',(out.points[j][1]+q[3]*(8+i*3)).toFixed(2))}dots.style.display=''}else dots.style.display='none';overlays?.paint(out.points,b,cfg);if(colors.length&&!staticBody)path.setAttribute('fill',morphColor(colors,t/1000));if(tierName==='spinner')svg.style.transform=staticBody?'':`rotate(${((t/1000)*40)%360}deg)`;if(el.hasAttribute('data-stable-hit')){el.style.transform=old.transform;if(tierName!=='spinner')svg.style.transform=out.press>.004&&!staticBody?`scale(${1-out.press*.03},${1-out.press*.015})`:''}else if(out.press>.004&&!staticBody)el.style.transform=`scale(${1-out.press*.03},${1-out.press*.015})`;else el.style.transform=old.transform;const work=out.active||dirty||(!staticBody&&(colors.length>0||tierName==='spinner'||!!(tier.depth&&tier.lobes&&cfg.drift&&(b.near||b.inside))));dirty=false;return work},rewind:()=>{rewindBody(b);rectValid=false;rAt=0;dirty=true;lastD=''},reseed:()=>{b.seed=bodySeed(el)},refresh:()=>{repaint();dirty=true;wake()}}
   const bodyAC=new AbortController(),bo={signal:bodyAC.signal}
   const press=()=>{if(!cfg.press||el.matches(':disabled,[aria-disabled="true"]'))return;b.press.to=1;b.press.k=260;wake()}
   const release=()=>{if(b.press.to){b.press.to=0;b.ripple=1;wake()}}
   el.addEventListener('focusin',()=>{b.focus=true;repaint();wake()},bo);el.addEventListener('focusout',()=>{b.focus=false;release();repaint();wake()},bo)
   el.addEventListener('pointerenter',()=>{el.setAttribute('data-hover','');repaint();wake()},bo);el.addEventListener('pointerleave',()=>{el.removeAttribute('data-hover');repaint();wake()},bo)
   el.addEventListener('pointerdown',press,bo);el.addEventListener('pointercancel',release,bo);document.addEventListener('pointerup',release,bo)
   el.addEventListener('keydown',e=>{if(e.key===' '||e.key==='Enter')press()},bo);el.addEventListener('keyup',release,bo)
   const ro=new ResizeObserver(entries=>{for(const entry of entries){const R=entry.contentRect;if(Math.abs(Math.round(R.width)-b.w)<1&&Math.abs(Math.round(R.height)-b.h)<1)continue;invalidate();wake()}});ro.observe(el)
   retuneProfile=()=>{
    const nextSettings=getMotionSettings(),nextProfile=getMorphProfile()
    if(nextSettings.mode!==settings.mode||nextSettings.cats[category]!==settings.cats[category]){attach();signature=visualSignature();return}
    const nextTier=resolveMorphTier(el,tierName,nextProfile);if(!nextTier)return
    const nextCfg={...nextProfile.cfg};if(staticBody)for(const key of ['rest','reach','merge','hold','jiggleOn','press'] as const)nextCfg[key]=false
    if(JSON.stringify(cfg)===JSON.stringify(nextCfg)&&JSON.stringify(tier)===JSON.stringify(nextTier))return
    const resize=cfg.quality!==nextCfg.quality||cfg.echo!==nextCfg.echo||cfg.echoOff!==nextCfg.echoOff||cfg.echoScale!==nextCfg.echoScale||cfg.dots!==nextCfg.dots||tier.reach!==nextTier.reach||tier.press!==nextTier.press||tier.depth!==nextTier.depth
    Object.assign(cfg,nextCfg);Object.assign(tier,nextTier)
    if(resize)b.w=b.h=0
    dirty=true;const time=clock??performance.now();instance.measure(time);instance.frame(time,0);wake()
   }
   repaint();instances.add(instance);const time=clock??performance.now();instance.measure(time);instance.frame(time,0);wake()
   destroyBody=()=>{instances.delete(instance);bodyAC.abort();ro.disconnect();overlays?.dispose();svg.remove();el.classList.remove('v-morph-host','v-morph-live');if(relative)el.classList.remove('v-morph-rel');el.removeAttribute('data-hover');if(!explicit&&el.dataset.morph===mode)delete el.dataset.morph;if(!explicit)delete el.dataset.autoMorph;for(const [key,value]of [['--mfill',old.fill],['--mstroke',old.stroke],['--mpad',old.pad]]){if(value)el.style.setProperty(key,value);else el.style.removeProperty(key)}el.style.transform=old.transform;if(!instances.size)stop()}
  }
  attach()
  signature=visualSignature()
  const sync=()=>{if(visualSignature()!==signature){attach();signature=visualSignature()}else repairBody()}
  syncHost.current=sync
  const children=new MutationObserver(()=>{repairBody()});children.observe(el,{childList:true})
  const unsubscribe=subscribeMotion(()=>{retuneProfile();signature=visualSignature()})
  const unregister=registerMorphHost(el,{refresh:()=>{attach();signature=visualSignature()},disable:()=>{destroyBody();automaticRadius=undefined;destroyBody=()=>{};repairBody=()=>{}}})
  const attributes=new MutationObserver(()=>{if(visualSignature()!==signature){attach();signature=visualSignature()}})
  attributes.observe(el,{attributes:true,attributeFilter:['class','style','data-morph','data-tier','data-motion','data-reach','data-inside','data-amp','data-lobes','data-depth','data-asym','data-spread','data-r','data-shape','data-sw','data-dash','data-colors','aria-selected','aria-current','aria-pressed','aria-checked','aria-expanded','disabled','aria-disabled','aria-busy','data-state','data-highlighted']})
  const ancestors=new MutationObserver(records=>{if(records.some(record=>record.attributeName==='data-motion'||record.attributeName==='hidden')||visualSignature()!==signature){attach();signature=visualSignature()}})
  for(let parent=el.parentElement;parent;parent=parent.parentElement)ancestors.observe(parent,{attributes:true,attributeFilter:['data-motion','hidden','style','class']})
  mq.addEventListener('change',attach,opts)
  const releaseEnvironment=acquireEnvironment()
  return ()=>{syncHost.current=()=>{};children.disconnect();attributes.disconnect();ancestors.disconnect();unregister();unsubscribe();destroyBody();automaticRadius=undefined;ac.abort();releaseEnvironment()}
 },[host,category])
 React.useLayoutEffect(()=>{syncHost.current()})
 return ref
}
