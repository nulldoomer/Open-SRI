"use client"
import * as React from "react"
import { cva, type VariantProps } from "class-variance-authority"
import { cn } from "@/lib/utils"

const LabelVariants=cva("v-label [font-size:var(--fs-control)] [font-weight:600] [line-height:1.5] [box-shadow:none] [border:0] [background:none]",{variants:{variant:{"default":""},size:{"default":"","sm":"-sm [font-size:13px]"}},defaultVariants:{variant:"default",size:"default"}})
export type LabelProps=React.ComponentProps<"label"> & VariantProps<typeof LabelVariants> & { as?:React.ElementType }
export function Label({as:Tag="label",className,variant,size,...props}:LabelProps){return <Tag data-slot="label" data-part="root" className={cn(LabelVariants({variant,size}),className)} {...props}/>}

const StatsVariants=cva("v-stats [display:flex] [gap:var(--s-5)_var(--s-5)] [flex-wrap:wrap] [margin-top:var(--s-4)] [max-width:100%]",{variants:{variant:{"default":""},size:{"default":""}},defaultVariants:{variant:"default",size:"default"}})
export type StatsProps=React.ComponentProps<"div"> & VariantProps<typeof StatsVariants> & { as?:React.ElementType }
export function Stats({as:Tag="div",className,variant,size,...props}:StatsProps){return <Tag data-slot="label-stats" data-part="stats" className={cn(StatsVariants({variant,size}),className)} {...props}/>}

const StatVariants=cva("v-stat [display:grid] [gap:4px] [min-width:0]",{variants:{variant:{"default":"","ul":"-ul [padding-bottom:8px] [border-bottom:2px_solid_var(--wm,var(--v-text))]"},size:{"default":""}},defaultVariants:{variant:"default",size:"default"}})
export type StatProps=React.ComponentProps<"div"> & VariantProps<typeof StatVariants> & { as?:React.ElementType }
export function Stat({as:Tag="div",className,variant,size,...props}:StatProps){return <Tag data-slot="label-stat" data-part="stat" className={cn(StatVariants({variant,size}),className)} {...props}/>}
