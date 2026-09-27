import { factoryTiers, type Category, type MotionSettings, type MorphProfile, type TierName } from "@/lib/cojeev-motion/settings"
export const CATS:Record<Category,{sel:string;tier:TierName}>={
buttons:{sel:'.v-btn:not(.-ghost),.v-select,.v-toggle,.v-native,.v-dock__action,.v-menubar__trigger,.v-cal__month,.v-caption-pill',tier:'pill'},
icons:{sel:'.v-ibtn,.v-disk:not(.v-input>.v-disk):not(.v-igroup>.v-disk):not(.v-select .v-disk):not(.v-menu__item .v-disk),.v-search__disk,.v-collapse,.v-assist__close,.v-attached,.v-play,.v-edit,.v-node__port,.v-receipt__dot,.v-mood .v-face',tier:'tile'},
pills:{sel:'.v-badge:not(.-count):not(.-sm),.v-time,.v-stamp,.v-delta,.v-kbd,.v-capsule__name,.v-nav__count,.v-cal__wk.-on,.v-week__day,.v-bubble,.v-pager button,.v-marker,.v-prov',tier:'tile'},
cards:{sel:'.v-card,.v-record,.v-resource,.v-file,.v-alert,.v-state,.v-md__detail,.v-event,.v-needs,.v-upgrade,.v-node,.v-inspector,.v-palette__item,.v-validation,.v-compact,.v-widget,.v-capsule,.v-band,.v-kv.-panel,.v-quest__opt,.v-collapsible__body,.v-acc>details,.v-acc>[data-slot="accordion-item"],.v-empty,.v-feature__body,.v-resizable,.v-table-wrap,.v-list.-grouped,.v-mobile,.v-notch',tier:'card'},
nav:{sel:'.v-nav__item[aria-current="page"],.v-tabs:not(.-underline) .v-tabs:not(.-underline) .v-tab[aria-selected="true"],.v-item,.v-tabs.-pills,.v-tabs.-lenses,.v-seg,.v-menubar,.v-dock__item[aria-current="page"],.v-datestrip button[aria-pressed="true"],.v-cal__d:is([aria-selected="true"],[aria-pressed="true"],[data-state="active"]),.v-menu__item:is([aria-selected="true"],[data-highlighted],[data-state="checked"])',tier:'nav'},
inputs:{sel:'label.v-input,div.v-input,.v-igroup,.v-cmd__input,.v-combo .v-input,.v-otp input,.v-stepper button,.v-textarea',tier:'nav'},
controls:{sel:'.v-switch,.v-weekdays label,.v-iradio,.v-check input,.v-radio input,.v-slider,.v-track,.v-mood button,.v-hex,.v-avatar:not(.-square),.v-ratio,.v-file__thumb,.v-mask,.v-drops .v-drop',tier:'tile'},
surfaces:{sel:'.v-dialog,.v-sheet,.v-drawer,.v-menu,.v-popover,.v-cmd,.v-toast,.v-assist,.v-dock,.v-dockpanel,.v-sidebar,.v-scroller,.v-scroll,.v-hovercard>.v-popover,.v-collage,.v-feature,.v-ring,.v-prose',tier:'card'},
/* skeletons are the loudest bodies in the library: blob tier, so they breathe at rest and reach for the pointer */
skeleton:{sel:'.v-skel',tier:'pill'}};
export const FILL_SEL='.v-btn:not(.-outline):not(.-ghost):not(.v-seg .v-btn),.v-badge:not(.-dashed):not(.-test),.v-card,.v-disk,.v-ibtn.-ink,.v-ibtn.-pink,.v-ibtn.-beige,.v-ibtn.-cream,.v-select,.v-time,.v-stamp,.v-delta,.v-toggle[aria-pressed="true"],.v-alert,.v-state:not(.-filtered),.v-event,.v-item.-selected,.v-md__detail,.v-tabs:not(.-underline) .v-tab[aria-selected="true"],.v-nav__item[aria-current="page"],.v-dock__action,.v-kbd,.v-bubble,.v-capsule__name,.v-nav__count,.v-week__day,.v-cal__month,.v-caption-pill,.v-menubar__trigger[aria-expanded="true"],.v-search__disk,.v-collapse,.v-assist__close,.v-attached,.v-play,.v-receipt__dot,.v-mood .v-face,.v-pager button[aria-current="page"],.v-datestrip button[aria-pressed="true"],.v-cal__d:is([aria-selected="true"],[aria-pressed="true"],[data-state="active"]),.v-cal__wk.-on,.v-switch,.v-weekdays label,.v-iradio:has(input:checked),.v-hex,.v-avatar,.v-skel,.v-track,.v-quest__opt,.v-band,.v-compact,.v-widget,.v-capsule,.v-node,.v-inspector,.v-palette__item,.v-validation,.v-upgrade,.v-needs,.v-record,.v-resource,.v-file,.v-kv.-panel,.v-collapsible__body,.v-acc>details,.v-acc>[data-slot="accordion-item"],.v-list.-grouped,.v-table-wrap,.v-mobile,.v-notch,.v-dialog,.v-sheet,.v-drawer,.v-menu,.v-popover,.v-cmd,.v-toast,.v-assist,.v-dock,.v-dockpanel,.v-sidebar,.v-scroller,.v-collage,.v-feature__body,.v-feature,.v-ratio,.v-file__thumb,.v-mask,.v-igroup,label.v-input,div.v-input,.v-cmd__input,.v-item,.v-menu__item:is([aria-selected="true"],[data-highlighted],[data-state="checked"]),.v-dock__item[aria-current="page"],.v-toggle';
export const STROKE_SEL='.v-btn.-outline,.v-badge.-dashed,.v-badge.-test,.v-ibtn,.v-menubar,.v-otp,.v-ring,.v-pager button,.v-resizable,.v-scroll,.v-state.-filtered,.v-drops .v-drop,.v-mood button,.v-node__port,.v-hovercard>.v-popover';
export const NEVER_SEL='.v-label,.v-field,.v-help,.v-prov,.v-marker,.v-prose,.v-caps,.v-meta,.v-quiet,.v-sr,label:not(.v-switch):not(.v-weekdays label),legend,dt,dd,p,h1,h2,h3,h4,caption,th:not([data-slot="calendar-week-number"])';
const VOID=/^(INPUT|SELECT|TEXTAREA|HR|IMG|BR)$/
export type MorphHost={mode:"fill"|"stroke"|"both";tierName:TierName;explicit:boolean}
/** The hook category is an eligibility request, never permission to decorate arbitrary layout/text. */
export function resolveMorphHost(el:HTMLElement,category:Category,settings:MotionSettings):MorphHost|null {
 if(VOID.test(el.tagName)||el.closest('[data-motion="off"],[hidden],svg,.tp,.ap,.code'))return null
 const css=getComputedStyle(el);if(/auto|scroll/.test(css.overflowX+css.overflowY))return null
 // Explicit alive opts into a body, but never turns text/layout into control geometry.
 const ownClasses=/^(v-alive|v-morph-host|v-morph-rel|v-morph-live)$/
 const bare=/^(DIV|SPAN)$/.test(el.tagName)&&!el.dataset.shape&&!el.dataset.tier&&[...el.classList].every(name=>ownClasses.test(name))
 if(el.matches(NEVER_SEL)||el.matches('.v-tip,.v-tooltip')||bare)return null
 const explicit=el.dataset.morph
 if(explicit==='none')return null
 if(explicit==='fill'||explicit==='stroke'||explicit==='both'){
  const tierName=(el.dataset.tier||(el.dataset.shape?'blob':el.matches('.v-card,[role="tablist"]')?'card':el.matches('.v-nav__item,.v-tab')?'nav':el.matches('.v-ibtn')?'tile':'pill')) as TierName
  return {mode:explicit,tierName,explicit:true}
 }
 if(settings.mode==='off'||!settings.cats[category]||!el.matches(CATS[category].sel)||el.matches(NEVER_SEL))return null
 const fill=el.matches(FILL_SEL),stroke=!fill&&el.matches(STROKE_SEL)
 if(!fill&&!stroke)return null
 return {mode:stroke?'stroke':'fill',tierName:(el.dataset.tier||CATS[category].tier) as TierName,explicit:false}
}
/** Authored tier values beat markup; only unchanged factory fields accept per-element overrides. */
export function resolveMorphTier(el:HTMLElement,name:TierName,profile:MorphProfile){
 const current=profile.TIER[name],factory=factoryTiers[name];if(!current)return null
 const tier={...current}
 for(const key of ['reach','inside','amp','lobes','depth','asym','spread'] as const){const value=el.dataset[key];if(value!=null&&Number.isFinite(+value)&&current[key]===factory[key])tier[key]=+value}
 return tier
}
const hosts=new Map<HTMLElement,{refresh:()=>void;disable:()=>void}>()
export function registerMorphHost(el:HTMLElement,host:{refresh:()=>void;disable:()=>void}){hosts.set(el,host);return ()=>{hosts.delete(el)}}
/** Scoped helpers include the root and only touch React-owned hosts. Explicit ownership is preserved. */
export function enableMorph(root:HTMLElement){hosts.forEach((host,el)=>{if(el===root||root.contains(el))host.refresh()})}
export function disableMorph(root:HTMLElement){hosts.forEach((host,el)=>{if((el===root||root.contains(el))&&el.hasAttribute('data-auto-morph'))host.disable()})}
