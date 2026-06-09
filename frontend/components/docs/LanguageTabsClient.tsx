"use client";

import { useState } from "react";
import { Tabs, TabsList, TabsTrigger, TabsContent } from "@/components/ui/tabs";
import CopyButton from "./CopyButton";

export interface RenderedTab {
  label: string;
  code: string;
  html: string;
}

export default function LanguageTabsClient({ tabs }: { tabs: RenderedTab[] }) {
  const [active, setActive] = useState(tabs[0]?.label ?? "");
  const activeCode = tabs.find((t) => t.label === active)?.code ?? tabs[0]?.code ?? "";

  return (
    <Tabs
      value={active}
      onValueChange={setActive}
      className="border border-border rounded-sm overflow-hidden"
    >
      <div className="flex items-center justify-between px-4 py-2 bg-muted/50 border-b border-border">
        <TabsList variant="line" className="h-auto p-0 bg-transparent gap-1">
          {tabs.map((tab) => (
            <TabsTrigger key={tab.label} value={tab.label}>
              {tab.label}
            </TabsTrigger>
          ))}
        </TabsList>
        <CopyButton code={activeCode} />
      </div>
      {tabs.map((tab) => (
        <TabsContent key={tab.label} value={tab.label} className="mt-0">
          <div
            className="[&>pre]:p-5 [&>pre]:overflow-x-auto [&>pre]:text-xs [&>pre]:leading-relaxed [&>pre]:rounded-none"
            dangerouslySetInnerHTML={{ __html: tab.html }}
          />
        </TabsContent>
      ))}
    </Tabs>
  );
}
