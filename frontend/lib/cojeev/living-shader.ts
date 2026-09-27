"use client";

import * as React from "react";
import { useMotionVisibility } from "@/lib/cojeev-motion/use-motion-visibility";

export type FieldTone = "balanced" | "warm" | "cool";
export type LivingShaderKind = "pigment" | "contour";
export type LivingShaderStatus = "pending" | "webgl" | "fallback" | "lost";
export type FieldMotionProps = {
  /** Relative pace, from 0 (still) to 3. */
  speed?: number;
  /** Color and relief strength, from 0 to 2. */
  intensity?: number;
  /** Accent selection from the inherited Cojeev theme. */
  tone?: FieldTone;
  /** Freeze the current artwork without removing it. */
  paused?: boolean;
};

function finite(value: number | undefined, fallback: number, min: number, max: number) {
  return typeof value === "number" && Number.isFinite(value) ? Math.min(max, Math.max(min, value)) : fallback;
}

export function normalizeShaderMotion(speed?: number, intensity?: number) {
  return { speed: finite(speed, 1, 0, 3), intensity: finite(intensity, 1, 0, 2) };
}

/** A large retina hero must not allocate an unbounded drawing buffer. */
export function shaderResolution(cssWidth: number, cssHeight: number, deviceRatio: number) {
  if (cssWidth <= 0 || cssHeight <= 0) return { width: 1, height: 1 };
  const width = finite(cssWidth, 1, 1, 1e7);
  const height = finite(cssHeight, 1, 1, 1e7);
  const ratio = finite(deviceRatio, 1, 1, 2);
  const scale = Math.min(ratio, 4096 / width, 4096 / height, Math.sqrt(2_000_000 / (width * height)));
  return { width: Math.max(1, Math.floor(width * scale)), height: Math.max(1, Math.floor(height * scale)) };
}

const vertexSource = `
attribute vec2 aPosition;
void main() { gl_Position = vec4(aPosition, 0.0, 1.0); }
`;

// Original Cojeev field functions: analytic color densities and topographic bands.
// No framebuffer simulation, DOM capture, texture assets, or third-party shader code.
const fragmentSource = `
precision mediump float;
uniform vec2 uSize;
uniform float uTime;
uniform float uIntensity;
uniform float uKind;
uniform vec3 uPaper;
uniform vec3 uA;
uniform vec3 uB;
uniform vec3 uC;

float mound(vec2 p, vec2 center, vec2 stretch) {
  vec2 d = (p - center) * stretch;
  return exp(-dot(d, d));
}

void main() {
  vec2 uv = gl_FragCoord.xy / uSize;
  float aspect = uSize.x / max(uSize.y, 1.0);
  vec2 p = vec2((uv.x - 0.5) * min(aspect, 2.5), uv.y - 0.5);
  float t = uTime * 0.16;
  vec2 drift = vec2(sin(t * 0.71), cos(t * 0.53)) * 0.12;
  vec2 folded = p + vec2(sin(p.y * 5.7 + t), cos(p.x * 4.1 - t * 0.8)) * 0.09;
  float a = mound(folded, vec2(-0.43, 0.16) + drift, vec2(1.8, 2.8));
  float b = mound(folded, vec2(0.40, -0.23) - drift * 0.8, vec2(2.1, 2.3));
  float c = mound(folded, vec2(0.05, 0.38) + drift.yx * 0.6, vec2(2.9, 1.9));
  vec3 color = uPaper;
  if (uKind < 0.5) {
    float wash = sin(folded.x * 10.0 + folded.y * 7.0 + t * 0.7) * 0.08;
    color = mix(color, uA, smoothstep(0.07, 0.94, a + wash) * 0.70);
    color = mix(color, uB, smoothstep(0.05, 0.90, b - wash) * 0.65);
    color = mix(color, uC, smoothstep(0.08, 0.94, c) * 0.40);
    float edge = exp(-abs(a - 0.42) * 35.0) * 0.035;
    color = mix(color, uA, edge);
  } else {
    float height = a * 1.25 + b * 0.92 + c * 0.55;
    height += sin(folded.x * 4.5 - folded.y * 3.0 + t * 0.3) * 0.13;
    float phase = height * 13.0;
    float distanceToLine = abs(fract(phase) - 0.5);
    float line = 1.0 - smoothstep(0.018, 0.07, distanceToLine);
    float broadLine = 1.0 - smoothstep(0.10, 0.18, distanceToLine);
    vec3 terrain = mix(uA, uB, smoothstep(0.2, 1.5, height));
    color = mix(uPaper, terrain, 0.10 + height * 0.07);
    color = mix(color, terrain, broadLine * 0.16 + line * 0.38);
    color = mix(color, uC, line * smoothstep(0.8, 1.8, height) * 0.24);
  }
  float fiber = sin(gl_FragCoord.x * 1.37 + gl_FragCoord.y * 2.17) * 0.003;
  color = mix(uPaper, color, min(uIntensity, 1.0));
  color = mix(color, color * 0.94, max(uIntensity - 1.0, 0.0));
  gl_FragColor = vec4(clamp(color + fiber * min(uIntensity, 1.0), 0.0, 1.0), 1.0);
}
`;

