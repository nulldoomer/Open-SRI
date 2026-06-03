import Link from "next/link";
import { HugeiconsIcon } from "@hugeicons/react";
import { GithubIcon, ArrowRight01Icon } from "@hugeicons/core-free-icons";
import { Button } from "@/components/ui/button";

const version = "v1.0.0";
const javaSDKVersion = "v1.2.0";

export default function HeroSection() {
  return (
    <section className="relative bg-white dark:bg-neutral-950 min-h-[92vh] flex flex-col overflow-hidden">
      {/* Grid — adapts to mode */}
      <div className="absolute inset-0 [background-image:linear-gradient(rgba(0,0,0,0.05)_1px,transparent_1px),linear-gradient(90deg,rgba(0,0,0,0.05)_1px,transparent_1px)] dark:[background-image:linear-gradient(rgba(255,255,255,0.03)_1px,transparent_1px),linear-gradient(90deg,rgba(255,255,255,0.03)_1px,transparent_1px)] [background-size:52px_52px]" />


      {/* Vertical accent line */}
      <div className="absolute right-[20%] top-0 bottom-0 w-px bg-red-200/60 dark:bg-red-900/30 hidden lg:block" />
      <div className="relative flex-1 flex flex-col max-w-7xl mx-auto w-full px-8 lg:px-16 py-12">

        {/* System status bar */}
        <div className="flex items-center gap-3 font-mono text-[11px] text-neutral-400 dark:text-neutral-600 uppercase tracking-widest mb-16 lg:mb-20">
          <span className="text-red-600 text-xs">■■■</span>
          <span> NULL-PRIVATIZATION / OPENSRI</span>
          <div className="flex-1 h-px bg-neutral-200 dark:bg-neutral-800" />
          <span className="hidden sm:inline">{version}</span>
          <div className="w-px h-3 bg-neutral-300 dark:bg-neutral-700 hidden sm:block" />
          <span className="text-emerald-600 dark:text-emerald-500 flex items-center gap-1.5">
            <span className="inline-block w-1.5 h-1.5 rounded-full bg-emerald-500 animate-pulse" />
            ONLINE
          </span>
        </div>

        {/* Main content */}
        <div className="flex-1 flex flex-col lg:flex-row items-start gap-16">
          {/* Left — headline */}
          <div className="flex-1">
            <h1 className="font-heading font-black uppercase leading-[0.88] tracking-tight mb-8">
              <span className="block text-[clamp(2.8rem,7.5vw,6.5rem)] text-neutral-900 dark:text-white">
                FACTURACIÓN
              </span>
              <span className="block text-[clamp(2.8rem,7.5vw,6.5rem)] text-neutral-900 dark:text-white">
                ELECTRÓNICA
              </span>
              <span className="relative block text-[clamp(2.8rem,7.5vw,6.5rem)] text-green-300">
                ACCESIBLE
                <span className="absolute -left-5 top-0 bottom-0 w-1 bg-red-600 hidden lg:block" />
              </span>
            </h1>

            <p className="font-mono text-sm text-neutral-500 uppercase tracking-widest mb-5">
              UNA SOLUCIÓN DESCENTRALIZADA ENFOCADA EN DESARROLLADORES
            </p>

            <p className="text-neutral-600 dark:text-neutral-400 text-base leading-relaxed max-w-lg mb-10">
              Clave de acceso de 49 dígitos, XML validado por XSD, firma XAdES-BES con P12
              y comunicacion SOAP al SRI — Encapsulados en una sola llamada Open source.
            </p>

            <p className="font-heading font-black uppercase leading-[0.88] tracking-tight mb-8">
              YA DISPONIBLE - JAVA !!
            </p>

            <div className="flex flex-wrap gap-3 mb-14">


              <Button asChild size="lg" className="uppercase tracking-widest gap-2">
                <Link href="/playground">
                  Probar ahora
                  <HugeiconsIcon icon={ArrowRight01Icon} size={14} strokeWidth={2} />
                </Link>
              </Button>


              <Button asChild variant="outline" size="lg" className="uppercase tracking-widest gap-2">
                <a
                  href="https://github.com/nulldoomer/Open-SRI"
                  target="_blank"
                  rel="noopener noreferrer"
                >
                  <HugeiconsIcon icon={GithubIcon} size={14} strokeWidth={1.5} />
                  GitHub
                </a>
              </Button>


            </div>

            {/* Tech tags */}
            <div className="flex flex-wrap gap-x-6 gap-y-2 font-mono text-[11px] text-neutral-400 dark:text-neutral-600 border-t border-neutral-200 dark:border-neutral-800 pt-6">
              <span>▷ JAVA SDK {javaSDKVersion}</span>
              <span>▷ APACHE 2.0</span>
              <span>▷ FIRMA XADES-BES + SOAP COMMUNICATION</span>
              <span>▷ PIPELINE OBSERVABLE</span>
            </div>
          </div>

          {/* Right — analysis panel */}
          <div className="hidden lg:flex flex-col gap-3 w-72 shrink-0 mt-6">
            <div className="border border-neutral-200 dark:border-neutral-800 bg-neutral-50 dark:bg-neutral-900/60 overflow-hidden">
              <div className="flex items-center gap-2 px-4 py-2.5 border-b border-neutral-200 dark:border-neutral-800 bg-red-50 dark:bg-red-950/40">
                <span className="text-red-600 text-xs">■</span>
                <span className="font-mono text-[10px] text-red-600 dark:text-red-400 uppercase tracking-widest">
                  ANÁLISIS DE RIESGOS
                </span>
              </div>
              <div className="p-4 space-y-3 font-mono text-[11px]">
                {[
                  ["Tiempo de integración", "text-red-600 dark:text-red-400", "SEMANAS/MESES"],
                  ["Errores de implementación", "text-red-600 dark:text-red-400", "CRÍTICO"],
                  ["Documentación del SRI", "text-orange-500", "INSUFICIENTE"],
                  ["Reinvención del flujo", "text-red-600 dark:text-red-400", "RECURRENTE"],
                ].map(([label, cls, val]) => (
                  <div key={label as string} className="flex justify-between">
                    <span className="text-neutral-500">{label}</span>
                    <span className={cls as string}>{val}</span>
                  </div>
                ))}
              </div>
            </div>

            <div className="border border-neutral-200 dark:border-neutral-800 bg-neutral-50 dark:bg-neutral-900/60 overflow-hidden">
              <div className="flex items-center gap-2 px-4 py-2.5 border-b border-neutral-200 dark:border-neutral-800 bg-emerald-50 dark:bg-emerald-950/30">
                <span className="text-emerald-600 dark:text-emerald-500 text-xs">■</span>
                <span className="font-mono text-[10px] text-emerald-700 dark:text-emerald-500 uppercase tracking-widest">
                  OPENSRI
                </span>
              </div>
              <div className="p-4 font-mono text-[11px] text-neutral-500 space-y-0.5 leading-relaxed">
                <p className="text-neutral-700 dark:text-neutral-300">client.sendInvoice(invoice);</p>
                <p>// Una línea.</p>
                <p>// Status: RECIBIDA</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}
