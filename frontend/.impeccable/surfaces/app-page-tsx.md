---
version: 1
slug: "app-page-tsx"
primary_target: "app/page.tsx"
related_targets: []
---

# Landing (/)

Scope: la landing pública. Modo: **Persuade**. El mundo (000h paleta Graphite + firma Evangelion) se extiende al resto de rutas: Docs y SDK en modo Read, Playground en modo Operate.

Audiencia y acción: un dev ecuatoriano que evalúa si el SDK le ahorra la integración con el SRI. Hay dos acciones del mismo peso: **Probar el Playground** (ver el pipeline real contra SRI pruebas) y **Quick Start** (instalar desde Maven). Qué se evita: que se vea tierno o pastel, y que se pierda la actitud NULL-PRIVATIZATION / Evangelion.

## Direction contract

THESIS: Cada factura es un envío y la clave de acceso de 49 dígitos es su número de tracking. Se rechaza el arreglo típico de SDK (titular + código a la derecha + grilla de 3 tarjetas).

OWN-WORLD: Base 000h en paleta Graphite (canvas #F4F5F6, papel #FFFFFF, inset #E5E8EB; oscuro #111418, #20252C, #2C333D), tinta #111418 y un solo acento de señal #E5482E para lo "impreso", el estado actual y la marca. Los pasteles de Graphite solo aparecen como semántica de estado (salvia = autorizado, arena = en curso). Piezas: guía de envío en papel con borde perforado; campos con etiqueta mono (Geist Mono) y un marcador ■ dibujado; código de barras derivado de la clave; sellos RECIBIDA / AUTORIZADO; botones pill de 000h; titulares Bricolage Grotesque en mayúsculas; texto DM Sans. Los ■■■ viven en la barra de estado y en las etiquetas de datos, nunca como eyebrow encima de un titular.

STORY: El visitante entiende que el SDK hace el envío completo (factura → clave → XML → firma → SOAP → respuesta). Cree que es real porque ve la guía, la clave válida con su anatomía y el tracking. Termina probando el Playground o yendo al Quick Start.

FIRST VIEWPORT: Arriba, la barra de estado ■■■ a todo el ancho. En la columna izquierda (unas 6/12): el titular FACTURACIÓN ELECTRÓNICA ACCESIBLE a escala display, con ACCESIBLE en el acento; debajo, la bajada y dos CTAs pill del mismo tamaño (Playground relleno, Quick Start contorno) más el enlace a GitHub. En la derecha (unas 5/12): la guía de envío viva (remitente, destinatario SRI pruebas, contenido y la clave de 49 dígitos con barras y los 9 campos etiquetados), con el sello RECIBIDA. Al pie del viewport, a todo el ancho, la ruta de tracking: una línea con 6 paradas presentes como fantasmas.

FORM: Guía de envío SOAP (courier + sobre SOAP + sello de recepción), candidata 6 de 7 de la lista propia (RIDE, anatomía de la clave, ventanilla y sello, ficha técnica, talonario autorizado, guía SOAP, notaría). Seed key 4cb913b4. Aportes de las demás: estados fantasma impresos sin tween (cathode); una sola línea que marca el "ahora" (drum); cada campo etiquetado con puntero fino (busytown); estados honestos (provenance); pasos numerados en círculo en Quick Start y Docs (sewing). Interacción firma: al cargar, las paradas de la ruta y los campos de la clave se imprimen en secuencia con cambio seco de estado; con `prefers-reduced-motion`, todo aparece ya impreso.

FINISH: unreviewed and undocumented is unfinished; this build ends with the finish review, the verdict, DESIGN.md, and every shipping raster carrying its provenance
