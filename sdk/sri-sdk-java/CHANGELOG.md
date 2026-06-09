# CHANGELOG

---
Todos los cambios notables en la librería específica de JAVA serán documentados
en este archivo.

---
## [1.0.0] - 2026-05-20

### Añadido

- MVP del SDK del SRI para la emisión y autorización de facturas.
- Documentación JavaDoc en español en las capas específicas referenciadas a las tablas o documentación del SRI.
- MVP actual mantiene soporte para la emisión de facturas (v1.0.0).
- Firma electrónica XAdES-BES.
- Clientes SOAP para servicios del SRI (Recepción y Autorización).

## [1.0.1] - 2026-05-20

### Arreglado

- Fix del JavaDoc para un clean build de java usando gradlew.

## [1.0.2] - 2026-05-20

### Arreglado

- Orden de creación de los recursos para los test solucionado.


## [1.1.0] - 2026-06-09

### Añadido

- Implementación de todas las versiones de facturas para la creación y serialización 1.0.0, 1.1.0, 2.0.0 y 2.1.0.
- Actualización de fluid builder de Invoice para los nuevos campos de las versiones variantes.
- Actualización del dominio de Invoice.
- Actualización de los tests unitarios, de integración y test E2E.
