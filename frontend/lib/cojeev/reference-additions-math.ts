const finite=(value:number,min:number,max:number,fallback:number)=>Number.isFinite(value)?Math.max(min,Math.min(max,value)):fallback;
const segment=(text:string)=>Array.from(new Intl.Segmenter(undefined,{granularity:"grapheme"}).segment(text),item=>item.segment);
const pool="#%&@/+<>*=~ABCDEFGHKMNPRSTUVWXYZ0123456789";
export type HeadingDecodeOptions={scrambleLength:number;preserveChance:number;tailChance:number};
export function decodeHeading(text:string,progress:number,frame:number,options:HeadingDecodeOptions):string[]{
 const chars=segment(text),p=finite(progress,0,1,1);
 if(p>=1)return chars;
 const boundary=Math.floor((1-(1-p)**2)*chars.length),noise=Math.round(finite(options.scrambleLength,0,30,10));
 return chars.map((char,index)=>{
  if(index<boundary||/\s/u.test(char))return char;
  const sample=((index+1)*73+Math.floor(frame)*37+index*index*11)%997/997;
  const preserve=index<boundary+noise?finite(options.preserveChance,0,1,.25):1-finite(options.tailChance,0,1,.12);
  return sample<preserve?char:pool[(index*17+Math.floor(frame)*7)%pool.length];
 });
}
export function headingTiming(duration:number,stagger:number,count:number){return{duration:finite(duration,100,3000,700),delay:Math.min(finite(stagger,0,600,140),1200/Math.max(1,count-1))};}
export function normalizePortal(speed:number,intensity:number,distortion:number){return{speed:finite(speed,0,3,.7),intensity:finite(intensity,0,2,1),distortion:finite(distortion,0,1.5,.7)};}
/** Fixed precision makes the semantic server fallback identical across JS engines. */
export function portalContour(time:number,distortion:number,offset=0){
 const d=finite(distortion,0,1.5,.7),t=finite(time,0,1e9,0),points:string[]=[];
 for(let i=0;i<96;i++){const a=i/96*Math.PI*2,r=126+offset+(Math.sin(a*3+t*.4)*13+Math.cos(a*5-t*.3)*7)*d;points.push(`${(320+Math.cos(a)*r*1.3).toFixed(3)} ${(220+Math.sin(a)*r).toFixed(3)}`);}
 return "M"+points.join("L")+"Z";
}
