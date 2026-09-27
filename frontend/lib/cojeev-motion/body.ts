import {rim,path,spring,type Spring,type Rim} from "@/lib/cojeev-motion/geometry"
import type {Tier,TierName,MorphConfig} from "@/lib/cojeev-motion/settings"
type BodyFrame={d:string;points:number[][];press:number;active:boolean}
const drawnFrames=new WeakMap<Body,{at:number;frame:BodyFrame}>()
export type Body = {
 tier:Tier;tierName:TierName;seed:number;w:number;h:number;base:Rim;R:{left:number;top:number;right:number;bottom:number;width:number;height:number};
 lobe:Spring;arc:Spring;hold:Spring;press:Spring;dirS:{x:Spring;y:Spring};dir:{x:number;y:number};
 ripple:number;jiggle:number;jArc:number;mergeT:number;inside:boolean;near:boolean;focus:boolean;
 lob:null|{n:number;a:number;L:{c:number;a:number;w:number}[]}
}
const scalar=(k:number,z=1):Spring=>({x:0,v:0,to:0,k,z})
export function createBody(tier:Tier,tierName:TierName,seed=0):Body{return {tier,tierName,seed,w:0,h:0,base:rim(1,1,.5,2.5),R:{left:0,top:0,right:1,bottom:1,width:1,height:1},lobe:scalar(90,.9),arc:scalar(26),hold:scalar(55),press:scalar(140,.9),dirS:{x:scalar(40),y:scalar(40)},dir:{x:0,y:-1},ripple:0,jiggle:0,jArc:0,mergeT:0,inside:false,near:false,focus:false,lob:null}}
export function rewindBody(b:Body){for(const s of [b.lobe,b.arc,b.hold,b.press,b.dirS.x,b.dirS.y]){s.x=s.v=s.to=0}b.jiggle=b.ripple=b.mergeT=0;b.inside=b.near=b.focus=false;b.lob=null;drawnFrames.delete(b)}
export function bodyPadding(tier:Tier,cfg:MorphConfig,w:number,h:number){const m=Math.min(w,h);return Math.ceil(tier.reach+(tier.press+tier.depth*1.6)*m+(cfg.echo?cfg.echoOff+m*Math.abs(cfg.echoScale-1)+2:0)+(cfg.dots?16:0))+6}
export function stepBody(b:Body,P:{x:number;y:number},dt:number,T:number,cfg:MorphConfig,RM=false,throttleRest=false):BodyFrame{
const tier=b.tier,base=b.base,per=base.per,m=Math.min(b.w,b.h);
const R=b.R!;const sx=b.w/R.width,sy=b.h/R.height;const cx=(P.x-R.left)*sx,cy=(P.y-R.top)*sy;const inBox=P.x>R.left-tier.R&&P.x<R.right+tier.R&&P.y>R.top-tier.R&&P.y<R.bottom+tier.R;const has=P.x>-1e3&&!RM&&tier.reach>0&&inBox;
let ni=0,nd=1e9;if(has){for(let i=0;i<base.length;i+=2){const q=base[i];const dd=(q[0]-cx)**2+(q[1]-cy)**2;if(dd<nd){nd=dd;ni=i}}}
const ins=has&&cx>0&&cx<b.w&&cy>0&&cy<b.h;const dist=Math.sqrt(nd);const near=has&&!ins&&dist<tier.R;
if(has&&(near||ins)){const q=base[ni];let da=q[4]-b.arc.x;da-=Math.round(da/per)*per;b.arc.to=b.arc.x+da;const dx=cx-q[0],dy=cy-q[1],L=Math.hypot(dx,dy)||1;b.dirS.x.to=dx/L;b.dirS.y.to=dy/L}
b.lobe.to=(near&&cfg.reach)?tier.reach*Math.pow(.5*(1-Math.cos(Math.PI*(1-dist/tier.R))),cfg.curve):0;
b.arc.k=cfg.arcK;b.hold.k=cfg.holdK;b.press.k=b.press.to?260:cfg.pressK;if(b.mergeT>0){b.mergeT-=dt;b.lobe.z=cfg.mergeZ;b.lobe.k=105}else{b.lobe.z=cfg.lobeZ;b.lobe.k=cfg.lobeK}
b.hold.to=cfg.hold?(ins?tier.inside*Math.max(.35,1-Math.min(dist,60)/80):(b.focus?tier.inside*.6:0)):0;
if(ins&&!b.inside&&cfg.merge)b.mergeT=.6;if(b.near&&!near&&!ins&&!RM&&cfg.jiggleOn){b.jiggle=1;b.jArc=b.arc.x}
b.inside=ins;b.near=near;
const active=near||ins||b.lobe.x>.02||b.hold.x>.02||b.press.x>.002||b.press.to||b.ripple>0||b.jiggle>0||b.mergeT>0;/* Rest breath no longer keeps the loop alive: it only modulates a body that is ALREADY awake through
     reach, hover, press or an explicit decorative blob. A settled page redraws nothing. */
// The reference checks current spring values before integrating. In particular,
// a newly focused idle body begins its hold on the next 250ms rest refresh.
const cached=drawnFrames.get(b),moving=tier.depth&&tier.lobes&&cfg.drift&&(near||ins)
if(throttleRest&&!active&&!moving&&cached&&T*1000-cached.at<250)return {...cached.frame,active:false}
if(b.jiggle>0)b.jiggle=Math.max(0,b.jiggle-dt*cfg.jiggleDecay);if(b.ripple>0)b.ripple=Math.max(0,b.ripple-dt*1.25);
spring(b.lobe,dt);spring(b.arc,dt);spring(b.hold,dt);spring(b.press,dt);spring(b.dirS.x,dt);spring(b.dirS.y,dt);const dl=Math.hypot(b.dirS.x.x,b.dirS.y.x)||1;b.dir={x:b.dirS.x.x/dl,y:b.dirS.y.x/dl};
const idle=(RM||!cfg.rest)?0:tier.amp*m,lobe=b.lobe.x,hold=b.hold.x,pr=b.press.x,rip=b.ripple,jg=b.jiggle;const isSpin=b.tierName==='spinner';const sig2=2*tier.sig*tier.sig;const pa=Math.atan2(cy-b.h/2,(cx-b.w/2)*(b.h/b.w));
if(tier.depth&&tier.lobes&&(!b.lob||b.lob.n!==tier.lobes||b.lob.a!==tier.asym)){const L=[];let s=b.seed;const rnd=()=>{s=(s*9301+49297)%233280;return s/233280};for(let j=0;j<tier.lobes;j++)L.push({c:j/tier.lobes+(rnd()-.5)*tier.asym*.5/tier.lobes,a:1+(rnd()-.5)*tier.asym*.9,w:1+(rnd()-.5)*tier.asym*.6});b.lob={n:tier.lobes,a:tier.asym,L}}
const dr=RM?0:T*.05*cfg.drift,armSig=tier.lobes?(tier.spread||.55)/tier.lobes*.5:0;
const N=base.length,pts=new Array<number[]>(N);
for(let i=0;i<N;i++){const q=base[i],x=q[0],y=q[1],nx=q[2],ny=q[3],arc=q[4],u=arc/per;
 let k=idle*(Math.sin(u*Math.PI*2*(isSpin?3:2)+T*.38*cfg.restSpeed+b.seed)*.6+Math.sin(u*Math.PI*2*3-T*.26*cfg.restSpeed+b.seed*1.3)*.4);
 if(armSig){let bump=-.35;for(const l of b.lob!.L){let du=u-l.c-dr*(1+l.a*.2);du-=Math.round(du);const s2=armSig*l.w;bump+=l.a*Math.exp(-(du*du)/(2*s2*s2))}k+=tier.depth*m*bump}
 let da=arc-b.arc.x;da-=Math.round(da/per)*per;const g=Math.exp(-(da*da)/sig2);
 k+=hold*(g*1.2-.15);
 if(pr&&cfg.press){const a=Math.atan2(ny,nx);const c=Math.cos(a-pa);k+=pr*tier.press*m*(-.9*c*c+.5*(1-c*c))}
 if(rip)k+=Math.sin(u*Math.PI*2*2-(1-rip)*Math.PI*4)*rip*rip*tier.press*m*.6;
 if(jg){let dj=arc-b.jArc;dj-=Math.round(dj/per)*per;const gj=Math.exp(-(dj*dj)/sig2);const t=1-jg;k+=gj*tier.reach*cfg.jiggle*Math.exp(-t*3.2)*Math.cos(t*Math.PI*3.4)}
 const lx=lobe*g*b.dir.x,ly=lobe*g*b.dir.y;const px=x+nx*k+lx,py=y+ny*k+ly;pts[i]=[px,py];}

const output={d:path(pts,!!base.poly),points:pts,press:pr,active:!!active};drawnFrames.set(b,{at:T*1000,frame:output});return output
}
