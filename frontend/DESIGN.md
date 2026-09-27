---
name: OpenSRI
description: SDK open source de facturación electrónica del SRI, presentado como una guía de envío SOAP.
colors:
  canvas: "#F4F5F6"
  paper: "#FFFFFF"
  inset: "#E5E8EB"
  inset-quiet: "#eceef0"
  ink: "#24262b"
  text-secondary: "#454850"
  text-tertiary: "#51555d"
  border: "#aeb0b3"
  edge: "#6b6d70"
  signal: "#E5482E"
  signal-ink: "#B8321C"
  signal-soft: "#fbe6e1"
  structure: "#14171B"
  structure-text: "#E2E6ED"
  structure-line: "#565D68"
  structure-quiet: "#242931"
  on-structure: "#F9FAFC"
  status-ok-bg: "#f5f7f3"
  status-ok-ink: "#575e50"
  status-live: "#a1ae90"
  status-warn-bg: "#fcf9f2"
  status-warn-ink: "#655d47"
  status-info-bg: "#f4f7fa"
  status-info-ink: "#535d6c"
  status-danger-bg: "#fae7e6"
  status-danger-ink: "#96342d"
  danger-fill: "#B92D28"
  canvas-dark: "#111418"
  paper-dark: "#20252C"
  inset-dark: "#2C333D"
  ink-dark: "#fafafa"
  text-secondary-dark: "#c2c6ca"
  border-dark: "#4e535c"
  signal-dark: "#F06A4F"
  signal-ink-dark: "#FF7A5C"
  signal-soft-dark: "#3a2723"
  status-danger-ink-dark: "#e99992"
typography:
  display:
    fontFamily: "Bricolage Grotesque, ui-sans-serif, system-ui, sans-serif"
    fontSize: "clamp(3.1rem, 8vw, 6rem)"
    fontWeight: 800
    lineHeight: 0.86
    letterSpacing: "-0.02em"
    fontVariation: "'wdth' 78"
  headline:
    fontFamily: "Bricolage Grotesque, ui-sans-serif, system-ui, sans-serif"
    fontSize: "clamp(2.4rem, 5vw, 3.75rem)"
    fontWeight: 800
    lineHeight: 0.9
    letterSpacing: "-0.015em"
    fontVariation: "'wdth' 80"
  title:
    fontFamily: "Bricolage Grotesque, ui-sans-serif, system-ui, sans-serif"
    fontSize: "1.5rem"
    fontWeight: 700
    lineHeight: 1.2
    letterSpacing: "-0.025em"
  lead:
    fontFamily: "DM Sans, ui-sans-serif, system-ui, sans-serif"
    fontSize: "1.125rem"
    fontWeight: 400
    lineHeight: 1.625
  body:
    fontFamily: "DM Sans, ui-sans-serif, system-ui, sans-serif"
    fontSize: "15px"
    fontWeight: 400
    lineHeight: "28px"
  control:
    fontFamily: "DM Sans, ui-sans-serif, system-ui, sans-serif"
    fontSize: "14px"
    fontWeight: 500
    lineHeight: 1
  label:
    fontFamily: "Geist Mono, ui-monospace, monospace"
    fontSize: "10.5px"
    fontWeight: 400
    lineHeight: 1.2
    letterSpacing: "0.16em"
  status-bar:
    fontFamily: "Geist Mono, ui-monospace, monospace"
    fontSize: "11px"
    fontWeight: 400
    lineHeight: 1.2
    letterSpacing: "0.18em"
  data:
    fontFamily: "Geist Mono, ui-monospace, monospace"
    fontSize: "13px"
    fontWeight: 400
    lineHeight: 1
    fontFeature: "'tnum'"
rounded:
  code: "6px"
  card-sm: "16px"
  card: "20px"
  pill: "999px"
