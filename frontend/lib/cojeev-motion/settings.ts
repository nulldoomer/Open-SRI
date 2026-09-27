export const RUNTIME={cfg:{rest:false,reach:true,merge:true,hold:true,jiggleOn:true,press:true,echo:false,dots:0,grain:.08,sheen:.6,curve:1.2,lobeK:130,lobeZ:1,mergeZ:.8,arcK:40,holdK:80,pressK:170,jiggle:.35,jiggleDecay:1.6,restSpeed:1,drift:1,quality:2.5},
 tier:{pill:{amp:0,reach:3,inside:1.2,press:.02},tile:{amp:0,reach:2.4,inside:1,press:.02},nav:{amp:0,reach:1.2,inside:.5,press:.008},card:{amp:0,reach:1,inside:.4,press:.004},blob:{amp:.006,reach:3,inside:1.2,press:.03}}};
/* Per-tier authoring domains live with the profile, so a slider cannot offer a value the tier cannot mean
   (the spinner's amp .14 is legitimate and must not be clamped to the pill's 3 % ceiling). */
export const DOMAIN={amp:{max:{pill:3,tile:3,nav:1,card:1,blob:8,spinner:20},min:0},reach:{max:{pill:24,tile:24,nav:12,card:12,blob:32,spinner:0},min:0},inside:{max:{pill:12,tile:12,nav:6,card:6,blob:16,spinner:0},min:0},press:{max:{pill:12,tile:12,nav:4,card:4,blob:16,spinner:0},min:0},R:{max:{pill:240,tile:240,nav:160,card:160,blob:320,spinner:0},min:0},sig:{max:{pill:120,tile:120,nav:80,card:160,blob:160,spinner:8},min:1}};
export const factoryTiers={pill:{amp:.008,reach:4,inside:2,sig:24,R:80,press:.03,lobes:0,depth:0,asym:0,spread:.55},tile:{amp:.008,reach:3.5,inside:1.6,sig:18,R:64,press:.035,lobes:0,depth:0,asym:0,spread:.55},nav:{amp:.0006,reach:3,inside:1.2,sig:24,R:64,press:.010,lobes:0,depth:0,asym:0,spread:.55},card:{amp:.0003,reach:2.5,inside:.9,sig:44,R:56,press:.004,lobes:0,depth:0,asym:0,spread:.55},blob:{amp:.012,reach:4,inside:1.8,sig:22,R:64,press:.05,lobes:0,depth:0,asym:0,spread:.55},spinner:{amp:.14,reach:0,inside:0,sig:1,R:0,press:0,lobes:0,depth:0,asym:0,spread:.55}};
export const factoryCfg={quality:2.5,grain:.12,sheen:1,curve:1,lobeK:90,lobeZ:.9,mergeZ:.62,arcK:26,holdK:55,pressK:140,jiggle:.45,jiggleDecay:1.35,restSpeed:1,drift:1,rest:true,reach:true,merge:true,hold:true,jiggleOn:true,press:true,echo:false,echoOff:6,echoScale:1.04,dots:0};


