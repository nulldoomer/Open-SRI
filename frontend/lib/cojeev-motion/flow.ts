import { createMotionLane, motionTokens, resolveChoreography } from "@/lib/cojeev-motion/choreography"
import { createFlowPainter, animateFlowLanding } from "@/lib/cojeev-motion/flow-motion"
import { FLOW_CHARACTERS, getSettingsSnapshot, applyFlowSettings, getFlowSettings, getMotionSettings, subscribeSettings, flowTokenMs, type FlowVariant } from "@/lib/cojeev-motion/settings"
import { scheduleMotion, cancelMotion, getMotionTime, type MotionTimer } from "@/lib/cojeev-motion/clock"
export { flowClock, motionClock } from "@/lib/cojeev-motion/clock"

export type FlowGroupOptions={kind?:"pill"|"bar"|"fill";itemSelector?:string;activeSelector?:string}
export const FLOW_GROUPS='[role="tablist"],[data-filters],.v-seg,.v-pager,.v-weekdays,.v-tabs,.v-nav,.v-quest__opts,.v-iradios,.v-menu,.v-cmd__list,.v-list,.v-stepper-flow,.v-carousel__dots,.v-cal__grid,[data-flow-group],[data-flow-fields],[data-flow-owned]'
const SPECIAL:[string,string,string|null][]=[['.v-menu','.v-menu__item',null],['.v-cmd__list','.v-menu__item',null],['.v-list','.v-item',null],['.v-stepper-flow','.v-step','.v-step__n'],['.v-carousel__dots','button',null],['.v-cal__grid','.v-cal__d',null]]
const ITEMS='button,[role="tab"],label,.v-nav__item,.v-item,[role="radio"]'
export const FLOW_FIELD_ITEMS='.v-input,.v-textarea,.v-igroup,.v-native,.v-otp input'
const ACTIVE='[aria-selected="true"],[aria-pressed="true"],[aria-checked="true"],[aria-current="page"],[aria-current="true"],[aria-current="step"],.-on,.-selected,label:has(input:checked),[data-state="active"],[data-state="on"],[data-state="checked"]'
const LAYERS=['v-glide__pill','v-glide__hover','v-glide__trail']
const PHASES=['-phase1','-gather','-shoot','-lead','-land']
type Box={x:number;y:number;w:number;h:number;r:string}
type Group={element:HTMLElement;place:(animate?:boolean)=>void;resolve:()=>void;dispose:()=>void}
const groups=new Map<HTMLElement,Group>()
const all=(selector:string,root:ParentNode)=>Array.from(root.querySelectorAll<HTMLElement>(selector))
let users=0,releaseEnvironment=()=>{},trigger:null|{x:number;y:number;t:number}=null
export function isFlowQuiet(el?:HTMLElement){return getMotionSettings().mode==='off'||getFlowSettings().variant==='off'||(typeof matchMedia!=='undefined'&&matchMedia('(prefers-reduced-motion: reduce)').matches)||!!el?.closest('[data-flow="off"],[data-no-glide]')}
/** Shared activation/settings/visibility subscriptions exist only while a component owns flow. */
export function acquireFlowEnvironment(){
 if(users++===0){
  const ac=new AbortController(),opts={signal:ac.signal,capture:true},rm=matchMedia('(prefers-reduced-motion: reduce)')
  const refresh=()=>{applyFlowSettings();groups.forEach(group=>group.resolve())}
  window.addEventListener('pointerdown',event=>{trigger={x:event.clientX,y:event.clientY,t:getMotionTime()}},opts)
  window.addEventListener('keydown',event=>{if((event.key==='Enter'||event.key===' ')&&event.target instanceof Element){const r=event.target.getBoundingClientRect();trigger={x:r.left+r.width/2,y:r.top+r.height/2,t:getMotionTime()}}},opts)
  window.addEventListener('resize',()=>replaceFlow(),{signal:ac.signal,passive:true})
  document.addEventListener('visibilitychange',()=>groups.forEach(group=>group.resolve()),{signal:ac.signal})
  rm.addEventListener('change',refresh,{signal:ac.signal})
  const unsubscribe=subscribeSettings(refresh);applyFlowSettings()
  releaseEnvironment=()=>{ac.abort();unsubscribe();trigger=null}
 }
 let released=false;return ()=>{if(released)return;released=true;if(--users===0)releaseEnvironment()}
}
function variantFor(el:HTMLElement):FlowVariant{
 if(getMotionSettings().mode==='off'||getFlowSettings().variant==='off'||el.closest('[data-flow="off"],[data-no-glide]'))return 'off'
 const pin=el.closest<HTMLElement>('[data-flow]:not(html)')?.dataset.flow
 return pin&&Object.hasOwn(FLOW_CHARACTERS,pin)?pin as FlowVariant:getFlowSettings().variant
}
/** Only this mounted host and its own nearest-group items are measured or mutated. */
export function attachFlowGroup(g:HTMLElement,options:FlowGroupOptions={}):()=>void{
 const existing=groups.get(g);if(existing){existing.resolve();return ()=>{}}
 const release=acquireFlowEnvironment(),ac=new AbortController(),opts={signal:ac.signal}
 const kind=options.kind??(g.hasAttribute('data-flow-fields')?'fill':g.classList.contains('-underline')?'bar':'pill')
 const spec=SPECIAL.find(([selector])=>g.matches(selector)),anchor=spec?.[2]
 const itemSel=options.itemSelector??(kind==='fill'?FLOW_FIELD_ITEMS:spec?.[1]??ITEMS),isMenu=g.matches('.v-menu,.v-cmd__list,[role="menu"],[role="listbox"]')
 const oldAttrs=new Map(['data-flow-owned','data-flow-kind','data-flow-v','data-dir','data-flow-engine'].map(name=>[name,g.getAttribute(name)]))
 const oldClasses=new Set(g.classList),oldStyles=new Map<string,string>(),activeAttrs=new Map<HTMLElement,string|null>()
 const write=(name:string,value:string)=>{if(!oldStyles.has(name))oldStyles.set(name,g.style.getPropertyValue(name));if(g.style.getPropertyValue(name)!==value)g.style.setProperty(name,value)}
 g.setAttribute('data-flow-owned','');g.setAttribute('data-flow-engine','motion')
 const owned=(item:HTMLElement)=>{
  if(item.closest(FLOW_GROUPS)!==g)return false
  if(kind==='fill'){
   const boundary=item.closest('form,fieldset');if(boundary&&boundary!==g&&g.contains(boundary))return false
   if(item.parentElement?.closest(FLOW_FIELD_ITEMS)&&g.contains(item.parentElement.closest(FLOW_FIELD_ITEMS)))return false
  }
  return true
 }
 const items=(visible=true)=>all(itemSel,g).filter(item=>owned(item)&&(!visible||item.getClientRects().length>0))
 const mk=(className:string)=>{const span=document.createElement('span');span.className=className;span.setAttribute('aria-hidden','true');span.append(document.createElement('i'));return span}
 const [pill,hov,trail]=LAYERS.map(mk),timers=new Set<MotionTimer>(),painter=createFlowPainter(write)
 let cancelLanding=()=>{}
 const stopMovement=()=>{painter.stop(false);cancelLanding();cancelLanding=()=>{}}
 let prev:Box|null=null,lastActive:HTMLElement|null=null,dScale=1,queued:MotionTimer|null=null,resizeTimer:MotionTimer|null=null,stillTimer:MotionTimer|null=null,disposed=false,attached=false,reseat=false,initial=true,inView=true
 const later=(fn:()=>void,ms:number,scaled=true)=>{const timer=scheduleMotion(()=>{timers.delete(timer);if(!disposed)fn()},scaled?ms*dScale/getFlowSettings().speed:ms);timers.add(timer);return timer}
 const clearPhases=()=>{stopMovement();timers.forEach(cancelMotion);timers.clear();PHASES.forEach(c=>pill.classList.remove(c))}
 const markStill=()=>{g.classList.add('-still');cancelMotion(stillTimer);stillTimer=scheduleMotion(()=>{stillTimer=null;if(!oldClasses.has('-still'))g.classList.remove('-still')},flowTokenMs('--t-flow-still',60))}
 const unmark=()=>{for(const [item,old]of activeAttrs){if(old===null)item.removeAttribute('data-glide-active');else item.setAttribute('data-glide-active',old)}activeAttrs.clear()}
 const seat=()=>{
  const candidates=items(false),single=kind==='fill'||!candidates.some(item=>item.matches('[role="checkbox"]')||!!item.querySelector('input[type="checkbox"]'))||candidates.some(item=>item.matches('[role="radio"]')||!!item.querySelector('input[type="radio"]'))
  if(candidates.length<2||!single){[pill,hov,trail].forEach(layer=>layer.remove());unmark();if(!oldClasses.has('v-glide'))g.classList.remove('v-glide');attached=false;prev=null;lastActive=null;clearPhases();painter.hide(true,true);g.removeAttribute('data-flow-v');g.removeAttribute('data-flow-kind');return false}
  if(!attached){attached=true;g.classList.add('v-glide');g.dataset.flowKind=kind;g.dataset.flowV=variantFor(g);markStill()}
  if(trail.parentNode!==g)g.prepend(trail);if(hov.parentNode!==g)g.prepend(hov);if(pill.parentNode!==g)g.prepend(pill)
  return true
 }
 const box=(item:HTMLElement):Box=>{
  const el=anchor?item.querySelector<HTMLElement>(anchor)??item:item,r=el.getBoundingClientRect(),gr=g.getBoundingClientRect(),css=getComputedStyle(el)
  const b={x:r.left-gr.left-g.clientLeft+g.scrollLeft,y:r.top-gr.top-g.clientTop+g.scrollTop,w:r.width,h:r.height,r:css.borderRadius&&css.borderRadius!=='0px'?css.borderRadius:'999px'}
  if(kind==='bar'){b.y+=b.h-2.5;b.h=2.5;b.r='2px'}return b
 }
 const paint=(b:Box,hover=false,immediate=false)=>painter.paint(b,hover,immediate||isFlowQuiet(g)||document.hidden||!inView,variantFor(g))
 const active=()=>{
  const candidates=items()
  if(kind==='fill')return (options.activeSelector?candidates.find(item=>item.matches(options.activeSelector!)):null)??candidates.find(item=>item===document.activeElement||item.contains(document.activeElement))??null
  return candidates.find(item=>item.matches(options.activeSelector??ACTIVE)||(isMenu&&(item===document.activeElement||item.hasAttribute('data-highlighted'))))??null
 }
 const land=()=>{if(g.dataset.flowV==='glide'||isFlowQuiet(g)||document.hidden||!inView)return;cancelLanding();cancelLanding=animateFlowLanding(pill.firstElementChild as HTMLElement,g.dataset.flowV??'glide',g.dataset.dir)}
 function suspend(){clearPhases();painter.hide(true,true);[pill,hov,trail].forEach(layer=>layer.remove());unmark();attached=false;prev=null;lastActive=null;for(const name of ['v-glide','-still'])if(!oldClasses.has(name))g.classList.remove(name);for(const name of ['data-flow-kind','data-flow-v','data-dir']){const old=oldAttrs.get(name);if(old==null)g.removeAttribute(name);else g.setAttribute(name,old)}for(const [name,old]of oldStyles){if(old)g.style.setProperty(name,old);else g.style.removeProperty(name)}}
 const place=(animate=true)=>{
  if(disposed)return;if(variantFor(g)==='off'){suspend();return}if(!g.isConnected){dispose();return}if(!seat())return
  const a=active()
  for(const item of activeAttrs.keys())if(item!==a){const old=activeAttrs.get(item);if(old===null)item.removeAttribute('data-glide-active');else item.setAttribute('data-glide-active',old!);activeAttrs.delete(item)}
  if(!a){clearPhases();painter.hide(false,isFlowQuiet(g)||!!lastActive&&!g.contains(lastActive));prev=null;lastActive=null;return}
  if(!activeAttrs.has(a))activeAttrs.set(a,a.getAttribute('data-glide-active'))
  if(!a.hasAttribute('data-glide-active'))a.setAttribute('data-glide-active','')
  if(!animate&&lastActive&&a!==lastActive&&prev)animate=true
  if(lastActive!==a)painter.hide(true,isFlowQuiet(g)||!inView)
  lastActive=a
  const b=box(a),v=g.dataset.flowV??variantFor(g),moved=prev&&(Math.abs(b.x-prev.x)>.5||Math.abs(b.y-prev.y)>.5||Math.abs(b.w-prev.w)>.5||Math.abs(b.h-prev.h)>.5)
  if(!moved&&prev)return
  // A phased interruption starts from the body the person currently sees,
  // rather than the destination of the previous selection.
  let origin=prev
  if(animate&&moved&&prev&&['stretch','drop','rubber'].includes(v)&&!isFlowQuiet(g)){
   const r=pill.getBoundingClientRect(),gr=g.getBoundingClientRect()
   origin={x:r.left-gr.left-g.clientLeft+g.scrollLeft,y:r.top-gr.top-g.clientTop+g.scrollTop,w:r.width,h:r.height,r:getComputedStyle(pill).borderRadius}
  }
  clearPhases()
  if(animate&&moved&&origin&&inView&&!document.hidden&&!isFlowQuiet(g)&&v!=='off'){
   const dx=b.x-origin.x,dy=b.y-origin.y,horizontal=Math.abs(dx)>=Math.abs(dy)
   g.dataset.dir=horizontal?'x':'y';dScale=Math.min(1.25,Math.max(.8,.8+Math.hypot(dx,dy)/600));write('--glide-d',dScale.toFixed(2))
   if(v==='stretch'){
    const union={x:Math.min(b.x,origin.x),y:Math.min(b.y,origin.y),w:0,h:0,r:b.r};union.w=Math.max(b.x+b.w,origin.x+origin.w)-union.x;union.h=Math.max(b.y+b.h,origin.y+origin.h)-union.y
    pill.classList.add('-phase1');paint(union);later(()=>{pill.classList.remove('-phase1');paint(b);land()},flowTokenMs('--t-flow-stretch-p1',165))
   }else if(v==='drop'){
    const d=Math.max(6,Math.min(12,b.h)),c0={x:origin.x+origin.w/2-d/2,y:origin.y+origin.h/2-d/2,w:d,h:d,r:'999px'},c1={x:b.x+b.w/2-d/2,y:b.y+b.h/2-d/2,w:d,h:d,r:'999px'}
    pill.classList.add('-gather');paint(c0);const gather=flowTokenMs('--t-flow-drop-gather',140),shoot=flowTokenMs('--t-flow-drop-shoot',180)
    later(()=>{pill.classList.remove('-gather');pill.classList.add('-shoot');paint(c1)},gather)
    later(()=>{pill.classList.remove('-shoot');paint(b);land()},gather+shoot)
   }else if(v==='rubber'){
    const lead={...b}
    if(horizontal){if(dx>=0){lead.x=origin.x;lead.w=b.x+b.w-origin.x}else lead.w=origin.x+origin.w-b.x;lead.y=origin.y;lead.h=origin.h}
    else{if(dy>=0){lead.y=origin.y;lead.h=b.y+b.h-origin.y}else lead.h=origin.y+origin.h-b.y;lead.x=origin.x;lead.w=origin.w}
    pill.classList.add('-lead');paint(lead);later(()=>{pill.classList.remove('-lead');paint(b);land()},flowTokenMs('--t-flow-rubber-lead',200))
   }else{paint(b);land()}
  }else paint(b,false,true)
  prev=b
 }
 const hideHover=()=>{if(attached)painter.hide(true,isFlowQuiet(g)||document.hidden)}
 const resolve=()=>{if(disposed)return;const variant=variantFor(g);if(variant==='off'){suspend();return}
  if(g.dataset.flowV&&g.dataset.flowV!==variant){clearPhases();prev=null;markStill()}
  g.dataset.flowV=variant
  if(document.hidden||matchMedia('(prefers-reduced-motion: reduce)').matches){clearPhases();prev=null;markStill();place(false)}else place(false)
 }
 const q=()=>{if(queued||disposed)return;queued=scheduleMotion(()=>{queued=null;if(reseat){reseat=false;place(false)}else place(!initial)},16)}
 g.addEventListener('click',q,opts);g.addEventListener('change',q,opts);g.addEventListener('input',q,opts)
 g.addEventListener('keyup',event=>{if(/Arrow|Home|End| |Enter/.test(event.key))q()},opts)
 const hoverItem=(target:EventTarget|null)=>{
  const item=target instanceof Element?target.closest<HTMLElement>(itemSel):null
  if(!inView||!getFlowSettings().hover||g.closest('[data-flow-hover="off"]')||!attached||variantFor(g)==='off'||!item||!g.contains(item)||!owned(item)||item.hasAttribute('data-glide-active')||item.matches(':disabled,[aria-disabled="true"],[data-disabled]:not([data-disabled="false"])')){hideHover();return}
  paint(box(item),true)
 }
 g.addEventListener('focusin',event=>{q();hoverItem(event.target)},opts);g.addEventListener('focusout',event=>{if(!g.contains(event.relatedTarget as Node|null)){hideHover();q()}},opts)
 g.addEventListener('pointerover',event=>{if(event.pointerType==='touch'){hideHover();return}hoverItem(event.target)},opts)
 g.addEventListener('pointerleave',hideHover,opts);g.addEventListener('pointerdown',hideHover,opts)
 const mo=new MutationObserver(records=>{
  // React may replace className while this host and its motion owner survive.
  // Restore only our marker: otherwise the paint layers become flex/grid items.
  if(attached&&!g.classList.contains('v-glide')){g.classList.add('v-glide');prev=null;reseat=true;q()}
  if(records.every(m=>LAYERS.some(c=>(m.target instanceof Element)&&(m.target.classList.contains(c)||m.target.parentElement?.classList.contains(c)))||(m.target===g&&m.type==='attributes'&&m.attributeName==='class')))return
  if(records.some(m=>m.target===g&&['data-flow','data-no-glide'].includes(m.attributeName??'')))resolve()
  if(records.some(m=>m.type==='childList')){
   // Filtering can remove the hovered row without a pointerleave event. Its
   // old absolute highlight must not retain the previous list's scroll range.
   painter.hide(true,true)
   if(pill.parentNode!==g)reseat=true
  }
  q()
 })
 mo.observe(g,{attributes:true,childList:true,characterData:true,subtree:true,attributeFilter:['aria-selected','aria-pressed','aria-checked','aria-current','data-state','data-highlighted','checked','class','data-flow','data-no-glide','data-flow-hover','hidden','open']})
 const ancestor=new MutationObserver(resolve)
 for(let parent=g.parentElement;parent;parent=parent.parentElement)ancestor.observe(parent,{attributes:true,attributeFilter:['data-flow','data-no-glide','hidden','open','data-state']})
 const ro=new ResizeObserver(()=>{if(!resizeTimer)resizeTimer=scheduleMotion(()=>{resizeTimer=null;place(false)},16)});ro.observe(g)
 const visibility=new IntersectionObserver(([entry])=>{inView=entry.isIntersecting;if(!inView){clearPhases();if(prev)painter.paint(prev,false,true,variantFor(g));painter.hide(true,true)}else place(false)},{threshold:0});visibility.observe(g)
 function dispose(){if(disposed)return;disposed=true;groups.delete(g);ac.abort();mo.disconnect();ancestor.disconnect();ro.disconnect();visibility.disconnect();cancelMotion(queued);cancelMotion(resizeTimer);cancelMotion(stillTimer);clearPhases();painter.dispose();[pill,hov,trail].forEach(layer=>layer.remove());unmark()
  for(const name of ['v-glide','-still'])if(!oldClasses.has(name))g.classList.remove(name)
  for(const [name,value]of oldAttrs){if(value===null)g.removeAttribute(name);else g.setAttribute(name,value)}
  for(const [name,value]of oldStyles){if(value)g.style.setProperty(name,value);else g.style.removeProperty(name)}release()
 }
 groups.set(g,{element:g,place,resolve,dispose});resolve();void document.fonts.ready.then(()=>{initial=false;if(!disposed)place(false)});return dispose
}
export function replaceFlow(root?:HTMLElement){groups.forEach(group=>{if(!root||group.element===root||root.contains(group.element))group.place(false)})}
export function detachFlowGroup(el:HTMLElement){groups.get(el)?.dispose()}
export type FlowSlideMode='slide-inline'|'slide-block'
type Edges={left:number;right:number;top:number;bottom:number}
/** Resting border-box travel includes the gap between the surface and viewport. */
export function flowSlideTravel(rect:Edges,viewport:Edges,mode:FlowSlideMode,side:'start'|'end'='end',direction='ltr'){
 if(mode==='slide-block')return {x:0,y:Math.max(0,viewport.bottom-rect.top)}
 const left=(side==='start')!==(direction==='rtl')
 return {x:left?-Math.max(0,rect.right-viewport.left):Math.max(0,viewport.right-rect.left),y:0}
}
/** Presence keeps one progress owner through rapid open/close reversals. Radix
 * retains closed nodes through the nonvisual CSS sentinel in flow-press.css. */
