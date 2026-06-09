import DocsSidebar from "@/components/docs/DocsSidebar";
import { LanguageProvider } from "@/contexts/docs-language";

export default function DocLayout({ children }: { children: React.ReactNode }) {
  return (
    <LanguageProvider>
      <div className="flex gap-12 max-w-6xl mx-auto px-8 py-16 min-h-screen">
        <DocsSidebar />
        <main className="flex-1 min-w-0">{children}</main>
      </div>
    </LanguageProvider>
  );
}
