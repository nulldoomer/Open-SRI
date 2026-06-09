import { highlight } from "@/lib/shiki";
import CopyButton from "./CopyButton";

interface CodeProps {
  className?: string;
  children?: string;
}

interface PreProps {
  children?: React.ReactElement<CodeProps>;
}

export default async function MdxCodeBlock({ children }: PreProps) {
  const className = children?.props?.className ?? "";
  const code = (children?.props?.children ?? "").trimEnd();
  const lang = className.replace("language-", "") || "text";

  let html: string;
  try {
    html = await highlight(code, lang);
  } catch {
    html = `<pre><code>${code.replace(/</g, "&lt;")}</code></pre>`;
  }

  return (
    <div className="relative border border-border rounded-sm overflow-hidden my-6">
      <div className="flex items-center justify-between px-4 py-2 bg-muted/50 border-b border-border">
        <span className="text-xs text-muted-foreground font-mono">{lang}</span>
        <CopyButton code={code} />
      </div>
      <div
        className="[&>pre]:p-5 [&>pre]:overflow-x-auto [&>pre]:text-xs [&>pre]:leading-relaxed [&>pre]:rounded-none"
        dangerouslySetInnerHTML={{ __html: html }}
      />
    </div>
  );
}
