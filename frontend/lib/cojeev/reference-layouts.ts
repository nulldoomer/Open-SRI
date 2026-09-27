export function reconcileSwapOrder(order:readonly string[],ids:readonly string[]){return [...new Set([...order.filter(id=>ids.includes(id)),...ids])];}
export function swapLayoutItems(order:readonly string[],first:string,second:string){
 const next=[...order],a=next.indexOf(first),b=next.indexOf(second);if(a<0||b<0||a===b)return next;[next[a],next[b]]=[next[b],next[a]];return next;
}
export function shouldDismissDrawer(offset:number,width:number,side:"left"|"right"){
 return Number.isFinite(offset)&&Number.isFinite(width)&&width>0&&(side==="left"?-offset:offset)>Math.max(48,width*.28);
}
export function supportDestination(href:string):string|undefined{
 const value=href.trim();if(!value||/[\u0000-\u001f]/.test(value))return;
 if(value.startsWith("/")&&!value.startsWith("//"))return value;
 if(value.startsWith("#")&&value.length>1)return value;
 try{const url=new URL(value);if(url.protocol==="https:"||url.protocol==="http:"||url.protocol==="mailto:")return value;}catch{/* No destination is rendered for malformed URLs. */}
}
