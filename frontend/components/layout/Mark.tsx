import { cn } from "@/lib/utils";

/* The Evangelion ■ marker, drawn as a box so it matches the palette and never falls back to a font glyph. */
export default function Mark({ count = 1, className }: { count?: number; className?: string }) {
  return (
    <span aria-hidden="true" className={cn("inline-flex shrink-0 gap-[3px] text-signal", className)}>
      {Array.from({ length: count }, (_, i) => (
        <span key={i} className="size-[7px] bg-current" />
      ))}
    </span>
  );
}
