// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.crypto.signing;

import io.github.opensri.application.ports.DocumentSigner;
import io.github.opensri.infrastructure.crypto.certificates.model.SigningKey;
import io.github.opensri.shared.exceptions.SRICertificateException;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import xades4j.algorithms.EnvelopedSignatureTransform;
import xades4j.production.DataObjectReference;
import xades4j.production.SignedDataObjects;
import xades4j.production.XadesBesSigningProfile;
import xades4j.production.XadesSigner;
import xades4j.properties.DataObjectDesc;
import xades4j.providers.impl.DirectKeyingDataProvider;

/**
 * Signs XML documents using an XAdES-BES profile and a loaded signing certificate.
 *
 * <p>This infrastructure implementation applies an enveloped XML signature over the provided
 * document using the private key and certificate contained in {@link SigningKey}. It relies on
 * xades4j to produce the signature structure expected by the SRI signing process.
 *
 * <p>It implements {@link DocumentSigner}.
 */
class XAdEsSigner implements DocumentSigner {

  // DI
  private final SigningKey signingKey;

  public XAdEsSigner(SigningKey signingKey) {
    this.signingKey = signingKey;
  }

  /**
   * Produces an XML document signed with XAdES-BES.
   *
   * <p>The input XML is parsed into a DOM document, signed as an enveloped signature, and then
   * serialized back to UTF-8 text.
   *
   * @param xmlDocument XML document to sign
   * @return signed XML document
   * @throws SRICertificateException if the XML cannot be parsed or the signing process fails
   */
  @Override
  public String signDocument(String xmlDocument) {

    try {
      // Turn the String XML into a Document
      Document doc = parseXml(xmlDocument);

      // Set the key provider and certificate
      DirectKeyingDataProvider keyProvider =
          new DirectKeyingDataProvider(signingKey.certificate(), signingKey.privateKey());

      // Create the BES profile
      // Here we configure the private key of the certificate
      XadesBesSigningProfile profile = new XadesBesSigningProfile(keyProvider);
      XadesSigner signer = profile.newSigner();

      // Define what are we going to sign
      // SRI expects the "Enveloped" signature
      DataObjectDesc dataObject =
          new DataObjectReference("").withTransform(new EnvelopedSignatureTransform());

      // Sign
      // This introduces the tag <ds:Signature> onto the root XML
      signer.sign(new SignedDataObjects(dataObject), doc.getDocumentElement());

      return documentToString(doc);
    } catch (Exception e) {
      throw new SRICertificateException("XML document cannot be null or empty", e);
    }
  }

  private Document parseXml(String xmlDocument) throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    // ============================== HARDENING ===============================
    factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
    factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
    factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
    factory.setNamespaceAware(true);
    factory.setXIncludeAware(false);
    factory.setExpandEntityReferences(false);
    // ============================== HARDENING ===============================

    DocumentBuilder builder = factory.newDocumentBuilder();

    return builder.parse(new ByteArrayInputStream(xmlDocument.getBytes(StandardCharsets.UTF_8)));
  }

  private String documentToString(Document doc) throws Exception {
    TransformerFactory tFactory = TransformerFactory.newInstance();

    Transformer transformer = tFactory.newTransformer();
    transformer.setOutputProperty("encoding", "UTF-8");
    transformer.setOutputProperty("omit-xml-declaration", "no");
    transformer.setOutputProperty("indent", "no");

    StringWriter writer = new StringWriter();

    transformer.transform(new DOMSource(doc), new StreamResult(writer));
    return writer.toString();
  }
}
