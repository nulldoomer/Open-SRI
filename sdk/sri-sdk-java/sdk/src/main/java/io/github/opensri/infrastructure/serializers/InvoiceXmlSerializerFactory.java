package io.github.opensri.infrastructure.serializers;

import io.github.opensri.application.ports.XmlSerializer;
import io.github.opensri.domain.entities.invoice.Invoice;

public class InvoiceXmlSerializerFactory {
    public static XmlSerializer<Invoice> create(){
        return new InvoiceXmlSerializer();
    }
}
