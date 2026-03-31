package io.github.opensri.infrastructure.models;

import io.github.opensri.domain.entities.common.Tax;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class ImpuestoXML {

    @XmlElement(name = "codigo")
    private String codigo;

    @XmlElement(name = "codigoPorcentaje")
    private String codigoPorcentaje;

    @XmlElement(name = "tarifa")
    private String tarifa;

    @XmlElement(name = "baseImponible")
    private String baseImponible;

    @XmlElement(name = "valor")
    private String valor;

    public ImpuestoXML() {}

    public static ImpuestoXML fromDomain(Tax tax){
        ImpuestoXML xml = new ImpuestoXML();

        xml.codigo = tax.code();
        xml.codigoPorcentaje = tax.rateCode();
        xml.tarifa = String.valueOf(tax.rate());
        xml.baseImponible = String.valueOf(tax.taxableBase());
        xml.valor = String.valueOf(tax.value());

        return xml;
    }
}
