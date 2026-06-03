"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";

interface InstallSnippetProps {
  code: string;
  available: boolean;
}

export default function InstallSnippet({ code, available }: InstallSnippetProps) {
  const [copied, setCopied] = useState(false);

  if (!available) {
    return <span className="text-muted-foreground text-xs">Próximamente</span>;
  }

  const copy = async () => {
    await navigator.clipboard.writeText(code);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <div className="flex items-center gap-3 min-w-0">
      <code className="font-mono text-xs bg-muted px-3 py-1.5 rounded-sm truncate max-w-xs">
        {code}
      </code>
      <Button variant="ghost" size="xs" onClick={copy} className="shrink-0">
        {copied ? "Copiado" : "Copiar"}
      </Button>
    </div>
  );
}
