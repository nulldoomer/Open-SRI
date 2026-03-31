package io.github.opensri.infrastructure.models;

import io.github.opensri.domain.entities.common.IssuerProfile;
import io.github.opensri.domain.entities.invoice.Invoice;
import io.github.opensri.domain.enums.Environment;
import jakarta.xml.bind.annotation.*;

import java.util.List;

/**
 * Root JAXB model for the SRI invoice XML document.
 *
 * <p>This class assembles the top-level invoice structure, including tax information,
 * invoice totals, detail lines, and optional additional fields. It mirrors the
 * {@code factura} element expected by the SRI schema.
 */
@XmlRootElement(name = "factura")
@XmlAccessorType(XmlAccessType.FIELD)
public class FacturaXML{
    // TODO: Crear una constante, ya que este valor no cambia en ningún tipo de
    //  documento
    @XmlAttribute
    private String id = "comprobante";

    // TODO: Manejar la versión del XML desde el cliente, eliminar el atributo
    //  hardcoded
    @XmlAttribute
    private String version = "1.1.0";

    @XmlElement(name = "infoTributaria")
    private InfoTributariaXML infoTributaria;

    @XmlElement(name = "infoFactura")
    private InfoFacturaXML infoFactura;

    @XmlElementWrapper(name = "detalles")
    @XmlElement(name = "detalle")
    private List<DetalleXML> detalles;

    @XmlElementWrapper(name = "infoAdicional")
    @XmlElement(name = "campoAdicional")
    private List<InfoAdicionalXML> infoAdicional;

    // Constructor vacío para generar la instancia del contexto de JAXB
    public FacturaXML(){}

    /**
     * Creates the complete XML model for an invoice document.
     *
     * @param invoice domain invoice to transform
     * @param accessKey access key previously generated for the document
     * @param env target SRI environment of the XML
     * @param profile issuer profile used to complete the fiscal sections
     * @return root JAXB model representing the invoice XML document
     */
    public static FacturaXML fromDomain(Invoice invoice, String accessKey,
                                        Environment env, IssuerProfile profile){
        FacturaXML xml= new FacturaXML();

        xml.infoTributaria = InfoTributariaXML.fromDomain(
                invoice.taxInfo(),
                invoice.documentNumber(),
                accessKey,
                env
        );

        xml.infoFactura = InfoFacturaXML.fromDomain(invoice, profile);

        xml.detalles = invoice.items().stream()
                .map(DetalleXML::fromDomain)
                .toList();

        xml.infoAdicional = invoice.additionalInfo().stream()
                .map(InfoAdicionalXML::fromDomain)
                .toList();

        return xml;
    }
}
