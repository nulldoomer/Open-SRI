"use client";

import { useTheme } from "next-themes";
import { useEffect, useState } from "react";
import { HugeiconsIcon } from "@hugeicons/react";
import { Sun01Icon, Moon01Icon } from "@hugeicons/core-free-icons";

/*
  ========= ThemeToggle component =========
  - Provides a button to toggle between light and dark themes
  - Uses next-themes for theme management
  - Displays appropriate icon based on current theme
  - Handles SSR hydration with mounted state
*/
export default function ThemeToggle() {

  const { resolvedTheme, setTheme } = useTheme();
  const [mounted, setMounted] = useState(false);

  useEffect(() => setMounted(true), []);

  return (
    <button
      onClick={() => setTheme(resolvedTheme === "dark" ? "light" : "dark")}
      aria-label="Toggle theme"
      className="text-muted-foreground hover:text-foreground transition-colors p-1.5 rounded-sm border border-border hover:border-foreground/30"
    >
      {mounted ? (
        <HugeiconsIcon
          icon={resolvedTheme === "dark" ? Sun01Icon : Moon01Icon}
          size={16}
          strokeWidth={1.5}
        />
      ) : (
        <span className="w-4 h-4 block" />
      )}
    </button>
  );
}
