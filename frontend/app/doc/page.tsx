import Link from "next/link";
import LanguageTabs from "@/components/docs/LanguageTabs";
import LangCodeBlock from "@/components/docs/LangCodeBlock";

const mavenCode = `<dependency>
  <groupId>io.github.nulldoomer</groupId>
  <artifactId>opensri</artifactId>
  <version>1.2.4</version>
</dependency>`;

const gradleCode = `implementation 'io.github.nulldoomer:opensri:1.2.4'`;

const gradleKotlinCode = `implementation("io.github.nulldoomer:opensri:1.2.4")`;

const quickStartCode = `// Leer el certificado P12
byte[] cert = Files.readAllBytes(Paths.get("certificado.p12"));

// 1. Configurar el cliente
OpenSRIClient client = OpenSRIClientBuilder.builder()
    .environment(Environment.PRUEBAS)
    .certificate(cert)
    .certificatePassword("contraseña")
    .certificateAlias("alias")
    .issuerProfile(new IssuerProfile(
        new Ruc("1791248678001"), null, AccountingObligation.SI))
    .timeout(30)
    .build();

// 2. Construir el ítem y la factura
InvoiceItem item = new InvoiceItem(
    "P001", null, "Servicio de consultoría",
    BigDecimal.ONE, new BigDecimal("100.00"), BigDecimal.ZERO,
    new BigDecimal("100.00"), List.of(),
    List.of(new Tax("2", "0", BigDecimal.ZERO, new BigDecimal("100.00"))));

Invoice invoice = InvoiceBuilder.builder()
    .issueDate(IssueDate.now())
    .establishmentDirection("Calle Principal 123")
    .taxInfo(new TaxInfo(1, new Issuer("MI EMPRESA S.A.", new Ruc("1791248678001")), "Calle Principal 123"))
    .documentNumber(new DocumentNumber("01", "001", "001", "000000001"))
    .documentVersion(DocumentVersion.VERSION_100)
    .client(new Client(new NationalId("1234567890"), "JUAN PÉREZ"))
    .addItems(List.of(item))
    .addPayments(List.of(new ImmediatePayment(
        PaymentMethod.SIN_SISTEMA_FINANCIERO, new BigDecimal("100.00"))))
    .build();

// 3. Enviar al SRI
SendInvoiceResult result = client.sendInvoice(invoice);

System.out.println(result.response().status()); // RECIBIDA
System.out.println(result.accessKey());          // 49 dígitos`;

function Step({ n, children }: { n: number; children: React.ReactNode }) {
  return (
    <h2 className="mb-5 flex items-center gap-3 font-heading text-2xl font-bold tracking-tight">
      <span
        aria-hidden="true"
        className="flex size-8 shrink-0 items-center justify-center rounded-full border border-foreground font-mono text-sm tabular-nums"
      >
        {n}
      </span>
      {children}
    </h2>
  );
}

const link = "text-foreground underline decoration-border underline-offset-4 transition-colors hover:decoration-signal";

export default function Docs() {
  return (
    <>
      <header className="mb-12">
        <h1 className="font-heading text-[clamp(2.25rem,4.5vw,3.25rem)] font-extrabold uppercase leading-[0.92] tracking-[-0.015em] [font-stretch:82%]">
          Quick Start
        </h1>
        <p className="mt-4 text-lg leading-relaxed text-muted-foreground">
          Envía tu primera factura al SRI en menos de 5 minutos.
        </p>
      </header>

      <ol className="space-y-14">
        <li>
          <Step n={1}>Agregar la dependencia</Step>
          <LanguageTabs
            tabs={[
              { label: "Maven", code: mavenCode, lang: "xml" },
              { label: "Gradle", code: gradleCode, lang: "groovy" },
              { label: "Kotlin DSL", code: gradleKotlinCode, lang: "kotlin" },
            ]}
          />
          <p className="mt-4 text-[15px] leading-7 text-muted-foreground">
            La librería Java está publicada en{" "}
            <a
              href="https://central.sonatype.com/artifact/io.github.nulldoomer/opensri"
              target="_blank"
              rel="noreferrer noopener"
              className={link}
            >
              Maven Central
            </a>
            .
          </p>
        </li>

        <li>
          <Step n={2}>Enviar una factura</Step>
          <LangCodeBlock java={{ code: quickStartCode, lang: "java" }} />
        </li>

        <li>
          <Step n={3}>Probar en el Playground</Step>
          <p className="text-[15px] leading-7 text-muted-foreground">
            El{" "}
            <Link href="/playground" className={link}>
              Playground interactivo
            </Link>{" "}
            te permite generar una factura sin escribir código, ver el XML generado y observar cada paso del
            pipeline en tiempo real, con la firma XAdES-BES, el envío SOAP y la respuesta del SRI incluidos.
          </p>
        </li>
      </ol>

      <section className="mt-16 border-t border-border pt-8">
        <h2 className="mb-5 font-heading text-2xl font-bold tracking-tight">Requisitos</h2>
        <dl className="divide-y divide-border rounded-[var(--r-card-sm)] border border-border bg-card">
          {[
            ["Java 17+", "LTS recomendado"],
            ["Certificado P12", "Emitido por el Banco Central del Ecuador (BCE)"],
            ["RUC válido", "13 dígitos con verificación de módulo 11"],
          ].map(([term, detail]) => (
            <div key={term} className="grid gap-1 px-5 py-4 sm:grid-cols-[10rem_1fr] sm:gap-6">
              <dt className="font-medium">{term}</dt>
              <dd className="text-[15px] text-muted-foreground">{detail}</dd>
            </div>
          ))}
        </dl>
      </section>
    </>
  );
}
