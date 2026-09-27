"use client";
import { ScrollAreaList } from "@/components/ui/scroll-area";

import * as React from "react";
import { cva } from "class-variance-authority";
import { cn } from "@/lib/utils";
import {
  controlRadiusStyle,
  type ControlAppearanceProps,
} from "@/lib/cojeev/control-appearance";
import {
  ItemAdornment,
  itemText,
  menuAdornment,
  type ItemAdornmentItemProps,
} from "@/components/ui/item-adornment";
import { StateChevron, AnimatedIcon } from "@/components/ui/animated-icon";
import { Icon } from "@/components/ui/icon";
import * as Primitive from "@radix-ui/react-select";
import { useMorph } from "@/lib/cojeev-motion/use-morph";
import { useFlowPress } from "@/lib/cojeev-motion/flow-press";
import { assignMotionRef } from "@/lib/cojeev-motion/refs";
import {
  useFlowAppearance,
  useFlowGroup,
} from "@/lib/cojeev-motion/use-flow";
const SelectInteractionContext = React.createContext<{
  pointer: boolean;
  setPointer: (pointer: boolean) => void;
  triggerWidth: number;
  setTriggerWidth: (width: number) => void;
}>({
  pointer: false,
  setPointer: () => {},
  triggerWidth: 0,
  setTriggerWidth: () => {},
});
export type SelectProps = React.ComponentProps<typeof Primitive.Root> & {
  containerProps?: React.ComponentProps<"span">;
};
export function Select({
  children,
  containerProps,
  value,
  defaultValue,
  onValueChange,
  ...props
}: SelectProps) {
  const [pointer, setPointer] = React.useState(false);
  const [triggerWidth, setTriggerWidth] = React.useState(0);
  const [localValue, setLocalValue] = React.useState(defaultValue ?? "");
  const currentValue = value ?? localValue;
  const initialValue = React.useRef(value ?? defaultValue ?? "");
  const resetting = React.useRef(false);
  const host = React.useRef<HTMLSpanElement>(null);
  const latest = React.useRef({ value, currentValue, onValueChange });
  React.useLayoutEffect(() => {
    latest.current = { value, currentValue, onValueChange };
  });
  const containerRef = containerProps?.ref;
  const attachContainer = React.useCallback(
    (node: HTMLSpanElement | null) => {
      host.current = node;
      return assignMotionRef(containerRef, node);
    },
    [containerRef],
  );
  React.useEffect(() => {
    const form = props.form
      ? host.current?.ownerDocument.getElementById(props.form)
      : host.current?.closest("form");
    if (!(form instanceof HTMLFormElement)) return;
    let timer: ReturnType<typeof setTimeout> | undefined;
    const reset = (event: Event) => {
      resetting.current = true;
      clearTimeout(timer);
      // Radix resets on the form's target before bubbling handlers can cancel.
      // Keep a single value owner and wait until the complete event dispatch.
      timer = setTimeout(() => {
        const config = latest.current;
        if (
          !event.defaultPrevented &&
          config.currentValue !== initialValue.current
        ) {
          if (config.value === undefined) setLocalValue(initialValue.current);
          config.onValueChange?.(initialValue.current);
        }
        resetting.current = false;
      }, 0);
    };
    form.addEventListener("reset", reset, true);
    return () => {
      form.removeEventListener("reset", reset, true);
      clearTimeout(timer);
      resetting.current = false;
    };
  }, [props.form]);
  return (
    <SelectInteractionContext.Provider
      value={{ pointer, setPointer, triggerWidth, setTriggerWidth }}
    >
      <Primitive.Root
        {...props}
        value={currentValue}
        onValueChange={(next) => {
          if (resetting.current || next === currentValue) return;
          if (value === undefined) setLocalValue(next);
          onValueChange?.(next);
        }}
      >
        <span
          data-slot="select"
          data-part="root"
          data-select=""
          {...containerProps}
          ref={attachContainer}
          className={cn(
            "v-menuhost relative inline-block",
            containerProps?.className,
          )}
        >
          {children}
        </span>
      </Primitive.Root>
    </SelectInteractionContext.Provider>
  );
}
export const selectTriggerVariants = cva(
  "v-select [display:inline-flex] [align-items:center] [justify-content:space-between] [border-radius:var(--r-pill)] [font-size:var(--fs-control)] [white-space:nowrap] [cursor:pointer] [height:40px] [padding:0_12px_0_16px] [gap:10px] [box-shadow:inset_0_0_0_1px_var(--v-edge)] [background:var(--v-canvas)] [color:var(--v-text)] [font-weight:500]",
);
export type SelectTriggerProps = React.ComponentProps<
  typeof Primitive.Trigger
