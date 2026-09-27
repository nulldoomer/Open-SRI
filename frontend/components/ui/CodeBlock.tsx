import { highlight } from "@/lib/shiki";
import { cn } from "@/lib/utils";

interface CodeBlockProps {
  code: string;
  lang: string;
  className?: string;
}

export default async function CodeBlock({ code, lang, className }: CodeBlockProps) {
  let html: string;
  try {
    html = await highlight(code, lang);
  } catch {
    html = `<pre><code>${code.replace(/&/g, "&amp;").replace(/</g, "&lt;")}</code></pre>`;
  }
  return (
    <div
      className={cn(
        "[&>pre]:!bg-transparent [&>pre]:overflow-x-auto [&>pre]:p-5 [&>pre]:font-mono [&>pre]:text-[13px] [&>pre]:leading-relaxed",
        className,
      )}
      // highlight() returns Shiki-generated HTML; the fallback escapes the code itself
      dangerouslySetInnerHTML={{ __html: html }}
    />
  );
}
