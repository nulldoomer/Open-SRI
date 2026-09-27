import { signatureShapePaths, type SignatureShapeName } from "@/lib/cojeev/signature-shapes";

export type OrganismKind = "profile" | "side-panel" | "dock" | "chat" | "focus" | "invite" | "dashboard";
export type AssemblyPartId = "surface" | "cover" | "avatar" | "badge" | "identity" | "stats" | "composer" | "primary" | "secondary" | "save" | "thread" | "progress" | "caption" | "participants" | `task-${number}` | `tool-${number}`;
export type AssemblyRect = { x:number; y:number; width:number; height:number; rotate?:number };
export type AssemblyContour = SignatureShapeName | "rounded" | "circle";
export const organismChoices: {value:OrganismKind;label:string;icon:string}[] = [
  {value:"profile",label:"Profile",icon:"user"}, {value:"side-panel",label:"Panel",icon:"panel-left"},
  {value:"dock",label:"Dock",icon:"layout-grid"}, {value:"chat",label:"Chat",icon:"message-circle"},
  {value:"focus",label:"Focus",icon:"timer"}, {value:"invite",label:"Invite",icon:"calendar"},
];
const shapes: AssemblyContour[] = ["scalloped-square","daisy-12","ribbon-soft","cloud-3","seed-wing","clover-soft","petal-7","cushion"];
export const floatingContour = (part:string):AssemblyContour => shapes[Array.from(part).reduce((sum,char)=>sum+char.charCodeAt(0),0)%shapes.length];
export type AssemblyGeometry = {height:number;parts:Partial<Record<AssemblyPartId,AssemblyRect>>};
export type AssemblyGeometryOptions = {profileComposer?:boolean;itemCount?:number};

/** Matching 96-point topology: the actual native surface can keep one continuous contour. */
export function assemblyContour(contour:AssemblyContour, width:number, height:number, radius=16):string {
  let points: number[][];
  if(contour!=="rounded"&&contour!=="circle") {
    const numbers=signatureShapePaths[contour].match(/-?\d+(?:\.\d+)?/g)!.map(Number);
    points=Array.from({length:96},(_,i)=>i===0?numbers.slice(0,2):numbers.slice(2+(i-1)*6+4,2+i*6));
  } else {
    const w=Math.max(1,width)/2,h=Math.max(1,height)/2,r=Math.min(radius,w,h);
    points=Array.from({length:96},(_,i)=>{
      const angle=i/96*Math.PI*2,dx=Math.cos(angle),dy=Math.sin(angle);
      if(contour==="circle")return[50+49.8*dx,50+49.8*dy];
      // Each corner keeps eight samples even on a tall panel or a very wide input.
      const quarter=Math.floor(i/24),u=(i%24)/8,W=quarter%2?h:w,H=quarter%2?w:h;
      const point=u<1?[W,(H-r)*u]:u<2?[W-r+r*Math.cos((u-1)*Math.PI/2),H-r+r*Math.sin((u-1)*Math.PI/2)]:[(W-r)*(3-u),H];
      const turn=quarter*Math.PI/2,px=point[0]*Math.cos(turn)-point[1]*Math.sin(turn),py=point[0]*Math.sin(turn)+point[1]*Math.cos(turn);
      return[50+px/w*49.8,50+py/h*49.8];
    });
  }
  return `polygon(${points.map(([x,y])=>`${x.toFixed(3)}% ${y.toFixed(3)}%`).join(",")})`;
}

