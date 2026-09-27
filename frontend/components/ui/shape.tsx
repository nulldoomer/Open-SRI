"use client"
import * as React from "react"
import { animate, motion, useMotionValue } from "motion/react"
import { cn } from "@/lib/utils"
import { shapeData } from "@/lib/cojeev/shape-data"
import { signatureShapePaths, type SignatureShapeName } from "@/lib/cojeev/signature-shapes"
import { useChoreography } from "@/lib/cojeev-motion/choreography"
import { useMotionVisibility } from "@/lib/cojeev-motion/use-motion-visibility"
import { assignMotionRef } from "@/lib/cojeev-motion/refs"
export type ShapeProps = React.ComponentProps<"span"> & { name?: string; as?: React.ElementType }
export function Shape({as:Tag="i",name="star-4",className,style,...props}:ShapeProps){
 const mask=shapeData[name];
 if(!mask) throw new Error(`Unknown Cojeev shape: ${name}`)
 return <Tag data-slot="shape" aria-hidden="true" className={cn("v-shape inline-block h-[1em] w-[1em] shrink-0 bg-[var(--c,currentColor)]",`-${name}`,className)} style={{...style,"--m":mask,mask:"var(--m) center / contain no-repeat",WebkitMask:"var(--m) center / contain no-repeat"} as React.CSSProperties} {...props}/>
}
export const shapeNames=Object.keys(shapeData)
export const signatureShapeNames=Object.keys(signatureShapePaths) as SignatureShapeName[]
export type { SignatureShapeName }
export type ShapeMorphProps = Omit<React.ComponentProps<"svg">, "name"> & {
 name?: SignatureShapeName
 variant?: "fill" | "outline"
 /** Omit for a decorative shape; supply when the graphic carries meaning. */
 label?: string
}
/** Original fixed-topology silhouettes; changing name retargets the visible path. */
export function ShapeMorph({name="daisy-12",variant="fill",label,className,ref,...props}:ShapeMorphProps){
 const host=React.useRef<SVGSVGElement>(null)
 const hostRef=React.useCallback((element:SVGSVGElement|null)=>{host.current=element;return assignMotionRef(ref,element)},[ref])
 const {quiet,transition}=useChoreography()
 const {enabled,inView}=useMotionVisibility(host)
 const target=signatureShapePaths[name]
 if(!target)throw new Error(`Unknown Cojeev signature shape: ${name}`)
 const path=useMotionValue(target)
 React.useEffect(()=>{
  if(quiet||!enabled||!inView){path.jump(target);return}
  const animation=animate(path,target,transition)
  return ()=>animation.stop()
 },[target,quiet,enabled,inView,path,transition])
 return <svg {...props} ref={hostRef} data-slot="shape-morph" data-shape={name} data-variant={variant} className={cn("v-shape-morph",className)} viewBox="0 0 100 100" role={label?"img":undefined} aria-label={label} aria-hidden={label?undefined:true} focusable="false">
  <motion.path d={path} fill={variant==="fill"?"currentColor":"none"} stroke={variant==="outline"?"currentColor":"none"} strokeWidth={variant==="outline"?2:0} vectorEffect="non-scaling-stroke" />
 </svg>
}
