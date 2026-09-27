import type { PipelineStep as PipelineStepType } from "@/types/playground";
import PipelineStep from "./PipelineStep";

interface PipelineViewerProps {
  steps: PipelineStepType[];
  elapsed: number;
  isRunning: boolean;
}

export default function PipelineViewer({ steps, elapsed, isRunning }: PipelineViewerProps) {
  const completed = steps.filter((s) => s.status === "ok" || s.status === "error").length;
  const idle = steps.every((s) => s.status === "idle") && !isRunning;

  return (
    <div className="space-y-6">
      {idle ? (
        <p className="text-sm leading-relaxed text-muted-foreground">
          Completa la guía y pulsa <span className="font-medium text-foreground">Generar y enviar al SRI</span>. Cada
          paso se marcará aquí a medida que el SDK lo ejecute.
        </p>
      ) : (
        <p aria-live="polite" className="flex justify-between font-mono text-xs tabular-nums text-muted-foreground">
          <span>
            Paso {completed}/{steps.length}
          </span>
          <span>{(elapsed / 1000).toFixed(1)} s</span>
        </p>
      )}

      <ol aria-label="Pasos del pipeline">
        {steps.map((step, i) => (
          <PipelineStep key={step.id} step={step} index={i} isLast={i === steps.length - 1} />
        ))}
      </ol>
    </div>
  );
}
