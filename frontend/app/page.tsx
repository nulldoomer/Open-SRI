import Link from "next/link";
import { HugeiconsIcon } from "@hugeicons/react";
import { ArrowRight01Icon } from "@hugeicons/core-free-icons";
import HeroSection from "@/components/landing/HeroSection";
import CodeBlock from "@/components/ui/CodeBlock";
import CodeFrame from "@/components/ui/CodeFrame";
import { Button } from "@/components/ui/button";
import Mark from "@/components/layout/Mark";

const risks = [
  { risk: "Tiempo de integración", without: "Semanas / meses", with: "Una llamada: client.sendInvoice()" },
  { risk: "Errores de implementación", without: "Crítico", with: "XML validado contra los XSD oficiales" },
  { risk: "Documentación del SRI", without: "Insuficiente", with: "Docs y Playground contra SRI pruebas" },
  { risk: "Reinvención del flujo", without: "Recurrente", with: "SDK open source, Apache 2.0" },
];

const reasons = [
  {
    title: "El problema",
    body: "Integrar facturación electrónica implica implementar validaciones, generación de XML, firma digital y comunicación con el SRI. Ante la falta de herramientas abiertas y documentación unificada, muchos equipos terminan dependiendo de servicios de terceros para acelerar la integración.",
  },
  {
    title: "La solución",
    body: "OpenSRI ofrece una alternativa open source para integrar facturación electrónica sin depender de proveedores externos. Su objetivo es convertir un conocimiento fragmentado en herramientas accesibles para toda la comunidad de desarrolladores, intentando parar la privatización injustificada de estos servicios.",
  },
  {
    title: "Open source",
    body: "Licencia Apache 2.0. Arquitectura limpia por capas, con pruebas y documentación completa. SDK estable para Java; C# en desarrollo y Go y Python planificados.",
  },
];

const code = `byte[] certBytes = Files.readAllBytes(Path.of(certPath));

OpenSRIClient client = OpenSRIClientBuilder.builder()
    .environment(Environment.PRUEBAS)
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

var result = client.sendInvoice(invoice);
// result.response().status() → RECIBIDA
// result.accessKey()         → 49 dígitos`;

/* Line ranges (0-based) of the sample above, pinned to the tracking route. */
const notes = [
  { from: 2, to: 9, label: "Cliente", detail: "Certificado P12, ambiente y emisor. Se configura una vez." },
  { from: 14, to: 23, label: "01 · Factura construida", detail: "Tu parte: InvoiceBuilder.build()" },
  {
    from: 25,
    to: 25,
    label: "02–06 · Dentro del SDK",
    detail: "Clave de acceso, XML, firma XAdES-BES, envío SOAP y respuesta del SRI.",
    signal: true,
  },
];

const LINE = 22; // px, matches the code line-height below
const PAD = 20; // px, matches the code block padding

const container = "mx-auto max-w-7xl px-4 sm:px-8 lg:px-16";
const riskValue = "font-mono text-xs font-semibold uppercase tracking-[0.12em] text-[var(--status-danger-ink)]";
const h2 = "font-heading font-extrabold uppercase leading-[0.9] tracking-[-0.015em] [font-stretch:80%] text-[clamp(2.4rem,5vw,3.75rem)] text-balance";

