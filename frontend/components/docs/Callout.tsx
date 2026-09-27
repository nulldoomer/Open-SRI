import { cn } from "@/lib/utils";

type CalloutType = "info" | "warning" | "note";

interface CalloutProps {
  type?: CalloutType;
  children: React.ReactNode;
}

const styles: Record<CalloutType, string> = {
  warning: "bg-[var(--status-warn-bg)] text-[var(--status-warn-ink)] ring-[var(--v-yellow)]",
  info: "bg-[var(--status-info-bg)] text-[var(--status-info-ink)] ring-[var(--v-blue)]",
  note: "bg-muted text-muted-foreground ring-border",
};

const labels: Record<CalloutType, string> = {
  warning: "Advertencia",
  info: "Nota",
  note: "Nota",
};

export default function Callout({ type = "info", children }: CalloutProps) {
  return (
    <aside className={cn("my-6 rounded-[var(--r-card-sm)] px-5 py-4 text-[15px] leading-relaxed ring-1 ring-inset", styles[type])}>
      <p className="mb-1 font-mono text-[10.5px] font-semibold uppercase tracking-[0.16em]">{labels[type]}</p>
      <div className="[&_p]:mb-0 [&_p]:text-inherit">{children}</div>
    </aside>
  );
}