spacing:
  s-1: "4px"
  s-2: "8px"
  s-3: "12px"
  s-4: "16px"
  s-5: "20px"
  s-6: "24px"
  s-8: "32px"
  s-10: "40px"
  s-12: "48px"
  s-16: "64px"
  section: "96px"
components:
  button-primary:
    backgroundColor: "{colors.ink}"
    textColor: "{colors.canvas}"
    typography: "{typography.control}"
    rounded: "{rounded.pill}"
    padding: "0 20px"
    height: "40px"
  button-primary-lg:
    backgroundColor: "{colors.ink}"
    textColor: "{colors.canvas}"
    rounded: "{rounded.pill}"
    padding: "0 24px"
    height: "48px"
    width: "13rem"
  button-outline:
    backgroundColor: "transparent"
    textColor: "{colors.ink}"
    rounded: "{rounded.pill}"
    padding: "0 24px"
    height: "48px"
  status-chip-ok:
    backgroundColor: "{colors.status-ok-bg}"
    textColor: "{colors.status-ok-ink}"
    rounded: "{rounded.pill}"
    padding: "6px 12px"
  code-frame:
    backgroundColor: "{colors.structure}"
    textColor: "{colors.structure-text}"
    rounded: "{rounded.card-sm}"
  waybill:
    backgroundColor: "{colors.paper}"
    textColor: "{colors.ink}"
    rounded: "{rounded.card}"
  step-circle:
    backgroundColor: "transparent"
    textColor: "{colors.ink}"
    rounded: "{rounded.pill}"
    size: "32px"
  inline-code:
    backgroundColor: "{colors.inset}"
    textColor: "{colors.ink}"
    rounded: "{rounded.code}"
    padding: "2px 6px"
---

# Design System: OpenSRI

## Overview

**Creative North Star: "Guía de envío SOAP"**

Cada factura es un envío y la clave de acceso de 49 dígitos es su número de tracking. El sitio se lee como papelería de courier técnica: una guía en papel con borde perforado, campos etiquetados en mono, un código de barras derivado de la clave, sellos de recepción y una ruta de tracking cuyas paradas se imprimen. La base es 000h de Cojeev, adoptado completo (componentes vendorizados en `components/ui`, `styles/cojeev`, `lib/cojeev`), en su paleta "graphite" horneada como tokens estáticos en `app/globals.css`.

La firma Evangelion se conserva como detalle, no como escenografía: la barra de estado `■■■` a todo el ancho, las etiquetas de datos en mono con un `■` dibujado, los titulares en mayúsculas condensadas y el panel de "Análisis de riesgos". Un solo acento de señal rojo-naranja marca lo impreso, el estado actual y la marca; todo lo demás es grafito, papel y tinta.

La densidad cambia por modo. **Persuade** (landing) respira con secciones de 96px y titulares display. **Read** (Docs, SDK, About) es una columna de lectura de 72ch con pasos numerados en círculo. **Operate** (Playground) es formulario más panel de seguimiento pegajoso. Nunca tierno ni pastel: los tonos pastel de graphite existen solo como semántica de estado.

**Key Characteristics:**
- 000h completo: botones pill, tabs pill, inputs y badges de Cojeev sin reestilizar salvo el acento.
- Un solo acento (`--signal`) que reemplaza al `--v-pink` de Cojeev en todos los componentes.
- Titulares Bricolage Grotesque 800, en mayúsculas y condensados (`font-stretch` 78–82%).
- Datos y etiquetas en Geist Mono con cifras tabulares; prosa en DM Sans.
- Estados fantasma que se imprimen sin interpolación; sello RECIBIDA que se presiona.
- Claro y oscuro con paridad completa vía `data-mode` en `<html>`.

## Colors

Grafito frío y papel blanco, tinta casi negra y una sola señal rojo-naranja; los pasteles de graphite solo hablan de estado.

