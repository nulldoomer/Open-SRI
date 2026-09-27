import type * as React from 'react'
/** React 19 callback refs may return their own teardown; object refs are mutable commit targets. */
export function assignMotionRef<T>(ref:React.Ref<T>|undefined,value:T|null):()=>void{
 if(typeof ref==='function'){
  const cleanup=ref(value)
  return ()=>{if(typeof cleanup==='function')cleanup();else ref(null)}
 }
 if(ref)ref.current=value
 return ()=>{if(ref)ref.current=null}
}
