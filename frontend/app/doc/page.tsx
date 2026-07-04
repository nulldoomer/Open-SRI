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

export default function Docs() {
  return (
    <>
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
          <LanguageTabs
            tabs={[
              { label: "Maven", code: mavenCode, lang: "xml" },
              { label: "Gradle (Groovy)", code: gradleCode, lang: "groovy" },
              { label: "Gradle (Kotlin)", code: gradleKotlinCode, lang: "kotlin" },
            ]}
          />
          <p className="mt-3 text-sm text-muted-foreground">
            La librería Java está publicada en{" "}
            <a
              href="https://central.sonatype.com/artifact/io.github.nulldoomer/opensri"
              target="_blank"
              rel="noreferrer noopener"
              className="underline underline-offset-2 hover:text-foreground"
            >
              Maven Central
            </a>
            .
          </p>
        </section>

        <section>
          <h2 className="font-heading font-semibold text-xl mb-4">
            2. Enviar una factura
          </h2>
          <LangCodeBlock
            java={{ code: quickStartCode, lang: "java" }}
          />
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
              por el Banco Central del Ecuador (BCE)
            </li>
            <li>
              <span className="font-medium text-foreground">RUC válido</span> — 13 dígitos con
              verificación de módulo 11
            </li>
          </ul>
        </section>
      </div>
    </>
  );
}