### Primary
- **Rojo Señal** (`signal`; oscuro `signal-dark`): lo impreso y lo actual. Relleno del marcador `■`, la palabra acentuada de un titular ("ACCESIBLE", "TODO"), el "SRI" del logotipo, la última parada de la ruta, el subrayado en hover de los enlaces, el anillo de foco (`--ring`) y el borde del sello. Mapeado también a `--v-pink`, `--v-brand` y `--sel-edge`, así que cada componente Cojeev que marcaba selección en rosa ahora marca en señal.
- **Tinta Señal** (`signal-ink`; oscuro `signal-ink-dark`): la señal cuando es texto pequeño: texto del sello, detalle de la parada final, estado "En curso" del pipeline.
- **Señal Tenue** (`signal-soft`; oscuro `signal-soft-dark`): fondo de selección de texto (`::selection`) y relleno del nodo en curso del pipeline.

### Neutral
- **Lienzo Grafito** (`canvas`; oscuro `canvas-dark`): fondo de página (`--background`).
- **Papel** (`paper`; oscuro `paper-dark`): tarjetas, la guía de envío, el panel del Playground (`--card`).
- **Inset** (`inset`; oscuro `inset-dark`): código en línea, grupos segmentados, `--secondary`/`--accent`/`--input`.
- **Inset Quieto** (`inset-quiet`): banda del Análisis de riesgos y `--muted` (asides, blockquotes, cabecera de tablas).
- **Tinta** (`ink`; oscuro `ink-dark`): texto principal y relleno del botón primario (`--primary`).
- **Texto Secundario / Terciario** (`text-secondary`, `text-tertiary`): bajadas, etiquetas mono, texto de apoyo (`--muted-foreground`).
- **Filete** (`border`; oscuro `border-dark`) y **Canto** (`edge`): divisores sólidos y punteados; `edge` dibuja los bordes fantasma y las perforaciones.
- **Estructura** (`structure`, igual en ambos modos) con `structure-text`, `structure-line`, `structure-quiet` y `on-structure`: superficie oscura del código (CodeFrame) y la banda de cierre de la landing.

### Estado (semántica de graphite)
- **Salvia** (`status-ok-bg` / `status-ok-ink`, punto vivo `status-live`): autorizado, estable, "SRI pruebas" en línea.
- **Arena** (`status-warn-bg` / `status-warn-ink`): en desarrollo, advertencias.
- **Azul niebla** (`status-info-bg` / `status-info-ink`): callouts de nota.
- **Peligro** (`status-danger-bg` / `status-danger-ink`, relleno `danger-fill`): errores del SRI, valores negativos, nodo de paso fallido.

### Named Rules
**The One Signal Rule.** `--signal` se reserva para marcas impresas (`■`, sello, nodo final), el estado actual (ruta activa, paso en curso, foco) y la marca (logotipo, palabra acentuada del titular). Nunca rellena superficies ni botones.

**The Negative Is Not Signal Rule.** Un valor negativo o de riesgo ("Semanas / meses", "Crítico") usa `--status-danger-ink`, no `--signal`. El rojo de señal significa "impreso/ahora", no "malo".

**The Pastel Is Status Rule.** Salvia, arena y azul niebla aparecen solo como semántica de estado, nunca como decoración ni fondo de sección.

## Typography

**Display Font:** Bricolage Grotesque (con `ui-sans-serif, system-ui`), cargada con `next/font` con ejes `opsz` y `wdth` como `--font-display` / `--font-heading`.
**Body Font:** DM Sans (con `ui-sans-serif, system-ui`), `--font-text` / `--font-sans`.
**Label/Mono Font:** Geist Mono (`--font-mono`).

**Character:** Un grotesco condensado y pesado en mayúsculas da la voz de sistema; DM Sans deja la prosa tranquila; Geist Mono es la voz del documento: etiquetas, claves, números de guía, trazas.

