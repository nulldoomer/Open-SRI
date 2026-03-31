package io.github.opensri.infrastructure.models;

import io.github.opensri.domain.entities.common.Tax;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

/**
 * JAXB model for a tax entry attached to an invoice item.
 *
 * <p>This class maps the tax code, percentage code, rate, taxable base,
 * and calculated value required by the {@code impuesto} element in the SRI schema.
 */
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

    /**
     * Creates the XML model for an item-level tax.
     *
     * @param tax domain tax to transform
     * @return JAXB-ready XML tax model
     */
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
