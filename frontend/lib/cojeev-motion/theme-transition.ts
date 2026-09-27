"use client";
import { createMotionLane, resolveChoreography } from "@/lib/cojeev-motion/choreography";
import { getSettingsSnapshot, subscribeSettings } from "@/lib/cojeev-motion/settings";

export type ThemeRevealOrigin = { x:number; y:number };
export type ThemeChangeDetails = { origin:ThemeRevealOrigin };
export type ThemeRevealOptions = { origin?:ThemeRevealOrigin };
type ThemeMode = "light" | "dark";
let pendingOrigin: {origin:ThemeRevealOrigin; element:HTMLElement; time:number} | undefined;
let previousZone=-1;

/** Choose corners, edges and centre without repeating the previous region. */
export function variedThemeOrigin(width:number,height:number,random= Math.random,previous=-1) {
  const zones=[[.07,.07],[.93,.07],[.93,.93],[.07,.93],[.06,.5],[.94,.5],[.5,.06],[.5,.94],[.5,.5]];
  const draw=Math.max(0,Math.min(.999999,random()));
  let zone=Math.floor(draw*(previous>=0?8:9));
  if(previous>=0&&zone>=previous)zone++;
  const [x,y]=zones[zone];
  const jitter=()=> (Math.max(0,Math.min(1,random()))-.5)*.07;
  return {origin:{x:Math.max(12,Math.min(width-12,width*(x+jitter()))),y:Math.max(12,Math.min(height-12,height*(y+jitter())))},zone};
}

/** Record a varied origin at activation, never during rendering or hydration. */
export function rememberThemeOrigin(element:HTMLElement):ThemeChangeDetails {
  const root=element.ownerDocument.documentElement;
  const choice=variedThemeOrigin(root.clientWidth,root.clientHeight,Math.random,previousZone);
  const origin=choice.origin;previousZone=choice.zone;
  pendingOrigin={origin,element,time:performance.now()};
  return {origin};
}

/** Twelve smooth lobed segments, in viewport pixels, expanding beyond every corner. */
export function themeRevealPath(progress:number,origin:ThemeRevealOrigin,width:number,height:number,phase=0) {
  const p=Math.max(0,Math.min(1,Number.isFinite(progress)?progress:0));
  const w=Math.max(1,Number.isFinite(width)?width:1),h=Math.max(1,Number.isFinite(height)?height:1);
  const x=Math.max(0,Math.min(w,Number.isFinite(origin.x)?origin.x:w/2)),y=Math.max(0,Math.min(h,Number.isFinite(origin.y)?origin.y:h/2));
  const reach=Math.hypot(Math.max(x,w-x),Math.max(y,h-y))/.72;
  const points=Array.from({length:12},(_,index)=>{
    const angle=index*Math.PI/6;
    const radius=reach*p*(1+.12*Math.sin(angle*3+p*.65+phase)+.065*Math.cos(angle*5-p*.35-phase*.7));
    return {x:x+Math.cos(angle)*radius,y:y+Math.sin(angle)*radius};
  });
  const f=(n:number)=>Number(n.toFixed(2));
  let path=`M${f(points[0].x)} ${f(points[0].y)}`;
  for(let i=0;i<points.length;i++){
    const before=points[(i+11)%12],start=points[i],end=points[(i+1)%12],after=points[(i+2)%12];
    path+=`C${f(start.x+(end.x-before.x)/6)} ${f(start.y+(end.y-before.y)/6)} ${f(end.x-(after.x-start.x)/6)} ${f(end.y-(after.y-start.y)/6)} ${f(end.x)} ${f(end.y)}`;
  }
  return path+"Z";
}

type Reveal = {scope:HTMLElement;from:ThemeMode;to:ThemeMode;requested:ThemeMode;ready:boolean;done:boolean;lane:ReturnType<typeof createMotionLane>;view:ViewTransition|null;finish:()=>void;run:()=>void};
let active:Reveal|undefined;

