import Link from "next/link";
import { HugeiconsIcon } from "@hugeicons/react";
import { ArrowRight01Icon } from "@hugeicons/core-free-icons";
import { Button } from "@/components/ui/button";

const links = [
  {
    label: "Repositorio GitHub",
    href: "https://github.com/nulldoomer/Open-SRI",
    description: "Código fuente, issues y roadmap",
  },
  {
    label: "Issues abiertos",
    href: "https://github.com/nulldoomer/Open-SRI/issues",
    description: "Reporta bugs o propón features",
  },
  {
    label: "Licencia Apache 2.0",
    href: "https://github.com/nulldoomer/Open-SRI/blob/main/LICENSE",
    description: "Libre de usar en proyectos comerciales",
  },
];

export default function About() {
  return (
    <main className="max-w-3xl mx-auto px-8 py-16">
      <div className="mb-12">
        <h1 className="font-heading text-4xl font-bold mb-3">Sobre OpenSRI</h1>
        <p className="text-muted-foreground text-lg">
          Un proyecto open-source para acabar con el sufrimiento de integrar
          facturación electrónica en Ecuador.
        </p>
      </div>

      <div className="space-y-10">
        <section>
          <h2 className="font-heading font-semibold text-xl mb-4">
            ¿Por qué existe OpenSRI?
          </h2>
          <div className="space-y-3 text-muted-foreground leading-relaxed">
            <p>
              Integrar facturación electrónica con el SRI es un proceso técnicamente
              complejo: hay que generar una clave de acceso de 49 dígitos con módulo 11,
              serializar la factura a XML validado contra los XSD del SRI, firmar el
              documento con XAdES-BES usando un certificado P12, y finalmente enviarla
              via SOAP al webservice del SRI.
            </p>
            <p>
              No existe una librería oficial. Cada empresa, startup y equipo independiente
              reinventa la misma solución desde cero, acumulando errores difíciles de
              diagnosticar y semanas de tiempo perdido.
            </p>
            <p>
              OpenSRI encapsula ese flujo completo en un SDK open-source, bien testeado
              y documentado. Con una sola llamada se puede generar y enviar una factura.
            </p>
          </div>
        </section>

        <div className="border-t border-border" />

        <section>
          <h2 className="font-heading font-semibold text-xl mb-4">Cómo contribuir</h2>
          <div className="space-y-3 text-muted-foreground leading-relaxed">
            <p>
              El proyecto necesita más que código. Puedes ayudar de muchas formas:
            </p>
            <ul className="list-disc list-inside space-y-1 text-sm">
              <li>Portar el SDK Java a C#, Go o Python</li>
              <li>Reportar bugs con los webservices del SRI</li>
              <li>Mejorar la documentación y los ejemplos</li>
              <li>Agregar soporte para otros tipos de comprobante (notas de crédito, guías de remisión)</li>
              <li>Testear en entornos de producción reales</li>
            </ul>
          </div>
          <div className="mt-6">
            <Button asChild>
              <a
                href="https://github.com/nulldoomer/Open-SRI"
                target="_blank"
                rel="noopener noreferrer"
              >
                Ver en GitHub
              </a>
            </Button>
          </div>
        </section>

        <div className="border-t border-border" />

        <section>
          <h2 className="font-heading font-semibold text-xl mb-4">Links</h2>
          <div className="space-y-3">
            {links.map((link) => (
              <a
                key={link.href}
                href={link.href}
                target="_blank"
                rel="noopener noreferrer"
                className="flex items-center justify-between p-4 border border-border rounded-sm hover:bg-muted/30 transition-colors group"
              >
                <div>
                  <p className="font-medium text-sm group-hover:underline underline-offset-2">
                    {link.label}
                  </p>
                  <p className="text-xs text-muted-foreground mt-0.5">
                    {link.description}
                  </p>
                </div>
                <HugeiconsIcon icon={ArrowRight01Icon} size={14} strokeWidth={1.5} className="text-muted-foreground shrink-0" />
              </a>
            ))}
          </div>
        </section>
      </div>
    </main>
  );
}