> &
  ControlAppearanceProps;
export function SelectTrigger({
  radius,
  appearance,
  style,
  className,
  ref,
  children,
  onPointerDown,
  onKeyDown,
  ...props
}: SelectTriggerProps) {
  const { setPointer, setTriggerWidth } = React.useContext(
    SelectInteractionContext,
  );
  const morphRef = useMorph<HTMLButtonElement>("buttons", ref);
  const pressRef = useFlowPress(morphRef);
  const attach = React.useCallback(
    (node: HTMLButtonElement | null) => {
      const release = assignMotionRef(pressRef, node);
      if (!node) return release;
      // Popper reports a transformed rect. Use layout width so the press spring
      // cannot resize a portalled menu or feed its ScrollArea observers in Safari.
      const measure = () => setTriggerWidth(node.offsetWidth);
      measure();
      const observer = new ResizeObserver(measure);
      observer.observe(node);
      return () => {
        observer.disconnect();
        release();
      };
    },
    [pressRef, setTriggerWidth],
  );
  return (
    <Primitive.Trigger
      ref={attach}
      onPointerDown={(event) => {
        onPointerDown?.(event);
        if (!event.defaultPrevented && event.button === 0) setPointer(true);
      }}
      onKeyDown={(event) => {
        onKeyDown?.(event);
        if (!event.defaultPrevented) setPointer(false);
      }}
      data-slot="select-trigger"
      data-stable-hit
      data-appearance={appearance}
      data-motion={appearance === "editorial" ? "off" : undefined}
      style={{ ...style, ...controlRadiusStyle(radius) }}
      data-part="trigger"
      className={cn(selectTriggerVariants(), className)}
      {...props}
    >
      {children}
      <StateChevron />
    </Primitive.Trigger>
  );
}
export type SelectValueProps = React.ComponentProps<typeof Primitive.Value>;
export function SelectValue(props: SelectValueProps) {
  return <Primitive.Value data-slot="select-value" data-value="" {...props} />;
}
export type SelectContentProps = React.ComponentProps<typeof Primitive.Content>;
export function SelectContent({
  className,
  ref,
  children,
  position = "popper",
  sideOffset = 6,
  onKeyDownCapture,
  onPointerMove,
  style,
  ...props
}: SelectContentProps) {
  const { pointer, setPointer, triggerWidth } = React.useContext(
    SelectInteractionContext,
  );
  const morphRef = useMorph<HTMLDivElement>("surfaces", ref);
  const groupRef = useFlowGroup<HTMLDivElement>(morphRef, {
    itemSelector: ".v-menu__item",
    activeSelector: "[data-highlighted]",
  });
  const flowRef = useFlowAppearance<HTMLDivElement>(true, groupRef, "grow");
  return (
    <Primitive.Portal>
      <Primitive.Content
        ref={flowRef}
        data-slot="select-content"
        data-part="content"
        data-pointer-interaction={pointer ? "" : undefined}
        onKeyDownCapture={(event) => {
          onKeyDownCapture?.(event);
          if (!event.defaultPrevented) setPointer(false);
        }}
        onPointerMove={(event) => {
          onPointerMove?.(event);
          if (!event.defaultPrevented && event.pointerType === "mouse")
            setPointer(true);
        }}
        className={cn("v-listbox v-menu", className)}
        position={position}
        sideOffset={sideOffset}
        collisionPadding={12}
        style={
          {
            "--select-anchor-width": `${triggerWidth}px`,
            ...style,
          } as React.CSSProperties
        }
        {...props}
      >
        <SelectScrollUpButton />
        <ScrollAreaList
          maxHeight="min(320px, calc(var(--radix-select-content-available-height, 60dvh) - 24px))"
          viewportWrapper={(viewport) => (
            <Primitive.Viewport asChild>{viewport}</Primitive.Viewport>
          )}
        >
          {children}
        </ScrollAreaList>
        <SelectScrollDownButton />
      </Primitive.Content>
    </Primitive.Portal>
  );
}
export type SelectItemProps = React.ComponentProps<typeof Primitive.Item> &
  ItemAdornmentItemProps & {
    showIndicator?: boolean;
    /** Supporting text stays outside the selected value. */ description?: React.ReactNode;
  };
