const clamp=(value:number,min:number,max:number)=>Math.max(min,Math.min(max,Number.isFinite(value)?value:min));
const f=(value:number)=>Number(value.toFixed(3));

/** Ratios of the available rail height; value, not speed, stretches the gum. */
export function sliderRubberProfile(extent:number){
 const stretch=clamp(extent,0,1);
 return {center:.08+.7*(1-stretch)**2,attachment:.82};
}

export function sliderRubberPath(width:number,height:number,extent:number,velocity=0){
 const w=Math.max(0,Number.isFinite(width)?width:0),h=Math.max(0,Number.isFinite(height)?height:0);
 if(!w||!h)return "M0 0Z";
 const profile=sliderRubberProfile(extent),r=Math.min(h*profile.attachment/2,w/2),waist=Math.min(h*profile.center/2,r);
 const cy=h/2,middle=w/2,bend=clamp(velocity,-1,1)*waist*.18,k=.55228475;
 const shoulder=Math.min(w*.22,r*2.5),top=cy-waist+bend,bottom=cy+waist+bend;
 return `M${f(r)} ${f(cy-r)}C${f(r+shoulder)} ${f(cy-r)} ${f(middle-shoulder/2)} ${f(top)} ${f(middle)} ${f(top)}C${f(middle+shoulder/2)} ${f(top)} ${f(w-r-shoulder)} ${f(cy-r)} ${f(w-r)} ${f(cy-r)}C${f(w-r+r*k)} ${f(cy-r)} ${f(w)} ${f(cy-r*k)} ${f(w)} ${f(cy)}C${f(w)} ${f(cy+r*k)} ${f(w-r+r*k)} ${f(cy+r)} ${f(w-r)} ${f(cy+r)}C${f(w-r-shoulder)} ${f(cy+r)} ${f(middle+shoulder/2)} ${f(bottom)} ${f(middle)} ${f(bottom)}C${f(middle-shoulder/2)} ${f(bottom)} ${f(r+shoulder)} ${f(cy+r)} ${f(r)} ${f(cy+r)}C${f(r-r*k)} ${f(cy+r)} 0 ${f(cy+r*k)} 0 ${f(cy)}C0 ${f(cy-r*k)} ${f(r-r*k)} ${f(cy-r)} ${f(r)} ${f(cy-r)}Z`;
}

export function sliderScreenVelocity(velocity:number,{orientation="horizontal",dir,inverted=false}:{orientation?:"horizontal"|"vertical";dir?:"ltr"|"rtl";inverted?:boolean}={}){
 const axis=orientation==="vertical"?-1:dir==="rtl"?-1:1;
 return clamp(velocity,-1,1)*axis*(inverted?-1:1);
}

export function sliderThumbPath(orientation:"horizontal"|"vertical",velocity=0){
 const motion=clamp(velocity,-1,1),travel=Math.abs(motion),along=9.5+travel*2.3,across=9.5-travel*.85;
 const cx=12+(orientation==="horizontal"?motion*.65:0),cy=12+(orientation==="vertical"?motion*.65:0);
 const rx=orientation==="horizontal"?along:across,ry=orientation==="vertical"?along:across,k=.55228475;
 return `M${f(cx+rx)} ${f(cy)}C${f(cx+rx)} ${f(cy+ry*k)} ${f(cx+rx*k)} ${f(cy+ry)} ${f(cx)} ${f(cy+ry)}C${f(cx-rx*k)} ${f(cy+ry)} ${f(cx-rx)} ${f(cy+ry*k)} ${f(cx-rx)} ${f(cy)}C${f(cx-rx)} ${f(cy-ry*k)} ${f(cx-rx*k)} ${f(cy-ry)} ${f(cx)} ${f(cy-ry)}C${f(cx+rx*k)} ${f(cy-ry)} ${f(cx+rx)} ${f(cy-ry*k)} ${f(cx+rx)} ${f(cy)}Z`;
}

export function sliderRailPath(width:number,height:number,velocity=0){
 const w=Math.max(0,Number.isFinite(width)?width:0),h=Math.max(0,Number.isFinite(height)?height:0);
 if(!w||!h)return "M0 0Z";
 const direction=clamp(velocity,-1,1),r=Math.min(h*.39,w/2),cy=h/2,bend=direction*h*.12,tail=Math.min(w*.18,h*2.4),k=.55228475;
 return `M${f(r)} ${f(cy-r)}C${f(w*.3)} ${f(cy-r+bend)} ${f(w-tail)} ${f(cy-r-bend)} ${f(w-r)} ${f(cy-r)}C${f(w-r+r*k)} ${f(cy-r)} ${f(w)} ${f(cy-r+r*k)} ${f(w)} ${f(cy)}C${f(w)} ${f(cy+r-r*k)} ${f(w-r+r*k)} ${f(cy+r)} ${f(w-r)} ${f(cy+r)}C${f(w-tail)} ${f(cy+r+bend)} ${f(w*.3)} ${f(cy+r-bend)} ${f(r)} ${f(cy+r)}C${f(r-r*k)} ${f(cy+r)} 0 ${f(cy+r-r*k)} 0 ${f(cy)}C0 ${f(cy-r+r*k)} ${f(r-r*k)} ${f(cy-r)} ${f(r)} ${f(cy-r)}Z`;
}
