import { highlight } from "@/lib/shiki";
import LangCodeBlockClient from "./LangCodeBlockClient";
import type { SdkLang } from "@/contexts/docs-language";

interface LangVariant {
  code: string;
  lang: string; // shiki language id
}

type LangCodeBlockProps = Partial<Record<SdkLang, LangVariant>>;

export default async function LangCodeBlock(props: LangCodeBlockProps) {
  const entries = Object.entries(props) as [SdkLang, LangVariant][];

  const rendered = await Promise.all(
    entries.map(async ([sdkLang, variant]) => [
      sdkLang,
      {
        code: variant.code,
        html: await highlight(variant.code, variant.lang),
      },
    ] as const)
  );

  const variants = Object.fromEntries(rendered) as Partial<
    Record<SdkLang, { code: string; html: string }>
  >;

  return <LangCodeBlockClient variants={variants} />;
}
