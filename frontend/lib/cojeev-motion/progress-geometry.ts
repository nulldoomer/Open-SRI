const bounded=(value:number,min:number,max:number)=>Math.max(min,Math.min(max,Number.isFinite(value)?value:min));
const f=(value:number)=>Number(value.toFixed(3));
export function progressRatio(value:number,max=100){return bounded(value/((Number.isFinite(max)&&max>0)?max:100),0,1)}
export function organicBandPath(width:number,height:number,ratio=1,energy=0){
 const w=Math.max(0,Number.isFinite(width)?width:0),h=Math.max(0,Number.isFinite(height)?height:0),end=w*bounded(ratio,0,1);
 if(!end||!h)return "M0 0Z";
 const cy=h/2,r=Math.min(h*.4,end/2),s=bounded(energy,-1,1),top=cy-r,bottom=cy+r,waist=Math.min(end*.26,h*1.4),bend=s*r*.22;
 return `M${f(r)} ${f(top)}C${f(end*.32)} ${f(top+bend)} ${f(end-waist)} ${f(top-bend)} ${f(end-r)} ${f(top)}C${f(end-r*.35)} ${f(top)} ${f(end)} ${f(cy-r*.6)} ${f(end)} ${f(cy)}C${f(end)} ${f(cy+r*.6)} ${f(end-r*.35)} ${f(bottom)} ${f(end-r)} ${f(bottom)}C${f(end-waist)} ${f(bottom+bend)} ${f(end*.32)} ${f(bottom-bend)} ${f(r)} ${f(bottom)}C${f(r*.35)} ${f(bottom)} 0 ${f(cy+r*.6)} 0 ${f(cy)}C0 ${f(cy-r*.6)} ${f(r*.35)} ${f(top)} ${f(r)} ${f(top)}Z`;
}
export function organicLinePath(width:number,height:number,ratio=1,energy=0){
 const w=Math.max(0,Number.isFinite(width)?width:0),h=Math.max(0,Number.isFinite(height)?height:0),end=w*bounded(ratio,0,1),y=h/2,b=bounded(energy,-1,1)*h*.22;
 return `M0 ${f(y)}C${f(end*.25)} ${f(y-b)} ${f(end*.35)} ${f(y+b)} ${f(end*.5)} ${f(y)}C${f(end*.7)} ${f(y-b)} ${f(end*.85)} ${f(y+b)} ${f(end)} ${f(y)}`;
}
export function organicThumbPath(engagement=0,energy=0){
 const e=bounded(engagement,0,1),speed=bounded(energy,-1,1),points=Array.from({length:8},(_,i)=>{const a=i*Math.PI/4,r=9.2+Math.sin(a*3+.35)*(.55+e*.6)+Math.cos(a*2)*speed*.6;return{x:12+Math.cos(a)*r,y:12+Math.sin(a)*r*(1-e*.1)}});
 let d=`M${f(points[0].x)} ${f(points[0].y)}`;
 for(let i=0;i<8;i++){const p=points[(i+7)%8],a=points[i],b=points[(i+1)%8],n=points[(i+2)%8];d+=`C${f(a.x+(b.x-p.x)/6)} ${f(a.y+(b.y-p.y)/6)} ${f(b.x-(n.x-a.x)/6)} ${f(b.y-(n.y-a.y)/6)} ${f(b.x)} ${f(b.y)}`}
 return d+"Z";
}
export function organicOrbitPath(ratio=1,energy=0){
 const p=bounded(ratio,0,1),e=bounded(energy,-1,1),count=12,points=Array.from({length:count+1},(_,i)=>{const a=-Math.PI/2+i/count*p*Math.PI*2,r=40+Math.sin(a*3)*e*1.5;return{x:50+Math.cos(a)*r,y:50+Math.sin(a)*r}});
 let d=`M${f(points[0].x)} ${f(points[0].y)}`;
 for(let i=0;i<count;i++){const a=points[i],b=points[i+1],before=i?points[i-1]:{x:a.x-(b.x-a.x),y:a.y-(b.y-a.y)},after=i<count-1?points[i+2]:{x:b.x+(b.x-a.x),y:b.y+(b.y-a.y)};d+=`C${f(a.x+(b.x-before.x)/6)} ${f(a.y+(b.y-before.y)/6)} ${f(b.x-(after.x-a.x)/6)} ${f(b.y-(after.y-a.y)/6)} ${f(b.x)} ${f(b.y)}`}
 return d;
}
export function segmentCount(value=12){return Math.round(bounded(value,3,32))}
