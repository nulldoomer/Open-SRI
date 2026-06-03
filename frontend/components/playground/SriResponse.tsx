import type { SriResponse as SriResponseType } from "@/types/playground";
import { cn } from "@/lib/utils";

const statusStyles = {
  RECIBIDA: "text-emerald-600 dark:text-emerald-400",
  DEVUELTA: "text-destructive",
  AUTORIZADO: "text-emerald-600 dark:text-emerald-400",
  NO_AUTORIZADO: "text-destructive",
};

const statusDescriptions = {
  RECIBIDA: "El comprobante fue recibido correctamente por el SRI.",
  DEVUELTA: "El comprobante fue devuelto por el SRI con errores.",
  AUTORIZADO: "El comprobante fue autorizado por el SRI.",
  NO_AUTORIZADO: "El comprobante no fue autorizado por el SRI.",
};

interface SriResponseProps {
  response: SriResponseType | null;
}

export default function SriResponse({ response }: SriResponseProps) {
  if (!response) {
    return (
      <div className="flex items-center justify-center py-12 text-muted-foreground text-sm">
        La respuesta del SRI aparecerá aquí después de enviar la factura.
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex items-start gap-4 p-5 border border-border rounded-sm">
        <div className="flex-1 space-y-3">
          <div>
            <p className="text-xs text-muted-foreground uppercase tracking-widest font-semibold mb-1">
              Estado
            </p>
            <p className={cn("text-2xl font-heading font-bold", statusStyles[response.status])}>
              {response.status}
            </p>
            <p className="text-sm text-muted-foreground mt-1">
              {statusDescriptions[response.status]}
            </p>
          </div>

          <div>
            <p className="text-xs text-muted-foreground uppercase tracking-widest font-semibold mb-1">
              Clave de acceso
            </p>
            <p className="font-mono text-xs break-all text-foreground/70">{response.accessKey}</p>
          </div>

          {response.authorizationDate && (
            <div>
              <p className="text-xs text-muted-foreground uppercase tracking-widest font-semibold mb-1">
                Fecha de autorización
              </p>
              <p className="text-sm">{response.authorizationDate}</p>
            </div>
          )}
        </div>
      </div>

      {response.messages.length > 0 && (
        <div>
          <p className="text-xs text-muted-foreground uppercase tracking-widest font-semibold mb-3">
            Mensajes del SRI
          </p>
          <div className="space-y-2">
            {response.messages.map((msg, i) => (
              <div
                key={i}
                className={cn(
                  "p-3 border rounded-sm text-sm",
                  msg.type === "ERROR"
                    ? "border-destructive/30 bg-destructive/5 text-destructive"
                    : "border-border bg-muted/20"
                )}
              >
                <span className="font-mono text-xs opacity-70 mr-2">[{msg.identifier}]</span>
                {msg.message}
                {msg.additionalInfo && (
                  <p className="text-xs opacity-70 mt-1">{msg.additionalInfo}</p>
                )}
              </div>
            ))}
          </div>
        </div>
      )}

      {response.messages.length === 0 && (
        <p className="text-sm text-muted-foreground text-center py-2">
          Sin mensajes del SRI.
        </p>
      )}
    </div>
  );
}
