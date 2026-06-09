"use client";

import { useDocLanguage, type SdkLang } from "@/contexts/docs-language";
import CopyButton from "./CopyButton";

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

export default function LangCodeBlockClient({ variants }: LangCodeBlockClientProps) {
  const { lang } = useDocLanguage();
  const variant = variants[lang];

  if (!variant) {
    return (
      <div className="border border-dashed border-border rounded-sm p-8 text-center my-6">
        <p className="text-sm text-muted-foreground">
          El SDK de{" "}
          <span className="font-medium text-foreground">{langLabels[lang]}</span>{" "}
          está en desarrollo.{" "}
          <a
            href={`/doc/${lang}`}
            className="underline underline-offset-2 hover:text-foreground"
          >
            Ver estado
          </a>
        </p>
      </div>
    );
  }

  return (
    <div className="border border-border rounded-sm overflow-hidden my-6">
      <div className="flex items-center justify-between px-4 py-2 bg-muted/50 border-b border-border">
        <span className="text-xs text-muted-foreground font-mono">
          {langLabels[lang].toLowerCase()}
        </span>
        <CopyButton code={variant.code} />
      </div>
      <div
        className="[&>pre]:p-5 [&>pre]:overflow-x-auto [&>pre]:text-xs [&>pre]:leading-relaxed [&>pre]:rounded-none"
        dangerouslySetInnerHTML={{ __html: variant.html }}
      />
    </div>
  );
}