export function createFlowPresence(el:HTMLElement,grow:boolean|'fade'|FlowSlideMode=true){
 const names=['opacity','scale','translate','transform-origin','animation','--flow-exit-duration'],original=new Map(names.map(name=>[name,el.style.getPropertyValue(name)])),old=el.getAttribute('data-flow-presence')
 el.setAttribute('data-flow-presence','')
 const slide=grow==='slide-inline'||grow==='slide-block',fade=grow==='fade',rect=el.getBoundingClientRect(),fresh=grow===true&&trigger&&getMotionTime()-trigger.t<1500
 // Radix initially measures an offscreen wrapper. Its live origin variable
 // follows final collision/side placement; a captured client rect does not.
 const anchored=!!el.closest('[data-radix-popper-content-wrapper]')
 if(!slide)el.style.transformOrigin=anchored?'var(--radix-popper-transform-origin, 50% 50%)':fresh?`${Math.max(0,Math.min(rect.width,trigger!.x-rect.left))}px ${Math.max(0,Math.min(rect.height,trigger!.y-rect.top))}px`:getComputedStyle(el).transformOrigin
 let opened=true,disposed=false
 const restorePaint=()=>{for(const name of ['opacity','scale','translate']){const value=original.get(name);if(value)el.style.setProperty(name,value);else el.style.removeProperty(name)}}
 let travel={x:0,y:0},baseTranslate=['0px','0px'],baseOpacity=1
 const measureSlide=()=>{
  if(!slide)return
  restorePaint()
  const bounds=el.getBoundingClientRect(),style=getComputedStyle(el),viewport=window.visualViewport
  const left=viewport?.offsetLeft??0,top=viewport?.offsetTop??0
  travel=flowSlideTravel(bounds,{left,top,right:left+(viewport?.width??window.innerWidth),bottom:top+(viewport?.height??window.innerHeight)},grow,el.dataset.side==='start'?'start':'end',style.direction)
  // Computed calc() expressions can contain spaces; keep each authored axis intact.
  baseTranslate=style.translate==='none'?['0px','0px']:style.translate.split(/\s+(?![^()]*\))/)
  baseOpacity=Number(style.opacity)
 }
 const lane=createMotionLane(1,value=>{
  if(disposed)return
  const progress=Math.max(0,Math.min(1,value))
  el.style.opacity=String(progress*(slide?baseOpacity:1))
  if(slide)el.style.setProperty('translate',`calc(${baseTranslate[0]} + ${travel.x*(1-progress)}px) calc(${baseTranslate[1]??'0px'} + ${travel.y*(1-progress)}px)${baseTranslate[2]?` ${baseTranslate[2]}`:''}`)
  else if(!fade){el.style.setProperty('scale',String(.975+.025*value));el.style.setProperty('translate',`0 ${(grow?4:10)*(1-value)}px`)}
 })
 function set(open:boolean,animate=true){
  opened=open
  const quiet=isFlowQuiet(el)||document.hidden,snapshot=getSettingsSnapshot(),{transition}=resolveChoreography(snapshot,quiet)
  if(slide&&(lane.get()===0||lane.get()===1)){const current=lane.get();measureSlide();lane.jump(current)}
  const exit=slide?.38:motionTokens.duration.exit
  el.style.setProperty('--flow-exit-duration',quiet?'0s':`${(exit+.04)/snapshot.flow.speed}s`)
  if(quiet||!animate){
   lane.jump(open?1:0)
   if(open)restorePaint()
   else queueMicrotask(()=>{
    // Removing the CSS sentinel under quiet mode can cancel its end event.
    // Finish every Radix surface's retained exit, not only sliding drawers,
    // even while the document clock is suspended.
    if(!disposed&&!opened&&el.isConnected)el.dispatchEvent(new AnimationEvent('animationend',{animationName:getComputedStyle(el).animationName.split(',')[0].trim(),bubbles:true}))
   })
   return
  }
  lane.to(open?1:0,slide?{duration:(open?.45:exit)/snapshot.flow.speed,ease:[...(open?motionTokens.ease.enter:motionTokens.ease.settle)]}:open?transition:{duration:exit/snapshot.flow.speed,ease:[...motionTokens.ease.exit]},()=>{if(opened&&open){restorePaint();replaceFlow(el)}})
 }
 return {
  set,
  enter(){lane.jump(0);set(true)},
  quiet(){set(opened,false)},
  dispose(){if(disposed)return;disposed=true;lane.dispose();if(old===null)el.removeAttribute('data-flow-presence');else el.setAttribute('data-flow-presence',old);for(const [name,value]of original){if(value)el.style.setProperty(name,value);else el.style.removeProperty(name)}},
 }
}
/** Bounded surface appearance; callers own cleanup. */
export function appearFlow(el:HTMLElement,grow=true){
 const presence=createFlowPresence(el,grow)
 if(!isFlowQuiet(el))presence.enter()
 return ()=>presence.dispose()
}
const pulses=new WeakMap<HTMLElement,()=>void>()
export function cancelFlowPulse(el:HTMLElement){pulses.get(el)?.();pulses.delete(el)}
export function pulseFlow(el:HTMLElement){
 cancelFlowPulse(el)
 if(isFlowQuiet(el)||document.hidden||el.closest('.v-glide')||el.matches(':disabled,[aria-disabled="true"],[data-disabled]:not([data-disabled="false"])')||el.closest('[inert],[hidden]'))return
 const surface=el.hasAttribute('data-stable-hit')?el.querySelector<SVGElement>('svg.v-morph'):el
 if(!surface)return
 const scale=surface.style.getPropertyValue('scale'),intensity=Math.min(getFlowSettings().intensity,2)
 let active=true
 const lane=createMotionLane(.975,value=>{if(active)surface.style.setProperty('scale',String(1+(value-1)*intensity))})
 const cleanup=()=>{if(!active)return;active=false;lane.dispose();if(scale)surface.style.setProperty('scale',scale);else surface.style.removeProperty('scale');pulses.delete(el)}
 pulses.set(el,cleanup)
 lane.to(1,resolveChoreography(getSettingsSnapshot(),false).transition,cleanup)
}
