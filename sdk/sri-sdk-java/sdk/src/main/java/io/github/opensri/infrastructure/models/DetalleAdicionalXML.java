package io.github.opensri.infrastructure.models;

import io.github.opensri.domain.entities.invoice.AdditionalDetail;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class DetalleAdicionalXML {

    @XmlAttribute
    private String nombre;

    @XmlAttribute
    private String valor;

    public DetalleAdicionalXML() {}

    public static DetalleAdicionalXML fromDomain(AdditionalDetail detail) {
        DetalleAdicionalXML xml = new DetalleAdicionalXML();

        xml.nombre = detail.name();
        xml.valor = detail.value();

        return xml;
    }
}
