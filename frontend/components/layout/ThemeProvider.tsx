"use client";

import { ThemeProvider as NextThemesProvider } from "next-themes";
import type { ComponentProps } from "react";

/*
  ========= ThemeProvider wrapper =========
  - Implements next-themes for global theme management
  - Provides a consistent interface for theme toggling
  - Ensures SSR compatibility with defaultTheme and disableTransitionOnChange
*/
export default function ThemeProvider({
  children,
  ...props
}: ComponentProps<typeof NextThemesProvider>) {
  return <NextThemesProvider {...props}>{children}</NextThemesProvider>;
}
