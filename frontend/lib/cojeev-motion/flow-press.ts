"use client"
import * as React from "react"
import { acquireFlowEnvironment, isFlowQuiet, pulseFlow, cancelFlowPulse } from "@/lib/cojeev-motion/flow"
import { subscribeSettings } from "@/lib/cojeev-motion/settings"
export const pulseFlowPress=pulseFlow
const semanticStateTransitions=new Set(['checked:unchecked','unchecked:checked','on:off','off:on','open:closed','closed:open'])
/** Standalone release/change pulse; selected children defer to their group's travelling body. */
export function useFlowPress<T extends HTMLElement>(externalRef?:React.Ref<T>):React.RefCallback<T>{
 return React.useCallback((el:T|null)=>{
  let externalCleanup:void|(()=>void)
  if(typeof externalRef==='function')externalCleanup=externalRef(el);else if(externalRef)externalRef.current=el
  if(!el)return
  const release=acquireFlowEnvironment(),ac=new AbortController(),options={capture:true,signal:ac.signal},touched=new Map<HTMLElement,{attribute:string|null;styles:Map<string,string>}>()
  const pulse=(target:HTMLElement)=>{
   if(!touched.has(target))touched.set(target,{attribute:target.getAttribute('data-flow-land'),styles:new Map(['--flow-ease','--flow-dur','--flow-land','--flow-glow'].map(key=>[key,target.style.getPropertyValue(key)]))})
   pulseFlow(target)
  }
  const restore=()=>{for(const [target,old]of touched){cancelFlowPulse(target);if(old.attribute===null)target.removeAttribute('data-flow-land');else target.setAttribute('data-flow-land',old.attribute);for(const [key,value]of old.styles){if(value)target.style.setProperty(key,value);else target.style.removeProperty(key)}}touched.clear()}
  const change=(event:Event)=>{const target=event.target
   if(el.matches('.v-slider,.v-sliderwrap')||target instanceof Element&&target.matches('.v-slider,[role="slider"]')){const output=el.closest('.v-sliderwrap')?.querySelector<HTMLElement>('output');if(output)pulse(output);return}
   if(target instanceof HTMLElement&&target.matches('input[type="checkbox"],input[type="radio"]')){const host=target.closest<HTMLElement>('.v-switch,.v-check,.v-radio,.v-iradio,.v-quest__opt');pulse(host?.matches('.v-check,.v-radio')?target:host??el)}else pulse(el)
  }
  el.addEventListener('pointerup',()=>pulse(el),options)
  el.addEventListener('keyup',event=>{if(event.key==='Enter'||event.key===' ')pulse(el)},options)
  el.addEventListener('change',change,options);el.addEventListener('input',change,options)
  const states=new MutationObserver(records=>{
   if(el.matches(':disabled,[aria-disabled="true"],[data-disabled]:not([data-disabled="false"]),[inert],[hidden]')){restore();return}
   const nextValues=new Map<string,string|null>();let changed=false
   for(let index=records.length-1;index>=0;index--){const record=records[index],name=record.attributeName;if(record.target!==el||!name)continue
    const next=nextValues.has(name)?nextValues.get(name)!:el.getAttribute(name);nextValues.set(name,record.oldValue)
    if(name==='data-state')changed ||= semanticStateTransitions.has(`${record.oldValue}:${next}`)
    else if(name==='aria-checked'||name==='aria-pressed')changed ||= record.oldValue!==next
   }
   if(changed)pulse(el)
  })
  states.observe(el,{attributes:true,attributeOldValue:true,attributeFilter:['data-state','aria-checked','aria-pressed','disabled','aria-disabled','data-disabled','inert','hidden']})
  const rm=matchMedia('(prefers-reduced-motion: reduce)'),quiet=()=>{if(isFlowQuiet(el)||document.hidden)restore()}
  rm.addEventListener('change',quiet,{signal:ac.signal});document.addEventListener('visibilitychange',quiet,{signal:ac.signal});const unsubscribe=subscribeSettings(quiet)
  return ()=>{ac.abort();states.disconnect();unsubscribe();restore();release();if(typeof externalCleanup==='function')externalCleanup();else if(typeof externalRef==='function')externalRef(null);else if(externalRef)externalRef.current=null}
 },[externalRef])
}