### Hierarchy
- **Display** (800, `clamp(3.1rem,8vw,6rem)`, 0.86, stretch 78%, mayúsculas): solo el titular del hero.
- **Headline** (800, `clamp(2.4rem,5vw,3.75rem)`, 0.9, stretch 80%, mayúsculas): titulares de sección de la landing y H1 de Playground y SDK. El H1 de Docs baja a `clamp(2.25rem,4.5vw,3.25rem)`, 0.92, stretch 82%.
- **Title** (700, 24px, tracking ajustado, sin mayúsculas): H2 de docs y pasos numerados; H3 a 18px/600.
- **Lead** (400, 18px, 1.625): bajadas bajo un H1, máx. ~34rem en el hero.
- **Body** (400, 15px/28px, `--muted-foreground`): prosa de docs en columna de 72ch; énfasis en `--foreground` 600.
- **Label** (Geist Mono 10.5px, `0.16em`, mayúsculas): etiquetas de campo, cabeceras de tabla, títulos de callout. La barra de estado usa 11px/`0.18em`.
- **Data** (Geist Mono 13px, `tabular-nums`): dígitos de la clave, números de factura, versiones. Las subetiquetas de la anatomía de la clave bajan a 9.5px/`0.12em`.

### Named Rules
**The Mono Is Data Rule.** Todo lo que es un dato del documento (clave, RUC, serie, versión, endpoint, número de paso) va en Geist Mono con cifras tabulares; la prosa nunca.

**The Upper Case Is Voice Rule.** Los titulares Bricolage van en mayúsculas condensadas; los títulos de nivel inferior (H2 de docs, H3, nombres de SDK) van en caja normal.

## Layout

Contenedor único `max-w-7xl` (1280px) con márgenes `16px / 32px / 64px` (móvil / sm / lg). Ritmo base de 4px de Cojeev (`s-1`…`s-16`); secciones de landing a 96px verticales, banda de cierre a 80px, cabeceras de página a 40–64px.

- **Persuade (landing):** barra de estado `■■■` a todo el ancho; hero en grilla de 12 columnas con el titular en 6 y la guía de envío en 5 (columna 8 en adelante); la ruta de tracking a todo el ancho al pie, en 6 columnas desde `md` y vertical en móvil. Las secciones siguientes se alternan entre lienzo, banda `inset-quiet` con filetes arriba y abajo, y la banda de cierre en `structure`.
- **Read (Docs):** barra lateral de 14rem más columna de lectura `max-w-[72ch]`, separadas 64px; en móvil la barra se apila arriba. **SDK** y **About** usan una sola columna `max-w-4xl`.
- **Operate (Playground):** formulario flexible más panel de seguimiento de 24rem (28rem en `xl`), pegajoso a `top-24`; en móvil se apila y el envío hace scroll al seguimiento.

Navegación pegajosa con fondo translúcido y blur; en móvil los enlaces bajan a una segunda fila con scroll horizontal. Las tablas anchas se convierten en listas de definición bajo `sm`.

## Elevation & Depth

Plano por defecto. La profundidad se construye con tono (lienzo → papel → inset), anillos de 1px y filetes, no con sombras. Hay una sola sombra en todo el sistema, reservada al objeto físico del mundo: la guía de envío.

### Shadow Vocabulary
- **Papel levantado** (`box-shadow: 0 1px 0 var(--v-border), 0 24px 48px -28px rgb(20 23 27 / 0.45)`; en oscuro `0 1px 0 var(--v-border), 0 24px 48px -24px rgb(0 0 0 / 0.8)`): solo la guía de envío del hero.

### Named Rules
**The Paper Only Rule.** Solo un objeto de papel del mundo (la guía) proyecta sombra. Paneles, tarjetas y CodeFrame se separan con tono y un anillo de 1px (`ring-1 ring-border` o `ring-[var(--structure-line)]/60`).

**The No Nested Cards Rule.** Una superficie de papel no contiene otra tarjeta. Dentro de un panel se divide con filetes sólidos o punteados, nunca con cajas anidadas.

## Shapes

