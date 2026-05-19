// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.application;

import io.github.opensri.application.ports.AccessKeyGenerator;
import io.github.opensri.application.ports.DocumentSigner;
import io.github.opensri.application.ports.SRIGateway;
import io.github.opensri.application.ports.XmlSerializer;
import io.github.opensri.domain.entities.common.issuer.IssuerProfile;
import io.github.opensri.domain.entities.invoice.Invoice;
import io.github.opensri.domain.entities.responses.ReceiptResponse;
import io.github.opensri.domain.entities.responses.SendInvoiceResult;
import io.github.opensri.domain.enums.Environment;
import java.io.IOException;

class SendInvoiceUseCase {

  private final AccessKeyGenerator accessKeyGenerator;
  private final XmlSerializer<Invoice> xmlSerializer;
  private final DocumentSigner documentSigner;
  private final SRIGateway sriGateway;

  SendInvoiceUseCase(
      AccessKeyGenerator accessKeyGenerator,
      XmlSerializer<Invoice> xmlSerializer,
      DocumentSigner documentSigner,
      SRIGateway sriGateway) {

    this.accessKeyGenerator = accessKeyGenerator;
    this.xmlSerializer = xmlSerializer;
    this.documentSigner = documentSigner;
    this.sriGateway = sriGateway;
  }

  //  5. Reuse the same access key to request authorization from the SRI
  //  Important: do not recover the access key by parsing the XML.
  //  It should remain an explicit value of the application flow.

  SendInvoiceResult execute(Invoice invoice, Environment environment, IssuerProfile issuerProfile) {

    try {

      String accessKey =
          accessKeyGenerator.generate(
              invoice.issueDate(), invoice.documentNumber(), invoice.taxInfo(), environment);

      String unsignedXml = xmlSerializer.serialize(invoice, accessKey, environment, issuerProfile);

      String signedXml = documentSigner.signDocument(unsignedXml);

      ReceiptResponse response = sriGateway.sendDocument(signedXml);

      return new SendInvoiceResult(accessKey, signedXml, response);

    } catch (IOException | InterruptedException e) {

      throw new RuntimeException(e);
    }
  }
}
