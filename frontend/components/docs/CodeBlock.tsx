import CodeBlock from "@/components/ui/CodeBlock";
import CodeFrame from "@/components/ui/CodeFrame";

interface DocsCodeBlockProps {
  code: string;
  language?: string;
  lang?: string;
}

export default function DocsCodeBlock({ code, language = "java", lang }: DocsCodeBlockProps) {
  // `language` is the display label; `lang` is the Shiki language id (falls back to language)
  return (
    <CodeFrame label={language} code={code}>
      <CodeBlock code={code} lang={lang ?? language} />
    </CodeFrame>
  );
}
