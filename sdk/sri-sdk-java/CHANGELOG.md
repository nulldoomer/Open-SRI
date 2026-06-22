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
- Actualización de fluent builder de Invoice para los nuevos campos de las versiones variantes.
- Actualización del dominio de Invoice.
- Actualización de los tests unitarios, de integración y test E2E.


## [1.2.0] - 2026-06-21

### Añadido

- Implementación de todos los documentos electrónicos para la creación y serialización de todas sus versiones.
- Creación de fluent builder para cada tipo de documento.
- Creación del dominio de cada documento.
- Implementación de modelos para la serialización XML de cada documento.
- Desacoplamiento del `SendoDocumentUseCase` a una abstracción genérica para cualquier tipo de documento electrónico.
- Implementación de `ElectronicDocument`, abstracción genérica de atributos generales en todos los documentos 
  electrónicos en la capa de dominio.
- Creación de Test Unitarios de validación con los documentos XSD de cada tipo de comprobante.

## [1.2.1] - 2026-06-21

### Arreglado

- Actualizada la version del build en gradle.

## [1.2.2] - 2026-06-22

### Arreglado

- Cambio del nombre del package para consistencia con el namespace de maven central.
- Configuración del `build.gradle.kts` para la publicación de la librería en maven central.
