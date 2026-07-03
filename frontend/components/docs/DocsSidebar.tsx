"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { cn } from "@/lib/utils";
import { useDocLanguage, type SdkLang } from "@/contexts/docs-language";

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
    label: "API Reference",
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

export default function DocsSidebar() {
  const pathname = usePathname();
  const { lang, setLang } = useDocLanguage();

  return (
    <aside className="w-56 shrink-0">
      <nav className="sticky top-8 space-y-6">
        {/* Language selector */}
        <div>
          <p className="text-xs font-semibold tracking-widest uppercase text-muted-foreground mb-2">
            SDK
          </p>
          <div className="flex gap-1">
            {languages.map((l) => (
              <button
                key={l.lang}
                onClick={() => setLang(l.lang)}
                title={l.available ? undefined : "Próximamente"}
                className={cn(
                  "relative px-3 py-1 text-xs font-mono rounded-sm border transition-colors",
                  lang === l.lang
                    ? "border-foreground bg-foreground text-background"
                    : "border-border text-muted-foreground hover:text-foreground hover:border-foreground/40",
                  !l.available && "opacity-50"
                )}
              >
                {l.label}
                {!l.available && (
                  <span className="absolute -top-1.5 -right-1.5 text-[9px] leading-none bg-muted text-muted-foreground border border-border rounded px-0.5">
                    soon
                  </span>
                )}
              </button>
            ))}
          </div>
        </div>

        {/* Nav sections */}
        {sections.map((section) => (
          <div key={section.label}>
            <p className="text-xs font-semibold tracking-widest uppercase text-muted-foreground mb-2">
              {section.label}
            </p>
            <ul className="space-y-0.5">
              {section.items.map((item) => (
                <li key={item.href}>
                  <Link
                    href={item.href}
                    className={cn(
                      "block text-sm px-2 py-1 rounded-sm transition-colors",
                      pathname === item.href
                        ? "text-foreground font-medium bg-muted"
                        : "text-muted-foreground hover:text-foreground"
                    )}
                  >
                    {item.label}
                  </Link>
                </li>
              ))}
            </ul>
          </div>
        ))}
      </nav>
    </aside>
  );
}
