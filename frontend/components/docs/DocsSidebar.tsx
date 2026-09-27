"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { cn } from "@/lib/utils";
import { useDocLanguage, type SdkLang } from "@/contexts/docs-language";
import Mark from "@/components/layout/Mark";

const sections = [
  {
    label: "Empezar",
    items: [
      { href: "/doc", label: "Quick Start" },
      { href: "/doc/instalacion", label: "Instalación" },
    ],
  },
  {
    label: "Conceptos",
    items: [
      { href: "/doc/clave-acceso", label: "Clave de acceso" },
      { href: "/doc/xades-bes", label: "Firma XAdES-BES" },
      { href: "/doc/soap-sri", label: "SOAP del SRI" },
    ],
  },
  {
    label: "Referencia de la API",
    items: [
      { href: "/doc/api/invoice-builder", label: "InvoiceBuilder" },
      { href: "/doc/api/opensri-client", label: "OpenSRIClient" },
      { href: "/doc/api/send-invoice-result", label: "SendDocumentResult" },
    ],
  },
];

const languages: { lang: SdkLang; label: string; available: boolean }[] = [
  { lang: "java", label: "Java", available: true },
  { lang: "csharp", label: "C#", available: false },
  { lang: "go", label: "Go", available: false },
];

const groupLabel = "mb-2 font-mono text-[10.5px] uppercase tracking-[0.16em] text-muted-foreground";

export default function DocsSidebar() {
  return (
    <aside>
      {/* Phones and tablets: the index folds away above the article. */}
      <details className="group rounded-[var(--r-card-sm)] border border-border bg-card lg:hidden">
        <summary className="flex h-12 cursor-pointer list-none items-center justify-between px-4 text-sm font-medium [&::-webkit-details-marker]:hidden">
          Índice de la documentación
          <span aria-hidden="true" className="font-mono text-muted-foreground group-open:rotate-45 transition-transform">
            +
          </span>
        </summary>
        <div className="border-t border-border p-4">
          <SidebarNav />
        </div>
      </details>

      <div className="sticky top-24 hidden lg:block">
        <SidebarNav />
      </div>
    </aside>
  );
}

function SidebarNav() {
  const pathname = usePathname();
  const { lang, setLang } = useDocLanguage();

  return (
    <nav aria-label="Documentación" className="space-y-7">
      <div>
        <p className={groupLabel}>SDK</p>
        <div role="group" aria-label="Lenguaje del SDK" className="inline-flex rounded-full bg-[var(--v-beige)] p-1">
          {languages.map((l) => {
            const selected = lang === l.lang;
            return (
              <button
                key={l.lang}
                type="button"
                aria-pressed={selected}
                onClick={() => setLang(l.lang)}
                title={l.available ? undefined : "En desarrollo"}
                className={cn(
                  "h-8 rounded-full px-3.5 font-mono text-xs transition-colors",
                  selected
                    ? "bg-foreground text-background"
                    : "text-muted-foreground hover:text-foreground",
                )}
              >
                {l.label}
                {!l.available && <span className="sr-only"> (en desarrollo)</span>}
              </button>
            );
          })}
        </div>
      </div>

      {sections.map((section) => (
        <div key={section.label}>
          <p className={groupLabel}>{section.label}</p>
          <ul className="space-y-0.5">
            {section.items.map((item) => {
              const active = pathname === item.href;
              return (
                <li key={item.href}>
                  <Link
                    href={item.href}
                    aria-current={active ? "page" : undefined}
                    className={cn(
                      "-mx-3 flex h-9 items-center gap-2.5 rounded-full px-3 text-sm transition-colors",
                      active
                        ? "bg-[var(--v-beige)] font-medium text-foreground"
                        : "text-muted-foreground hover:bg-muted hover:text-foreground",
                    )}
                  >
                    {active && <Mark />}
                    {item.label}
                  </Link>
                </li>
              );
            })}
          </ul>
        </div>
      ))}
    </nav>
  );
}