type ShaderOptions = ReturnType<typeof normalizeShaderMotion> & { tone: FieldTone; visible: boolean; moving: boolean };
export type LivingShader = { update: (options: ShaderOptions) => void; dispose: () => void };

/** Owns one GPU program and its complete context, resize, theme and frame lifecycle. */
export function createLivingShader(
  canvas: HTMLCanvasElement,
  host: HTMLElement,
  kind: LivingShaderKind,
  onStatus: (status: LivingShaderStatus) => void,
): LivingShader {
  let gl: WebGLRenderingContext | null = null;
  let program: WebGLProgram | null = null;
  let buffer: WebGLBuffer | null = null;
  const shaders: WebGLShader[] = [];
  let locations: Record<string, WebGLUniformLocation | null> = {};
  let disposed = false, lost = false, failed = false, frame = 0, previous = 0, elapsed = 0;
  let options: ShaderOptions = { speed: 1, intensity: 1, tone: "balanced", visible: false, moving: false };
  let themeDirty = true, sizeDirty = true, announced = false;
  const colors = document.createElement("canvas");
  colors.width = colors.height = 1;
  const colorContext = colors.getContext("2d", { willReadFrequently: true });

  function cancel() {
    if (frame) cancelAnimationFrame(frame);
    frame = 0;
    previous = 0;
  }
  function release() {
    if (gl && !lost) {
      if (buffer) gl.deleteBuffer(buffer);
      if (program) gl.deleteProgram(program);
      for (const shader of shaders) gl.deleteShader(shader);
    }
    buffer = null; program = null; shaders.length = 0; locations = {};
  }
  function unavailable() {
    cancel(); release(); failed = true; onStatus("fallback");
  }
  function compile(type: number, source: string) {
    const shader = gl!.createShader(type);
    if (!shader) throw new Error("Shader allocation failed");
    shaders.push(shader);
    gl!.shaderSource(shader, source); gl!.compileShader(shader);
    if (!gl!.getShaderParameter(shader, gl!.COMPILE_STATUS)) throw new Error("Shader compilation failed");
    return shader;
  }
  function initialize() {
    if (disposed || lost || failed || program) return;
    try {
      gl = canvas.getContext("webgl", { alpha: false, antialias: false, depth: false, stencil: false, powerPreference: "low-power" });
      if (!gl || gl.isContextLost()) throw new Error("WebGL unavailable");
      program = gl.createProgram();
      if (!program) throw new Error("Program allocation failed");
      gl.attachShader(program, compile(gl.VERTEX_SHADER, vertexSource));
      gl.attachShader(program, compile(gl.FRAGMENT_SHADER, fragmentSource));
      gl.linkProgram(program);
      if (!gl.getProgramParameter(program, gl.LINK_STATUS)) throw new Error("Program link failed");
      buffer = gl.createBuffer();
      if (!buffer) throw new Error("Buffer allocation failed");
      gl.bindBuffer(gl.ARRAY_BUFFER, buffer);
      gl.bufferData(gl.ARRAY_BUFFER, new Float32Array([-1, -1, 3, -1, -1, 3]), gl.STATIC_DRAW);
      gl.useProgram(program);
      const position = gl.getAttribLocation(program, "aPosition");
      gl.enableVertexAttribArray(position);
      gl.vertexAttribPointer(position, 2, gl.FLOAT, false, 0, 0);
      for (const name of ["uSize", "uTime", "uIntensity", "uKind", "uPaper", "uA", "uB", "uC"]) locations[name] = gl.getUniformLocation(program, name);
      themeDirty = sizeDirty = true;
      announced = false;
    } catch { unavailable(); }
  }
  function color(value: string, fallback: string) {
    if (!colorContext) return [0.8, 0.8, 0.8];
    colorContext.clearRect(0, 0, 1, 1);
    colorContext.fillStyle = fallback;
    colorContext.fillStyle = value || fallback;
    colorContext.fillRect(0, 0, 1, 1);
    const pixel = colorContext.getImageData(0, 0, 1, 1).data;
    return [pixel[0] / 255, pixel[1] / 255, pixel[2] / 255];
  }
  function repaintTheme() {
    const css = getComputedStyle(host);
    const accents = options.tone === "warm" ? ["pink", "yellow", "olive"] : options.tone === "cool" ? ["blue", "olive", "pink"] : ["pink", "blue", "yellow"];
    const tokens = ["--v-paper", ...accents.map(name => `--v-${name}`)];
    const defaults = ["#fbf4e6", "#f5b8db", "#b6caeb", "#f5d867"];
    ["uPaper", "uA", "uB", "uC"].forEach((name, index) => gl!.uniform3fv(locations[name], color(css.getPropertyValue(tokens[index]).trim(), defaults[index])));
    themeDirty = false;
  }
  function canDraw() { return !disposed && !lost && !failed && options.visible && !document.hidden; }
  function request() { if (canDraw() && !frame) frame = requestAnimationFrame(draw); }
  function draw(time: number) {
    frame = 0;
    if (!canDraw()) { previous = 0; return; }
    initialize();
    if (!gl || !program || failed) return;
    try {
      if (sizeDirty) {
        const { width, height } = shaderResolution(host.clientWidth, host.clientHeight, window.devicePixelRatio);
        if (canvas.width !== width || canvas.height !== height) { canvas.width = width; canvas.height = height; }
        gl.viewport(0, 0, width, height);
        gl.uniform2f(locations.uSize, width, height);
        sizeDirty = false;
      }
      if (themeDirty) repaintTheme();
      const delta = previous ? Math.min(Math.max(0, (time - previous) / 1000), 0.05) : 0;
      if (options.moving) elapsed += delta * options.speed;
      previous = time;
      gl.uniform1f(locations.uTime, elapsed);
      gl.uniform1f(locations.uIntensity, options.intensity);
      gl.uniform1f(locations.uKind, kind === "pigment" ? 0 : 1);
      gl.drawArrays(gl.TRIANGLES, 0, 3);
      if (!announced) { announced = true; onStatus("webgl"); }
      if (options.moving && options.speed > 0 && options.intensity > 0) request();
      else previous = 0;
    } catch { unavailable(); }
  }
  function resize() { sizeDirty = true; request(); }
  function appearance() { themeDirty = true; request(); }
  function visibility() { cancel(); if (!document.hidden) request(); }
  function onLost(event: Event) {
    event.preventDefault(); lost = true; cancel(); release(); onStatus("lost");
  }
  function onRestored() {
    if (disposed) return;
    lost = false; failed = false; gl = null; themeDirty = sizeDirty = true; request();
  }
  canvas.addEventListener("webglcontextlost", onLost);
  canvas.addEventListener("webglcontextrestored", onRestored);
  document.addEventListener("visibilitychange", visibility);
  window.addEventListener("resize", resize, { passive: true });
  window.addEventListener("cojeev:appearancechange", appearance);
  const resizeObserver = new ResizeObserver(resize); resizeObserver.observe(host);
  // Theme scopes can be nested; observe the actual ancestry, not only :root.
  const themeObserver = new MutationObserver(appearance);
  let ancestor: HTMLElement | null = host;
  while (ancestor) { themeObserver.observe(ancestor, { attributes: true, attributeFilter: ["style", "class", "data-mode", "data-theme"] }); ancestor = ancestor.parentElement; }

  return {
    update(next) {
      if (disposed) return;
      const motion = normalizeShaderMotion(next.speed, next.intensity);
      themeDirty ||= options.tone !== next.tone;
      const changed = options.speed !== motion.speed || options.intensity !== motion.intensity || options.tone !== next.tone || options.visible !== next.visible || options.moving !== next.moving;
      options = { ...next, ...motion };
      if (!options.visible || !options.moving) cancel();
      if (changed || themeDirty || sizeDirty) request();
    },
    dispose() {
      if (disposed) return;
      disposed = true; cancel();
      canvas.removeEventListener("webglcontextlost", onLost);
      canvas.removeEventListener("webglcontextrestored", onRestored);
      document.removeEventListener("visibilitychange", visibility);
      window.removeEventListener("resize", resize);
      window.removeEventListener("cojeev:appearancechange", appearance);
      resizeObserver.disconnect(); themeObserver.disconnect(); release();
      // Releasing GPU objects is enough; forcing loss breaks React Strict Mode's
      // immediate remount on the same canvas. The browser retires detached contexts.
      gl = null;
    },
  };
}

export function useLivingShader(kind: LivingShaderKind, props: FieldMotionProps) {
  const host = React.useRef<HTMLDivElement>(null);
  const canvas = React.useRef<HTMLCanvasElement>(null);
  const renderer = React.useRef<LivingShader | null>(null);
  const [status, setStatus] = React.useState<LivingShaderStatus>("pending");
  const { enabled, inView } = useMotionVisibility(host);
  const { speed, intensity } = normalizeShaderMotion(props.speed, props.intensity);
  const tone = props.tone === "cool" || props.tone === "warm" ? props.tone : "balanced";
  const moving = enabled && !props.paused && speed > 0 && intensity > 0;

  React.useEffect(() => {
    if (!canvas.current || !host.current) return;
    renderer.current = createLivingShader(canvas.current, host.current, kind, next => setStatus(previous => previous === next ? previous : next));
    return () => { renderer.current?.dispose(); renderer.current = null; };
  }, [kind]);
  React.useEffect(() => { renderer.current?.update({ speed, intensity, tone, visible: inView, moving }); }, [speed, intensity, tone, inView, moving]);
  return { host, canvas, status, tone, intensity, moving: status === "webgl" && moving && inView };
}
