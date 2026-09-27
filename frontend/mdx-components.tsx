import type { MDXComponents } from "mdx/types";
import MdxCodeBlock from "@/components/docs/MdxCodeBlock";

const components: MDXComponents = {
  h1: ({ children }) => (
    <h1 className="mb-4 font-heading text-[clamp(2.25rem,4.5vw,3.25rem)] font-extrabold uppercase leading-[0.92] tracking-[-0.015em] [font-stretch:82%] text-balance">
      {children}
    </h1>
  ),
  h2: ({ children }) => (
    <h2 className="mb-4 mt-14 border-t border-border pt-8 font-heading text-2xl font-bold tracking-tight first:mt-0">
      {children}
    </h2>
  ),
  h3: ({ children }) => <h3 className="mb-3 mt-8 font-heading text-lg font-semibold">{children}</h3>,
  p: ({ children }) => <p className="mb-5 text-[15px] leading-7 text-muted-foreground">{children}</p>,
  ul: ({ children }) => (
    <ul className="mb-5 list-disc space-y-2 pl-5 text-[15px] leading-7 text-muted-foreground marker:text-border">
      {children}
    </ul>
  ),
  ol: ({ children }) => (
    <ol className="mb-5 list-decimal space-y-2 pl-5 text-[15px] leading-7 text-muted-foreground marker:font-mono marker:text-xs">
      {children}
    </ol>
  ),
  li: ({ children }) => <li className="pl-1">{children}</li>,
  strong: ({ children }) => <strong className="font-semibold text-foreground">{children}</strong>,
  a: ({ href, children }) => (
    <a
      href={href}
      className="text-foreground underline decoration-border underline-offset-4 transition-colors hover:decoration-signal"
    >
      {children}
    </a>
  ),
  code: ({ children }) => (
    <code className="rounded-[6px] bg-[var(--v-beige)] px-1.5 py-0.5 font-mono text-[0.85em] text-foreground">
      {children}
    </code>
  ),
  pre: MdxCodeBlock as MDXComponents["pre"],
  hr: () => <hr className="my-10 border-border" />,
  blockquote: ({ children }) => (
    <blockquote className="my-6 rounded-[var(--r-card-sm)] bg-muted px-5 py-4 text-[15px] text-muted-foreground [&_p]:mb-0">
      {children}
    </blockquote>
  ),
  table: ({ children }) => (
    <div className="my-6 overflow-x-auto rounded-[var(--r-card-sm)] border border-border">
      <table className="w-full border-collapse text-sm">{children}</table>
    </div>
  ),
  thead: ({ children }) => <thead className="border-b border-border bg-muted">{children}</thead>,
  tbody: ({ children }) => <tbody className="divide-y divide-border">{children}</tbody>,
  tr: ({ children }) => <tr>{children}</tr>,
  th: ({ children }) => (
    <th className="px-4 py-3 text-left font-mono text-[10.5px] font-normal uppercase tracking-[0.14em] text-muted-foreground">
      {children}
    </th>
  ),
  td: ({ children }) => <td className="px-4 py-3 align-top text-sm text-foreground">{children}</td>,
};

export function useMDXComponents(): MDXComponents {
  return components;
}
