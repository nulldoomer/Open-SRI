import { cn } from "@/lib/utils";
import type { PipelineStep as PipelineStepType, StepStatus } from "@/types/playground";

/* Same node grammar as the landing's tracking route: ghost → printed, signal while it runs. */
const node: Record<StepStatus, string> = {
  idle: "border-dashed border-[var(--v-edge)] bg-transparent",
  running: "border-2 border-signal bg-[var(--signal-soft)]",
  ok: "border-foreground bg-foreground",
  error: "border-[var(--v-danger-fill)] bg-[var(--v-danger-fill)]",
};

const statusText: Record<StepStatus, string> = {
  idle: "",
  running: "En curso",
  ok: "OK",
  error: "Error",
};

interface PipelineStepProps {
  step: PipelineStepType;
  index: number;
  isLast: boolean;
}

export default function PipelineStep({ step, index, isLast }: PipelineStepProps) {
  return (
    <li className="relative flex gap-4 pb-6 last:pb-0">
      {!isLast && (
        <span
          aria-hidden="true"
          className={cn("absolute bottom-0 left-[9px] top-5 w-px", step.status === "ok" ? "bg-foreground" : "bg-border")}
        />
      )}
      <span aria-hidden="true" className={cn("relative z-10 mt-0.5 size-[19px] shrink-0 rounded-full border", node[step.status])} />

      <div className="min-w-0 flex-1">
        <div className="flex items-baseline justify-between gap-3">
          <p className={cn("text-sm font-semibold", step.status === "idle" && "text-muted-foreground")}>
            <span className="mr-2 font-mono text-[11px] font-normal tabular-nums text-muted-foreground">
              {String(index + 1).padStart(2, "0")}
            </span>
            {step.label}
          </p>
          {statusText[step.status] && (
            <span
              className={cn(
                "shrink-0 font-mono text-[10.5px] uppercase tracking-[0.14em]",
                step.status === "running" && "text-signal-ink",
                step.status === "ok" && "text-[var(--status-ok-ink)]",
                step.status === "error" && "text-[var(--v-danger-ink)]",
              )}
            >
              {statusText[step.status]}
            </span>
          )}
        </div>
        {step.detail && <p className="mt-1 break-all font-mono text-xs text-muted-foreground">{step.detail}</p>}
      </div>
    </li>
  );
}
