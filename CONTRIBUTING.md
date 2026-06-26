# Contribuir a Open-SRI

Gracias por tu interés en contribuir. Este documento explica cómo funciona el proyecto
y cómo puedes ayudar.

---

## Por qué existe este proyecto

Open-SRI nació de una frustración real: implementar facturación electrónica con el SRI
en Ecuador es más difícil de lo que debería ser. La documentación está dispersa, muchas
referencias son inconsistentes o están desactualizadas, y las soluciones existentes suelen
depender de servicios de terceros con modelos de suscripción que representan una barrera
para desarrolladores independientes y empresas pequeñas.

No existe una librería oficial que abstraiga el flujo completo. Como resultado, cada
equipo termina reimplementando la misma lógica: generar la clave de acceso de 49 dígitos,
construir el XML con el esquema correcto, aplicar la firma XAdES-BES con un certificado P12,
y comunicarse con los webservices SOAP del SRI. Una semana de investigación y prueba que
podría resolverse en cinco minutos.

La meta es que lo que hoy tarda días, mañana tarde una tarde. El SDK cubre el flujo técnico;
el Playground interactivo permite entenderlo y diagnosticarlo sin instalar nada.

---

## Cómo puedes ayudar

No todo es código. El proyecto necesita ayuda en varios frentes:

**Código**
- Portar el SDK Java a C#, Go o Python siguiendo la misma Clean Architecture
- Agregar soporte para otros tipos de comprobante: notas de crédito, notas de débito, guías de remisión, retenciones
- Escribir tests de integración contra el entorno de pruebas del SRI

**Calidad**
- Reportar bugs con los webservices del SRI (comportamientos inesperados, mensajes de error poco claros)
- Testear el SDK en entornos de producción reales y reportar diferencias con el entorno de pruebas

**Documentación**
- Mejorar los ejemplos de uso
- Traducir documentación
- Documentar casos de error comunes y cómo resolverlos

---

## Proceso para contribuir

1. **Abre un issue primero** para discutir el cambio que quieres hacer, especialmente si es significativo.
   Esto evita trabajo duplicado y asegura que el cambio esté alineado con la dirección del proyecto.

2. **Haz fork del repositorio** y crea una rama descriptiva:
   ```bash
   git checkout -b feat/csharp-sdk-core
   git checkout -b fix/xades-signing-with-expired-cert
   ```

3. **Escribe tests** para lo que implementes. El umbral mínimo de cobertura en módulos core es 70%.

4. **Asegúrate de que el build pase** antes de abrir el PR:
   ```bash
   # SDK Java
   cd sdk/sri-sdk-java && ./gradlew spotlessCheck test

   # Backend
   cd playground-service && ./gradlew test
   ```

5. **Abre el PR** con una descripción clara de qué cambia y por qué.

---

## Estándares de código

### SDK Java
- Formato: Google Java Format (aplicado automáticamente con `./gradlew spotlessApply`)
- Arquitectura: Clean Architecture — domain → application → infrastructure → api
- Sin lógica de negocio en la capa de infraestructura
- Licencia en cabecera: `SPDX-License-Identifier: Apache-2.0`

### Backend (Spring Boot)
- Mismo formato Spotless
- Reactive first: usar `Mono`/`Flux`, no bloquear hilos
- Los endpoints deben tener tests de integración con `WebTestClient`

### Nuevos SDKs (C#, Go, etc.)
- Misma estructura de capas que el SDK Java
- Mismo contrato HTTP del sidecar: `POST /invoke`, `POST /validate`, `GET /health`
- Tests con los mismos XMLs de referencia del SDK Java

---

## Reportar bugs

Usa [GitHub Issues](https://github.com/nulldoomer/Open-SRI/issues). Incluye:

- Versión del SDK
- Código mínimo que reproduce el problema
- Error completo (stack trace si aplica)
- Versión del comprobante XSD que estás usando (v1.0, v1.1, v2.0, v2.1)

---

## Licencia

Al contribuir, aceptas que tu código se publique bajo la [licencia Apache 2.0](LICENSE).
