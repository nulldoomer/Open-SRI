"use client";
import { useMorph } from "@/lib/cojeev-motion/use-morph";
import * as React from "react";
import { cva, type VariantProps } from "class-variance-authority";
import { cn } from "@/lib/utils";
import { Icon } from "@/components/ui/icon";
import { Button } from "@/components/ui/button";
import {
  controlRadiusStyle,
  type ControlAppearanceProps,
} from "@/lib/cojeev/control-appearance";

const inputVariants = cva(
  "v-input flex w-full items-center gap-[12px] h-[48px] px-[18px] py-0 rounded-[var(--r-pill)] [border:0] bg-[var(--input)] text-[color:var(--v-text)] text-[length:var(--fs-body)] [box-shadow:inset_0_0_0_1px_var(--v-edge)] outline-none",
  {
    variants: {
      variant: {
        default: "",
        cream:
          "-cream bg-[var(--v-canvas)] [box-shadow:inset_0_0_0_1px_var(--v-border)]",
      },
      size: {
        default: "",
        sm: "-sm h-[40px] px-[14px] text-[length:var(--fs-control)]",
      },
    },
    defaultVariants: { variant: "default", size: "default" },
  },
);
export type InputProps = Omit<React.ComponentProps<"input">, "size"> &
  VariantProps<typeof inputVariants> &
  ControlAppearanceProps & { nativeSize?: number };
export function Input({
  className,
  variant,
  size,
  nativeSize,
  radius,
  appearance,
  style,
  ...props
}: InputProps) {
  return (
    <input
      data-slot="input"
      data-appearance={appearance}
      style={{ ...style, ...controlRadiusStyle(radius) }}
      data-part="root"
      data-state={
        props.disabled
          ? "disabled"
          : props["aria-invalid"] === true || props["aria-invalid"] === "true"
            ? "error"
            : "rest"
      }
      className={cn(inputVariants({ variant, size }), className)}
      size={nativeSize}
      {...props}
    />
  );
}
export type InputWrapperProps = React.HTMLAttributes<HTMLElement> &
  VariantProps<typeof inputVariants> &
  ControlAppearanceProps & {
    ref?: React.Ref<HTMLElement>;
    as?: "div" | "label";
  };
export function InputWrapper({
  ref: externalMorphRef,
  radius,
  appearance,
  style,
  className,
  variant,
  size,
  ...props
}: InputWrapperProps): React.ReactElement {
  const ownedMorphRef = useMorph<HTMLElement>("inputs", externalMorphRef);
  const { as = "div", ...rest } = props;
  return React.createElement(as, {
    ref: ownedMorphRef,
    "data-slot": "input",
    "data-stable-hit": "",
    "data-appearance": appearance,
    "data-motion": appearance === "editorial" ? "off" : undefined,
    style: { ...style, ...controlRadiusStyle(radius) },
    "data-part": "root",
    className: cn(inputVariants({ variant, size }), className),
    ...rest,
  });
}
export type InputControlProps = React.ComponentProps<"input">;
export function InputControl({ className, ...props }: InputControlProps) {
  return (
    <input
      data-slot="input-control"
      data-part="input"
      className={cn(
        "min-w-0 flex-1 h-full [border:0] bg-transparent [outline:0] [padding:1px_2px] placeholder:text-[color:var(--v-text-2)]",
        className,
      )}
      {...props}
    />
  );
}
export type InputAddonProps = React.ComponentProps<"span">;
export function InputAddon({ className, ...props }: InputAddonProps) {
  return (
    <span
      data-slot="input-addon"
      data-part="addon"
      className={cn(
        "v-disk grid shrink-0 place-items-center size-[32px] bg-[var(--disk-bg,var(--v-beige))] text-[color:var(--v-text)] [border-radius:46%_54%_50%_50%/50%_46%_54%_50%]",
        className,
      )}
      {...props}
    />
  );
}
export type InputAffixProps = React.ComponentProps<"span">;
export function InputAffix({ className, ...props }: InputAffixProps) {
  return (
    <span
      data-slot="input-affix"
      className={cn(
        "v-affix shrink-0 whitespace-nowrap text-[13px] text-[color:var(--v-text-2)]",
        className,
      )}
      {...props}
    />
  );
}
export type InputClearProps = React.ComponentProps<"button"> & {
  onClear?: () => void;
};
export function InputClear({
  className,
  onClear,
  onClick,
  children,
  ...props
}: InputClearProps) {
  return (
    <Button
      data-slot="input-clear"
      data-part="clear"
      type="button"
      variant="ghost"
      data-stable-hit=""
      aria-label="Clear input"
      className={cn(
        "v-clear grid shrink-0 place-items-center text-[color:var(--v-text-2)]",
        className,
      )}
      onClick={(event) => {
        onClick?.(event);
        if (!event.defaultPrevented) onClear?.();
      }}
      {...props}
    >
      {children ?? <Icon name="x" />}
    </Button>
  );
}
export { inputVariants };
