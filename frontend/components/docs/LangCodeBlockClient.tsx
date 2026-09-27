"use client";

import { useDocLanguage, type SdkLang } from "@/contexts/docs-language";
import CodeFrame from "@/components/ui/CodeFrame";

interface RenderedVariant {
  code: string;
  html: string;
}

interface LangCodeBlockClientProps {
  variants: Partial<Record<SdkLang, RenderedVariant>>;
}

const langLabels: Record<SdkLang, string> = {
  java: "Java",
  csharp: "C#",
  go: "Go",
};

export const shikiBody =
  "[&>pre]:!bg-transparent [&>pre]:overflow-x-auto [&>pre]:p-5 [&>pre]:font-mono [&>pre]:text-[13px] [&>pre]:leading-relaxed";

export default function LangCodeBlockClient({ variants }: LangCodeBlockClientProps) {
  const { lang } = useDocLanguage();
  const variant = variants[lang];

  if (!variant) {
    return (
      <div className="my-6 rounded-[var(--r-card-sm)] border border-dashed border-[var(--v-edge)] px-6 py-8 text-center">
        <p className="font-mono text-[10.5px] uppercase tracking-[0.16em] text-muted-foreground">En desarrollo</p>
        <p className="mt-2 text-[15px] text-muted-foreground">
          El SDK de <span className="font-medium text-foreground">{langLabels[lang]}</span> todavía no está disponible.{" "}
          <a
            href={`/doc/${lang}`}
            className="text-foreground underline decoration-border underline-offset-4 hover:decoration-signal"
          >
            Ver estado
          </a>
        </p>
      </div>
    );
  }

  return (
    <CodeFrame label={langLabels[lang].toLowerCase()} code={variant.code} className="my-6">
      <div className={shikiBody} dangerouslySetInnerHTML={{ __html: variant.html }} />
    </CodeFrame>
  );
}
