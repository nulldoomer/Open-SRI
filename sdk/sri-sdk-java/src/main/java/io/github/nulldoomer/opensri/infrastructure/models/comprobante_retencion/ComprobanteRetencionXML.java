// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.infrastructure.models.comprobante_retencion;

import io.github.nulldoomer.opensri.domain.entities.common.issuer.IssuerProfile;
import io.github.nulldoomer.opensri.domain.entities.withholding.WithholdingReceipt;
import io.github.nulldoomer.opensri.domain.enums.DocumentVersion;
import io.github.nulldoomer.opensri.domain.enums.Environment;
import io.github.nulldoomer.opensri.infrastructure.models.common.InfoAdicionalXML;
import io.github.nulldoomer.opensri.infrastructure.models.common.InfoTributariaXML;
import jakarta.xml.bind.annotation.*;
import java.util.List;

/**
 * Root JAXB model for the SRI withholding-receipt XML document (version 1.0.0).
 *
 * <p>Assembles the {@code comprobanteRetencion} structure: tax information, the withholding info
 * block, the flat list of withholdings, and optional additional fields. Reuses the shared {@link
 * InfoTributariaXML} and {@link InfoAdicionalXML} models.
 */
@XmlRootElement(name = "comprobanteRetencion")
@XmlAccessorType(XmlAccessType.FIELD)
public class ComprobanteRetencionXML {

  @XmlAttribute private String id = "comprobante";

  @XmlAttribute private String version;

  @XmlElement(name = "infoTributaria")
  private InfoTributariaXML infoTributaria;

  @XmlElement(name = "infoCompRetencion")
  private InfoCompRetencionXML infoCompRetencion;

  @XmlElementWrapper(name = "impuestos")
  @XmlElement(name = "impuesto")
  private List<ImpuestoRetencionXML> impuestos;

  @XmlElementWrapper(name = "docsSustento")
  @XmlElement(name = "docSustento")
  private List<DocSustentoXML> docsSustento;

  @XmlElementWrapper(name = "infoAdicional")
  @XmlElement(name = "campoAdicional")
  private List<InfoAdicionalXML> infoAdicional;

  /** Required no-arg constructor for JAXB deserialization. */
  public ComprobanteRetencionXML() {}

  /**
   * Creates the complete XML model for a withholding receipt (version 1.0.0).
   *
   * @param receipt withholding-receipt domain object to transform
   * @param accessKey access key previously generated for the document
   * @param env target SRI environment of the XML
   * @param profile issuer profile used to complete the fiscal sections
   * @return root JAXB model representing the withholding-receipt XML document
   */
  public static ComprobanteRetencionXML fromDomain(
      WithholdingReceipt receipt, String accessKey, Environment env, IssuerProfile profile) {
    ComprobanteRetencionXML xml = new ComprobanteRetencionXML();

    xml.version = receipt.documentVersion().getVersion();

    xml.infoTributaria =
        InfoTributariaXML.fromDomain(receipt.taxInfo(), receipt.documentNumber(), accessKey, env);

    xml.infoCompRetencion = InfoCompRetencionXML.fromDomain(receipt, profile);

    if (receipt.documentVersion() == DocumentVersion.VERSION_200) {
      xml.docsSustento =
          receipt.supportDocuments().stream().map(DocSustentoXML::fromDomain).toList();
    } else {
      xml.impuestos =
          receipt.withholdings().stream().map(ImpuestoRetencionXML::fromDomain).toList();
    }

    if (!receipt.additionalInfo().isEmpty()) {
      xml.infoAdicional =
          receipt.additionalInfo().stream().map(InfoAdicionalXML::fromDomain).toList();
    }

    return xml;
  }
}
