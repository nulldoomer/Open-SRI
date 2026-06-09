"use client";

import { createContext, useContext, useState } from "react";

export type SdkLang = "java" | "csharp" | "go";

interface LanguageContextValue {
  lang: SdkLang;
  setLang: (lang: SdkLang) => void;
}

const LanguageContext = createContext<LanguageContextValue>({
  lang: "java",
  setLang: () => {},
});

export function LanguageProvider({ children }: { children: React.ReactNode }) {
  const [lang, setLang] = useState<SdkLang>("java");
  return (
    <LanguageContext.Provider value={{ lang, setLang }}>
      {children}
    </LanguageContext.Provider>
  );
}

export function useDocLanguage() {
  return useContext(LanguageContext);
}
