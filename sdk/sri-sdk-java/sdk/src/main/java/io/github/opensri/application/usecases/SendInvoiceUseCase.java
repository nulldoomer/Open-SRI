package io.github.opensri.application.usecases;

import io.github.opensri.application.ports.AccessKeyGenerator;
import io.github.opensri.application.ports.DocumentSigner;
import io.github.opensri.application.ports.SRIGateway;
import io.github.opensri.application.ports.XmlSerializer;
import io.github.opensri.domain.entities.invoice.Invoice;

class SendInvoiceUseCase {

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

}
