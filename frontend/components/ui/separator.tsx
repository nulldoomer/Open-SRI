"use client"
import * as React from "react"
import { cva, type VariantProps } from "class-variance-authority"
import { cn } from "@/lib/utils"

const SeparatorVariants=cva("v-sep [border:0] [height:1px] [color:gray] [background:var(--v-border)] [margin:var(--s-4)_0]",{variants:{variant:{"default":"","v":"-v [width:1px] [height:auto] [align-self:stretch] [margin:0_var(--s-4)]"},size:{"default":""}},defaultVariants:{variant:"default",size:"default"}})
export type SeparatorProps=React.ComponentProps<"hr"> & VariantProps<typeof SeparatorVariants> & { orientation?:"horizontal"|"vertical"; decorative?:boolean }
export function Separator({className,variant,size,orientation,decorative=false,...props}:SeparatorProps){const resolved=orientation??(variant==="v"?"vertical":"horizontal");return <hr data-slot="separator" data-part="root" role={decorative?"none":"separator"} aria-orientation={decorative?undefined:resolved} className={cn(SeparatorVariants({variant:resolved==="vertical"?"v":"default",size}),className)} {...props}/>}
