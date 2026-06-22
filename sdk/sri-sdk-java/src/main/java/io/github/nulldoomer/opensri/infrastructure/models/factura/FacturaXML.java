// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.infrastructure.models.factura;

import io.github.nulldoomer.opensri.domain.entities.common.issuer.IssuerProfile;
import io.github.nulldoomer.opensri.domain.entities.invoice.Invoice;
import io.github.nulldoomer.opensri.domain.enums.DocumentVersion;
import io.github.nulldoomer.opensri.domain.enums.Environment;
import io.github.nulldoomer.opensri.infrastructure.models.common.InfoAdicionalXML;
import io.github.nulldoomer.opensri.infrastructure.models.common.InfoTributariaXML;
import jakarta.xml.bind.annotation.*;
import java.util.List;

/**
 * Root JAXB model for the SRI invoice XML document.
 *
 * <p>This class assembles the top-level invoice structure, including tax information, invoice
 * totals, detail lines, and optional additional fields. It mirrors the {@code factura} element
 * expected by the SRI schema and now receives the XML version explicitly from the serialization
 * flow.
 */
@XmlRootElement(name = "factura")
@XmlAccessorType(XmlAccessType.FIELD)
public class FacturaXML {
  // TODO: Crear una constante, ya que este valor no cambia en ningún tipo de
  //  documento
  @XmlAttribute private String id = "comprobante";

  @XmlAttribute private String version;

  @XmlElement(name = "infoTributaria")
  private InfoTributariaXML infoTributaria;

  @XmlElement(name = "infoFactura")
  private InfoFacturaXML infoFactura;

  @XmlElementWrapper(name = "detalles")
  @XmlElement(name = "detalle")
  private List<DetalleXML> detalles;

  @XmlElementWrapper(name = "retenciones")
  @XmlElement(name = "retencion")
  private List<RetencionXML> retenciones;

  @XmlElement(name = "infoSustitutivaGuiaRemision")
  private InfoSustitutivaGuiaRemisionXML infoSustitutivaGuiaRemision;

  @XmlElementWrapper(name = "otrosRubrosTerceros")
  @XmlElement(name = "rubro")
  private List<RubroXML> otrosRubrosTerceros;

  @XmlElementWrapper(name = "infoAdicional")
  @XmlElement(name = "campoAdicional")
  private List<InfoAdicionalXML> infoAdicional;

  /** Required no-arg constructor for JAXB deserialization. */
  public FacturaXML() {}

  /**
   * Creates the complete XML model for an invoice document.
   *
   * @param invoice domain invoice to transform
   * @param accessKey access key previously generated for the document
   * @param env target SRI environment of the XML
   * @param profile issuer profile used to complete the fiscal sections
   * @return root JAXB model representing the invoice XML document
   */
  public static FacturaXML fromDomain(
      Invoice invoice, String accessKey, Environment env, IssuerProfile profile) {
    FacturaXML xml = new FacturaXML();

    xml.version = invoice.documentVersion().getVersion();

    xml.infoTributaria =
        InfoTributariaXML.fromDomain(invoice.taxInfo(), invoice.documentNumber(), accessKey, env);

    xml.infoFactura = InfoFacturaXML.fromDomain(invoice, profile);

    xml.detalles = invoice.items().stream().map(DetalleXML::fromDomain).toList();

    DocumentVersion v = invoice.documentVersion();
    boolean supportsRetenciones =
        v == DocumentVersion.VERSION_110 || v == DocumentVersion.VERSION_210;
    boolean supportsTransportFields =
        v == DocumentVersion.VERSION_200 || v == DocumentVersion.VERSION_210;

    if (supportsRetenciones && !invoice.retenciones().isEmpty()) {
      xml.retenciones = invoice.retenciones().stream().map(RetencionXML::fromDomain).toList();
    }

    if (supportsTransportFields && invoice.remisionGuideSubstituteInfo() != null) {
      xml.infoSustitutivaGuiaRemision =
          InfoSustitutivaGuiaRemisionXML.fromDomain(invoice.remisionGuideSubstituteInfo());
    }

    if (supportsTransportFields && !invoice.otrosRubrosTerceros().isEmpty()) {
      xml.otrosRubrosTerceros =
          invoice.otrosRubrosTerceros().stream().map(RubroXML::fromDomain).toList();
    }

    if (invoice.additionalInfo() != null && !invoice.additionalInfo().isEmpty()) {
      xml.infoAdicional =
          invoice.additionalInfo().stream().map(InfoAdicionalXML::fromDomain).toList();
    }

    return xml;
  }
}
