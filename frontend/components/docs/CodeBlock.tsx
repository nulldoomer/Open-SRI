import CodeBlock from "@/components/ui/CodeBlock";
import CopyButton from "./CopyButton";

interface DocsCodeBlockProps {
  code: string;
  language?: string;
  lang?: string;
}

export default function DocsCodeBlock({ code, language = "java", lang }: DocsCodeBlockProps) {
  // `language` is the display label; `lang` is the Shiki language id (falls back to language)
  const shikiLang = lang ?? language;
  return (
    <div className="relative group border border-border rounded-sm overflow-hidden">
      <div className="flex items-center justify-between px-4 py-2 bg-muted/50 border-b border-border">
        <span className="text-xs text-muted-foreground font-mono">{language}</span>
        <CopyButton code={code} />
      </div>
      <CodeBlock code={code} lang={shikiLang} />
    </div>
  );
}
