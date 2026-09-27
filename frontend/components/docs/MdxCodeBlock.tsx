import CodeBlock from "@/components/ui/CodeBlock";
import CodeFrame from "@/components/ui/CodeFrame";

interface CodeProps {
  className?: string;
  children?: string;
}

interface PreProps {
  children?: React.ReactElement<CodeProps>;
}

export default function MdxCodeBlock({ children }: PreProps) {
  const className = children?.props?.className ?? "";
  const code = (children?.props?.children ?? "").trimEnd();
  const lang = className.replace("language-", "") || "text";

  return (
    <CodeFrame label={lang} code={code} className="my-6">
      <CodeBlock code={code} lang={lang} />
    </CodeFrame>
  );
}
