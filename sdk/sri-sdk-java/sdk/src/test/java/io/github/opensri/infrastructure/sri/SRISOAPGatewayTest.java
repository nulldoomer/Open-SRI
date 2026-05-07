// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.sri;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import ec.sri.autorizacion.Autorizacion;
import ec.sri.autorizacion.Mensaje;
import ec.sri.autorizacion.RespuestaComprobante;
import ec.sri.recepcion.RespuestaSolicitud;
import io.github.opensri.domain.entities.responses.AuthorizationResponse;
import io.github.opensri.domain.entities.responses.ReceiptResponse;
import io.github.opensri.infrastructure.sri.proxy.AuthorizationProxy;
import io.github.opensri.infrastructure.sri.proxy.SendReceiptProxy;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SRISOAPGatewayTest {

  private SendReceiptProxy receiptProxy;
  private AuthorizationProxy authorizationProxy;
  private SRISOAPGateway gateway;

  @BeforeEach
  void setUp() {
    receiptProxy = mock(SendReceiptProxy.class);
    authorizationProxy = mock(AuthorizationProxy.class);
    gateway = new SRISOAPGateway(receiptProxy, authorizationProxy);
  }

  @Test
  @DisplayName("Debe retornar estado RECIBIDA cuando el SRI acepta el documento")
  void should_return_received_status_when_sri_accepts_document() {
    // GIVEN
    String signedXml = "<factura>...</factura>";
    RespuestaSolicitud mockResponse = mock(RespuestaSolicitud.class);
    when(mockResponse.getEstado()).thenReturn("RECIBIDA");
    when(receiptProxy.sendReceipt(any(byte[].class))).thenReturn(mockResponse);

    // WHEN
    ReceiptResponse result = gateway.sendDocument(signedXml);

    // THEN
    assertEquals("RECIBIDA", result.status());
    assertTrue(result.messages().isEmpty());
    verify(receiptProxy).sendReceipt(any(byte[].class));
  }

  @Test
  @DisplayName("Debe capturar mensajes de error cuando el SRI devuelve el documento (DEVUELTA)")
  void should_capture_errors_when_sri_returns_document() {
    // GIVEN
    RespuestaSolicitud mockResponse = mock(RespuestaSolicitud.class);
    RespuestaSolicitud.Comprobantes mockComprobantes = mock(RespuestaSolicitud.Comprobantes.class);
    ec.sri.recepcion.Comprobante mockComp = mock(ec.sri.recepcion.Comprobante.class);
    ec.sri.recepcion.Comprobante.Mensajes mockMensajes =
        mock(ec.sri.recepcion.Comprobante.Mensajes.class);
    ec.sri.recepcion.Mensaje mockMsg = mock(ec.sri.recepcion.Mensaje.class);

    when(mockResponse.getEstado()).thenReturn("DEVUELTA");
    when(mockResponse.getComprobantes()).thenReturn(mockComprobantes);
    when(mockComprobantes.getComprobante()).thenReturn(List.of(mockComp));
    when(mockComp.getMensajes()).thenReturn(mockMensajes);
    when(mockMensajes.getMensaje()).thenReturn(List.of(mockMsg));

    when(mockMsg.getIdentificador()).thenReturn("43");
    when(mockMsg.getMensaje()).thenReturn("CLAVE ACCESO DEFECTUOSA");
    when(mockMsg.getInformacionAdicional()).thenReturn("La clave no cumple el algoritmo");
    when(mockMsg.getTipo()).thenReturn("ERROR");

    when(receiptProxy.sendReceipt(any(byte[].class))).thenReturn(mockResponse);

    // WHEN
    ReceiptResponse result = gateway.sendDocument("invalid-xml");

    // THEN
    assertEquals("DEVUELTA", result.status());
    assertEquals(1, result.messages().size());
    assertEquals("CLAVE ACCESO DEFECTUOSA", result.messages().get(0).message());
    assertEquals("ERROR", result.messages().get(0).type());
  }

  @Test
  @DisplayName("Debe procesar una autorización exitosa (AUTORIZADO)")
  void should_process_successful_authorization() throws Exception {
    // GIVEN
    String accessKey = "1234567890123456789012345678901234567890123456789";
    RespuestaComprobante mockResponse = createMockAuthorizationResponse("AUTORIZADO", accessKey);

    when(authorizationProxy.singleAuthorize(accessKey)).thenReturn(mockResponse);

    // WHEN
    AuthorizationResponse result = gateway.sendAuthorization(accessKey);

    // THEN
    assertEquals("AUTORIZADO", result.status());
    assertEquals(accessKey, result.authorizationNumber());
    assertNotNull(result.authorizationDate());
    assertEquals("PRUEBAS", result.environment());
    verify(authorizationProxy).singleAuthorize(accessKey);
  }

  @Test
  @DisplayName("Debe manejar el estado EN PROCESO reemplazando espacios por guiones bajos")
  void should_handle_in_progress_status_with_underscores() throws Exception {
    // GIVEN
    String accessKey = "12345";
    RespuestaComprobante mockResponse = createMockAuthorizationResponse("EN PROCESO", accessKey);
    when(authorizationProxy.singleAuthorize(accessKey)).thenReturn(mockResponse);

    // WHEN
    AuthorizationResponse result = gateway.sendAuthorization(accessKey);

    // THEN
    assertEquals("EN_PROCESO", result.status());
  }

  @Test
  @DisplayName("Debe capturar múltiples mensajes en una autorización rechazada")
  void should_capture_multiple_messages_on_rejected_authorization() throws Exception {
    // GIVEN
    String accessKey = "123";
    RespuestaComprobante mockResponse = createMockAuthorizationResponse("RECHAZADO", accessKey);
    Autorizacion auth = mockResponse.getAutorizaciones().getAutorizacion().get(0);

    Mensaje msg1 = createMockMessage("1", "Error 1");
    Mensaje msg2 = createMockMessage("2", "Error 2");
    when(auth.getMensajes().getMensaje()).thenReturn(List.of(msg1, msg2));

    when(authorizationProxy.singleAuthorize(accessKey)).thenReturn(mockResponse);

    // WHEN
    AuthorizationResponse result = gateway.sendAuthorization(accessKey);

    // THEN
    assertEquals("RECHAZADO", result.status());
    assertEquals(2, result.messages().size());
    assertEquals("Error 1", result.messages().get(0).message());
    assertEquals("Error 2", result.messages().get(1).message());
  }

  // --- MÉTODOS HELPER PARA MOCKS COMPLEJOS ---

  private RespuestaComprobante createMockAuthorizationResponse(String status, String accessKey)
      throws Exception {
    RespuestaComprobante mockResponse = mock(RespuestaComprobante.class);
    RespuestaComprobante.Autorizaciones mockAuths = mock(RespuestaComprobante.Autorizaciones.class);
    Autorizacion mockAuth = mock(Autorizacion.class);
    Autorizacion.Mensajes mockMensajes = mock(Autorizacion.Mensajes.class);

    when(mockResponse.getAutorizaciones()).thenReturn(mockAuths);
    when(mockAuths.getAutorizacion()).thenReturn(List.of(mockAuth));
    when(mockAuth.getEstado()).thenReturn(status);
    when(mockAuth.getNumeroAutorizacion()).thenReturn(accessKey);
    when(mockAuth.getAmbiente()).thenReturn("PRUEBAS");
    when(mockAuth.getComprobante()).thenReturn("<xml>...</xml>");
    when(mockAuth.getMensajes()).thenReturn(mockMensajes);
    when(mockMensajes.getMensaje()).thenReturn(Collections.emptyList());

    XMLGregorianCalendar xmlDate =
        DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());
    when(mockAuth.getFechaAutorizacion()).thenReturn(xmlDate);

    return mockResponse;
  }

  private Mensaje createMockMessage(String id, String text) {
    Mensaje m = mock(Mensaje.class);
    when(m.getIdentificador()).thenReturn(id);
    when(m.getMensaje()).thenReturn(text);
    when(m.getTipo()).thenReturn("ERROR");
    return m;
  }
}
