"use client";

import * as React from "react";
import { motion } from "motion/react";
import { Button, type ButtonProps } from "@/components/ui/button";
import { useChoreography } from "@/lib/cojeev-motion/choreography";
import { cn } from "@/lib/utils";
import { rememberThemeOrigin, type ThemeChangeDetails } from "@/lib/cojeev-motion/theme-transition";

export type ThemeMode = "light" | "dark";
export { applyTheme } from "@/lib/cojeev-motion/theme-transition";
export type { ThemeChangeDetails, ThemeRevealOrigin, ThemeRevealOptions } from "@/lib/cojeev-motion/theme-transition";
export type ThemeToggleProps = Omit<ButtonProps,"onChange"> & {
  mode: ThemeMode;
  onModeChange: (mode: ThemeMode, details?:ThemeChangeDetails) => void;
  showLabel?: boolean;
  label?: React.ReactNode;
  /** Keep a label on desktop and the same 44px icon target on small screens. */
  responsive?: boolean;
};
const sun = "M12 5C15.866 5 19 8.134 19 12C19 15.866 15.866 19 12 19C8.134 19 5 15.866 5 12C5 8.134 8.134 5 12 5Z";
const moon = "M18.9 15.2C17.2 19.3 12.9 21.2 8.8 19.4C4.7 17.6 2.7 13 4.4 8.8C5.4 6.2 7.7 4.3 10.4 3.8C8.1 10.4 12.3 16.8 18.9 15.2Z";

/** Controlled theme switch: the application owns persistence and the theme scope. */
export function ThemeToggle({mode,onModeChange,showLabel=true,label,responsive=false,className,onClick,...props}:ThemeToggleProps) {
  const {quiet,transition}=useChoreography();
  const dark=mode==="dark";
  return <Button variant="ghost" size="sm" data-label={showLabel?"visible":"hidden"} data-responsive={responsive} data-morph="both" data-tier="pill" data-lobes="3" data-depth=".008" data-asym=".05" role="switch" aria-checked={dark} aria-label="Dark appearance" title={`Switch to ${dark?"light":"dark"} theme`} className={cn("v-theme-toggle",className)} onClick={event=>{onClick?.(event);if(!event.defaultPrevented)onModeChange(dark?"light":"dark",rememberThemeOrigin(event.currentTarget))}} {...props} data-slot="theme-toggle">
    <svg data-slot="theme-toggle-icon" viewBox="0 0 24 24" width="22" height="22" fill="none" aria-hidden="true" focusable="false">
      <motion.g initial={false} animate={{rotate:dark?45:0}} transition={transition} style={{transformOrigin:"12px 12px"}}>
        {Array.from({length:8},(_,i)=><motion.path key={i} d="M12 1.5V3" transform={`rotate(${i*45} 12 12)`} stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" initial={false} animate={{pathLength:dark?0:1,opacity:dark?0:1}} transition={{duration:quiet?0:.2,delay:quiet?0:i*.015}}/>)}
      </motion.g>
      <motion.path initial={false} d={dark?moon:sun} animate={{d:dark?moon:sun,rotate:dark?-12:0}} transition={transition} fill="currentColor" style={{transformOrigin:"12px 12px"}}/>
    </svg>
    {showLabel&&<span className="v-theme-toggle__label">{label ?? (dark?"Dark":"Light")}</span>}
  </Button>;
}
