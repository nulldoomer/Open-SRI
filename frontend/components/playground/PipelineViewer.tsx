import { HugeiconsIcon } from "@hugeicons/react";
import { Invoice01Icon } from "@hugeicons/core-free-icons";
import type { PipelineStep as PipelineStepType } from "@/types/playground";
import PipelineStep from "./PipelineStep";

interface PipelineViewerProps {
  steps: PipelineStepType[];
  elapsed: number;
  isRunning: boolean;
}

const ESTIMATED_MS = 4000;

export default function PipelineViewer({ steps, elapsed, isRunning }: PipelineViewerProps) {
  const completedSteps = steps.filter((s) => s.status === "ok" || s.status === "error").length;
  const progress = Math.min((elapsed / ESTIMATED_MS) * 100, 100);

  if (steps.every((s) => s.status === "idle") && !isRunning) {
    return (
      <div className="flex flex-col items-center justify-center py-16 gap-3 text-center">
        <HugeiconsIcon
          icon={Invoice01Icon}
          size={36}
          strokeWidth={1}
          className="text-muted-foreground/30"
        />
        <p className="text-muted-foreground text-sm">
          Completa el formulario y pulsa{" "}
          <span className="font-medium text-foreground">Generar + enviar</span> para ver el
          pipeline en tiempo real.
        </p>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      {isRunning && (
        <div className="space-y-1">
          <div className="flex justify-between text-xs text-muted-foreground">
            <span>Paso {completedSteps}/{steps.length}</span>
            <span>{(elapsed / 1000).toFixed(1)}s</span>
          </div>
          <div className="w-full h-1 bg-muted rounded-full overflow-hidden">
            <div
              className="h-full bg-emerald-500 transition-all duration-200 rounded-full"
              style={{ width: `${progress}%` }}
            />
          </div>
        </div>
      )}

      <div className="pt-2">
        {steps.map((step, i) => (
          <PipelineStep key={step.id} step={step} isLast={i === steps.length - 1} />
        ))}
      </div>
    </div>
  );
}
