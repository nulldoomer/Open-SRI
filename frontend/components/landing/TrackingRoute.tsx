import type { CSSProperties } from "react";

const stops = [
  { label: "Factura construida", detail: "InvoiceBuilder.build()" },
  { label: "Clave de acceso", detail: "49 dígitos · módulo 11" },
  { label: "XML serializado", detail: "validado contra XSD" },
  { label: "Firmado XAdES-BES", detail: "RSA-SHA256 con P12" },
  { label: "Enviado por SOAP", detail: "RecepcionComprobantes" },
  { label: "Respuesta del SRI", detail: "RECIBIDA / AUTORIZADO" },
];

const delay = (i: number) => ({ "--strike-delay": `${1350 + i * 280}ms` }) as CSSProperties;

/*
 * The signature moment: every stop is present from the start as a ghost and
 * "prints" in order, with no tween (see .strike in globals.css). With reduced
 * motion the route renders already complete.
 */
export default function TrackingRoute() {
  return (
    <ol aria-label="Ruta de la factura dentro del SDK" className="grid gap-0 md:grid-cols-6">
      {stops.map((stop, i) => {
        const last = i === stops.length - 1;
        const style = delay(i);
        return (
          <li key={stop.label} className="relative flex gap-4 pb-6 md:block md:pb-0 md:pr-4">
            {/* rail: vertical on phones, horizontal from md */}
            {!last && (
              <span
                aria-hidden="true"
                className="strike absolute left-[9px] top-5 bottom-0 w-px bg-foreground md:left-5 md:right-0 md:top-[9px] md:h-px md:w-auto"
                style={delay(i + 1)}
              />
            )}
            <span
              aria-hidden="true"
              className={
                last
                  ? "strike-node relative z-10 flex size-[19px] shrink-0 items-center justify-center rounded-full border border-solid border-signal bg-signal"
                  : "strike-node relative z-10 flex size-[19px] shrink-0 items-center justify-center rounded-full border border-solid border-foreground bg-foreground"
              }
              style={style}
            />
            <div className="strike md:mt-4" style={style}>
              <p className="font-mono text-[10.5px] tabular-nums text-muted-foreground">{String(i + 1).padStart(2, "0")}</p>
              <p className="mt-0.5 text-sm font-semibold leading-snug">{stop.label}</p>
              <p className={last ? "mt-1 font-mono text-[11px] text-signal-ink" : "mt-1 font-mono text-[11px] text-muted-foreground"}>
                {stop.detail}
              </p>
            </div>
          </li>
        );
      })}
    </ol>
  );
}
