import type { CSSProperties } from "react";
import Mark from "@/components/layout/Mark";
import ClaveAnatomy from "@/components/ui/ClaveAnatomy";

/*
 * Example clave de acceso, valid módulo 11. It is also the "tracking number"
 * the SRI uses to answer AutorizacionComprobantes.
 */
const CLAVE = "2709202601179124867800110010010000000011234567810";

// ponytail: Code 128 bars for the clave above, generated once with jsbarcode (not a dependency). Regenerate if CLAVE changes.
const BARCODE =
  "M0 0h2v1h-2zM3 0h1v1h-1zM6 0h3v1h-3zM11 0h3v1h-3zM15 0h2v1h-2zM19 0h1v1h-1zM22 0h2v1h-2zM26 0h1v1h-1zM29 0h1v1h-1zM33 0h2v1h-2zM37 0h1v1h-1zM40 0h3v1h-3zM44 0h3v1h-3zM49 0h1v1h-1zM52 0h2v1h-2zM55 0h2v1h-2zM59 0h2v1h-2zM62 0h2v1h-2zM66 0h1v1h-1zM69 0h3v1h-3zM74 0h2v1h-2zM77 0h4v1h-4zM82 0h2v1h-2zM85 0h2v1h-2zM88 0h3v1h-3zM92 0h1v1h-1zM95 0h2v1h-2zM99 0h4v1h-4zM104 0h1v1h-1zM107 0h1v1h-1zM110 0h2v1h-2zM116 0h1v1h-1zM118 0h1v1h-1zM121 0h2v1h-2zM124 0h2v1h-2zM128 0h2v1h-2zM132 0h2v1h-2zM137 0h1v1h-1zM140 0h1v1h-1zM143 0h2v1h-2zM146 0h2v1h-2zM150 0h2v1h-2zM154 0h2v1h-2zM158 0h1v1h-1zM162 0h1v1h-1zM165 0h2v1h-2zM169 0h2v1h-2zM172 0h2v1h-2zM176 0h2v1h-2zM179 0h2v1h-2zM183 0h2v1h-2zM187 0h2v1h-2zM190 0h2v1h-2zM194 0h2v1h-2zM198 0h2v1h-2zM201 0h2v1h-2zM205 0h2v1h-2zM209 0h2v1h-2zM212 0h2v1h-2zM216 0h2v1h-2zM220 0h2v1h-2zM225 0h1v1h-1zM228 0h1v1h-1zM231 0h3v1h-3zM235 0h2v1h-2zM238 0h3v1h-3zM242 0h1v1h-1zM244 0h3v1h-3zM248 0h2v1h-2zM253 0h1v1h-1zM258 0h1v1h-1zM260 0h2v1h-2zM264 0h1v1h-1zM267 0h1v1h-1zM269 0h4v1h-4zM275 0h3v1h-3zM279 0h1v1h-1zM281 0h4v1h-4zM286 0h1v1h-1zM289 0h3v1h-3zM293 0h2v1h-2zM297 0h4v1h-4zM302 0h1v1h-1zM305 0h1v1h-1zM308 0h2v1h-2zM313 0h3v1h-3zM317 0h1v1h-1zM319 0h2v1h-2z";

export default function Waybill() {
  return (
    <article
      aria-label="Guía de envío de una factura de ejemplo"
      className="waybill relative rounded-[var(--r-card)] bg-card text-card-foreground shadow-[0_1px_0_var(--v-border),0_24px_48px_-28px_rgb(20_23_27/0.45)] dark:shadow-[0_1px_0_var(--v-border),0_24px_48px_-24px_rgb(0_0_0/0.8)]"
    >
      <header className="flex flex-col gap-3 border-b border-dashed border-border px-5 pb-4 pt-6 sm:flex-row sm:items-start sm:justify-between sm:gap-4 sm:px-6">
        <div>
          <p className="font-heading text-lg font-extrabold uppercase leading-none tracking-tight [font-stretch:85%]">
            Guía de envío
          </p>
          <p className="mt-1.5 font-mono text-[11px] text-muted-foreground">Sobre SOAP · RecepcionComprobantes</p>
        </div>
        <p className="font-mono text-[11px] leading-tight text-muted-foreground sm:text-right">
          Factura <br className="hidden sm:block" />
          <span className="text-foreground tabular-nums">001-001-000000001</span>
        </p>
      </header>

      <dl className="grid grid-cols-2">
        <Field label="Remitente" className="border-r border-dashed border-border">
          Tu sistema
          <span className="block font-mono text-xs text-muted-foreground tabular-nums">RUC 1791248678001</span>
        </Field>
        <Field label="Destinatario">
          SRI · pruebas
          <span className="block font-mono text-xs text-muted-foreground">celcer.sri.gob.ec</span>
        </Field>
        <Field label="Contenido" className="col-span-2 border-t border-dashed border-border">
          <span className="font-mono text-[13px]">factura.xml · XSD v2.1.0 · firma XAdES-BES</span>
        </Field>
      </dl>

      <div className="relative px-5 pb-6 pt-4 sm:px-6">
        <p className="flex items-center gap-2 font-mono text-[10.5px] uppercase tracking-[0.16em] text-muted-foreground">
          <Mark />
          Clave de acceso · 49 dígitos
        </p>

        <svg
          viewBox="0 0 321 1"
          preserveAspectRatio="none"
          aria-hidden="true"
          className="mt-3 h-12 w-full text-foreground"
        >
          <path d={BARCODE} fill="currentColor" />
        </svg>

        <ClaveAnatomy clave={CLAVE} strikeFrom={250} className="mt-3" />

        <div className="mt-5 flex items-end justify-between gap-4">
          <p className="font-mono text-[10.5px] text-muted-foreground">Datos de ejemplo</p>
          <p
            className="stamp -rotate-6 rounded-[6px] border-2 border-signal px-2.5 py-1 font-mono text-sm font-semibold uppercase tracking-[0.2em] text-signal-ink"
            style={{ "--strike-delay": "3100ms" } as CSSProperties}
          >
            Recibida
          </p>
        </div>

        {/* Perforated tear line: notches at the edges and a row of punched holes between them */}
        <span aria-hidden="true" className="absolute -left-2 -top-2 size-4 rounded-full bg-background" />
        <span aria-hidden="true" className="absolute -right-2 -top-2 size-4 rounded-full bg-background" />
        <span
          aria-hidden="true"
          className="absolute inset-x-4 -top-1 h-2 bg-[radial-gradient(circle,var(--background)_2.4px,var(--v-edge)_2.6px,var(--v-edge)_3.2px,transparent_3.6px)] bg-[length:12px_8px] bg-repeat-x"
        />
      </div>
    </article>
  );
}

function Field({ label, className = "", children }: { label: string; className?: string; children: React.ReactNode }) {
  return (
    <div className={`px-5 py-3.5 sm:px-6 ${className}`}>
      <dt className="flex items-center gap-2 font-mono text-[10.5px] uppercase tracking-[0.16em] text-muted-foreground">
        <Mark />
        {label}
      </dt>
      <dd className="mt-1.5 text-[15px] font-medium leading-snug">{children}</dd>
    </div>
  );
}
