import { cn } from "@/lib/utils";

type CalloutType = "info" | "warning" | "note";

interface CalloutProps {
  type?: CalloutType;
  children: React.ReactNode;
}

const styles: Record<CalloutType, string> = {
  warning:
    "border-amber-200 bg-amber-50 text-amber-800 dark:border-amber-900/50 dark:bg-amber-950/20 dark:text-amber-200",
  info: "border-blue-200 bg-blue-50 text-blue-800 dark:border-blue-900/50 dark:bg-blue-950/20 dark:text-blue-200",
  note: "border-border bg-muted/50 text-muted-foreground",
};

const labels: Record<CalloutType, string> = {
  warning: "Advertencia",
  info: "Nota",
  note: "Nota",
};

export default function Callout({ type = "info", children }: CalloutProps) {
  return (
    <div className={cn("p-4 border rounded-sm text-sm my-6", styles[type])}>
      <p className="font-semibold mb-1">{labels[type]}</p>
      <div>{children}</div>
    </div>
  );
}
