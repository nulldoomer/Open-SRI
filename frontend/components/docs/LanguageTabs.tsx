import { highlight } from "@/lib/shiki";
import LanguageTabsClient from "./LanguageTabsClient";

export interface Tab {
  label: string;
  code: string;
  lang: string;
}

export default async function LanguageTabs({ tabs }: { tabs: Tab[] }) {
  const rendered = await Promise.all(
    tabs.map(async ({ label, code, lang }) => ({
      label,
      code,
      html: await highlight(code, lang),
    }))
  );
  return <LanguageTabsClient tabs={rendered} />;
}
