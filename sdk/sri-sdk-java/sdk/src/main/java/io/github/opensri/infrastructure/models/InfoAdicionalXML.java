package io.github.opensri.infrastructure.models;

import io.github.opensri.domain.entities.invoice.AdditionalInfo;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
public class InfoAdicionalXML {

    @XmlAttribute
    private String nombre;

    @XmlValue
    private String valor;

    public InfoAdicionalXML(){}

    public static InfoAdicionalXML fromDomain(AdditionalInfo addInfo){
        InfoAdicionalXML xml = new InfoAdicionalXML();

        xml.nombre = addInfo.name();
        xml.valor = addInfo.value();

        return xml;
    }
}
