export type MotionTimer = {at:number;run:()=>void;native:ReturnType<typeof setTimeout>|null;cancelled:boolean}
let time:number|null=null
const pending=new Set<MotionTimer>()
const drivers=new Set<(time:number|null)=>void>()
const animationStarts=new Map<Animation,{at:number;wasRunning:boolean}>()
const now=()=>time??performance.now()
function arm(timer:MotionTimer){timer.native=setTimeout(()=>{timer.native=null;if(timer.cancelled)return;pending.delete(timer);timer.run()},Math.max(0,timer.at-performance.now()))}
export function scheduleMotion(run:()=>void,delay:number):MotionTimer{
 const timer:MotionTimer={at:now()+Math.max(0,delay),run,native:null,cancelled:false};pending.add(timer)
 if(time===null)arm(timer)
 return timer
}
export function cancelMotion(timer:MotionTimer|null|undefined){if(!timer)return;timer.cancelled=true;if(timer.native!==null)clearTimeout(timer.native);timer.native=null;pending.delete(timer)}
export function registerMotionClock(driver:(time:number|null)=>void){drivers.add(driver);return ()=>{drivers.delete(driver)}}
export function getMotionTime(){return now()}
export function isMotionClockFrozen(){return time!==null}
/** Freeze before an interaction. First spring delta is zero; timer callbacks can enqueue subsequent beats. */
export function motionClock(next:number|null){
 if(next===null){
  const was=time;time=null
  if(was!==null)for(const timer of pending){timer.at=performance.now()+Math.max(0,timer.at-was);arm(timer)}
  drivers.forEach(driver=>driver(null))
  for(const [animation,state] of animationStarts){if(state.wasRunning)animation.play()}
  animationStarts.clear();return
 }
 if(!Number.isFinite(next))throw new Error("Motion clock expects finite milliseconds or null.")
 if(time===null){const real=performance.now();for(const timer of pending){if(timer.native!==null)clearTimeout(timer.native);timer.native=null;timer.at=next+Math.max(0,timer.at-real)}}
 time=next
 drivers.forEach(driver=>driver(next))
 // Due callbacks observe this clock sample, matching the source phase queue.
 let due:MotionTimer|undefined
 while((due=[...pending].filter(timer=>timer.at<=next).sort((a,b)=>a.at-b.at)[0])){
  pending.delete(due);if(due.cancelled)continue;due.run()
 }
 time=next
 if(typeof document!=="undefined"){
  const animations=document.getAnimations()
  for(const animation of animations){
   if(!animationStarts.has(animation)){animationStarts.set(animation,{at:next,wasRunning:animation.playState==="running"});animation.pause()}
   animation.currentTime=Math.max(0,next-animationStarts.get(animation)!.at)
  }
  for(const animation of animationStarts.keys())if(!animations.includes(animation))animationStarts.delete(animation)
 }
}
export const flowClock=motionClock
