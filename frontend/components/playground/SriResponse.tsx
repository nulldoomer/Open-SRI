import type { SriResponse as SriResponseType } from "@/types/playground";
import { cn } from "@/lib/utils";
import ClaveAnatomy from "@/components/ui/ClaveAnatomy";

const accepted = new Set(["RECIBIDA", "AUTORIZADO"]);

const statusDescriptions: Record<string, string> = {
  RECIBIDA: "El comprobante fue recibido correctamente por el SRI.",
  DEVUELTA: "El comprobante fue devuelto por el SRI con errores.",
  AUTORIZADO: "El comprobante fue autorizado por el SRI.",
  NO_AUTORIZADO: "El comprobante no fue autorizado por el SRI.",
};

const label = "font-mono text-[10.5px] uppercase tracking-[0.16em] text-muted-foreground";

export default function SriResponse({ response }: { response: SriResponseType | null }) {
  if (!response) {
    return (
      <p className="text-sm leading-relaxed text-muted-foreground">
        El estado final, la clave de acceso y los mensajes del SRI aparecerán aquí después de enviar la factura.
      </p>
    );
  }

  const ok = accepted.has(response.status);

  return (
    <div className="space-y-7">
      <div className="flex flex-wrap items-center gap-5">
        <p
          className={cn(
            "-rotate-3 rounded-[6px] border-2 px-3 py-1.5 font-mono text-lg font-semibold uppercase tracking-[0.2em]",
            ok ? "border-[var(--v-olive-deep)] text-[var(--status-ok-ink)]" : "border-[var(--v-danger)] text-[var(--v-danger-ink)]",
          )}
        >
          {response.status}
        </p>
        <p className="min-w-0 flex-1 text-sm text-muted-foreground">
          {statusDescriptions[response.status] ?? "El SRI devolvió un estado no reconocido."}
        </p>
      </div>

      <div>
        <p className={label}>Clave de acceso</p>
        <ClaveAnatomy clave={response.accessKey} className="mt-3" />
      </div>

      {response.authorizationDate && (
        <div>
          <p className={label}>Fecha de autorización</p>
          <p className="mt-1.5 font-mono text-sm tabular-nums">{response.authorizationDate}</p>
        </div>
      )}

      <div>
        <p className={label}>Mensajes del SRI</p>
        {response.messages.length === 0 ? (
          <p className="mt-2 text-sm text-muted-foreground">Sin mensajes.</p>
        ) : (
          <ul className="mt-3 space-y-2">
            {response.messages.map((msg, i) => (
              <li
                key={i}
                className={cn(
                  "rounded-[var(--r-sm)] px-4 py-3 text-sm",
                  msg.type === "ERROR"
                    ? "bg-[var(--status-danger-bg)] text-[var(--status-danger-ink)]"
                    : "bg-muted text-foreground",
                )}
              >
                <span className="mr-2 font-mono text-xs opacity-75">[{msg.identifier}]</span>
                {msg.message}
                {msg.additionalInfo && <p className="mt-1 text-xs opacity-75">{msg.additionalInfo}</p>}
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
}
