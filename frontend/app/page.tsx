import Link from "next/link";
import { HugeiconsIcon } from "@hugeicons/react";
import {
  Alert01Icon,
  CheckmarkCircle01Icon,
  ArrowRight01Icon,
  Invoice01Icon,
  Key01Icon,
  Xml01Icon,
  LockKeyIcon,
  Wifi01Icon,
  Clock01Icon,
} from "@hugeicons/core-free-icons";
import HeroSection from "@/components/landing/HeroSection";
import FeatureCard from "@/components/landing/FeatureCard";
import CodeBlock from "@/components/ui/CodeBlock";

const features = [
  {
    icon: Alert01Icon,
    title: "El Problema",
    description:
      "Integrar facturación electrónica implica implementar validaciones, generación de XML, firma digital y comunicación con el SRI. Ante la falta de herramientas abiertas y documentación unificada, muchos equipos terminan dependiendo de servicios de terceros para acelerar la integración."
    ,
    accent: "red" as const,
  },
  {
    icon: ArrowRight01Icon,
    title: "La Solución",
    description:
      "OpenSRI ofrece una alternativa open source para integrar facturación electrónica sin depender de proveedores externos. Su objetivo es convertir un conocimiento fragmentado en herramientas accesibles para toda la comunidad de desarrolladores, intentando parar la privatización injustificada de estos servicios."
    ,
    accent: "amber" as const,
  },
  {
    icon: CheckmarkCircle01Icon,
    title: "Open Source",
    description:
      "Licencia Apache 2.0. Arquitectura limpia por capas, más del 70% de cobertura de pruebas y documentación completa. SDK estable para Java, con soporte para C# y Go en desarrollo.",
    accent: "emerald" as const,
  },
];

const pipelineSteps = [
  { icon: Invoice01Icon, label: "Construir Factura", detail: "InvoiceBuilder.build()" },
  { icon: Key01Icon, label: "Clave de Acceso", detail: "49 dígitos — módulo 11" },
  { icon: Xml01Icon, label: "Serializar XML", detail: "XSD v2.1.0 validado" },
  { icon: LockKeyIcon, label: "Firmar XAdES-BES", detail: "RSA-SHA256 con P12" },
  { icon: Wifi01Icon, label: "Enviar SOAP", detail: "celcer.sri.gob.ec" },
  { icon: Clock01Icon, label: "Respuesta SRI", detail: "RECIBIDA / DEVUELTA" },
];

const codeAfter = `byte[] certBytes = Files.readAllBytes(Path.of(certPath));
        OpenSRIClient client = OpenSRIClientBuilder.builder()
            .environment(Environment.PRODUCCION)
            .certificate(certBytes)
            .certificatePassword(certPassword)
            .certificateAlias(certAlias)
            .issuerProfile(profile)
            .timeout(5000)
            .build();

List<InvoiceItem> items = InvoiceItemMapper.from(req.getItems());
List<Payment> payments = PaymentMapper.from(req.getPayments());

Invoice invoice = InvoiceBuilder.builder()
    .issueDate(IssueDate.now())
    .establishmentDirection(req.getEstablishmentDirection())
    .taxInfo(TaxInfoProvider.fromConfig())
    .documentNumber(DocumentNumberProvider.next())
    .documentVersion(DocumentVersion.VERSION_100)
    .client(ClientMapper.from(req.getClient()))
    .addItems(items)
    .addPayments(payments)
    .build();

SendInvoiceResult result = client.sendInvoice(invoice);
// result.getStatus()    → RECIBIDA
// result.getAccessKey() → 49 dígitos`;

