import { HugeiconsIcon } from "@hugeicons/react";
import { cn } from "@/lib/utils";
import type { PipelineStep as PipelineStepType } from "@/types/playground";

const statusBorder = {
  idle: "border-border bg-muted/20",
  running: "border-amber-400 bg-amber-50 dark:bg-amber-950/30",
  ok: "border-emerald-500/40 bg-emerald-50 dark:bg-emerald-950/30",
  error: "border-destructive/40 bg-destructive/5",
};

const statusLabel = {
  idle: "text-muted-foreground/50",
  running: "text-amber-500",
  ok: "text-emerald-600 dark:text-emerald-400",
  error: "text-destructive",
};

function StatusDot({ status }: { status: PipelineStepType["status"] }) {
  if (status === "idle") return <span className="w-2 h-2 rounded-full bg-muted-foreground/30 inline-block" />;
  if (status === "running") return <span className="w-2 h-2 rounded-full bg-amber-400 inline-block animate-pulse" />;
  if (status === "ok") return <span className="w-2 h-2 rounded-full bg-emerald-500 inline-block" />;
  return <span className="w-2 h-2 rounded-full bg-destructive inline-block" />;
}

interface PipelineStepProps {
  step: PipelineStepType;
  isLast: boolean;
}

export default function PipelineStep({ step, isLast }: PipelineStepProps) {
  return (
    <div className="flex gap-4">
      <div className="flex flex-col items-center">
        <div
          className={cn(
            "flex items-center justify-center w-9 h-9 rounded-sm border transition-colors duration-300",
            statusBorder[step.status]
          )}
        >
          <HugeiconsIcon
            icon={step.icon}
            size={16}
            strokeWidth={1.5}
            className={cn("transition-colors duration-300", statusLabel[step.status])}
          />
        </div>
        {!isLast && (
          <div
            className={cn(
              "w-px flex-1 mt-1 transition-colors duration-500",
              step.status === "ok" ? "bg-emerald-500/30" : "bg-border"
            )}
          />
        )}
      </div>

      <div className="pb-6 flex-1">
        <div className="flex items-center gap-2 mt-1.5">
          <StatusDot status={step.status} />
          <span
            className={cn(
              "text-sm font-medium transition-colors duration-300",
              statusLabel[step.status]
            )}
          >
            {step.label}
          </span>
        </div>
        {step.detail && (
          <p className="text-xs text-muted-foreground mt-1 font-mono">{step.detail}</p>
        )}
      </div>
    </div>
  );
}
