"use client";

import * as React from "react";
import { useMorph } from "@/lib/cojeev-motion/use-morph";
import { cva, type VariantProps } from "class-variance-authority";
import { cn } from "@/lib/utils";
import * as Primitive from "@radix-ui/react-tabs";
import { useFlowGroup } from "@/lib/cojeev-motion/use-flow";
import { MotionPresence, MotionSurface } from "@/components/ui/presence";
export const tabsVariants = cva("v-tabs [display:flex] [gap:var(--s-6)]", {
  variants: {
    variant: {
      default: "-pills -default gap-[var(--s-2)]",
      pills: "-pills gap-[var(--s-2)]",
      underline: "-underline gap-[var(--s-6)]",
      lenses: "-lenses gap-[var(--s-6)]",
      notebook: "-notebook",
      rail: "-rail",
    },
  },
  defaultVariants: { variant: "default" },
});
const TabsVariantContext =
  React.createContext<VariantProps<typeof tabsVariants>["variant"]>("default");
const TabsValueContext = React.createContext<string | undefined>(undefined);
export type TabsProps = React.ComponentProps<typeof Primitive.Root> &
  VariantProps<typeof tabsVariants>;
export function Tabs({
  className,
  variant = "default",
  children,
  value,
  defaultValue,
  onValueChange,
  ...props
}: TabsProps) {
  const [uncontrolledValue, setUncontrolledValue] =
    React.useState(defaultValue);
  const selected = value ?? uncontrolledValue;
  return (
    <TabsVariantContext.Provider value={variant}>
      <TabsValueContext.Provider value={selected}>
        <Primitive.Root
          data-slot="tabs"
          data-tabs-appearance={variant}
          className={cn("v-tabs-root grid min-w-0 gap-[var(--s-4)]", className)}
          value={selected}
          onValueChange={(next) => {
            if (value === undefined) setUncontrolledValue(next);
            onValueChange?.(next);
          }}
          {...props}
        >
          {children}
        </Primitive.Root>
      </TabsValueContext.Provider>
    </TabsVariantContext.Provider>
  );
}
export type TabsListProps = React.ComponentProps<typeof Primitive.List> &
  VariantProps<typeof tabsVariants>;
export function TabsList({ className, variant, ref, ...props }: TabsListProps) {
  const inherited = React.useContext(TabsVariantContext);
  const resolved = variant ?? inherited;
  // Underline travels as a ruled-line marker; every other variant moves its
  // whole selected surface (ink lozenge, beige lens, paper sheet, index line).
  const flowRef = useFlowGroup<HTMLDivElement>(ref, {
    kind: resolved === "underline" ? "bar" : "pill",
    itemSelector: '[data-slot="tabs-trigger"]',
    activeSelector: '[aria-selected="true"]',
  });
  return (
    <Primitive.List
      ref={flowRef}
      data-slot="tabs-list"
      data-part="root"
      data-flow-group=""
      className={cn(tabsVariants({ variant: resolved }), className)}
      {...props}
    />
  );
}
export type TabsTriggerProps = React.ComponentProps<typeof Primitive.Trigger>;
export function TabsTrigger({ className, ref, ...props }: TabsTriggerProps) {
  const morphRef = useMorph<HTMLButtonElement>("nav", ref);
  return (
    <Primitive.Trigger
      ref={morphRef}
      data-slot="tabs-trigger"
      data-part="trigger"
      className={cn("v-tab shrink-0", className)}
      {...props}
    />
  );
}
export type TabsContentProps = React.ComponentProps<typeof Primitive.Content>;
export function TabsContent({
  className,
  ref,
  forceMount,
  value,
  ...props
}: TabsContentProps) {
  const selected = React.useContext(TabsValueContext);
  const content = (
    <Primitive.Content
      ref={ref}
      value={value}
      forceMount
      data-slot="tabs-content"
      data-tabs-managed={!forceMount || undefined}
      data-part="content"
      className={className}
      {...props}
    />
  );
  // Explicit forceMount leaves visibility under the consumer's control.
  if (forceMount) return content;
  return (
    <MotionPresence>
      {selected === value && (
        <MotionSurface key={value} asChild preset="fade">
          {content}
        </MotionSurface>
      )}
    </MotionPresence>
  );
}
