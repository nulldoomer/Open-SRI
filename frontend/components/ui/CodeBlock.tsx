import { highlight } from "@/lib/shiki";

interface CodeBlockProps {
  code: string;
  lang: string;
  className?: string;
}

export default async function CodeBlock({ code, lang, className = "" }: CodeBlockProps) {
  const html = await highlight(code, lang);
  return (
    <div
      className={`[&>pre]:p-5 [&>pre]:overflow-x-auto [&>pre]:text-xs [&>pre]:leading-relaxed [&>pre]:rounded-none ${className}`}
      // highlight() returns Shiki-generated HTML which is safe — no user input is passed unsanitized
      dangerouslySetInnerHTML={{ __html: html }}
    />
  );
}
