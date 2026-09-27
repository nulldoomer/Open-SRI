# Diagramas de Open-SRI

HTML autocontenidos generados con [archify](https://github.com/tt-a1i/archify) a partir de los `.json` de esta carpeta.
Ábrelos en el navegador (tema claro/oscuro, zoom, vistas guiadas y exportación a PNG/SVG incluidos).

| # | Diagrama | Tipo | Sección |
|---|---|---|---|
| 01 | [Vista general del sistema](01-vista-general.html) | arquitectura | General |
| 02 | [Ejecución de una sesión del playground](02-flujo-playground.html) | secuencia | General |
| 03 | [SDK · Arquitectura hexagonal](03-sdk-arquitectura.html) | arquitectura | SDK |
| 04 | [SDK · Pipeline de envío de un comprobante](04-sdk-pipeline.html) | flujo de datos | SDK |
| 05 | [playground-service · Arquitectura interna](05-playground-arquitectura.html) | arquitectura | Playground |
| 06 | [playground-service · Ciclo de vida de una sesión](06-playground-sesion.html) | ciclo de vida | Playground |
| 07 | [Frontend · Arquitectura Next.js](07-frontend-arquitectura.html) | arquitectura | Frontend |
| 08 | [Frontend · Flujo de usePlaygroundSession](08-frontend-hook-playground.html) | workflow | Frontend |

Para regenerar uno después de editar su `.json`:

```bash
node <archify>/bin/archify.mjs deliver <tipo> 01-vista-general.architecture.json 01-vista-general.html --quality showcase
```