Radios de Cojeev: todo control es pill (999px); las superficies usan 16px (`card-sm`: CodeFrame, tablas, callouts, asides) o 20px (`card`: guía de envío, panel del Playground); el código en línea y el sello usan 6px. Los nodos de ruta y los pasos numerados son círculos perfectos (19px nodos, 32px pasos de docs, 28px pasos del formulario).

El filete punteado es la forma del "todavía no": divisiones internas de la guía, lista de próximos SDKs, bordes fantasma de nodos pendientes, placeholders de lenguajes sin SDK. El borde perforado de la guía (muescas circulares en los cantos y fila de agujeros radiales de 12px) es la única silueta recortada del sistema.

## Components

### Buttons
Los botones pill de 000h, sin reestilizar.
- **Shape:** pill (999px). Alturas 32 / 40 / 48px (`--ctl-sm/md/lg`), padding horizontal 16 / 20 / 24px.
- **Primary:** relleno de tinta (`--primary`) con texto de lienzo, 14px/500. En oscuro se mantiene tinta (blanco sobre grafito) por override en `globals.css`; Cojeev lo pintaría con el acento.
- **Hover / Active:** fondo `color-mix(in oklab, var(--primary) 88%, var(--v-canvas))`; se hunde 1px al presionar (`motion-safe`). Transición de 120ms (`--t-micro`), anulada con movimiento reducido.
- **Outline:** contorno sin relleno. Los dos CTAs de la landing (Playground relleno, Quick Start contorno) tienen el mismo ancho mínimo de 13rem.
- **Disabled:** cara `--v-disabled-face`, tinta `--v-disabled-ink`, anillo interno de 1px.

### Chips
- **Status chip:** pill con fondo `status-*-bg`, texto `status-*-ink` 14px y un punto de 6px en `currentColor` ("Estable", "Ya disponible").
- **Status label:** en listas y pipeline el estado va como etiqueta mono 10.5–11px en mayúsculas con color de estado, sin cápsula ("En desarrollo", "OK", "Error").
- **Segmented group:** grupo pill en `inset` con opciones pill de 32px en mono 12px (lenguaje del SDK, filtro de trazas).

### Cards / Containers
- **Corner Style:** 16px o 20px (ver Shapes).
- **Background:** papel (`--card`) sobre lienzo; `--muted` para asides; en oscuro las tablas de riesgo usan lienzo.
- **Shadow Strategy:** ninguna salvo la guía (ver Elevation).
- **Border:** anillo o borde de 1px `--border`.
- **Internal Padding:** 20–24px.

### Inputs / Fields
Inputs y selects de 000h vendorizados (`components/ui/input.tsx`, `select.tsx`), con `--input` en inset y foco en anillo de `--signal`.

### Navigation
Logotipo "OPEN**SRI**" en Bricolage 800 mayúsculas, "SRI" en señal. Enlaces pill de 36px en 14px, `--muted-foreground` con hover en `--muted`; la ruta activa pasa a `--foreground` 600 y lleva un `■` de señal delante. La barra lateral de docs repite la misma gramática.

### Mark (■ dibujado)
Cuadrado de 7px en `--signal` dibujado como caja (nunca un glifo de fuente), en grupos de 1 o 3 con 3px de separación. `■■■` abre la barra de estado del hero y el pie; `■` precede etiquetas de datos (campos de la guía, etiqueta de CodeFrame, cabecera de tabla), el nombre de la ruta activa y el título mono de una lista de datos.

### CodeFrame
Superficie `structure` de 16px de radio con anillo `structure-line`; tira superior de 40px en mono 12px con `■` + nombre de archivo y el botón COPIAR (pill mono 11px en mayúsculas). En la landing lleva un riel de anotaciones: corchetes de 2px en `structure-text` (en señal para lo que ocurre "dentro del SDK") alineados a las líneas de código.

