import Link from "next/link";
import { HugeiconsIcon } from "@hugeicons/react";
import { GithubIcon, ArrowRight01Icon } from "@hugeicons/core-free-icons";
import { Button } from "@/components/ui/button";
import Mark from "@/components/layout/Mark";
import Waybill from "./Waybill";
import TrackingRoute from "./TrackingRoute";

const javaSDKVersion = "1.2.4";

export default function HeroSection() {
  return (
    <section className="border-b border-border bg-background">
      <div className="mx-auto max-w-7xl px-4 pb-16 pt-6 sm:px-8 lg:px-16">
        {/* System status bar */}
        <div className="flex items-center gap-3 font-mono text-[11px] uppercase tracking-[0.18em] text-muted-foreground">
          <Mark count={3} />
          <span className="whitespace-nowrap">
            <span className="hidden sm:inline">Null-privatization / </span>OpenSRI
          </span>
          <span aria-hidden="true" className="h-px flex-1 bg-border" />
          <span className="hidden whitespace-nowrap tabular-nums sm:inline">SDK Java {javaSDKVersion}</span>
          <span aria-hidden="true" className="hidden h-3 w-px bg-border sm:block" />
          <span className="flex items-center gap-2 whitespace-nowrap text-foreground">
            <span className="size-1.5 rounded-full bg-[var(--v-olive-deep)]" aria-hidden="true" />
            SRI pruebas
          </span>
        </div>

        <div className="mt-10 grid items-start gap-12 lg:mt-12 lg:grid-cols-12 lg:gap-8">
          <div className="lg:col-span-6 lg:pt-4">
            <h1 className="font-heading font-extrabold uppercase leading-[0.86] tracking-[-0.02em] [font-stretch:78%] text-[clamp(3.1rem,8vw,6rem)] text-balance">
              Facturación electrónica <span className="text-signal">accesible</span>
            </h1>

            <p className="mt-8 max-w-[34rem] text-lg leading-relaxed text-muted-foreground">
              Clave de acceso de 49 dígitos, XML validado por XSD, firma XAdES-BES con P12 y
              comunicación SOAP con el SRI, encapsulados en una sola llamada open source.
            </p>

            <p className="mt-6 inline-flex items-center gap-2 rounded-full bg-[var(--status-ok-bg)] px-3 py-1.5 text-sm text-[var(--status-ok-ink)]">
              <span className="size-1.5 rounded-full bg-current" aria-hidden="true" />
              Ya disponible: SDK Java {javaSDKVersion} en Maven Central
            </p>

            <div className="mt-9 flex flex-wrap items-center gap-3">
              <Button asChild size="lg" className="min-w-[13rem]">
                <Link href="/playground">
                  Probar el Playground
                  <HugeiconsIcon icon={ArrowRight01Icon} size={16} strokeWidth={2} />
                </Link>
              </Button>
              <Button asChild size="lg" variant="outline" className="min-w-[13rem]">
                <Link href="/doc">Quick Start</Link>
              </Button>
              <a
                href="https://github.com/nulldoomer/Open-SRI"
                target="_blank"
                rel="noopener noreferrer"
                className="ml-1 inline-flex h-11 items-center gap-2 text-sm text-muted-foreground underline decoration-border underline-offset-4 transition-colors hover:text-foreground hover:decoration-signal"
              >
                <HugeiconsIcon icon={GithubIcon} size={16} strokeWidth={1.6} />
                Código en GitHub
              </a>
            </div>
          </div>

          <div className="lg:col-span-5 lg:col-start-8">
            <Waybill />
          </div>
        </div>

        <div className="mt-14 border-t border-border pt-8 lg:mt-12">
          <TrackingRoute />
        </div>
      </div>
    </section>
  );
}
