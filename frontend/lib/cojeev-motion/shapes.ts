import type {Rim} from "@/lib/cojeev-motion/geometry"
import { signatureShapePaths } from "@/lib/cojeev/signature-shapes"
// Authored true shapes, sampled by arc length using a detached SVG probe.
const star=(n:number,ro:number,ri:number)=>{const a=[];for(let i=0;i<n*2;i++){const r=i%2?ri:ro,t=-Math.PI/2+i*Math.PI/n;a.push([50+r*Math.cos(t),50+r*Math.sin(t)])}return 'M'+a.map(p=>p.map(v=>v.toFixed(2)).join(' ')).join('L')+'Z'};
const polar=(f:(th:number)=>number)=>(t:number)=>{const th=t*Math.PI*2;const r=f(th);return [50+r*Math.cos(th),50+r*Math.sin(th)]};
export const SHAPES:Record<string,string|((t:number)=>number[])>={
...signatureShapePaths,
heart:'M50 90C22 68 6 52 6 33A20 20 0 0 1 50 21A20 20 0 0 1 94 33C94 52 78 68 50 90Z',
crescent:'M60 5A45 45 0 1 0 60 95A50 50 0 0 1 60 5Z',
'star-8':star(8,48,30),'star-5':star(5,48,22),'star-6':star(6,48,28),
'star-4':'M50 2Q58 42 98 50Q58 58 50 98Q42 58 2 50Q42 42 50 2Z',
'blob-4':'M50 4C62 4 70 14 70 24C70 30 66 34 66 34C66 34 70 30 76 30C86 30 96 38 96 50C96 62 86 70 76 70C70 70 66 66 66 66C66 66 70 70 70 76C70 86 62 96 50 96C38 96 30 86 30 76C30 70 34 66 34 66C34 66 30 70 24 70C14 70 4 62 4 50C4 38 14 30 24 30C30 30 34 34 34 34C34 34 30 30 30 24C30 14 38 4 50 4Z',
cross:'M36 4H64Q68 4 68 8V32H92Q96 32 96 36V64Q96 68 92 68H68V92Q68 96 64 96H36Q32 96 32 92V68H8Q4 68 4 64V36Q4 32 8 32H32V8Q32 4 36 4Z',
droplet:'M50 4C50 4 12 46 12 64A38 38 0 0 0 88 64C88 46 50 4 50 4Z',
circle:'M50 4A46 46 0 1 1 49.99 4Z',
triangle:'M46 10Q50 4 54 10L94 84Q97 90 90 90H10Q3 90 6 84Z',
hex:'M25 6.7L75 6.7L100 50L75 93.3L25 93.3L0 50Z',
'flower-5':polar(th=>36+10*Math.cos(5*th)),'flower-6':polar(th=>36+10*Math.cos(6*th)),'flower-8':polar(th=>38+8*Math.cos(8*th)),
'splat-4':polar(th=>33+13*Math.cos(4*th)+2*Math.cos(8*th)),'splat-5':polar(th=>34+12*Math.cos(5*th)),
scallop:polar(th=>44.5+3*Math.cos(16*th)),pebble:polar(th=>42+3*Math.cos(3*th+.6)+2*Math.sin(5*th)),
squircle:t=>{const th=t*Math.PI*2,c=Math.cos(th),s=Math.sin(th);return [50+46*Math.sign(c)*Math.pow(Math.abs(c),.5),50+46*Math.sign(s)*Math.pow(Math.abs(s),.5)]},
egg:t=>{const th=t*Math.PI*2;return [50+38*Math.cos(th)*(1-.16*Math.sin(th)),50+44*Math.sin(th)]},
bean:t=>{const th=t*Math.PI*2,s=Math.sin(th);return [50+42*Math.cos(th),50-(26*s+14*Math.cos(2*th)*(1+s)/2)]},
leaf:t=>{const th=t*Math.PI*2,s=Math.sin(th),x=46*Math.cos(th),y=28*s*Math.pow(Math.abs(s),.45),c=Math.SQRT1_2;return [50+(x-y)*c,50+(x+y)*c]}};
export const shapeNames=Object.keys(SHAPES);
// dense raw polyline for a shape (≈600 points)
function rawShape(name:string){const s=SHAPES[name];const N=600,out=[];if(typeof s==='function'){for(let i=0;i<N;i++)out.push(s(i/N));return out}const pr=document.createElementNS('http://www.w3.org/2000/svg','path');pr.setAttribute('d',s);const L=pr.getTotalLength();for(let i=0;i<N;i++){const p=pr.getPointAtLength(L*i/N);out.push([p.x,p.y])}return out}
// resample a closed polyline by arc length into n points, fitted (contain, centred) into w×h; returns [x,y,nx,ny,arc] + per
export function fromShape(name:string,w:number,h:number,step:number){const raw=rawShape(name);let minx=1e9,miny=1e9,maxx=-1e9,maxy=-1e9;for(const [x,y] of raw){if(x<minx)minx=x;if(y<miny)miny=y;if(x>maxx)maxx=x;if(y>maxy)maxy=y}const sc=Math.min(w/(maxx-minx),h/(maxy-miny)),ox=(w-(maxx-minx)*sc)/2-minx*sc,oy=(h-(maxy-miny)*sc)/2-miny*sc;const pts=raw.map(([x,y])=>[x*sc+ox,y*sc+oy]);
const N=pts.length,cum=[0];for(let i=1;i<=N;i++){const a=pts[i-1],b=pts[i%N];cum.push(cum[i-1]+Math.hypot(b[0]-a[0],b[1]-a[1]))}const per=cum[N];const n=Math.max(48,Math.min(420,Math.round(per/step)));const out=[] as unknown as Rim;let j=0;
for(let i=0;i<n;i++){const d=per*i/n;while(j<N-1&&cum[j+1]<d)j++;const a=pts[j],b=pts[(j+1)%N],t=(d-cum[j])/((cum[j+1]-cum[j])||1);out.push([a[0]+(b[0]-a[0])*t,a[1]+(b[1]-a[1])*t,0,0,d])}
// normals from neighbours; orient outward using the centroid
let cx=0,cy=0;for(const p of out){cx+=p[0];cy+=p[1]}cx/=n;cy/=n;let sgn=0;for(let i=0;i<n;i++){const a=out[(i-1+n)%n],b=out[(i+1)%n];const tx=b[0]-a[0],ty=b[1]-a[1];const L=Math.hypot(tx,ty)||1;const nx=ty/L,ny=-tx/L;out[i][2]=nx;out[i][3]=ny;sgn+=nx*(out[i][0]-cx)+ny*(out[i][1]-cy)}if(sgn<0)for(const p of out){p[2]=-p[2];p[3]=-p[3]}
out.per=per;out.poly=true;return out}
