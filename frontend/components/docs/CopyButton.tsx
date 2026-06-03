"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";

export default function CopyButton({ code }: { code: string }) {
  const [copied, setCopied] = useState(false);

  const copy = async () => {
    await navigator.clipboard.writeText(code);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <Button variant="ghost" size="xs" onClick={copy}>
      {copied ? "Copiado" : "Copiar"}
    </Button>
  );
}
