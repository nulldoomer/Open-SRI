"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { HugeiconsIcon } from "@hugeicons/react";
import { GithubIcon } from "@hugeicons/core-free-icons";
import { cn } from "@/lib/utils";
import { Button } from "@/components/ui/button";
import ThemeToggle from "./ThemeToggle";
import Mark from "./Mark";

const links = [
  { href: "/doc", label: "Docs" },
  { href: "/playground", label: "Playground" },
  { href: "/sdk", label: "SDK" },
  { href: "/about", label: "Acerca de" },
];

/*
 *========= Main application navbar =========
 *
 * - Sticky top navigation; on phones the links drop to a second, scrollable row.
 * - The active route carries the signal ■ marker.
 * - Client component because usePathname() is client-only.
 */
export default function Navbar() {
  const pathname = usePathname();
  const isActive = (href: string) => pathname === href || pathname.startsWith(`${href}/`);

  return (
    <header className="sticky top-0 z-50 border-b border-border bg-background/95 supports-[backdrop-filter]:bg-background/80 supports-[backdrop-filter]:backdrop-blur-md">
      <nav
        aria-label="Principal"
        className="mx-auto flex max-w-7xl flex-wrap items-center gap-x-8 gap-y-2 px-4 py-3 sm:px-8 lg:px-16"
      >
        <Link
          href="/"
          className="font-heading text-xl font-extrabold uppercase tracking-tight [font-stretch:92%]"
        >
          Open<span className="text-signal">SRI</span>
        </Link>

        <ul className="order-last -mx-1 flex w-full gap-1 overflow-x-auto md:order-none md:w-auto md:overflow-visible">
          {links.map((link) => {
            const active = isActive(link.href);
            return (
              <li key={link.href}>
                <Link
                  href={link.href}
                  aria-current={active ? "page" : undefined}
                  className={cn(
                    "flex h-9 items-center gap-2 whitespace-nowrap rounded-full px-3 text-sm transition-colors",
                    active
                      ? "font-semibold text-foreground"
                      : "text-muted-foreground hover:bg-muted hover:text-foreground",
                  )}
                >
                  {active && <Mark />}
                  {link.label}
                </Link>
              </li>
            );
          })}
        </ul>

        <div className="ml-auto flex items-center gap-1">
          <ThemeToggle />
          <Button asChild variant="ghost" size="sm" className="w-[var(--ctl-sm)] px-0">
            <a
              href="https://github.com/nulldoomer/Open-SRI"
              target="_blank"
              rel="noopener noreferrer"
              aria-label="Repositorio en GitHub"
            >
              <HugeiconsIcon icon={GithubIcon} size={18} strokeWidth={1.6} />
            </a>
          </Button>
          <Button asChild size="sm" className="ml-1 hidden sm:inline-flex">
            <Link href="/doc">Empezar</Link>
          </Button>
        </div>
      </nav>
    </header>
  );
}
