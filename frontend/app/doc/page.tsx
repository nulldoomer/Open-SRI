import { HugeiconsIcon } from "@hugeicons/react";
import { Alert01Icon } from "@hugeicons/core-free-icons";
import DocsSidebar from "@/components/docs/DocsSidebar";
import CodeBlock from "@/components/docs/CodeBlock";

const quickStartCode = `// 1. Construir la factura
Invoice invoice = Invoice.builder()
    .issuer(Taxpayer.of("1790000000001", "Mi Empresa S.A."))
    .customer(Customer.of("1234567890", "Juan Pérez"))
    .environment(Environment.TESTING)
    .addItem(InvoiceItem.of("Producto A", 2, new BigDecimal("50.00")))
    .build();

// 2. Enviar al SRI (genera clave, serializa XML, firma y envía SOAP)
OpenSRIClient client = OpenSRIClient.builder()
    .p12Certificate(Paths.get("certificado.p12"), "contraseña")
    .build();

SendInvoiceResult result = client.sendInvoice(invoice);

System.out.println(result.getStatus());      // RECIBIDA
System.out.println(result.getAccessKey());   // 49 dígitos`;

const mavenCode = `<dependency>
  <groupId>io.github.nulldoomer</groupId>
  <artifactId>opensri-java-sdk</artifactId>
  <version>1.0.0</version>
</dependency>`;

export default function Docs() {
  return (
    <div className="flex gap-12 max-w-6xl mx-auto px-8 py-16 min-h-screen">
      <DocsSidebar />

      <main className="flex-1 min-w-0">
        <div className="mb-10">
          <h1 className="font-heading text-4xl font-bold mb-3">Quick Start</h1>
          <p className="text-muted-foreground text-lg">
            Envía tu primera factura al SRI en menos de 5 minutos.
          </p>
        </div>

        <div className="space-y-10">
          <section>
            <h2 className="font-heading font-semibold text-xl mb-4">
              1. Agregar dependencia
            </h2>
            <CodeBlock code={mavenCode} language="xml (Maven)" />
            <p className="text-sm text-muted-foreground mt-3">
              También disponible en Gradle. Ver{" "}
              <a
                href="https://github.com/nulldoomer/Open-SRI"
                className="underline underline-offset-2 hover:text-foreground"
                target="_blank"
                rel="noopener noreferrer"
              >
                README
              </a>{" "}
              para más opciones.
            </p>
          </section>

          <section>
            <h2 className="font-heading font-semibold text-xl mb-4">
              2. Enviar una factura
            </h2>
            <CodeBlock code={quickStartCode} language="java" />
          </section>

          <section>
            <h2 className="font-heading font-semibold text-xl mb-4">
              3. Probar en el Playground
            </h2>
            <p className="text-muted-foreground text-sm leading-relaxed">
              El{" "}
              <a href="/playground" className="underline underline-offset-2 hover:text-foreground">
                Playground interactivo
              </a>{" "}
              te permite generar una factura sin escribir código, ver el XML generado
              y observar cada paso del pipeline en tiempo real — firma XAdES-BES, SOAP y
              respuesta del SRI incluidos.
            </p>
          </section>

          <section className="border-t border-border pt-10">
            <h2 className="font-heading font-semibold text-xl mb-4">Requisitos</h2>
            <ul className="space-y-2 text-sm text-muted-foreground">
              <li>
                <span className="font-medium text-foreground">Java 17+</span> — LTS recomendado
              </li>
              <li>
                <span className="font-medium text-foreground">Certificado P12</span> — emitido
                por el BCE o Banco Central del Ecuador
              </li>
              <li>
                <span className="font-medium text-foreground">RUC válido</span> — 13 dígitos con
                verificación de módulo 11
              </li>
            </ul>
          </section>

          <div className="flex items-center gap-2 p-4 border border-amber-200 bg-amber-50 dark:border-amber-900/50 dark:bg-amber-950/20 rounded-sm text-sm">
            <HugeiconsIcon icon={Alert01Icon} size={16} strokeWidth={1.5} className="text-amber-600 dark:text-amber-400 shrink-0" />
            <p className="text-amber-800 dark:text-amber-200">
              El SDK actualmente soporta solo el <strong>ambiente de pruebas</strong> del SRI.
              El soporte de producción está en el roadmap.
            </p>
          </div>
        </div>
      </main>
    </div>
  );
}