/** Distinct native compositions, measured in the local stage rather than the viewport. */
export function organismGeometry(kind:OrganismKind, available:number, scattered=false, options:AssemblyGeometryOptions={}):AssemblyGeometry {
  const width=Math.max(240,Number.isFinite(available)?available:360);
  const frameWidth=Math.min(width-24,kind==="side-panel"||kind==="dashboard"?356:kind==="dock"?400:kind==="profile"||kind==="focus"?364:kind==="invite"?384:416);
  const left=(width-frameWidth)/2,pad=20,inside=frameWidth-pad*2,x=left+pad;
  const rect=(x:number,y:number,width:number,height:number,rotate=0):AssemblyRect=>({x,y,width,height,rotate});
  const parts:AssemblyGeometry["parts"]={};
  const count=Math.max(0,Math.min(4,Math.floor(Number.isFinite(options.itemCount)?options.itemCount!:3)));
  let height=454;
  if(kind==="profile"){
    height=options.profileComposer?510:454;
    Object.assign(parts,{
      surface:rect(left,20,frameWidth,height-44),cover:rect(left+8,28,frameWidth-16,124),
      avatar:rect(x,118,72,72),badge:rect(x+inside-104,168,104,24),
      identity:rect(x,212,inside,64),stats:rect(x,294,inside,40),
      primary:rect(x,356,(inside-10)/2,40),secondary:rect(x+(inside+10)/2,356,(inside-10)/2,40),
      save:rect(left+frameWidth-56,44,32,32),
    });
    if(options.profileComposer)parts.composer=rect(x,418,inside,52);
  }else if(kind==="dock"){
    height=262;
    const toolCount=count,gap=8,toolSize=Math.min(68,(frameWidth-80-Math.max(0,count-1)*gap)/Math.max(1,count));
    const lead=52,contentWidth=lead+toolCount*toolSize+Math.max(0,toolCount-1)*gap;
    const start=(width-contentWidth)/2;
    parts.identity=rect(left,40,frameWidth,32);parts.surface=rect(left,92,frameWidth,96);parts.avatar=rect(start,119,40,40);
    for(let index=0;index<toolCount;index++)parts[`tool-${index}`]=rect(start+lead+index*(toolSize+gap),106,toolSize,68);
    parts.caption=rect(left,208,frameWidth,34);
  }else if(kind==="chat"){
    height=528;
    Object.assign(parts,{
      surface:rect(left,20,frameWidth,484),avatar:rect(x,46,40,40),
      identity:rect(x+52,43,inside-92,48),save:rect(x+inside-32,48,32,32),
      badge:rect(x,104,inside,24),thread:rect(x,144,inside,242),composer:rect(x,411,inside,52),caption:rect(x,477,inside,16),
    });
  }else if(kind==="focus"){
    height=448;
    Object.assign(parts,{
      surface:rect(left,20,frameWidth,404),identity:rect(x,48,inside-64,70),cover:rect(x+inside-60,44,60,76),
      stats:rect(x,146,inside,88),progress:rect(x,259,inside,12),badge:rect(x,285,inside,40),
      primary:rect(x,342,inside-54,44),secondary:rect(x+inside-44,342,44,44),caption:rect(x,398,inside,16),
    });
  }else if(kind==="invite"){
    height=538;
    Object.assign(parts,{
      surface:rect(left,20,frameWidth,494),cover:rect(left+8,28,frameWidth-16,116),
      identity:rect(x,166,inside,88),stats:rect(x,271,inside,56),participants:rect(x,349,inside,40),
      caption:rect(x,409,inside,22),primary:rect(x,452,(inside-10)/2,44),secondary:rect(x+(inside+10)/2,452,(inside-10)/2,44),
    });
  }else{
    height=304+count*74;
    Object.assign(parts,{
      surface:rect(left,20,frameWidth,height-44),identity:rect(x,48,inside-70,70),cover:rect(x+inside-62,44,62,84),
      badge:rect(x,140,inside,24),progress:rect(x,184+count*74,inside,26),
      secondary:rect(x,218+count*74,inside,40),
    });
    for(let index=0;index<count;index++)parts[`task-${index}`]=rect(x,178+index*74,inside,64);
  }
  if(scattered){
    const entries=Object.keys(parts) as AssemblyPartId[];
    const stageHeight=Math.max(height,360),unit=Math.min(width*.21,stageHeight*.14,82);
    // Authored, uneven anchors retain identity without hydration randomness or a visible grid.
    const anchors=[
      [.23,.15,.96,-12],[.70,.13,.70,14],[.50,.34,1.20,-9],
      [.13,.44,.80,8],[.84,.48,.90,-17],[.29,.66,.67,13],
      [.70,.72,1,-8],[.13,.88,.73,18],[.57,.91,.84,-11],[.85,.90,.64,9],
    ];
    entries.forEach((part,index)=>{
      const [cx,cy,scale,rotation]=anchors[index],size=unit*scale;
      parts[part]=rect(width*cx-size/2,stageHeight*cy-size/2,size,size,rotation);
    });
    height=stageHeight;
  }
  return{height,parts};
}
