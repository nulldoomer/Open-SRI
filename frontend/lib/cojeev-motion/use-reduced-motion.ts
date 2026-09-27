"use client"
import { useSyncExternalStore } from 'react'
let query:MediaQueryList|undefined
const listeners=new Set<()=>void>()
const changed=()=>listeners.forEach(listener=>listener())
function media(){return query??=window.matchMedia('(prefers-reduced-motion: reduce)')}
function subscribe(listener:()=>void){
 const q=media();if(!listeners.size)q.addEventListener('change',changed);listeners.add(listener)
 return ()=>{listeners.delete(listener);if(!listeners.size){q.removeEventListener('change',changed);query=undefined}}
}
function snapshot(){return typeof window==='undefined'?false:media().matches}
const serverSnapshot=()=>false
/** Dynamic accessibility preference shared by genuine in-progress indicators and other component roles. */
export function useReducedMotion(){return useSyncExternalStore(subscribe,snapshot,serverSnapshot)}
