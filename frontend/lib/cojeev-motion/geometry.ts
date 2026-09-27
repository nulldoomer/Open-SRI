export type Spring = {x:number;v:number;to:number;k:number;z:number}
export type Rim = number[][] & {per:number;poly?:boolean}
// Authored rounded rim, Catmull-Rom and fixed 4ms integrator.
export function rim(w:number,h:number,r:number,step:number){const sw=Math.max(0,w-2*r),sh=Math.max(0,h-2*r),qa=Math.PI*r/2,per=2*sw+2*sh+4*qa,n=Math.max(64,Math.min(320,Math.round(per/step))),out=[] as unknown as Rim;
for(let i=0;i<n;i++){let d=(i/n)*per;const arc=d;let x,y,nx,ny;
if(d<sw){x=r+d;y=0;nx=0;ny=-1}else if((d-=sw)<qa){const a=-Math.PI/2+d/r;x=w-r+r*Math.cos(a);y=r+r*Math.sin(a);nx=Math.cos(a);ny=Math.sin(a)}
else if((d-=qa)<sh){x=w;y=r+d;nx=1;ny=0}else if((d-=sh)<qa){const a=d/r;x=w-r+r*Math.cos(a);y=h-r+r*Math.sin(a);nx=Math.cos(a);ny=Math.sin(a)}
else if((d-=qa)<sw){x=w-r-d;y=h;nx=0;ny=1}else if((d-=sw)<qa){const a=Math.PI/2+d/r;x=r+r*Math.cos(a);y=h-r+r*Math.sin(a);nx=Math.cos(a);ny=Math.sin(a)}
else if((d-=qa)<sh){x=0;y=h-r-d;nx=-1;ny=0}else{d-=sh;const a=Math.PI+d/r;x=r+r*Math.cos(a);y=r+r*Math.sin(a);nx=Math.cos(a);ny=Math.sin(a)}
out.push([x,y,nx,ny,arc])}out.per=per;return out}
const f2=(v:number)=>v.toFixed(2);
// Catmull-Rom for rounded bodies (dense → exact arcs); straight polyline for true shapes (keeps sharp tips)
export function path(p:number[][],poly=false){const n=p.length;if(poly){let d='M'+f2(p[0][0])+' '+f2(p[0][1]);for(let i=1;i<n;i++)d+='L'+f2(p[i][0])+' '+f2(p[i][1]);return d+'Z'}
let d='';for(let i=0;i<n;i++){const p0=p[(i-1+n)%n],p1=p[i],p2=p[(i+1)%n],p3=p[(i+2)%n];d+=(i?'':`M${f2(p1[0])} ${f2(p1[1])}`)+`C${f2(p1[0]+(p2[0]-p0[0])/6)} ${f2(p1[1]+(p2[1]-p0[1])/6)} ${f2(p2[0]-(p3[0]-p1[0])/6)} ${f2(p2[1]-(p3[1]-p1[1])/6)} ${f2(p2[0])} ${f2(p2[1])}`}return d+'Z'}
const SETTLE=.0008;/* below this the spring is snapped to target and its velocity zeroed */
export const spring=(s:Spring,dt:number)=>{const k=s.k||120,c=2*Math.sqrt(k)*(s.z??1);let t=Math.min(dt,.1);while(t>0){const h=Math.min(.004,t);s.v+=((s.to-s.x)*k-s.v*c)*h;s.x+=s.v*h;t-=h}if(Math.abs(s.to-s.x)<SETTLE&&Math.abs(s.v)<SETTLE){s.x=s.to;s.v=0}};
