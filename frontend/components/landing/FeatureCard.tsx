import { HugeiconsIcon } from "@hugeicons/react";
import type { HugeiconsIconProps } from "@hugeicons/react";

type HugeIcon = NonNullable<HugeiconsIconProps["icon"]>;

interface FeatureCardProps {
  icon: HugeIcon;
  title: string;
  description: string;
  accent?: "red" | "amber" | "emerald";
}

const accentMap = {
  red: {
    headerBg: "bg-red-50 dark:bg-red-950/40",
    iconColor: "text-red-600 dark:text-red-500",
    labelColor: "text-red-600 dark:text-red-400",
    topBorder: "border-t-red-600",
  },
  amber: {
    headerBg: "bg-amber-50 dark:bg-amber-950/30",
    iconColor: "text-amber-600 dark:text-amber-400",
    labelColor: "text-amber-600 dark:text-amber-400",
    topBorder: "border-t-amber-500",
  },
  emerald: {
    headerBg: "bg-emerald-50 dark:bg-emerald-950/30",
    iconColor: "text-emerald-700 dark:text-emerald-400",
    labelColor: "text-emerald-700 dark:text-emerald-400",
    topBorder: "border-t-emerald-600",
  },
};

export default function FeatureCard({
  icon,
  title,
  description,
  accent = "red",
}: FeatureCardProps) {
  const s = accentMap[accent];

  return (
    <div
      className={`border border-neutral-200 dark:border-neutral-800 bg-white dark:bg-neutral-900/80 overflow-hidden border-t-2 ${s.topBorder}`}
    >
      <div
        className={`flex items-center gap-2.5 px-5 py-3 border-b border-neutral-200 dark:border-neutral-800 ${s.headerBg}`}
      >
        <HugeiconsIcon icon={icon} size={14} strokeWidth={2} className={s.iconColor} />
        <span
          className={`font-mono text-[10px] uppercase tracking-widest font-semibold ${s.labelColor}`}
        >
          {title}
        </span>
      </div>
      <div className="px-5 py-5">
        <p className="text-neutral-600 dark:text-neutral-400 text-sm leading-relaxed">
          {description}
        </p>
      </div>
    </div>
  );
}
