package io.github.opensri.application.usecases;

import io.github.opensri.application.ports.AccessKeyGenerator;
import io.github.opensri.application.ports.DocumentSigner;
import io.github.opensri.application.ports.SRIGateway;
import io.github.opensri.application.ports.XmlSerializer;
import io.github.opensri.domain.entities.invoice.Invoice;

class SendInvoiceUseCase {

    // TODO: Keep this use case depending only on application ports.
    //  Concrete implementations must be created in OpenSRIClient and injected here
    //  through the constructor as part of the SDK wiring process.

    private final AccessKeyGenerator accessKeyGenerator;
    private final XmlSerializer<Invoice> xmlSerializer;
    private final DocumentSigner documentSigner;
    private final SRIGateway sriGateway;

    public SendInvoiceUseCase(AccessKeyGenerator accessKeyGenerator,
                              XmlSerializer<Invoice> xmlSerializer,
                              DocumentSigner documentSigner,
                              SRIGateway sriGateway) {

        this.accessKeyGenerator = accessKeyGenerator;
        this.xmlSerializer = xmlSerializer;
        this.documentSigner = documentSigner;
        this.sriGateway = sriGateway;
    }

    // TODO: Add execute(...) as the orchestration entry point for invoice submission.
    //  The use case should receive the Invoice plus the stable client context
    //  needed to complete the flow (at minimum Environment and IssuerProfile,
    //  either directly or through a command/request object).
    //  1. Generate the access key with AccessKeyGenerator
    //  2. Serialize the invoice to XML with the generated access key
    //  3. Sign the XML with DocumentSigner
    //  4. Send the signed XML through SRIGateway
    //  5. Reuse the same access key to request authorization from the SRI
    //  Important: do not recover the access key by parsing the XML.
    //  It should remain an explicit value of the application flow.
}
