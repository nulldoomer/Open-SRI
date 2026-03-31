package io.github.opensri.infrastructure.models;

import io.github.opensri.domain.entities.common.TotalTax;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class TotalImpuestoXML {

    @XmlElement(name = "codigo")
    private String codigo;

    @XmlElement(name = "codigoPorcentaje")
    private String codigoPorcentaje;

    @XmlElement(name = "baseImponible")
    private String baseImponible;

    @XmlElement(name = "valor")
    private String valor;

    // Constructor vacío para generar la instancia del contexto de JAXB
    public TotalImpuestoXML() {}

    public static TotalImpuestoXML fromDomain(TotalTax totalTax){
        TotalImpuestoXML xml = new TotalImpuestoXML();

        xml.codigo = totalTax.code();
        xml.codigoPorcentaje = totalTax.rateCode();
        xml.baseImponible = String.valueOf(totalTax.taxableBase());
        xml.valor = String.valueOf(totalTax.value());

        return xml;
    }
}
