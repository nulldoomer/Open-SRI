"use client";

import { useState } from "react";
import { Tabs, TabsList, TabsTrigger, TabsContent } from "@/components/ui/tabs";
import CodeFrame from "@/components/ui/CodeFrame";
import { shikiBody } from "./LangCodeBlockClient";

export interface RenderedTab {
  label: string;
  code: string;
  html: string;
}

export default function LanguageTabsClient({ tabs }: { tabs: RenderedTab[] }) {
  const [active, setActive] = useState(tabs[0]?.label ?? "");
  const activeCode = tabs.find((t) => t.label === active)?.code ?? tabs[0]?.code ?? "";

  return (
    // The strip is dark, so the tab tokens are re-pointed at the structure palette.
    <Tabs
      value={active}
      onValueChange={setActive}
      variant="underline"
      className="gap-0 [--v-border:transparent] [--v-text-2:var(--structure-text)] [--v-text:var(--on-structure)]"
    >
      <CodeFrame
        code={activeCode}
        label={
          <TabsList variant="underline" aria-label="Gestor de dependencias">
            {tabs.map((tab) => (
              <TabsTrigger key={tab.label} value={tab.label} className="!py-2.5 !text-sm font-sans normal-case tracking-normal">
                {tab.label}
              </TabsTrigger>
            ))}
          </TabsList>
        }
      >
        {tabs.map((tab) => (
          <TabsContent key={tab.label} value={tab.label} className="mt-0">
            <div className={shikiBody} dangerouslySetInnerHTML={{ __html: tab.html }} />
          </TabsContent>
        ))}
      </CodeFrame>
    </Tabs>
  );
}
