"use client";

import { useState } from "react";

/* Sits on the dark CodeFrame strip, so it carries its own structure-colored styling. */
export default function CopyButton({ code }: { code: string }) {
  const [copied, setCopied] = useState(false);

  const copy = async () => {
    try {
      await navigator.clipboard.writeText(code);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    } catch {
      // Clipboard can be blocked (insecure origin, permissions); the code stays selectable.
    }
  };

  return (
    <button
      type="button"
      onClick={copy}
      className="h-7 rounded-full px-3 font-mono text-[11px] uppercase tracking-[0.14em] text-[var(--structure-text)] transition-colors hover:bg-[var(--structure-quiet)] hover:text-[var(--on-structure)] focus-visible:outline-2 focus-visible:outline-signal"
    >
      <span aria-live="polite">{copied ? "Copiado" : "Copiar"}</span>
    </button>
  );
}
