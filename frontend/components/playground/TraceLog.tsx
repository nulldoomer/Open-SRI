"use client";

import { useState } from "react";
import { cn } from "@/lib/utils";
import CodeFrame from "@/components/ui/CodeFrame";
import type { TraceEvent, TraceLevel } from "@/types/playground";

// Levels read on the dark log surface in both modes.
const levelStyles: Record<TraceLevel, string> = {
  INFO: "text-[var(--structure-text)]",
  OK: "text-[#B4C3A0]",
  WARN: "text-[#E7CF98]",
  ERROR: "text-[#F0A99C]",
};

const filters: Array<{ value: TraceLevel | "ALL"; label: string }> = [
  { value: "ALL", label: "Todo" },
  { value: "OK", label: "OK" },
  { value: "WARN", label: "Warn" },
  { value: "ERROR", label: "Error" },
];

export default function TraceLog({ traces }: { traces: TraceEvent[] }) {
  const [filter, setFilter] = useState<TraceLevel | "ALL">("ALL");
  const visible = filter === "ALL" ? traces : traces.filter((t) => t.level === filter);

  return (
    <div className="space-y-4">
      <div role="group" aria-label="Filtrar por severidad" className="inline-flex rounded-full bg-[var(--v-beige)] p-1">
        {filters.map((f) => (
          <button
            key={f.value}
            type="button"
            aria-pressed={filter === f.value}
            onClick={() => setFilter(f.value)}
            className={cn(
              "h-8 rounded-full px-3.5 font-mono text-xs transition-colors",
              filter === f.value ? "bg-foreground text-background" : "text-muted-foreground hover:text-foreground",
            )}
          >
            {f.label}
          </button>
        ))}
      </div>

      <CodeFrame label="trazas del SDK">
        <div aria-live="polite" className="max-h-96 min-h-40 space-y-1 overflow-y-auto p-4 font-mono text-xs leading-relaxed">
          {traces.length === 0 ? (
            <p className="py-8 text-center text-[var(--structure-text)]">Las trazas aparecerán aquí en tiempo real.</p>
          ) : visible.length === 0 ? (
            <p className="py-8 text-center text-[var(--structure-text)]">Sin trazas con este filtro.</p>
          ) : (
            visible.map((trace, i) => (
              <p key={i} className="flex gap-3">
                <span className="shrink-0 tabular-nums opacity-60">{trace.timestamp}</span>
                <span className={cn("w-10 shrink-0 font-semibold", levelStyles[trace.level])}>{trace.level}</span>
                <span className="min-w-0 break-words text-[var(--on-structure)]">{trace.message}</span>
              </p>
            ))
          )}
        </div>
      </CodeFrame>
    </div>
  );
}
