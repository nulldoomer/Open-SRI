"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { cn } from "@/lib/utils";

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
      { href: "/doc/api/send-invoice-result", label: "SendInvoiceResult" },
    ],
  },
  {
    label: "Lenguajes",
    items: [
      { href: "/doc/java", label: "Java" },
      { href: "/doc/csharp", label: "C#" },
      { href: "/doc/go", label: "Go" },
    ],
  },
];

export default function DocsSidebar() {
  const pathname = usePathname();

  return (
    <aside className="w-56 shrink-0">
      <nav className="sticky top-8 space-y-6">
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
