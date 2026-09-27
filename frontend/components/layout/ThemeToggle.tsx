"use client";

import { useSyncExternalStore } from "react";
import { ThemeToggle as Toggle, applyTheme, type ThemeMode } from "@/components/ui/theme-toggle";

/*
  ========= ThemeToggle =========
  - The inline script in app/layout.tsx sets <html data-mode> before paint.
  - This reads that attribute, so the icon always matches the page.
  - applyTheme() runs the Cojeev reveal; localStorage keeps the choice.
*/
function subscribe(onChange: () => void) {
  const observer = new MutationObserver(onChange);
  observer.observe(document.documentElement, { attributeFilter: ["data-mode"] });
  return () => observer.disconnect();
}

const readMode = (): ThemeMode =>
  document.documentElement.dataset.mode === "dark" ? "dark" : "light";

export default function ThemeToggle() {
  const mode = useSyncExternalStore(subscribe, readMode, () => "light" as const);

  return (
    <Toggle
      mode={mode}
      showLabel={false}
      aria-label="Modo oscuro"
      title={mode === "dark" ? "Cambiar a modo claro" : "Cambiar a modo oscuro"}
      onModeChange={(next) => {
        applyTheme(next);
        try {
          localStorage.setItem("theme", next);
        } catch {
          // Private windows can block storage; the mode still applies for this visit.
        }
      }}
    />
  );
}
