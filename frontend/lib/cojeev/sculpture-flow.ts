import { sculptureNumber } from "@/lib/cojeev/sculpture-raster";
export type VelocityField = { width:number; height:number; x:Float32Array; y:Float32Array; nextX:Float32Array; nextY:Float32Array; texture:Uint8Array; energy:number };
export function createVelocityField(width=80,height=60):VelocityField {
  width=Math.round(sculptureNumber(width,80,24,96));height=Math.round(sculptureNumber(height,60,18,72));
  const length=width*height,field={width,height,x:new Float32Array(length),y:new Float32Array(length),nextX:new Float32Array(length),nextY:new Float32Array(length),texture:new Uint8Array(length*4),energy:0};
  clearVelocityField(field);return field;
}
export function clearVelocityField(field:VelocityField) {
  field.x.fill(0);field.y.fill(0);field.nextX.fill(0);field.nextY.fill(0);field.energy=0;
  for(let i=0;i<field.texture.length;i+=4){field.texture[i]=128;field.texture[i+1]=128;field.texture[i+2]=0;field.texture[i+3]=255;}
}
/** A local smooth impulse; advection evolves it, rather than a persistent cursor mask. */
export function stirVelocityField(field:VelocityField,x:number,y:number,dx:number,dy:number,radius=.12,swirl=.35) {
  if(![x,y,dx,dy].every(Number.isFinite))return;
  x=Math.max(0,Math.min(1,x));y=Math.max(0,Math.min(1,y));radius=sculptureNumber(radius,.12,.035,.3);swirl=sculptureNumber(swirl,.35,0,1);
  dx=Math.max(-1,Math.min(1,dx));dy=Math.max(-1,Math.min(1,dy));
  const aspect=field.width/field.height,force=Math.hypot(dx,dy),size=radius*radius;
  for(let row=1;row<field.height-1;row++)for(let column=1;column<field.width-1;column++){
    const ox=(column/(field.width-1)-x)*aspect,oy=row/(field.height-1)-y,r2=ox*ox+oy*oy;
    if(r2>size*9)continue;
    const weight=Math.exp(-r2/(size*.65)),i=row*field.width+column;
    field.x[i]=Math.max(-1,Math.min(1,field.x[i]+(dx-oy/radius*force*swirl)*weight));
    field.y[i]=Math.max(-1,Math.min(1,field.y[i]+(dy+ox/radius*force*swirl)*weight));
  }
  field.energy=Math.max(field.energy,force);
}
function sample(values:Float32Array,width:number,height:number,x:number,y:number){
  x=Math.max(0,Math.min(width-1.001,x));y=Math.max(0,Math.min(height-1.001,y));
  const left=Math.floor(x),top=Math.floor(y),fx=x-left,fy=y-top,i=top*width+left;
  return (values[i]*(1-fx)+values[i+1]*fx)*(1-fy)+(values[i+width]*(1-fx)+values[i+width+1]*fx)*fy;
}
/** Original bounded semi-Lagrangian transport with local viscosity and exponential decay. */
export function advanceVelocityField(field:VelocityField,seconds:number,settle=1){
  const dt=sculptureNumber(seconds,1/30,0,1/30),decay=Math.exp(-dt*sculptureNumber(settle,1,.25,3)*2.3),w=field.width,h=field.height;
  let energy=0;
  for(let row=1;row<h-1;row++)for(let col=1;col<w-1;col++){
    const i=row*w+col,bx=col-field.x[i]*dt*w*1.4,by=row-field.y[i]*dt*h*1.4;
    const vx=sample(field.x,w,h,bx,by),vy=sample(field.y,w,h,bx,by);
    const smoothX=(field.x[i-1]+field.x[i+1]+field.x[i-w]+field.x[i+w])*.25,smoothY=(field.y[i-1]+field.y[i+1]+field.y[i-w]+field.y[i+w])*.25;
    field.nextX[i]=(vx*.94+smoothX*.06)*decay;field.nextY[i]=(vy*.94+smoothY*.06)*decay;
    energy=Math.max(energy,Math.abs(field.nextX[i])+Math.abs(field.nextY[i]));
  }
  [field.x,field.nextX]=[field.nextX,field.x];[field.y,field.nextY]=[field.nextY,field.y];field.energy=energy;
  if(energy<.00008){clearVelocityField(field);return;}
  for(let i=0;i<field.x.length;i++){field.texture[i*4]=Math.round(128+field.x[i]*127);field.texture[i*4+1]=Math.round(128+field.y[i]*127);field.texture[i*4+2]=Math.min(255,Math.round((Math.abs(field.x[i])+Math.abs(field.y[i]))*180));field.texture[i*4+3]=255;}
}
