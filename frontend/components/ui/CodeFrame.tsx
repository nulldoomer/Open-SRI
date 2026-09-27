import { cn } from "@/lib/utils";
import Mark from "@/components/layout/Mark";
import CopyButton from "@/components/docs/CopyButton";

/* Dark code surface shared by the landing, docs and SDK pages: a label strip plus the highlighted body. */
export default function CodeFrame({
  label,
  code,
  actions,
  children,
  className,
}: {
  label?: React.ReactNode;
  code?: string;
  actions?: React.ReactNode;
  children: React.ReactNode;
  className?: string;
}) {
  return (
    <figure
      className={cn(
        "overflow-hidden rounded-[var(--r-card-sm)] bg-[var(--v-structure)] text-[var(--structure-text)] ring-1 ring-[var(--structure-line)]/60",
        className,
      )}
    >
      {(label || code || actions) && (
        <figcaption className="flex min-h-10 items-center justify-between gap-3 border-b border-[var(--structure-line)] px-4 font-mono text-xs">
          <span className="flex min-w-0 items-center gap-2 overflow-x-auto whitespace-nowrap [scrollbar-width:none]">
            <Mark />
            {label}
          </span>
          <span className="flex shrink-0 items-center gap-1">
            {actions}
            {code !== undefined && <CopyButton code={code} />}
          </span>
        </figcaption>
      )}
      {children}
    </figure>
  );
}
