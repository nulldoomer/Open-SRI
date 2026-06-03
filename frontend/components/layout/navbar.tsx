"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { Button } from "../ui/button";
import ThemeToggle from "./ThemeToggle";

const links = [

  { href: "/", label: "Why" },
  { href: "/doc", label: "Docs" },
  { href: "/playground", label: "Playground" },
  { href: "/sdk", label: "SDK" },
  { href: "/about", label: "About" }

];

/*
 *========= Main application navbar =========
 *
 * Features:
 * - Sticky top navigation
 * - Active route highlighting
 * - Theme switching
 * - Primary CTA access
 *
 * Client component is required because:
 * - usePathname() is client-only
 * - ThemeToggle requires interactivity
 */

export default function Navbar() {

  const pathname = usePathname();

  return (

    <nav className="flex items-center justify-between px-8 py-4 border-b border-border bg-background/80 backdrop-blur-sm sticky top-0 z-50">

      <Link href="/" className="font-heading font-bold text-lg tracking-tight">

        OpenSRI

      </Link>

      <div className="flex items-center gap-8">

        {
          links.map(

            (link) => (

              <Link

                key={link.href}
                href={link.href}

                className={
                  pathname === link.href
                    ? "text-sm font-semibold"
                    : "text-sm text-muted-foreground hover:text-foreground transition-colors"

                }
              >
                {link.label}
              </Link>
            )
          )
        }

      </div>

      {
        /* 
          Righ side section of the navbar where the theme toggle and the
          Get Started button are located.
        */
      }

      <div className="flex items-center gap-3">

        <ThemeToggle />

        <Button asChild size="sm">

          <Link href="/playground">Get Started</Link>

        </Button>

      </div>

    </nav>

  );

}
