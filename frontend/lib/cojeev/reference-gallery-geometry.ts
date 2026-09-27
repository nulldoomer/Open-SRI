export const galleryClamp = (n: number, low = 0, high = 1) => Math.min(high, Math.max(low, Number.isFinite(n) ? n : low));
export function galleryIndex(index: number, count: number) { return count > 0 ? ((Math.round(Number.isFinite(index) ? index : 0) % count) + count) % count : -1; }
export function galleryOffset(index: number, position: number, count: number) { return count > 0 ? ((index - position + count / 2) % count + count) % count - count / 2 : 0; }
export function spiralPlacement(offset: number, count: number, width: number) {
 const d = galleryOffset(offset, 0, count), angle = d * Math.PI * .52;
 return { x: Math.sin(angle) * Math.min(125, width * .28), y: d * 58, scale: .72 + (Math.cos(angle) + 1) * .19, depth: Math.cos(angle), opacity: Math.max(.18, 1 - Math.abs(d) / Math.max(count / 2, 1) * .68) };
}
export function stepGalleryTransition(current: number, target: number, delta: number, duration: number) {
 if (!Number.isFinite(duration) || duration <= 0) return target;
 const distance = Math.min(Math.abs(target - current), Math.max(0, delta) / duration);
 return galleryClamp(current + Math.sign(target - current) * distance);
}
const ramp = (p: number, start: number, end: number) => galleryClamp((p-start)/(end-start));
export function transitionLayers(progress: number, kind: "grain" | "dither") {
 const p=galleryClamp(progress);
 return kind === "grain"
  ? {first:1-ramp(p,.08,.35),second:ramp(p,.7,.95),cover:Math.min(ramp(p,0,.3),1-ramp(p,.68,1))}
  : {first:1-ramp(p,.22,.34),second:ramp(p,.64,.76),cover:Math.min(ramp(p,0,.3),1-ramp(p,.68,1))};
}
export function waveBoundary(progress: number, x: number) { const p=galleryClamp(progress); return 1.15 - p*1.3 + Math.sin(x*Math.PI*2+p*Math.PI)*.07*Math.sin(p*Math.PI); }
/** A 4x4 Bayer threshold repeats across a bounded 16x16 field. */
export function ditherCells(coverage: number) {
 const matrix=[0,8,2,10,12,4,14,6,3,11,1,9,15,7,13,5], cells: {x:number;y:number}[]=[];
 for(let y=0;y<16;y++) for(let x=0;x<16;x++) if((matrix[(y%4)*4+x%4]+.5)/16 <= galleryClamp(coverage)) cells.push({x,y});
 return cells;
}
