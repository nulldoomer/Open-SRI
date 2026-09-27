import type { CSSProperties } from "react";
import { cn } from "@/lib/utils";

/* Field layout of the 49-digit clave de acceso (ficha técnica del SRI). */
const FIELDS = [
  { size: 8, label: "Fecha" },
  { size: 2, label: "Tipo" },
  { size: 13, label: "RUC" },
  { size: 1, label: "Amb." },
  { size: 6, label: "Serie" },
  { size: 9, label: "Secuencial" },
  { size: 8, label: "Código" },
  { size: 1, label: "Emisión" },
  { size: 1, label: "DV" },
].map((f, i, all) => ({ ...f, start: all.slice(0, i).reduce((n, x) => n + x.size, 0) }));

/**
 * Splits a clave into its labelled fields. Anything that is not 49 digits is
 * shown raw, so an unexpected SRI value is never silently reshaped.
 */
export default function ClaveAnatomy({
  clave,
  strikeFrom,
  className,
}: {
  clave: string;
  /** Delay (ms) for the first field when the groups should print in sequence. */
  strikeFrom?: number;
  className?: string;
}) {
  if (!/^\d{49}$/.test(clave)) {
    return <p className={cn("break-all font-mono text-[13px] tabular-nums", className)}>{clave}</p>;
  }

  const groups = FIELDS.map((f) => ({ label: f.label, digits: clave.slice(f.start, f.start + f.size) }));

  return (
    <div className={className}>
      <span className="sr-only">Clave de acceso {clave}</span>
      <ol aria-hidden="true" className="flex flex-wrap gap-x-2.5 gap-y-2">
      {groups.map((g, i) => (
        <li
          key={g.label}
          className={cn("flex flex-col", strikeFrom !== undefined && "strike")}
          style={strikeFrom !== undefined ? ({ "--strike-delay": `${strikeFrom + i * 110}ms` } as CSSProperties) : undefined}
        >
          <span className="font-mono text-[13px] leading-none tabular-nums text-foreground">{g.digits}</span>
          <span className="mt-1 border-t border-border pt-1 font-mono text-[9.5px] uppercase tracking-[0.12em] text-muted-foreground">
            {g.label}
          </span>
        </li>
      ))}
      </ol>
    </div>
  );
}
