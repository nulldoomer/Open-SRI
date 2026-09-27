import { path } from '@/lib/cojeev-motion/geometry'
import type { Body } from '@/lib/cojeev-motion/body'
import type { MorphConfig } from '@/lib/cojeev-motion/settings'
const NS='http://www.w3.org/2000/svg'
let shared:SVGSVGElement|null=null,users=0,serial=0
function node<K extends keyof SVGElementTagNameMap>(tag:K,attrs:Record<string,string>={}):SVGElementTagNameMap[K]{const el=document.createElementNS(NS,tag);for(const [key,value]of Object.entries(attrs))el.setAttribute(key,value);return el}
/** One noise pattern and gradient while any alive body owns overlays. */
function acquireDefs(){
 if(!shared){
  const id=++serial;shared=node('svg',{id:'v-morph-defs',width:'0',height:'0','aria-hidden':'true'});shared.style.cssText='position:absolute;width:0;height:0;overflow:hidden'
  const defs=node('defs'),gradient=node('linearGradient',{id:'v-sheen-'+id,x1:'0',y1:'0',x2:'0',y2:'1'}),pattern=node('pattern',{id:'v-grain-'+id,patternUnits:'userSpaceOnUse',width:'160',height:'160'})
  for(const [offset,color,opacity]of [['0','rgb(255,255,255)','.09'],['.45','rgb(255,255,255)','0'],['1','rgb(0,0,0)','.03']])gradient.append(node('stop',{offset,'stop-color':color,'stop-opacity':opacity}))
  const grain=encodeURIComponent("<svg xmlns='http://www.w3.org/2000/svg' width='160' height='160'><filter id='n'><feTurbulence type='fractalNoise' baseFrequency='.9' numOctaves='2' stitchTiles='stitch'/><feColorMatrix values='0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 .6 0'/></filter><rect width='100%' height='100%' filter='url(#n)'/></svg>")
  pattern.append(node('image',{href:'data:image/svg+xml,'+grain,width:'160',height:'160'}));defs.append(gradient,pattern);shared.append(defs);document.body.prepend(shared)
 }
 users++;const id=serial;let released=false
 return {id,release:()=>{if(released)return;released=true;if(--users===0){shared?.remove();shared=null}}}
}
export function createMorphOverlays(svg:SVGElement){
 const defs=acquireDefs(),sheen=node('path',{'data-morph-overlay':'sheen',fill:`url(#v-sheen-${defs.id})`}),grain=node('path',{'data-morph-overlay':'grain',fill:`url(#v-grain-${defs.id})`})
 grain.style.mixBlendMode='soft-light';svg.append(sheen,grain);let previous=''
 return {paint:(points:number[][],body:Body,cfg:MorphConfig)=>{
  const inset=path(points.map(([x,y],i)=>[x-body.base[i][2]*.75,y-body.base[i][3]*.75]),!!body.base.poly)
  if(inset!==previous){sheen.setAttribute('d',inset);grain.setAttribute('d',inset);previous=inset}
  sheen.style.opacity=String(cfg.sheen);grain.style.opacity=String(cfg.grain)
 },dispose:()=>{sheen.remove();grain.remove();defs.release()}}
}
/** Source RGB interpolation. Six-digit authored hex values are the reference palette format. */
export function morphColor(colors:string[],seconds:number){
 const fraction=(seconds*.35)%colors.length,index=Math.floor(fraction),a=colors[index],b=colors[(index+1)%colors.length]
 const rgb=(color:string)=>[1,3,5].map(i=>parseInt(color.slice(i,i+2),16)),A=rgb(a),B=rgb(b)
 return `rgb(${A.map((v,i)=>Math.round(v+(B[i]-v)*(fraction-index))).join(',')})`
}
