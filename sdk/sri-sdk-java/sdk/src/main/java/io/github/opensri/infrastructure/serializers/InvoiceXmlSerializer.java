package io.github.opensri.infrastructure.serializers;

import io.github.opensri.application.ports.AccessKeyGenerator;
import io.github.opensri.application.ports.XmlSerializer;
import io.github.opensri.domain.enums.Environment;
import io.github.opensri.infrastructure.models.FacturaXML;

class InvoiceXmlSerializer implements XmlSerializer<FacturaXML> {

    final AccessKeyGenerator accessKeyGenerator;
    final Environment environment;

    InvoiceXmlSerializer(AccessKeyGenerator accessKeyGenerator,
                         Environment environment)
    {
        this.accessKeyGenerator = accessKeyGenerator;
        this.environment = environment;
    }


    @Override
    public String serialize(FacturaXML document) {
        return "";
    }
}