export default function Home() {
  return (
    <main className="flex flex-col">

      <HeroSection />

      {/* ──────────── LINK A LA DOCUMENTACION──────────────────────────────────────── */}
      <section className="bg-white dark:bg-neutral-950 border-t border-neutral-100 dark:border-neutral-800 px-8 lg:px-16 py-20">
        <div className="max-w-7xl mx-auto grid lg:grid-cols-2 gap-16 items-center">
          <div>
            <p className="font-mono text-[11px] text-neutral-400 dark:text-neutral-600 uppercase tracking-widest mb-4">
              ■■■ SDK SURFACE
            </p>
            <h2 className="font-heading font-black uppercase text-neutral-900 dark:text-white text-4xl lg:text-5xl leading-[0.9] mb-6">
              UNA LLAMADA
              <br />
              <span className="text-amber-400">TODO</span>
              <span> RESUELTO</span>
            </h2>
            <p className="text-neutral-600 dark:text-neutral-400 text-base leading-relaxed mb-6">
              Una herramienta que maneja toda la implementación de clave de acceso, serialización XML,
              firma del XML y la comunicación SOAP, mientras tu solo
              tienes que implementar el SDK.
            </p>
            <Link
              href="/doc"
              className="inline-flex items-center gap-1.5 font-mono text-xs text-neutral-500 uppercase tracking-widest hover:text-red-600 dark:hover:text-red-300 transition-colors"
            >
              Ver documentación
              <HugeiconsIcon icon={ArrowRight01Icon} size={12} strokeWidth={2} />
            </Link>
          </div>

          <div className="border border-neutral-200 dark:border-neutral-800 overflow-hidden">
            <div className="flex items-center gap-2 px-4 py-2.5 border-b border-neutral-200 dark:border-neutral-800 bg-neutral-900">
              <span className="w-2.5 h-2.5 rounded-full bg-red-500" />
              <span className="w-2.5 h-2.5 rounded-full bg-orange-400" />
              <span className="w-2.5 h-2.5 rounded-full bg-emerald-500" />
              <span className="font-mono text-[10px] text-neutral-500 ml-2 uppercase tracking-widest">
                OpenSRIClient.java
              </span>
            </div>
            <CodeBlock code={codeAfter} lang="java" />
          </div>
        </div>
      </section>

      {/* ── Pipeline ─────────────────────────────────────────── */}
      <section className="bg-neutral-50 dark:bg-neutral-900/50 border-t border-neutral-100 dark:border-neutral-800 px-8 lg:px-16 py-20">
        <div className="max-w-7xl mx-auto">
          <div className="max-w-xl mb-12">
            <p className="font-mono text-[11px] text-neutral-400 dark:text-neutral-600 uppercase tracking-widest mb-4">
              ■■■ PIPELINE INTERNO
            </p>
            <h2 className="font-heading font-black uppercase text-neutral-900 dark:text-white text-4xl lg:text-5xl leading-[0.9] mb-4">
              6 PASOS
              <br />
              <span className="text-red-600">SIN</span>
              <span> IMPLEMENTACIONES </span>
            </h2>
            <p className="text-neutral-600 dark:text-neutral-400 text-sm leading-relaxed">
              El SDK ejecuta el pipeline completo internamente. El Playground te lo muestra
              en tiempo real, paso a paso, con trazas del SDK y el XML generado.
            </p>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3 mb-10">
            {pipelineSteps.map((step, i) => (
              <div
                key={step.label}
                className="flex items-start gap-3 border border-neutral-200 dark:border-neutral-800 bg-white dark:bg-neutral-900/60 px-4 py-4"
              >
                <span className="font-mono text-[10px] text-neutral-400 dark:text-neutral-600 shrink-0 mt-0.5">
                  {String(i + 1).padStart(2, "0")}
                </span>
                <div className="flex-1">
                  <div className="flex items-center gap-2 mb-1">
                    <HugeiconsIcon
                      icon={step.icon}
                      size={14}
                      strokeWidth={1.5}
                      className="text-neutral-500 dark:text-neutral-400 shrink-0"
                    />
                    <span className="text-sm font-semibold text-neutral-900 dark:text-neutral-100">
                      {step.label}
                    </span>
                  </div>
                  <p className="font-mono text-[11px] text-neutral-400 dark:text-neutral-600">
                    {step.detail}
                  </p>
                </div>
              </div>
            ))}
          </div>

          <Link
            href="/playground"
            className="inline-flex items-center gap-2 border border-neutral-300 dark:border-neutral-700 text-neutral-600 dark:text-neutral-400 hover:border-red-600 hover:text-red-600 dark:hover:border-red-300 dark:hover:text-red-300 px-6 py-3 text-xs font-mono uppercase tracking-widest transition-colors"
          >
            <HugeiconsIcon icon={ArrowRight01Icon} size={12} strokeWidth={2} />
            Ver en el Playground
          </Link>
        </div>
      </section>

      {/* ── Feature cards ────────────────────────────────────── */}
      <section className="bg-white dark:bg-neutral-950 border-t border-neutral-100 dark:border-neutral-800 px-8 lg:px-16 py-20">
        <div className="max-w-7xl mx-auto">
          <div className="flex items-center gap-3 font-mono text-[11px] text-neutral-400 dark:text-neutral-700 uppercase tracking-widest mb-10">
            <span>■■■</span>
            <span>ANÁLISIS DEL SISTEMA</span>
            <div className="flex-1 h-px bg-neutral-100 dark:bg-neutral-800" />
            <span>03 MÓDULOS</span>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            {features.map((f) => (
              <FeatureCard key={f.title} {...f} />
            ))}
          </div>
        </div>
      </section>

      {/* ── CTA strip ────────────────────────────────────────── */}
      <section className="bg-neutral-50 dark:bg-neutral-900/60 border-t border-neutral-100 dark:border-neutral-800 px-8 lg:px-16 py-12">
        <div className="max-w-7xl mx-auto flex flex-col sm:flex-row items-center justify-between gap-6">
          <div>
            <p className="font-mono text-[11px] text-neutral-400 dark:text-neutral-600 uppercase tracking-widest mb-1">
              SIGUIENTE PASO
            </p>
            <p className="font-heading font-bold text-neutral-900 dark:text-neutral-100 text-xl">
              Mira el flujo pipeline del SDK trabajando
            </p>
          </div>
          <div className="flex gap-3 shrink-0">
            <Link
              href="/playground"
              className="inline-flex items-center gap-2 bg-neutral-900 dark:bg-white text-white dark:text-neutral-900 hover:bg-neutral-700 dark:hover:bg-neutral-100 px-6 py-2.5 text-xs font-mono uppercase tracking-widest transition-colors"
            >
              <HugeiconsIcon icon={ArrowRight01Icon} size={12} strokeWidth={2} />
              Playground
            </Link>
            <Link
              href="/doc"
              className="inline-flex items-center gap-2 border border-neutral-300 dark:border-neutral-700 text-neutral-600 dark:text-neutral-400 hover:border-neutral-500 dark:hover:border-neutral-500 px-6 py-2.5 text-xs font-mono uppercase tracking-widest transition-colors"
            >
              Docs
            </Link>
          </div>
        </div>
      </section>
    </main>
  );
}
