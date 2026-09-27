# Product

<!-- impeccable:product-schema 1 -->

## Platform

web

## Users

- **Desarrolladores que integran facturación electrónica del SRI** (Ecuador): freelancers, startups y pymes que necesitan emitir comprobantes desde su propio sistema. Llegan a evaluar si el SDK les ahorra construir clave de acceso, XML, firma y SOAP por su cuenta, y vuelven a la documentación mientras integran.
- **Contribuidores**: desarrolladores que quieren portar el SDK a C#, Go o Python, mejorar la documentación, agregar tipos de comprobante o reportar fallas de los webservices del SRI.

## Product Purpose

OpenSRI es un SDK open source multi-lenguaje, más un playground interactivo, para la facturación electrónica del SRI. El SDK encapsula en una sola llamada (`client.sendInvoice(invoice)`) el flujo completo: clave de acceso de 49 dígitos (módulo 11), serialización XML validada contra los XSD oficiales, firma XAdES-BES con certificado PKCS#12 y envío SOAP (`RecepcionComprobantes` + `AutorizacionComprobantes`).

El éxito se mide así: un desarrollador envía su primera factura sin descifrar los internos del SRI y sin pagar un proveedor de suscripción, y la comunidad contribuye nuevos SDKs.

## Positioning

Es una alternativa abierta (Apache 2.0) a los servicios de terceros por suscripción, con una postura explícita contra la privatización injustificada de este conocimiento. El pipeline es observable: el Playground ejecuta el flujo real contra el **ambiente de pruebas del SRI** y muestra cada paso, las trazas del SDK, el XML autorizado y la respuesta RECIBIDA/DEVUELTA en tiempo real.

## Operating Context

- Rutas del sitio: Landing (`/`), Docs (`/doc`, en MDX, con Quick Start, API y guías de clave de acceso, XAdES-BES y SOAP), Playground (`/playground`), SDK Explorer (`/sdk`) y About (`/about`).
- El Playground habla con `playground-service` (Spring Boot) mediante `app/api/sessions` y eventos SSE. Las respuestas vienen del ambiente de pruebas real del SRI.
- La audiencia trabaja con Java/Maven/Gradle, certificados P12 del BCE, RUC y las fichas técnicas del SRI.

## Capabilities and Constraints

- SDK Java estable (`io.github.nulldoomer:opensri` 1.2.4 en Maven Central). C# en desarrollo; Go y Python planificados (confirmado por el autor).
- Stack del frontend: Next.js 16 (App Router), React 19, Tailwind v4, componentes shadcn y MDX para la documentación.
- Idioma: **todo el sitio en español** (`lang="es"`). Los términos técnicos (SDK, XML, SOAP, XAdES-BES) se mantienen.
- Pendiente de decidir: la versión mínima de Java que se comunica (Docs dice 17+, el README muestra 21).

## Brand Commitments

- Nombre: **OpenSRI**. El repositorio es `nulldoomer/Open-SRI`.
- Voz: directa, técnica, en español, con postura comunitaria ("NULL-PRIVATIZATION").
- **Sistema de diseño fijado por el usuario:** 000h de Cojeev (https://000h.cojeev.com), adoptado completo vía el registry shadcn `@cojeev`. Licencia MIT; hay que conservar el aviso de atribución.
- **Firma visual fijada por el usuario:** el lenguaje "Evangelion" (labels mono con `■■■`, barra de estado del sistema, panel de análisis de riesgos, titulares en mayúsculas) se mantiene como firma de la marca.
- Debe verse profesional: es un proyecto open source abierto a la comunidad.

## Evidence on Hand

- Publicación en Maven Central (enlace en el README; captura en `../docs/maven-central.png`).
- Demo del Playground: `../docs/demo.gif`.
- Licencia Apache 2.0 (`../LICENSE`); guía de contribución (`../CONTRIBUTING.md`); Ko-fi `nulldoomer`.
- No se publica un porcentaje de cobertura de pruebas: no hay reporte que lo respalde (decisión del 2026-09-27).
- **No existen**: testimonios, lista de empresas usuarias, benchmarks ni métricas de adopción. No se deben inventar.

## Product Principles

1. **El flujo real primero.** Mostrar el pipeline verdadero (pasos, trazas, XML, respuesta del SRI) pesa más que prometer.
2. **Una llamada, cero misterio.** Cada superficie debe acercar al desarrollador a su primera factura enviada.
3. **Abierto por defecto.** Contribuir, leer el código y usar comercialmente deben estar siempre a un clic.
4. **Honestidad técnica.** Mostrar sin maquillaje los estados reales (estable, en desarrollo, planificado) y los errores del SRI.
