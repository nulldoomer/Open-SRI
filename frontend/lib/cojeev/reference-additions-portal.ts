import { shaderResolution } from "@/lib/cojeev/living-shader";
import { normalizePortal } from "@/lib/cojeev/reference-additions-math";
export type PortalOptions={speed:number;intensity:number;distortion:number;tone:"balanced"|"warm"|"cool";visible:boolean;moving:boolean};
export type PortalStatus="pending"|"webgl"|"fallback"|"lost";
const vertex=`attribute vec2 aPosition;void main(){gl_Position=vec4(aPosition,0.,1.);}`;
/** Original organic annular distance field: domain warp, luminous core, broad chromatic fringe. */
const fragment=`precision mediump float;
uniform vec2 uSize;uniform vec2 uPointer;uniform float uTime;uniform float uIntensity;uniform float uDistortion;
uniform vec3 uPaper;uniform vec3 uCore;uniform vec3 uFringe;
float wave(vec2 p,float t){return sin(p.x*5.3+t)*cos(p.y*4.7-t*.63)*.6+sin(p.y*8.1+p.x*2.3+t*.4)*.25;}
void main(){
 vec2 uv=gl_FragCoord.xy/uSize;float aspect=uSize.x/max(uSize.y,1.);
 vec2 p=(uv-.5)*vec2(aspect,1.);p+=(uPointer-.5)*.035;p.x*=.83;
 float t=uTime*.24;
 vec2 q=p+vec2(wave(p.yx*1.5,t),wave(p*1.8,-t*.8))*.07*uDistortion;
 float a=atan(q.y,q.x),r=length(q);
 float contour=.30+(sin(a*3.+t)*.023+cos(a*5.-t*.6)*.012)*uDistortion;
 float d1=abs(r-contour),d2=abs(r-contour-.045-wave(q*2.,t)*.014*uDistortion);
 float arc=.5+.5*smoothstep(-.6,.8,sin(a*2.+t*.5));
 float core=exp(-d1*145.)*arc,fringe=exp(-d2*31.)*(.6+.4*cos(a-t));
 float bloom=exp(-d1*21.)*.3,inside=exp(-r*r*9.)*.06;
 vec3 color=mix(uPaper,uFringe,clamp((fringe*.44+bloom+inside)*uIntensity,0.,.88));
 color=mix(color,uCore,clamp(core*.9*uIntensity,0.,1.));
 float fine=sin(gl_FragCoord.x*1.71+gl_FragCoord.y*2.13)*.002;
 gl_FragColor=vec4(clamp(color+fine,0.,1.),1.);
}`;
export function createPortalField(canvas:HTMLCanvasElement,host:HTMLElement,onStatus:(status:PortalStatus)=>void){
 let gl:WebGLRenderingContext|null=null,program:WebGLProgram|null=null,buffer:WebGLBuffer|null=null;
 const shaders:WebGLShader[]=[],uniforms:Record<string,WebGLUniformLocation|null>={};
 let frame=0,previous=0,elapsed=0,disposed=false,lost=false,failed=false,dirty=true,announced=false;
 let options:PortalOptions={speed:.7,intensity:1,distortion:.7,tone:"balanced",visible:false,moving:false};
 let targetX=.5,targetY=.5,pointerX=.5,pointerY=.5;
 const probe=document.createElement("canvas");probe.width=probe.height=1;const color=probe.getContext("2d",{willReadFrequently:true});
 const cancel=()=>{cancelAnimationFrame(frame);frame=0;previous=0;};
 const release=()=>{if(gl&&!lost){if(buffer)gl.deleteBuffer(buffer);if(program)gl.deleteProgram(program);shaders.forEach(shader=>gl!.deleteShader(shader));}buffer=null;program=null;shaders.length=0;};
 const initialize=()=>{
  if(program||failed||lost||disposed)return;
  try{
   gl=canvas.getContext("webgl",{alpha:false,antialias:false,depth:false,stencil:false,powerPreference:"low-power"});if(!gl)throw new Error("WebGL unavailable");
   program=gl.createProgram();if(!program)throw new Error("Program unavailable");
   for(const [type,source] of [[gl.VERTEX_SHADER,vertex],[gl.FRAGMENT_SHADER,fragment]] as const){const shader=gl.createShader(type);if(!shader)throw new Error("Shader unavailable");shaders.push(shader);gl.shaderSource(shader,source);gl.compileShader(shader);if(!gl.getShaderParameter(shader,gl.COMPILE_STATUS))throw new Error("Shader compilation failed");gl.attachShader(program,shader);}
   gl.linkProgram(program);if(!gl.getProgramParameter(program,gl.LINK_STATUS))throw new Error("Program link failed");
   buffer=gl.createBuffer();if(!buffer)throw new Error("Buffer unavailable");gl.bindBuffer(gl.ARRAY_BUFFER,buffer);gl.bufferData(gl.ARRAY_BUFFER,new Float32Array([-1,-1,3,-1,-1,3]),gl.STATIC_DRAW);
   gl.useProgram(program);const pos=gl.getAttribLocation(program,"aPosition");gl.enableVertexAttribArray(pos);gl.vertexAttribPointer(pos,2,gl.FLOAT,false,0,0);
   ["uSize","uPointer","uTime","uIntensity","uDistortion","uPaper","uCore","uFringe"].forEach(name=>{uniforms[name]=gl!.getUniformLocation(program!,name);});dirty=true;announced=false;
  }catch{failed=true;release();onStatus("fallback");}
 };
 const rgba=(value:string,fallback:string)=>{if(!color)return[.8,.7,.7];color.clearRect(0,0,1,1);color.fillStyle=fallback;color.fillStyle=value||fallback;color.fillRect(0,0,1,1);const px=color.getImageData(0,0,1,1).data;return[px[0]/255,px[1]/255,px[2]/255];};
 const request=()=>{if(!disposed&&!lost&&!failed&&options.visible&&!document.hidden&&!frame)frame=requestAnimationFrame(draw);};
 function draw(now:number){
  frame=0;if(disposed||lost||failed||!options.visible||document.hidden)return;initialize();if(!gl||!program||failed)return;
  if(dirty){const {width,height}=shaderResolution(host.clientWidth,host.clientHeight,window.devicePixelRatio);if(canvas.width!==width||canvas.height!==height){canvas.width=width;canvas.height=height;}gl.viewport(0,0,width,height);gl.uniform2f(uniforms.uSize,width,height);
   const css=getComputedStyle(host),tone=options.tone,core=tone==="cool"?"--v-blue":"--v-pink",fringe=tone==="warm"?"--v-yellow":tone==="cool"?"--v-olive":"--v-blue";
   gl.uniform3fv(uniforms.uPaper,rgba(css.getPropertyValue("--v-paper"),"#fbf4e6"));gl.uniform3fv(uniforms.uCore,rgba(css.getPropertyValue(core),"#f5b8db"));gl.uniform3fv(uniforms.uFringe,rgba(css.getPropertyValue(fringe),"#b6caeb"));dirty=false;
  }
  const dt=previous?Math.min(.05,Math.max(0,(now-previous)/1000)):0;previous=now;
  if(options.moving){elapsed+=dt*options.speed;pointerX+=(targetX-pointerX)*.08;pointerY+=(targetY-pointerY)*.08;}
  gl.uniform1f(uniforms.uTime,elapsed);gl.uniform1f(uniforms.uIntensity,options.intensity);gl.uniform1f(uniforms.uDistortion,options.distortion);gl.uniform2f(uniforms.uPointer,pointerX,pointerY);gl.drawArrays(gl.TRIANGLES,0,3);if(!announced){announced=true;onStatus("webgl");}
  if(options.moving&&options.speed>0&&options.intensity>0)request();
 }
 const repaint=()=>{dirty=true;request();},visibility=()=>{cancel();request();};
 const onLost=(event:Event)=>{event.preventDefault();lost=true;cancel();release();onStatus("lost");};
 const onRestored=()=>{lost=false;failed=false;gl=null;dirty=true;request();};
 canvas.addEventListener("webglcontextlost",onLost);canvas.addEventListener("webglcontextrestored",onRestored);document.addEventListener("visibilitychange",visibility);
 const resize=new ResizeObserver(repaint);resize.observe(host);const appearance=new MutationObserver(repaint);
 for(let ancestor:HTMLElement|null=host;ancestor;ancestor=ancestor.parentElement)appearance.observe(ancestor,{attributes:true,attributeFilter:["class","style","data-mode","data-theme"]});
 window.addEventListener("cojeev:appearancechange",repaint);
 return{
  update(next:PortalOptions){const values=normalizePortal(next.speed,next.intensity,next.distortion);dirty ||= next.tone!==options.tone;options={...next,...values};cancel();request();},
  pointer(x:number,y:number){targetX=Math.max(0,Math.min(1,x));targetY=Math.max(0,Math.min(1,y));},
  dispose(){disposed=true;cancel();resize.disconnect();appearance.disconnect();canvas.removeEventListener("webglcontextlost",onLost);canvas.removeEventListener("webglcontextrestored",onRestored);document.removeEventListener("visibilitychange",visibility);window.removeEventListener("cojeev:appearancechange",repaint);release();gl=null;}
 };
}