export default function Home() {
  return (
    <main className="flex flex-col">
      <HeroSection />

      {/* ── Una llamada ─────────────────────────────────────── */}
      <section className="py-24">
        <div className={container}>
          <div className="max-w-3xl">
            <h2 className={h2}>
              Una llamada. <span className="text-signal">Todo</span> resuelto.
            </h2>
            <p className="mt-6 text-base leading-relaxed text-muted-foreground">
              El SDK maneja toda la implementación de la clave de acceso, la serialización XML, la firma del XML y la
              comunicación SOAP; tú solo construyes la factura y la envías.{" "}
              <Link
                href="/doc"
                className="inline-flex items-center gap-1 font-medium text-foreground underline decoration-border underline-offset-4 transition-colors hover:decoration-signal"
              >
                Ver la documentación
                <HugeiconsIcon icon={ArrowRight01Icon} size={14} strokeWidth={2} />
              </Link>
            </p>
          </div>

          <CodeFrame label="OpenSRIClient.java" code={code} className="mt-12">
            <div className="lg:grid lg:grid-cols-[minmax(0,1fr)_18rem]">
              <CodeBlock code={code} lang="java" className="[&>pre]:py-5 [&>pre]:leading-[22px]" />
              {/* Annotation rail: each bracket spans the lines it explains. */}
              <ol aria-hidden="true" className="relative hidden border-l border-[var(--structure-line)] lg:block">
                {notes.map((n) => (
                  <li
                    key={n.label}
                    className="absolute left-0 right-5 pl-5"
                    style={{ top: PAD + LINE * n.from, minHeight: LINE * (n.to - n.from + 1) }}
                  >
                    <span
                      className={`absolute -left-px top-0 w-0.5 ${n.signal ? "bg-signal" : "bg-[var(--structure-text)]"}`}
                      style={{ height: LINE * (n.to - n.from + 1) }}
                    />
                    <p
                      className={`font-mono text-[11px] uppercase leading-[22px] tracking-[0.14em] ${n.signal ? "text-signal" : "text-[var(--on-structure)]"}`}
                    >
                      {n.label}
                    </p>
                    <p className="text-xs leading-relaxed text-[var(--structure-text)]">{n.detail}</p>
                  </li>
                ))}
              </ol>
            </div>
          </CodeFrame>

          <ol className="mt-6 grid gap-4 sm:grid-cols-3 lg:hidden">
            {notes.map((n) => (
              <li key={n.label} className={`border-l-2 pl-4 ${n.signal ? "border-signal" : "border-foreground"}`}>
                <p className="font-mono text-[11px] uppercase tracking-[0.14em]">{n.label}</p>
                <p className="mt-1 text-sm text-muted-foreground">{n.detail}</p>
              </li>
            ))}
          </ol>
        </div>
      </section>

      {/* ── Análisis de riesgos ─────────────────────────────── */}
      <section className="border-y border-border bg-[var(--v-beige-2)] py-24">
        <div className={container}>
          <h2 className={`${h2} max-w-3xl`}>Análisis de riesgos</h2>
          <p className="mt-6 max-w-3xl text-base leading-relaxed text-muted-foreground">
            <strong className="font-semibold text-foreground">El problema. </strong>
            {reasons[0].body}
          </p>

          <div className="mt-12 hidden overflow-hidden rounded-[var(--r-card-sm)] border border-border bg-card sm:block dark:bg-[var(--v-canvas)]">
            <table className="w-full text-left text-sm">
              <caption className="sr-only">Riesgos de integrar sin OpenSRI y cómo los cubre el SDK</caption>
              <thead>
                <tr className="border-b border-border font-mono text-[10.5px] uppercase tracking-[0.16em] text-muted-foreground">
                  <th scope="col" className="px-6 py-3.5 font-normal">
                    <span className="flex items-center gap-2">
                      <Mark /> Riesgo
                    </span>
                  </th>
                  <th scope="col" className="px-6 py-3.5 font-normal">Sin OpenSRI</th>
                  <th scope="col" className="px-6 py-3.5 font-normal">Con OpenSRI</th>
                </tr>
              </thead>
              <tbody>
                {risks.map((row) => (
                  <tr key={row.risk} className="border-b border-border last:border-0">
                    <th scope="row" className="px-6 py-4 font-medium">
                      {row.risk}
                    </th>
                    <td className="px-6 py-4">
                      <span className={riskValue}>{row.without}</span>
                    </td>
                    <td className="px-6 py-4 text-foreground">{row.with}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          <ul className="mt-10 divide-y divide-border rounded-[var(--r-card-sm)] border border-border bg-card sm:hidden dark:bg-[var(--v-canvas)]">
            {risks.map((row) => (
              <li key={row.risk} className="px-5 py-4">
                <p className="font-medium">{row.risk}</p>
                <dl className="mt-3 grid grid-cols-[6.5rem_1fr] gap-x-3 gap-y-2 text-sm">
                  <dt className="font-mono text-[10.5px] uppercase tracking-[0.14em] text-muted-foreground">Sin OpenSRI</dt>
                  <dd className={riskValue}>{row.without}</dd>
                  <dt className="font-mono text-[10.5px] uppercase tracking-[0.14em] text-muted-foreground">Con OpenSRI</dt>
                  <dd>{row.with}</dd>
                </dl>
              </li>
            ))}
          </ul>

          <div className="mt-12 max-w-3xl space-y-4 text-base leading-relaxed text-muted-foreground">
            {reasons.slice(1).map((r) => (
              <p key={r.title}>
                <strong className="font-semibold text-foreground">{r.title}. </strong>
                {r.body}
              </p>
            ))}
          </div>
        </div>
      </section>

      {/* ── Cierre ──────────────────────────────────────────── */}
      {/* The band re-points the button tokens so the stock Button variants read on the dark structure surface. */}
      <section className="bg-[var(--v-structure)] py-20 text-[var(--on-structure)] [--primary-foreground:var(--v-structure)] [--primary:var(--on-structure)] [--v-beige-2:var(--structure-quiet)] [--v-border:var(--structure-line)] [--v-text:var(--on-structure)]">
        <div className={`${container} flex flex-col items-start justify-between gap-10 lg:flex-row lg:items-end`}>
          <h2 className={`${h2} max-w-2xl`}>Mira el pipeline del SDK trabajando</h2>
          <div className="flex flex-wrap gap-3">
            <Button asChild size="lg" className="min-w-[13rem]">
              <Link href="/playground">
                Probar el Playground
                <HugeiconsIcon icon={ArrowRight01Icon} size={16} strokeWidth={2} />
              </Link>
            </Button>
            <Button asChild size="lg" variant="outline" className="min-w-[13rem]">
              <Link href="/doc">Quick Start</Link>
            </Button>
          </div>
        </div>
      </section>
    </main>
  );
}
