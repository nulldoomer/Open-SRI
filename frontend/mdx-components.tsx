import type { MDXComponents } from "mdx/types";
import MdxCodeBlock from "@/components/docs/MdxCodeBlock";

const components: MDXComponents = {
  h1: ({ children }) => (
    <h1 className="font-heading text-4xl font-bold mb-3">{children}</h1>
  ),
  h2: ({ children }) => (
    <h2 className="font-heading font-semibold text-xl mb-4 mt-10 first:mt-0 pt-6 border-t border-border">
      {children}
    </h2>
  ),
  h3: ({ children }) => (
    <h3 className="font-heading font-semibold text-base mb-3 mt-6">{children}</h3>
  ),
  p: ({ children }) => (
    <p className="text-muted-foreground text-sm leading-relaxed mb-4">{children}</p>
  ),
  ul: ({ children }) => (
    <ul className="space-y-1.5 text-sm text-muted-foreground mb-4 list-disc list-inside">
      {children}
    </ul>
  ),
  ol: ({ children }) => (
    <ol className="space-y-1.5 text-sm text-muted-foreground mb-4 list-decimal list-inside">
      {children}
    </ol>
  ),
  li: ({ children }) => <li className="leading-relaxed">{children}</li>,
  strong: ({ children }) => (
    <strong className="font-medium text-foreground">{children}</strong>
  ),
  a: ({ href, children }) => (
    <a
      href={href}
      className="underline underline-offset-2 hover:text-foreground transition-colors"
    >
      {children}
    </a>
  ),
  code: ({ children }) => (
    <code className="font-mono text-xs bg-muted px-1.5 py-0.5 rounded border border-border">
      {children}
    </code>
  ),
  pre: MdxCodeBlock as MDXComponents["pre"],
  hr: () => <hr className="border-border my-8" />,
  blockquote: ({ children }) => (
    <blockquote className="border-l-2 border-border pl-4 text-sm text-muted-foreground italic my-4">
      {children}
    </blockquote>
  ),
  table: ({ children }) => (
    <div className="overflow-x-auto my-6">
      <table className="w-full text-sm border-collapse">{children}</table>
    </div>
  ),
  thead: ({ children }) => (
    <thead className="border-b border-border">{children}</thead>
  ),
  tbody: ({ children }) => (
    <tbody className="divide-y divide-border">{children}</tbody>
  ),
  tr: ({ children }) => <tr className="group">{children}</tr>,
  th: ({ children }) => (
    <th className="px-4 py-2.5 text-left text-xs font-semibold uppercase tracking-wider text-muted-foreground">
      {children}
    </th>
  ),
  td: ({ children }) => (
    <td className="px-4 py-2.5 text-sm text-muted-foreground group-hover:text-foreground transition-colors">
      {children}
    </td>
  ),
};

export function useMDXComponents(): MDXComponents {
  return components;
}
