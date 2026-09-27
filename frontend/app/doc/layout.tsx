import DocsSidebar from "@/components/docs/DocsSidebar";
import { LanguageProvider } from "@/contexts/docs-language";

export default function DocLayout({ children }: { children: React.ReactNode }) {
  return (
    <LanguageProvider>
      <div className="mx-auto grid w-full max-w-7xl gap-8 px-4 py-10 sm:px-8 lg:grid-cols-[14rem_minmax(0,1fr)] lg:gap-16 lg:px-16 lg:py-16">
        <DocsSidebar />
        <main className="min-w-0 max-w-[72ch]">{children}</main>
      </div>
    </LanguageProvider>
  );
}
