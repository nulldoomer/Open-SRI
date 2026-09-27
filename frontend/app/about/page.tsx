import { HugeiconsIcon } from "@hugeicons/react";
import { ArrowRight01Icon } from "@hugeicons/core-free-icons";
import { Button } from "@/components/ui/button";
import Mark from "@/components/layout/Mark";

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

const h2 = "mb-5 font-heading text-2xl font-bold tracking-tight";

export default function About() {
  return (
    <main className="mx-auto w-full max-w-3xl px-4 py-10 sm:px-8 lg:py-16">
      <header className="mb-14 border-b border-border pb-10">
        <h1 className="font-heading text-[clamp(2.5rem,5vw,3.75rem)] font-extrabold uppercase leading-[0.9] tracking-[-0.015em] [font-stretch:80%]">
          Sobre OpenSRI
        </h1>
        <div className="mt-6 space-y-4 text-lg leading-relaxed text-muted-foreground">
          <p>
            Esta iniciativa surge de mi experiencia al implementar por primera vez un sistema de facturación
            electrónica. Durante el proceso encontré documentación dispersa, referencias inconsistentes y una gran
            cantidad de material desactualizado enfocado en tecnologías heredadas.
          </p>
          <p>
            Además, muchas de las soluciones disponibles se basan en servicios de terceros bajo modelos de
            suscripción, lo que puede representar una barrera para desarrolladores independientes y pequeñas empresas.
            Como respuesta a estas dificultades, decidí crear una alternativa abierta que facilite la integración de
            facturación electrónica y reduzca el tiempo necesario para comprender e implementar los requerimientos del
            SRI.
          </p>
        </div>
      </header>

      <div className="space-y-14">
        <section>
          <h2 className={h2}>¿Por qué existe OpenSRI?</h2>
          <div className="space-y-4 text-[15px] leading-7 text-muted-foreground">
            <p>
              Integrar facturación electrónica con el SRI implica mucho más que generar una factura. Es necesario
              construir una clave de acceso válida, generar documentos XML que cumplan con los esquemas oficiales,
              firmarlos digitalmente con certificados electrónicos y comunicarse con los servicios SOAP del SRI para su
              validación y autorización.
            </p>
            <p>
              A pesar de ser un requisito común para miles de sistemas en Ecuador, no existe una librería oficial que
              abstraiga este proceso. Como resultado, cada empresa, startup o desarrollador independiente termina
              implementando la misma lógica una y otra vez, invirtiendo tiempo en resolver problemas ya conocidos.
            </p>
            <p>
              OpenSRI encapsula todo ese flujo en un SDK open source, documentado y probado, permitiendo generar,
              firmar y enviar comprobantes electrónicos mediante una API simple y consistente.
            </p>
            <p>
              El objetivo es que los desarrolladores puedan concentrarse en construir sus productos, no en descifrar
              los detalles internos de la integración con el SRI.
            </p>
          </div>
        </section>

        <section>
          <h2 className={h2}>Cómo contribuir</h2>
          <p className="text-[15px] leading-7 text-muted-foreground">
            El proyecto necesita más que código. Puedes ayudar de muchas formas:
          </p>
          <ul className="mt-4 divide-y divide-border border-y border-border text-[15px]">
            {[
              "Portar el SDK Java a C#, Go o Python",
              "Reportar bugs con los webservices del SRI",
              "Mejorar la documentación y los ejemplos",
              "Agregar soporte para otros tipos de comprobante (notas de crédito, guías de remisión)",
              "Testear en entornos de producción reales",
            ].map((item) => (
              <li key={item} className="flex gap-3 py-3">
                <Mark className="mt-2 text-muted-foreground" />
                {item}
              </li>
            ))}
          </ul>
          <Button asChild className="mt-8">
            <a href="https://github.com/nulldoomer/Open-SRI" target="_blank" rel="noopener noreferrer">
              Ver en GitHub
              <HugeiconsIcon icon={ArrowRight01Icon} size={16} strokeWidth={2} />
            </a>
          </Button>
        </section>

        <section>
          <h2 className={h2}>Enlaces</h2>
          <ul className="overflow-hidden rounded-[var(--r-card-sm)] border border-border bg-card">
            {links.map((link) => (
              <li key={link.href} className="border-b border-border last:border-0">
                <a
                  href={link.href}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="group flex items-center justify-between gap-4 px-5 py-4 transition-colors hover:bg-muted"
                >
                  <span>
                    <span className="block text-[15px] font-medium">{link.label}</span>
                    <span className="mt-0.5 block text-sm text-muted-foreground">{link.description}</span>
                  </span>
                  <HugeiconsIcon
                    icon={ArrowRight01Icon}
                    size={16}
                    strokeWidth={1.6}
                    className="shrink-0 text-muted-foreground transition-transform group-hover:translate-x-0.5 group-hover:text-foreground"
                  />
                </a>
              </li>
            ))}
          </ul>
        </section>
      </div>
    </main>
  );
}