export function SelectItem({
  showIndicator = false,
  description,
  className,
  adornment,
  adornmentId,
  children,
  ref,
  ...props
}: SelectItemProps) {
  const morphRef = useMorph<HTMLDivElement>("nav", ref);
  const descriptionId = React.useId();
  return (
    <Primitive.Item
      ref={morphRef}
      data-slot="select-item"
      data-part="item"
      className={cn("v-menu__item outline-none", className)}
      aria-describedby={description ? descriptionId : undefined}
      {...props}
    >
      <ItemAdornment
        identity={adornmentId ?? props.value ?? itemText(children)}
        value={menuAdornment(adornment)}
      />
      {description ? (
        <span className="v-select__rich">
          <Primitive.ItemText>{children}</Primitive.ItemText>
          <span id={descriptionId} className="v-select__description">
            {description}
          </span>
        </span>
      ) : (
        <Primitive.ItemText>{children}</Primitive.ItemText>
      )}
      {showIndicator && (
        <Primitive.ItemIndicator
          className="v-select__indicator"
          aria-hidden="true"
        >
          <AnimatedIcon name="check" preset="validation" />
        </Primitive.ItemIndicator>
      )}
    </Primitive.Item>
  );
}
export type SelectGroupProps = React.ComponentProps<typeof Primitive.Group>;
export function SelectGroup(props: SelectGroupProps) {
  return <Primitive.Group data-slot="select-group" {...props} />;
}
export type SelectLabelProps = React.ComponentProps<typeof Primitive.Label>;
export function SelectLabel({ className, ...props }: SelectLabelProps) {
  return (
    <Primitive.Label
      data-slot="select-label"
      data-part="group"
      className={cn("v-menu__group", className)}
      {...props}
    />
  );
}
export type SelectSeparatorProps = React.ComponentProps<
  typeof Primitive.Separator
>;
export function SelectSeparator({ className, ...props }: SelectSeparatorProps) {
  return (
    <Primitive.Separator
      data-slot="select-separator"
      data-part="separator"
      className={cn("v-menu__sep", className)}
      {...props}
    />
  );
}
export type SelectScrollUpButtonProps = React.ComponentProps<
  typeof Primitive.ScrollUpButton
>;
export function SelectScrollUpButton({
  children,
  ...props
}: SelectScrollUpButtonProps) {
  return (
    <Primitive.ScrollUpButton
      data-slot="select-scroll-up"
      className="flex justify-center"
      {...props}
    >
      {children ?? <Icon name="chevron-up" size="sm" />}
    </Primitive.ScrollUpButton>
  );
}
export type SelectScrollDownButtonProps = React.ComponentProps<
  typeof Primitive.ScrollDownButton
>;
export function SelectScrollDownButton({
  children,
  ...props
}: SelectScrollDownButtonProps) {
  return (
    <Primitive.ScrollDownButton
      data-slot="select-scroll-down"
      className="flex justify-center"
      {...props}
    >
      {children ?? <Icon name="chevron-down" size="sm" />}
    </Primitive.ScrollDownButton>
  );
}
