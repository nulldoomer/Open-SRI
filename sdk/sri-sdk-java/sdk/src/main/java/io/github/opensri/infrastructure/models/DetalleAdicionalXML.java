package io.github.opensri.infrastructure.models;

import io.github.opensri.domain.entities.invoice.AdditionalDetail;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

/**
 * JAXB model for an additional detail entry inside an invoice item.
 *
 * <p>This XML structure maps the optional name-value attributes that can be attached
 * to a specific invoice line under the SRI {@code detallesAdicionales} section.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class DetalleAdicionalXML {

    @XmlAttribute
    private String nombre;

    @XmlAttribute
    private String valor;

    public DetalleAdicionalXML() {}

    /**
     * Creates the XML model for an additional item detail.
     *
     * @param detail domain additional detail to map
     * @return JAXB-ready XML model for the given additional detail
     */
    public static DetalleAdicionalXML fromDomain(AdditionalDetail detail) {
        DetalleAdicionalXML xml = new DetalleAdicionalXML();

        xml.nombre = detail.name();
        xml.valor = detail.value();

        return xml;
    }
}