export type MorphConfig = typeof factoryCfg
export type TierName = keyof typeof factoryTiers
export type Tier = typeof factoryTiers.pill
export type ProductCategory = "buttons" | "icons" | "pills" | "cards" | "skeleton"
export type Category = ProductCategory | "nav" | "inputs" | "controls" | "surfaces"
export type MotionSettings = {v:3;mode:"off"|"subtle";cats:Record<Category,boolean>}
export type MorphProfile = {cfg:MorphConfig;TIER:Record<TierName,Tier>}
export type FlowVariant = "glide"|"stretch"|"jelly"|"comet"|"drop"|"rubber"|"pebble"|"ripple"|"halo"|"off"
export type FlowSettings = {variant:FlowVariant;hover:boolean;speed:number;intensity:number;hoverStrength:number}
export type SettingsSnapshot = Readonly<{motion:MotionSettings;flow:FlowSettings;profile:MorphProfile;authored:boolean}>
export const MOTION_KEY = "v-motion"
export const MORPH_KEY = "v-morph-cfg-v3"
export const FLOW_KEY = "v-flow-v1"
export const PRODUCT_CATEGORIES:readonly ProductCategory[] = ["buttons","icons","pills","cards","skeleton"]
export const FLOW_DEFAULTS:Readonly<FlowSettings> = Object.freeze({variant:"glide",hover:true,speed:1,intensity:1,hoverStrength:1})
export const FLOW_CHARACTERS:Record<FlowVariant,{label:string;duration:number;ease:string;land:string;glow?:string}> = {
 glide:{label:"Glide",duration:.24,ease:"cubic-bezier(.2,.8,.2,1)",land:"none"},
 stretch:{label:"Stretch",duration:.3,ease:"cubic-bezier(.3,1.15,.4,1)",land:"vf-land"},
 jelly:{label:"Jelly",duration:.5,ease:"cubic-bezier(.3,1.3,.45,1)",land:"vf-jellyx"},
 comet:{label:"Comet",duration:.34,ease:"cubic-bezier(.2,.8,.2,1)",land:"vf-land"},
 drop:{label:"Ink drop",duration:.3,ease:"cubic-bezier(.3,1.25,.4,1)",land:"vf-bloom"},
 rubber:{label:"Rubber",duration:.36,ease:"cubic-bezier(.3,1.3,.4,1)",land:"vf-land"},
 pebble:{label:"Pebble",duration:.46,ease:"cubic-bezier(.32,1.25,.42,1)",land:"vf-lean"},
 ripple:{label:"Ripple",duration:.36,ease:"cubic-bezier(.2,.8,.2,1)",land:"vf-land",glow:"vf-ring"},
 halo:{label:"Halo",duration:.4,ease:"cubic-bezier(.3,1.2,.4,1)",land:"vf-land",glow:"vf-glow"},
 off:{label:"Off",duration:0,ease:"linear",land:"none"},
}
const defaultSettings:MotionSettings={v:3,mode:"subtle",cats:{buttons:true,icons:true,pills:true,cards:false,skeleton:true,nav:false,inputs:false,controls:false,surfaces:false}}
function runtime():MorphProfile {
 const TIER=structuredClone(factoryTiers)
 for(const name of Object.keys(RUNTIME.tier) as (keyof typeof RUNTIME.tier)[]) Object.assign(TIER[name],RUNTIME.tier[name])
 return {cfg:{...factoryCfg,...RUNTIME.cfg},TIER}
}
function immutable<T>(value:T):T {
 if(value && typeof value==="object"){Object.values(value).forEach(immutable);Object.freeze(value)}
 return value
}
const serverSnapshot:SettingsSnapshot=immutable({motion:structuredClone(defaultSettings),flow:{...FLOW_DEFAULTS},profile:runtime(),authored:false})
let settings=structuredClone(defaultSettings), profile=runtime(), flow={...FLOW_DEFAULTS}, authored=false, loaded=false
let snapshot=serverSnapshot
const listeners=new Set<()=>void>()
let releaseEvents:undefined|(()=>void), dispatching=false
function storage(){try{return typeof window==="undefined"?null:window.localStorage}catch{return null}}
function parse(key:string):unknown {try{return JSON.parse(storage()?.getItem(key)||"null")}catch{return null}}
function record(value:unknown):value is Record<string,unknown>{return !!value&&typeof value==="object"&&!Array.isArray(value)}
function mergeProfile(value:unknown,base:MorphProfile,strict=false):MorphProfile {
 if(!record(value)||(!record(value.cfg)&&!record(value.TIER)))throw new Error("Profile must contain a cfg or TIER object.")
 const next=structuredClone(base)
 if(record(value.cfg)) for(const key of Object.keys(next.cfg) as (keyof MorphConfig)[]){
  const v=value.cfg[key];if(v===undefined)continue
  if(typeof v!==typeof next.cfg[key]||(typeof v==="number"&&!Number.isFinite(v))){if(strict)throw new Error(`Invalid cfg.${key}: expected a finite ${typeof next.cfg[key]}.`);continue}
  Object.assign(next.cfg,{[key]:v})
 }
 if(record(value.TIER))for(const name of Object.keys(next.TIER) as TierName[]){const saved=value.TIER[name];if(!record(saved))continue
  for(const key of Object.keys(next.TIER[name]) as (keyof Tier)[]){const v=saved[key];if(v===undefined)continue
   if(typeof v!=="number"||!Number.isFinite(v)){if(strict)throw new Error(`Invalid TIER.${name}.${key}: expected a finite number.`);continue}
   next.TIER[name][key]=v
  }
 }
 return next
}
function normalizeFlow(value:unknown):FlowSettings {
 const v=record(value)?value:{},number=(key:string,fallback:number,min:number)=>typeof v[key]==="number"&&Number.isFinite(v[key])?Math.max(min,v[key] as number):fallback
 return {variant:typeof v.variant==="string"&&Object.hasOwn(FLOW_CHARACTERS,v.variant)?v.variant as FlowVariant:"glide",hover:typeof v.hover==="boolean"?v.hover:true,speed:number("speed",1,.25),intensity:number("intensity",1,0),hoverStrength:number("hoverStrength",1,0)}
}
export function flowToken(name:string,fallback:string,el?:Element){try{return getComputedStyle(el??document.documentElement).getPropertyValue(name).trim()||fallback}catch{return fallback}}
export function flowTokenMs(name:string,fallback:number,el?:Element){const v=flowToken(name,"",el),n=parseFloat(v);return Number.isFinite(n)?n*(v.endsWith("ms")?1:1000):fallback}
export function applyFlowSettings(){
 if(typeof document==="undefined")return
 const h=document.documentElement,variant=settings.mode==="off"?"off":flow.variant,c=FLOW_CHARACTERS[variant]
 h.dataset.flow=variant;h.dataset.flowHover=flow.hover?"on":"off"
 h.style.setProperty("--flow-speed",String(flow.speed));h.style.setProperty("--flow-intensity",String(flow.intensity));h.style.setProperty("--flow-hover",String(flow.hoverStrength))
 h.style.setProperty("--flow-ease",variant==="off"?c.ease:flowToken("--e-flow-"+variant,c.ease))
 h.style.setProperty("--flow-dur",((variant==="off"?0:flowTokenMs("--t-flow-"+variant,c.duration*1000))/1000/flow.speed).toFixed(3)+"s")
 h.style.setProperty("--flow-land",c.land);h.style.setProperty("--flow-glow",c.glow||"none")
}
function publish(){
 const next={motion:settings,flow,profile,authored}
 if(JSON.stringify(next)===JSON.stringify(snapshot)){applyFlowSettings();return}
 snapshot=immutable(structuredClone(next));applyFlowSettings();listeners.forEach(fn=>fn())
}
export function loadMotionSettings(){
 if(loaded||typeof window==="undefined")return
 loaded=true;settings=structuredClone(defaultSettings);profile=runtime();flow={...FLOW_DEFAULTS};authored=false
 const raw=parse(MOTION_KEY)
 if(record(raw)){
  if(raw.mode==="off"||raw.mode==="subtle")settings.mode=raw.mode
  if(raw.v===3&&record(raw.cats))for(const key of PRODUCT_CATEGORIES)if(typeof raw.cats[key]==="boolean")settings.cats[key]=raw.cats[key]
 }
 flow=normalizeFlow(parse(FLOW_KEY))
 const saved=parse(MORPH_KEY)
 if(saved)try{profile=mergeProfile(saved,{cfg:{...factoryCfg},TIER:structuredClone(factoryTiers)});authored=true}catch{/* Corrupt saved data keeps a usable runtime. */}
 try{for(const key of ["v-alive-settings","v-morph-cfg","v-morph-cfg-v2","v-motion-cfg"])storage()?.removeItem(key)}catch{}
 publish()
}
export function getMotionSettings(){loadMotionSettings();return structuredClone(settings)}
export function getMotionMode(){return getMotionSettings().mode}
export function getMorphProfile(){loadMotionSettings();return structuredClone(profile)}
export function getFlowSettings(){loadMotionSettings();return {...flow}}
export function getSettingsSnapshot(){loadMotionSettings();return snapshot}
export function getServerSettingsSnapshot(){return serverSnapshot}
export function reloadMotionSettings(){loaded=false;loadMotionSettings()}
function acquireEvents(){
 if(releaseEvents||typeof window==="undefined")return
 const onStorage=(event:StorageEvent)=>{if(event.key===null||[MOTION_KEY,MORPH_KEY,FLOW_KEY].includes(event.key))reloadMotionSettings()}
 const onEngine=()=>{if(!dispatching)reloadMotionSettings()}
 window.addEventListener("storage",onStorage);window.addEventListener("v-motion-change",onEngine);window.addEventListener("v-flow",onEngine)
 releaseEvents=()=>{window.removeEventListener("storage",onStorage);window.removeEventListener("v-motion-change",onEngine);window.removeEventListener("v-flow",onEngine);releaseEvents=undefined}
}
export function subscribeSettings(fn:()=>void){loadMotionSettings();listeners.add(fn);acquireEvents();return ()=>{listeners.delete(fn);if(!listeners.size)releaseEvents?.()}}
export const subscribeMotion=subscribeSettings
function persist(key:string,value:unknown){try{storage()?.setItem(key,JSON.stringify(value))}catch{/* Controls remain live when storage is unavailable. */}}
function event(name:string){if(typeof window==="undefined")return;dispatching=true;try{window.dispatchEvent(new CustomEvent(name,{detail:name==="v-flow"?getFlowSettings():getMotionSettings()}))}finally{dispatching=false}}
function saveSettings(){persist(MOTION_KEY,{v:3,mode:settings.mode,cats:Object.fromEntries(PRODUCT_CATEGORIES.map(k=>[k,settings.cats[k]]))});publish();event("v-motion-change")}
export function setMotionMode(mode:MotionSettings["mode"]){loadMotionSettings();settings.mode=mode==="off"?"off":"subtle";saveSettings()}
export function setMotionCategory(category:Category,on:boolean){loadMotionSettings();settings.cats[category]=!!on;saveSettings()}
export function setFlowSettings(patch:Partial<FlowSettings>){loadMotionSettings();flow=normalizeFlow({...flow,...patch});persist(FLOW_KEY,flow);publish();event("v-flow")}
export function resetFlow(){setFlowSettings({...FLOW_DEFAULTS})}
export function saveMorph(){loadMotionSettings();authored=true;persist(MORPH_KEY,profile);publish()}
export function setMorphConfig(patch:Partial<MorphConfig>){loadMotionSettings();profile=mergeProfile({cfg:patch},profile,true);saveMorph()}
export function setMorphTier(tier:TierName,patch:Partial<Tier>){loadMotionSettings();profile=mergeProfile({TIER:{[tier]:patch}},profile,true);saveMorph()}
export function exportMorphJSON(){return JSON.stringify({version:4,...getMorphProfile()},null,1)}
export function importMorphJSON(value:string|Partial<MorphProfile>){
 loadMotionSettings();let parsed:unknown=value
 if(typeof value==="string")try{parsed=JSON.parse(value)}catch{throw new Error("Invalid JSON. Paste an exported morph profile with cfg and TIER.")}
 const next=mergeProfile(parsed,profile,true);profile=next;saveMorph()
}
export function resetMorph(){loadMotionSettings();profile=runtime();authored=false;try{storage()?.removeItem(MORPH_KEY)}catch{}publish()}
