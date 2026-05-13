// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.sri.utils;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Reúne utilidades DOM para navegar respuestas SOAP del SRI de forma segura.
 *
 * <p>Centraliza el parseo namespace-aware del XML y las búsquedas por nombre local para que los
 * mappers y parsers del adapter SOAP no repitan lógica de navegación ni configuración del parser.
 */
public final class XmlUtils {

  // SINGLETON
  private static final DocumentBuilderFactory FACTORY = createFactory();

  private XmlUtils() {}

  /**
   * Parsea una cadena XML a un {@link Document} DOM seguro para lectura.
   *
   * @param xml contenido XML completo del envelope SOAP
   * @return documento DOM listo para navegar por nombre local
   * @throws Exception si el XML no es bien formado o el parser no puede construir el documento
   */
  public static Document parse(String xml) throws Exception {
    DocumentBuilder builder = FACTORY.newDocumentBuilder();
    return builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
  }

  /**
   * Busca el primer elemento del documento por nombre local sin depender del prefijo XML.
   *
   * @param document documento DOM donde se realizará la búsqueda
   * @param localName nombre local del elemento buscado
   * @return primer elemento coincidente o {@code null} si no existe
   */
  public static Element firstElementByLocalName(Document document, String localName) {
    NodeList matches = document.getElementsByTagNameNS("*", localName);
    return matches.getLength() > 0 ? (Element) matches.item(0) : null;
  }

  /**
   * Obtiene el primer hijo directo de un elemento usando solo el nombre local.
   *
   * @param parent elemento contenedor
   * @param localName nombre local del hijo esperado
   * @return primer hijo directo coincidente o {@code null} si no existe
   */
  public static Element firstDirectChild(Element parent, String localName) {
    NodeList children = parent.getChildNodes();

    for (int i = 0; i < children.getLength(); i++) {
      Node child = children.item(i);

      if (child instanceof Element element && localName.equalsIgnoreCase(element.getLocalName())) {
        return element;
      }
    }
    return null;
  }

  /**
   * Lista todos los hijos directos que coinciden con un nombre local.
   *
   * @param parent elemento contenedor
   * @param localName nombre local de los hijos a recopilar
   * @return hijos directos coincidentes en el orden en que aparecen en el XML
   */
  public static List<Element> children(Element parent, String localName) {
    List<Element> elements = new ArrayList<>();
    NodeList children = parent.getChildNodes();

    for (int i = 0; i < children.getLength(); i++) {
      Node child = children.item(i);
      if (child instanceof Element element && localName.equalsIgnoreCase(element.getLocalName())) {

        elements.add(element);
      }
    }
    return elements;
  }

  /**
   * Devuelve el contenido textual del primer hijo directo coincidente.
   *
   * @param parent elemento contenedor
   * @param localName nombre local del hijo cuyo texto se desea leer
   * @return texto del nodo encontrado o {@code null} si el hijo no existe
   */
  public static String textOfFirstChild(Element parent, String localName) {
    Element child = firstDirectChild(parent, localName);
    return child != null ? child.getTextContent() : null;
  }

  /**
   * Crea la factoría DOM compartida con defensas básicas contra XXE.
   *
   * @return factoría configurada para parsear envelopes SOAP del SRI
   */
  private static DocumentBuilderFactory createFactory() {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);

      factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

      factory.setFeature("http://xml.org/sax/features/external-general-entities", false);

      factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

      return factory;
    } catch (Exception e) {
      throw new RuntimeException("Cannot configure XML factory", e);
    }
  }
}