/** Reveal the destination DOM. Quiet/unsupported clients commit without an overlay. */
export function applyTheme(mode:ThemeMode,quiet=false,root?:HTMLElement,options:ThemeRevealOptions={}) {
  if(typeof document==="undefined")return;
  const scope=root??document.documentElement,doc=scope.ownerDocument,canvas=doc.documentElement;
  const reduced=()=>doc.defaultView?.matchMedia("(prefers-reduced-motion: reduce)").matches??false;
  const isQuiet=()=>quiet||doc.hidden||resolveChoreography(getSettingsSnapshot(),reduced()).quiet;
  if(active?.scope===scope&&!active.done){
    const reveal=active,changed=reveal.requested!==mode;
    reveal.requested=mode;pendingOrigin=undefined;
    if(isQuiet())reveal.finish();
    else if(changed&&reveal.ready)reveal.run();
    return;
  }
  active?.finish();
  if(scope.dataset.mode===mode){pendingOrigin=undefined;return;}
  const activation=pendingOrigin&&performance.now()-pendingOrigin.time<1500?pendingOrigin:undefined;
  const choice=variedThemeOrigin(canvas.clientWidth,canvas.clientHeight,Math.random,previousZone);
  const origin=options.origin??activation?.origin??choice.origin;
  if(!options.origin&&!activation)previousZone=choice.zone;
  const toggle=activation?.element.isConnected?activation.element:undefined;
  pendingOrigin=undefined;
  if(isQuiet()||typeof doc.startViewTransition!=="function"||!CSS.supports("clip-path",'path("M0 0L1 0L1 1Z")')){scope.dataset.mode=mode;return;}
  const from:ThemeMode=scope.dataset.mode==="dark"?"dark":"light";
  const width=canvas.clientWidth,height=canvas.clientHeight;
  const phase=Math.random()*Math.PI*2,duration=1.2+Math.random()*.18;
  const savedStyles=new Map(["--v-theme-clip","--v-theme-glyph","--v-theme-glyph-position","--v-theme-glyph-size","--v-theme-glyph-mask"].map(name=>[name,canvas.style.getPropertyValue(name)]));
  const savedAttributes=new Map(["data-theme-reveal","data-theme-progress","data-theme-origin"].map(name=>[name,canvas.getAttribute(name)]));
  // Include the activated control in the live root view throughout the reveal.
  // A disjoint capsule keeps the glyph moving without relying on named live
  // snapshots, whose new image can be blank in WebKit while the root paints.
  const control=toggle?.getBoundingClientRect();
  const controlPath=control?`M${control.left+control.height/2} ${control.top}H${control.right-control.height/2}Q${control.right} ${control.top} ${control.right} ${control.top+control.height/2}Q${control.right} ${control.bottom} ${control.right-control.height/2} ${control.bottom}H${control.left+control.height/2}Q${control.left} ${control.bottom} ${control.left} ${control.top+control.height/2}Q${control.left} ${control.top} ${control.left+control.height/2} ${control.top}Z`:"";
  const glyph=toggle?.querySelector<SVGSVGElement>("svg:not(.v-morph)");
  const glyphBox=glyph?.getBoundingClientRect();
  // Both the snapshot hole and its live replacement must meet on physical
  // pixels. Fractional edges antialias twice, leaving a light square seam.
  const ratio=doc.defaultView?.devicePixelRatio||1;
  const glyphTile=glyphBox?{
    x:Math.floor(glyphBox.x*ratio)/ratio,
    y:Math.floor(glyphBox.y*ratio)/ratio,
    right:Math.ceil(glyphBox.right*ratio)/ratio,
    bottom:Math.ceil(glyphBox.bottom*ratio)/ratio,
  }:undefined;
  const image=(svg:string)=>`url("data:image/svg+xml,${encodeURIComponent(svg)}")`;
  if(glyphTile){
    const{x,y,right,bottom}=glyphTile,w=right-x,h=bottom-y;
    canvas.style.setProperty("--v-theme-glyph-position",`${x}px ${y}px`);
    canvas.style.setProperty("--v-theme-glyph-size",`${w}px ${h}px`);
    canvas.style.setProperty("--v-theme-glyph-mask",image(`<svg xmlns="http://www.w3.org/2000/svg" width="${width}" height="${height}"><path fill="white" fill-rule="evenodd" d="M0 0H${width}V${height}H0ZM${x} ${y}H${x+w}V${y+h}H${x}Z"/></svg>`));
  }
  const paintGlyph=()=>{
    if(!glyph||!toggle||!glyphBox||!glyphTile)return;
    const face=toggle.querySelector<SVGPathElement>("svg.v-morph [data-morph-body]");
    const paint=face?getComputedStyle(face).fill:getComputedStyle(toggle).backgroundColor;
    const alpha=paint.match(/(?:rgba\([^,]+,[^,]+,[^,]+,|\/)\s*([\d.]+)(%)?\s*\)/);
    const opaque=paint!=="none"&&paint!=="transparent"&&(!alpha||Number(alpha[1])/(alpha[2]?100:1)>=.999)&&(!face||Number(getComputedStyle(face).opacity)>=.999);
    // A transparent control may sit over a gradient or a separate SVG surface.
    // Its tiny tile cannot reconstruct that backdrop. Keep the native snapshot
    // there; only replace the glyph when its face supplies an exact opaque fill.
    if(!opaque){
      canvas.style.removeProperty("--v-theme-glyph");
      canvas.style.removeProperty("--v-theme-glyph-mask");
      return;
    }
    const{x,y,right,bottom}=glyphTile;
    canvas.style.setProperty("--v-theme-glyph-mask",image(`<svg xmlns="http://www.w3.org/2000/svg" width="${width}" height="${height}"><path fill="white" fill-rule="evenodd" d="M0 0H${width}V${height}H0ZM${x} ${y}H${right}V${bottom}H${x}Z"/></svg>`));
    const copy=glyph.cloneNode(true) as SVGSVGElement;
    copy.setAttribute("xmlns","http://www.w3.org/2000/svg");copy.style.color=getComputedStyle(glyph).color;
    copy.setAttribute("width",String(glyphBox.width));copy.setAttribute("height",String(glyphBox.height));
    const tile=doc.createElementNS("http://www.w3.org/2000/svg","svg");
    tile.setAttribute("xmlns","http://www.w3.org/2000/svg");
    tile.setAttribute("width",String(glyphTile.right-glyphTile.x));
    tile.setAttribute("height",String(glyphTile.bottom-glyphTile.y));
    copy.setAttribute("x",String(glyphBox.x-glyphTile.x));
    copy.setAttribute("y",String(glyphBox.y-glyphTile.y));
    const back=doc.createElementNS("http://www.w3.org/2000/svg","rect");back.setAttribute("width","100%");back.setAttribute("height","100%");
    back.setAttribute("fill",paint);
    tile.append(back,copy);canvas.style.setProperty("--v-theme-glyph",image(tile.outerHTML));
  };
  let unsubscribe=()=>{};
  const media=doc.defaultView!.matchMedia("(prefers-reduced-motion: reduce)");
  const interrupt=()=>reveal.finish();
  const quietChanged=()=>{if(isQuiet())reveal.finish();};
  const lane=createMotionLane(0,value=>{
    if(reveal.done)return;
    // The root clip is live in both engines. Repaint this tiny existing glyph
    // from Motion's DOM values too, because WebKit freezes captured SVG content.
    paintGlyph();
    canvas.style.setProperty("--v-theme-clip",`path("${themeRevealPath(value,origin,width,height,phase)}${controlPath}")`);
    canvas.setAttribute("data-theme-progress",String(Number(value.toFixed(4))));
  });
  const reveal:Reveal={scope,from,to:mode,requested:mode,ready:false,done:false,lane,view:null,
    finish(){
      if(reveal.done)return;
      reveal.done=true;lane.dispose();scope.dataset.mode=reveal.requested;reveal.view?.skipTransition();
      unsubscribe();media.removeEventListener("change",quietChanged);doc.removeEventListener("visibilitychange",quietChanged);
      doc.removeEventListener("scroll",interrupt,true);doc.defaultView?.removeEventListener("resize",interrupt);
      if(active===reveal){
        for(const[name,value]of savedStyles){if(value)canvas.style.setProperty(name,value);else canvas.style.removeProperty(name);}
        for(const[name,value]of savedAttributes){if(value===null)canvas.removeAttribute(name);else canvas.setAttribute(name,value);}
        active=undefined;
      }
    },
    run(){
      const target=reveal.requested===reveal.to?1:0;
      lane.to(target,{duration:duration/Math.max(.25,getSettingsSnapshot().flow.speed),ease:[.42,0,.18,1]},()=>reveal.finish());
    },
  };
  active=reveal;canvas.setAttribute("data-theme-reveal",mode);canvas.setAttribute("data-theme-origin",`${origin.x} ${origin.y}`);lane.jump(0);
  unsubscribe=subscribeSettings(quietChanged);media.addEventListener("change",quietChanged);doc.addEventListener("visibilitychange",quietChanged);
  doc.addEventListener("scroll",interrupt,{capture:true,passive:true});doc.defaultView?.addEventListener("resize",interrupt,{passive:true});
  try{
    reveal.view=doc.startViewTransition(()=>{if(!reveal.done)scope.dataset.mode=reveal.to;});
    void reveal.view.ready.then(()=>{if(!reveal.done){reveal.ready=true;if(isQuiet())reveal.finish();else reveal.run();}},()=>reveal.finish());
    void reveal.view.finished.then(()=>reveal.finish(),()=>reveal.finish());
  }catch{reveal.finish();}
}
