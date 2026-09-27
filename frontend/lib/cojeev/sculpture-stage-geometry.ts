import type { SculptureGeometry } from "@/lib/cojeev/sculpture-geometry";
import { sculptureNumber } from "@/lib/cojeev/sculpture-raster";
export function orientSculptureTriangles(mesh:SculptureGeometry){
  const indices=Uint16Array.from(mesh.triangles),p=mesh.positions,n=mesh.normals;
  for(let i=0;i<indices.length;i+=3){const a=indices[i]*3,b=indices[i+1]*3,c=indices[i+2]*3,ux=p[b]-p[a],uy=p[b+1]-p[a+1],uz=p[b+2]-p[a+2],vx=p[c]-p[a],vy=p[c+1]-p[a+1],vz=p[c+2]-p[a+2];
    const dot=(uy*vz-uz*vy)*(n[a]+n[b]+n[c])+(uz*vx-ux*vz)*(n[a+1]+n[b+1]+n[c+1])+(ux*vy-uy*vx)*(n[a+2]+n[b+2]+n[c+2]);
    if(dot<0)[indices[i+1],indices[i+2]]=[indices[i+2],indices[i+1]];
  }return indices;
}
export function materialPixelSize(width:number,height:number,dpr:number){
  const cssWidth=sculptureNumber(width,1,1,20000),cssHeight=sculptureNumber(height,1,1,20000);
  const ratio=Math.min(sculptureNumber(dpr,1,.1,1.5),2048/Math.max(cssWidth,cssHeight),Math.sqrt(1000000/(cssWidth*cssHeight)));
  return {cssWidth,cssHeight,ratio,width:Math.max(1,Math.floor(cssWidth*ratio)),height:Math.max(1,Math.floor(cssHeight*ratio))};
}
