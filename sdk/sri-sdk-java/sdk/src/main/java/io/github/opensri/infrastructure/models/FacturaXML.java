package io.github.opensri.infrastructure.models;

import io.github.opensri.domain.entities.common.IssuerProfile;
import io.github.opensri.domain.entities.invoice.Invoice;
import io.github.opensri.domain.enums.Environment;
import jakarta.xml.bind.annotation.*;

import java.util.List;

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