### Guía de envío (Waybill)
Papel de 20px de radio con la única sombra del sistema: cabecera "GUÍA DE ENVÍO" en Bricolage condensado, campos en grilla separados por filetes punteados, código de barras Code 128 derivado de la clave, `ClaveAnatomy` y el sello RECIBIDA (borde de 2px en señal, texto `signal-ink` mono 14px/600 con `0.2em`, girado −6°).

### ClaveAnatomy
Los 49 dígitos partidos en sus 9 campos de la ficha técnica (Fecha, Tipo, RUC, Amb., Serie, Secuencial, Código, Emisión, DV): dígitos mono 13px tabulares sobre un filete fino y etiqueta mono 9.5px. Si el valor no son 49 dígitos se muestra crudo, nunca reformateado.

### Ruta de tracking / Pipeline
Nodos circulares de 19px unidos por un riel de 1px. Pendiente: borde punteado `edge` sin relleno (fantasma). Hecho: relleno y borde de tinta. Actual o final: señal. Error: `danger-fill`. Cada parada lleva número mono de dos dígitos, título 14px/600 y detalle mono 11px. La misma gramática sirve a la landing (horizontal) y al Playground (vertical).

### Pasos numerados en círculo
Número mono tabular dentro de un círculo con borde de tinta de 1px (32px en Quick Start, 28px en el formulario del Playground), junto al título del paso.

### Motion
- **Impresión (`.strike`, `.strike-node`):** el estado fantasma (opacidad 0.22, borde punteado sin relleno) se mantiene durante `--strike-delay` y cambia de golpe con `steps(1, end)`: una impresión, no un fundido. Paradas escalonadas cada 280ms desde 1350ms; campos de la clave cada 110ms desde 250ms.
- **Sello (`.stamp`):** de escala 1.8 y opacidad 0 a 1 en 220ms con `cubic-bezier(0.16, 1, 0.3, 1)`, tras 3100ms.
- **Movimiento reducido:** las tres animaciones se anulan y el estado de reposo ya es el impreso; los tiempos de Cojeev (`--t-*`) caen a 0ms.

## Do's and Don'ts

### Do:
- **Do** usar `--signal` solo para marcas impresas, estado actual y marca (≤ una palabra acentuada por titular).
- **Do** poner los `■■■` en la barra de estado y los `■` en etiquetas de datos, navegación activa y tiras de CodeFrame.
- **Do** escribir valores negativos o de riesgo en `--status-danger-ink`, mono 12px/600 en mayúsculas.
- **Do** mostrar lo pendiente como fantasma punteado y pasarlo a impreso con un cambio seco (`steps(1, end)`), con el estado impreso como reposo para movimiento reducido.
- **Do** mantener tinta el botón primario y la pestaña pill seleccionada también en modo oscuro.
- **Do** tras cualquier `shadcn add` de un ítem `@cojeev`, revisar `app/globals.css`: el comando reescribe el puente de tokens shadcn (restaurar `--accent`, `--ring`, `--muted-foreground-*`) y vuelve a añadir el import de `cojeev-fonts.css`, que rompe el build. Ver README, "Design system".
- **Do** conservar el aviso MIT de Cojeev (`lib/cojeev/NOTICES.txt` y la línea del pie).

### Don't:
- **Don't** poner `■■■` ni una etiqueta mono como eyebrow encima de un titular.
- **Don't** usar `--signal` para valores negativos, errores, fondos de sección ni rellenos de botón.
- **Don't** dejar que reaparezca el `--v-pink` de Cojeev; toda selección y acento pasa por `--signal`.
- **Don't** anidar tarjetas dentro de tarjetas.
- **Don't** añadir sombras fuera de la guía de envío.
- **Don't** usar los pasteles de graphite como decoración; se ve tierno y pastel, que es justo lo que el sistema rechaza.
- **Don't** dibujar el `■` con un glifo de fuente; usar el componente `Mark`.
- **Don't** interpolar la transición fantasma → impreso con un fundido.
