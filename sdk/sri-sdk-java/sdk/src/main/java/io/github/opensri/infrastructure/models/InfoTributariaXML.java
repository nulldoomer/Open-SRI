package io.github.opensri.infrastructure.models;

import io.github.opensri.domain.entities.common.DocumentNumber;
import io.github.opensri.domain.entities.common.TaxInfo;
import io.github.opensri.domain.enums.Environment;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class InfoTributariaXML {

    @XmlElement(name = "ambiente")
    private String ambiente;

    @XmlElement(name = "tipoEmision")
    private String tipoEmision;

    @XmlElement(name = "razonSocial")
    private String razonSocial;

    @XmlElement(name = "ruc")
    private String ruc;

    @XmlElement(name = "claveAcceso")
    private String claveAcceso;

    @XmlElement(name = "codDoc")
    private String codDoc;

    @XmlElement(name = "estab")
    private String estab;

    @XmlElement(name = "ptoEmi")
    private String ptoEmi;

    @XmlElement(name = "secuencial")
    private String secuencial;

    @XmlElement(name = "dirMatriz")
    private String dirMatriz;


    // Constructor vacío para generar la instancia del contexto de JAXB
    public InfoTributariaXML(){}

    public static InfoTributariaXML fromDomain(
            TaxInfo taxInfo,
            DocumentNumber documentNumber,
            String accessKey,
            Environment env
    ){

        InfoTributariaXML xml = new InfoTributariaXML();
        xml.ambiente = String.valueOf(env.getCode());
        xml.tipoEmision = String.valueOf(taxInfo.emissionType());
        xml.razonSocial = taxInfo.issuer().socialReason();
        xml.ruc = taxInfo.issuer().ruc().number();
        xml.claveAcceso = accessKey;
        xml.codDoc = documentNumber.documentCode();
        xml.estab = documentNumber.establishment();
        xml.ptoEmi = documentNumber.emissionPoint();
        xml.secuencial = documentNumber.sequentialNumber();
        xml.dirMatriz = taxInfo.parentAddress();

        return xml;
    }

}
