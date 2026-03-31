package io.github.opensri.infrastructure.models;

import io.github.opensri.domain.entities.common.TotalTax;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

/**
 * JAXB model for a grouped tax entry inside the invoice totals section.
 *
 * <p>This class maps the aggregated tax values that appear under the
 * {@code totalConImpuestos} block of the invoice XML.
 */
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

    /**
     * Creates the XML model for a grouped invoice tax total.
     *
     * @param totalTax domain aggregated tax entry to transform
     * @return JAXB-ready XML model for the grouped tax total
     */
    public static TotalImpuestoXML fromDomain(TotalTax totalTax){
        TotalImpuestoXML xml = new TotalImpuestoXML();

        xml.codigo = totalTax.code();
        xml.codigoPorcentaje = totalTax.rateCode();
        xml.baseImponible = String.valueOf(totalTax.taxableBase());
        xml.valor = String.valueOf(totalTax.value());

        return xml;
    }
}
