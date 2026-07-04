"use client";

import { useState } from "react";
import { cn } from "@/lib/utils";
import type { TraceEvent, TraceLevel } from "@/types/playground";

const levelStyles: Record<TraceLevel, string> = {
  INFO: "text-blue-500 dark:text-blue-400",
  OK: "text-emerald-600 dark:text-emerald-400",
  WARN: "text-amber-500",
  ERROR: "text-destructive",
};

const filters: Array<{ value: TraceLevel | "ALL"; label: string }> = [
  { value: "ALL", label: "Todo" },
  { value: "OK", label: "OK" },
  { value: "WARN", label: "WARN" },
  { value: "ERROR", label: "ERROR" },
];

interface TraceLogProps {
  traces: TraceEvent[];
}

export default function TraceLog({ traces }: TraceLogProps) {
  const [filter, setFilter] = useState<TraceLevel | "ALL">("ALL");

  const visible = filter === "ALL" ? traces : traces.filter((t) => t.level === filter);

  if (traces.length === 0) {
    return (
      <div className="space-y-4">
        <div className="space-y-1">
          <p className="text-[11px] uppercase tracking-[0.22em] text-muted-foreground font-semibold">
            Observabilidad
          </p>
          <h3 className="text-lg font-semibold text-foreground">Trazas del pipeline</h3>
          <p className="text-sm text-muted-foreground">
            Las trazas aparecerán aquí en tiempo real a medida que la sesión avance.
          </p>
        </div>

        <div className="flex items-center justify-center py-12 text-muted-foreground text-sm border border-border/70 rounded-xl bg-muted/10">
          Las trazas del SDK aparecerán aquí en tiempo real.
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      <div className="space-y-1">
        <p className="text-[11px] uppercase tracking-[0.22em] text-muted-foreground font-semibold">
          Observabilidad
        </p>
        <h3 className="text-lg font-semibold text-foreground">Trazas del pipeline</h3>
        <p className="text-sm text-muted-foreground">
          Filtra la ejecución por severidad para seguir el flujo paso a paso.
        </p>
      </div>

      <div className="flex gap-2 flex-wrap">
        {filters.map((f) => (
          <button
            key={f.value}
            onClick={() => setFilter(f.value)}
            className={cn(
              "px-3 py-1 text-xs font-semibold tracking-widest uppercase border rounded-sm transition-colors",
              filter === f.value
                ? "border-foreground bg-foreground text-background"
                : "border-border text-muted-foreground hover:border-foreground/40"
            )}
          >
            {f.label}
          </button>
        ))}
      </div>

      <div className="border border-border/70 rounded-xl bg-muted/10 min-h-40 max-h-80 overflow-y-auto p-3 space-y-0.5 font-mono text-xs shadow-sm">
        {visible.length === 0 ? (
          <p className="text-muted-foreground py-4 text-center">
            Sin trazas con este filtro.
          </p>
        ) : (
          visible.map((trace, i) => (
            <div key={i} className="flex gap-3 leading-relaxed">
              <span className="text-muted-foreground shrink-0">[{trace.timestamp}]</span>
              <span className={cn("shrink-0 font-semibold w-8", levelStyles[trace.level])}>
                {trace.level}
              </span>
              <span className="text-foreground/70">{trace.message}</span>
            </div>
          ))
        )}
      </div>
    </div>
  );
}
