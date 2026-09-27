import type { SculptureGeometry } from "@/lib/cojeev/sculpture-geometry";
import { sculptureNumber } from "@/lib/cojeev/sculpture-raster";
export type ParticleSpring = { rest:Float32Array; positions:Float32Array; velocities:Float32Array; energy:number };
export type ParticlePointer = { x:number;y:number;z:number;radius:number;strength:number;swirl:number };
/** Area-weighted sampling prevents small faces receiving as many particles as large faces. */
export function sampleSculptureSurface(mesh:SculptureGeometry,count=3600,seed=1){
  count=Math.round(sculptureNumber(count,3600,300,8000));seed=sculptureNumber(seed,1,0,0xffffffff)>>>0;
  const p=mesh.positions,t=mesh.triangles,cumulative=new Float64Array(t.length/3);let total=0;
  for(let i=0;i<t.length;i+=3){const a=t[i]*3,b=t[i+1]*3,c=t[i+2]*3,ux=p[b]-p[a],uy=p[b+1]-p[a+1],uz=p[b+2]-p[a+2],vx=p[c]-p[a],vy=p[c+1]-p[a+1],vz=p[c+2]-p[a+2];total+=Math.hypot(uy*vz-uz*vy,uz*vx-ux*vz,ux*vy-uy*vx)*.5;cumulative[i/3]=total;}
  if(!Number.isFinite(total)||total<1e-8)throw new Error("Use geometry with a nonzero triangle surface for particles.");
  const random=()=>{seed=(Math.imul(seed,1664525)+1013904223)>>>0;return seed/4294967296;},points=new Float32Array(count*3);
  for(let n=0;n<count;n++){
    const target=random()*total;let lo=0,hi=cumulative.length-1;
    while(lo<hi){const mid=(lo+hi)>>>1;if(cumulative[mid]<target)lo=mid+1;else hi=mid;}
    const a=t[lo*3]*3,b=t[lo*3+1]*3,c=t[lo*3+2]*3,r=Math.sqrt(random()),v=random(),wa=1-r,wb=r*(1-v),wc=r*v;
    for(let k=0;k<3;k++)points[n*3+k]=p[a+k]*wa+p[b+k]*wb+p[c+k]*wc;
  }
  return points;
}
export function createParticleSpring(rest:Float32Array):ParticleSpring {return {rest:Float32Array.from(rest),positions:Float32Array.from(rest),velocities:new Float32Array(rest.length),energy:0};}
export function resetParticleSpring(state:ParticleSpring){state.positions.set(state.rest);state.velocities.fill(0);state.energy=0;}
export function pulseParticleSpring(state:ParticleSpring,strength=1){
  strength=sculptureNumber(strength,1,0,2);
  for(let i=0;i<state.positions.length;i+=3){const angle=i*.61803398875,x=state.rest[i],y=state.rest[i+1],z=state.rest[i+2],length=Math.hypot(x,y,z)||1;state.velocities[i]+=(x/length*.9+Math.sin(angle)*.35)*strength*2;state.velocities[i+1]+=(y/length*.9+Math.cos(angle)*.35)*strength*2;state.velocities[i+2]+=(z/length*.8+Math.sin(angle*1.7)*.5)*strength*2;}
  state.energy=2*strength;
}
/** Bounded spring motion in object space; limits avoid numeric explosions after long frame gaps. */
export function stepParticleSpring(state:ParticleSpring,seconds:number,spring=1,damping=1,pointer?:ParticlePointer){
  const dt=sculptureNumber(seconds,1/30,0,1/30),k=sculptureNumber(spring,1,.25,3)*38,drag=Math.exp(-dt*(6+sculptureNumber(damping,1,.25,2)*5));
  const radius=sculptureNumber(pointer?.radius,.35,.1,.8),force=sculptureNumber(pointer?.strength,1,0,2)*24,swirl=sculptureNumber(pointer?.swirl,.35,0,1);
  const active=pointer&&[pointer.x,pointer.y,pointer.z].every(Number.isFinite);let energy=0;
  for(let i=0;i<state.positions.length;i+=3){
    const dx=state.positions[i]-(pointer?.x??0),dy=state.positions[i+1]-(pointer?.y??0),distance=Math.hypot(dx,dy),falloff=active?Math.max(0,1-distance/radius)**2:0,unit=distance>.001?distance:.001;
    for(let axis=0;axis<3;axis++){
      const j=i+axis,away=axis===0?(dx/unit-dy/unit*swirl):axis===1?(dy/unit+dx/unit*swirl):.22;
      let velocity=(state.velocities[j]+((state.rest[j]-state.positions[j])*k+away*falloff*force)*dt)*drag;
      velocity=Math.max(-8,Math.min(8,velocity));
      const displacement=Math.max(-1.35,Math.min(1.35,state.positions[j]+velocity*dt-state.rest[j]));
      state.positions[j]=state.rest[j]+displacement;state.velocities[j]=velocity;energy=Math.max(energy,Math.abs(displacement)+Math.abs(velocity));
    }
  }
  state.energy=energy;
  if(!active&&energy<.00008)resetParticleSpring(state);
}
