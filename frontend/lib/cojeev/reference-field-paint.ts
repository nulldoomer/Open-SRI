import { makeSwarm, stepSwarm, rippleOffset, type Point, type Ripple } from "@/lib/cojeev/reference-effect-geometry";

export type FieldKind = "swarm-cursor" | "ghost-cursor" | "click-spark" | "magic-rings" | "strands" | "meta-balls" | "elastic-mesh" | "ripple-distortion" | "image-trail";
type Trail = Point & { time: number; index: number };

/** Original bounded canvas effects. No global pointer interception or GPU prerequisite. */
export function createFieldPainter(kind: FieldKind, ctx: CanvasRenderingContext2D, count: number) {
  let width = 1, height = 1, time = 0, active = false;
  let pointer: Point = { x: .5, y: .5 }, lastTrail: Point = { x: -999, y: -999 };
  let waves: Ripple[] = [], trails: Trail[] = [], bursts: Ripple[] = [];
  const swarm = makeSwarm(count), mesh = Array.from({ length: 17 * 11 }, () => ({ x: 0, y: 0, vx: 0, vy: 0 }));
  const texture = typeof document === "undefined" ? null : document.createElement("canvas");
  let textureDirty = true;
  const dot = (x: number, y: number, r: number, color: string) => { ctx.fillStyle = color; ctx.beginPath(); ctx.arc(x, y, Math.max(.1, r), 0, Math.PI * 2); ctx.fill(); };
  const painter = {
    palette: ["#f5b8db", "#9aab63", "#b6caeb", "#f5d867"], ink: "#111111", tone: 0,
    images: [] as HTMLImageElement[],
    invalidate() { textureDirty = true; },
    resize(w: number, h: number) {
      const old = { x: width, y: height }; width = w; height = h;
      pointer = { x: width / 2, y: height / 2 };
      swarm.forEach(p => { p.x = old.x === 1 ? width / 2 + p.x : p.x / old.x * w; p.y = old.y === 1 ? height / 2 + p.y : p.y / old.y * h; });
      textureDirty = true;
    },
    move(p: Point) {
      pointer = p; active = true;
      if (kind === "ripple-distortion" && (!waves.length || time - waves.at(-1)!.time > .06)) waves = [...waves.slice(-11), { ...p, time }];
      if (kind === "image-trail" && Math.hypot(p.x - lastTrail.x, p.y - lastTrail.y) > 42) {
        trails = [...trails.slice(-9), { ...p, time, index: (trails.at(-1)?.index ?? -1) + 1 }]; lastTrail = p;
      }
    },
    leave() { active = false; lastTrail = { x: -999, y: -999 }; },
    click(p: Point) {
      pointer = p; active = true; bursts = [...bursts.slice(-7), { ...p, time }]; waves = [...waves.slice(-11), { ...p, time }];
      swarm.forEach((particle, i) => { particle.vx += Math.cos(i * 2.4) * 650; particle.vy += Math.sin(i * 2.4) * 650; });
      if (kind === "image-trail") { trails = [...trails.slice(-9), { ...p, time, index: (trails.at(-1)?.index ?? -1) + 1 }]; }
    },
    draw(delta: number, running: boolean) {
      if (running) time += delta / 1000;
      const color = painter.palette[painter.tone], palette = painter.palette;
      ctx.clearRect(0, 0, width, height);
      ctx.globalAlpha = 1; ctx.lineCap = "round"; ctx.lineJoin = "round";
      if (kind === "swarm-cursor") {
        if (running) stepSwarm(swarm, active ? pointer : { x: width / 2, y: height / 2 }, delta, time, Math.min(45, width * .13));
        for (let i = 0; i < swarm.length; i++) {
          const p = swarm[i]; ctx.strokeStyle = palette[i % 4]; ctx.lineWidth = 2;
          ctx.globalAlpha = .3; ctx.beginPath(); ctx.moveTo(p.x, p.y); ctx.lineTo(p.x - p.vx * .07, p.y - p.vy * .07); ctx.stroke();
          ctx.globalAlpha = .85; ctx.save(); ctx.translate(p.x, p.y); ctx.rotate(Math.atan2(p.vy, p.vx)); ctx.scale(1.4, .82); dot(0, 0, 4 + i % 3, palette[i % 4]); ctx.restore();
        }
      } else if (kind === "ghost-cursor") {
        if (running) stepSwarm(swarm, active ? pointer : { x: width / 2, y: height / 2 }, delta, time, 10);
        const head = swarm[0];
        if (running) trails = [...trails.slice(-27), { x: head.x, y: head.y, time, index: 0 }];
        for (let i = 0; i < trails.length; i++) { ctx.globalAlpha = i / trails.length * .18; dot(trails[i].x, trails[i].y, 8 + i / trails.length * 18, color); }
        ctx.globalAlpha = .75; ctx.strokeStyle = color; ctx.lineWidth = 1.5; ctx.beginPath(); ctx.ellipse(head.x, head.y, 18, 23, Math.sin(time) * .2, 0, Math.PI * 2); ctx.stroke();
      } else if (kind === "click-spark") {
        bursts = bursts.filter(burst => time - burst.time < .7);
        for (const burst of bursts) {
          const p = (time - burst.time) / .7, eased = 1 - (1 - p) ** 3;
          for (let i = 0; i < Math.min(count, 24); i++) {
            const angle = i / Math.min(count, 24) * Math.PI * 2, r = 8 + eased * 44;
            ctx.strokeStyle = palette[i % 4]; ctx.lineWidth = 3 * (1 - p); ctx.globalAlpha = 1 - p;
            ctx.beginPath(); ctx.moveTo(burst.x + Math.cos(angle) * r, burst.y + Math.sin(angle) * r); ctx.lineTo(burst.x + Math.cos(angle) * (r + 12 * (1 - p)), burst.y + Math.sin(angle) * (r + 12 * (1 - p))); ctx.stroke();
          }
        }
      } else if (kind === "magic-rings") {
        const cx = width / 2 + (active ? (pointer.x - width / 2) * .1 : 0), cy = height / 2 + (active ? (pointer.y - height / 2) * .1 : 0);
        for (let i = 0; i < Math.min(count, 10); i++) {
          const phase = (time * .18 + i / Math.min(count, 10)) % 1, r = 16 + phase * Math.min(width, height) * .43;
          ctx.globalAlpha = Math.sin(phase * Math.PI) * .9; ctx.strokeStyle = palette[i % 4]; ctx.lineWidth = 1.5 + (1 - phase) * 2;
          ctx.beginPath(); ctx.ellipse(cx, cy, r, r * (.72 + .08 * Math.sin(time + i)), -.3, .1 + time * .1, Math.PI * 2 - .4 + time * .1); ctx.stroke();
        }
      } else if (kind === "strands") {
        for (let i = 0; i < Math.min(count, 12); i++) {
          ctx.strokeStyle = palette[i % 4]; ctx.globalAlpha = .72; ctx.lineWidth = 1.5 + i % 3;
          ctx.beginPath();
          for (let x = 0; x <= width; x += 5) {
            const envelope = Math.sin(x / width * Math.PI), y = height / 2 + Math.sin(x / width * (6 + i * .25) + time * (.4 + i * .035) + i * .6) * height * .23 * envelope + (i - Math.min(count, 12) / 2) * 3;
            if (x === 0) ctx.moveTo(x, y); else ctx.lineTo(x, y);
          }
          ctx.stroke();
        }
      } else if (kind === "meta-balls") {
        // Marching squares over a small scalar field creates true liquid joins.
        const balls = Array.from({ length: Math.min(count, 10) }, (_, i) => ({ x: width / 2 + Math.sin(time * .4 + i * 2.2) * width * .24, y: height / 2 + Math.cos(time * .47 + i * 1.7) * height * .22, r: 21 + i % 3 * 5 }));
        if (active) balls.push({ ...pointer, r: 26 });
        const step = Math.max(4, width / 110), value = (x: number, y: number) => balls.reduce((sum, b) => sum + b.r * b.r / Math.max(1, (x - b.x) ** 2 + (y - b.y) ** 2), 0);
        ctx.fillStyle = color; ctx.beginPath();
        for (let y = 0; y < height; y += step) for (let x = 0; x < width; x += step) {
          const corners = [{ x, y }, { x: x + step, y }, { x: x + step, y: y + step }, { x, y: y + step }];
          const values = corners.map(p => value(p.x, p.y));
          const polygon: Point[] = [];
          for (let i = 0; i < 4; i++) {
            const a = corners[i], b = corners[(i + 1) % 4], av = values[i], bv = values[(i + 1) % 4];
            if (av >= 1) polygon.push(a);
            if ((av >= 1) !== (bv >= 1)) { const t = (1 - av) / (bv - av); polygon.push({ x: a.x + (b.x - a.x) * t, y: a.y + (b.y - a.y) * t }); }
          }
          if (polygon.length) { polygon.forEach((p, i) => { if (i) ctx.lineTo(p.x, p.y); else ctx.moveTo(p.x, p.y); }); ctx.closePath(); }
        }
        ctx.fill();
      } else if (kind === "image-trail") {
        trails = trails.filter(item => time - item.time < 1.5);
        const show = running ? trails : trails.length ? trails : [{ x: width / 2, y: height / 2, time, index: 0 }];
        show.forEach(item => {
          const age = Math.max(0, time - item.time), image = painter.images[item.index % Math.max(1, painter.images.length)];
          const side = Math.min(110, width * .27), opacity = Math.min(1, age * 12 + .15) * Math.min(1, (1.5 - age) * 2);
          ctx.save(); ctx.globalAlpha = running ? Math.max(0, opacity) : .8; ctx.translate(item.x, item.y - age * 12); ctx.rotate((item.index % 5 - 2) * .09); ctx.beginPath(); ctx.roundRect(-side / 2, -side * .62, side, side * 1.24, 12); ctx.clip();
          ctx.fillStyle = palette[item.index % 4]; ctx.fillRect(-side / 2, -side * .62, side, side * 1.24);
          if (image?.complete && image.naturalWidth) ctx.drawImage(image, -side / 2, -side * .62, side, side * 1.24);
          else { ctx.globalAlpha *= .8; dot(0, 0, side * .28, painter.ink); }
          ctx.restore();
        });
      } else if (kind === "ripple-distortion" || kind === "elastic-mesh") {
        if (!texture) return;
        if (textureDirty) {
          texture.width = Math.round(width); texture.height = Math.round(height);
          const paint = texture.getContext("2d")!;
          paint.fillStyle = palette[(painter.tone + 2) % 4]; paint.fillRect(0, 0, width, height);
          for (let i = 0; i < 7; i++) {
            paint.fillStyle = palette[i % 4]; paint.beginPath(); paint.ellipse(width * (.2 + i % 3 * .3), height * (.2 + Math.floor(i / 3) * .3), width * .19, height * .19, i, 0, Math.PI * 2); paint.fill();
            paint.strokeStyle = painter.ink; paint.lineWidth = 1; paint.stroke();
          }
          const image = painter.images[0];
          if (image?.complete && image.naturalWidth) {
            const ratio = Math.max(width / image.naturalWidth, height / image.naturalHeight), w = image.naturalWidth * ratio, h = image.naturalHeight * ratio;
            paint.drawImage(image, (width - w) / 2, (height - h) / 2, w, h);
          }
          textureDirty = false;
        }
        waves = waves.filter(wave => time - wave.time < 1.6);
        const columns = kind === "elastic-mesh" ? 16 : 32, rows = kind === "elastic-mesh" ? 10 : 20;
        const vertices: Point[] = [];
        for (let y = 0; y <= rows; y++) for (let x = 0; x <= columns; x++) {
          const px = x / columns * width, py = y / rows * height;
          let offset = rippleOffset(px, py, waves, time);
          if (kind === "elastic-mesh") {
            const p = mesh[y * (columns + 1) + x], d = Math.hypot(pointer.x - px, pointer.y - py), influence = active && running ? Math.exp(-d * d / 9000) : 0;
            if (running) { const dt = Math.min(delta / 1000, .033); p.vx = (p.vx + ((pointer.x - px) * influence * .38 - p.x) * 80 * dt) * Math.exp(-8 * dt); p.vy = (p.vy + ((pointer.y - py) * influence * .38 - p.y) * 80 * dt) * Math.exp(-8 * dt); p.x += p.vx * dt; p.y += p.vy * dt; }
            offset = { x: p.x, y: p.y };
          }
          const edge = Math.sin(x / columns * Math.PI) * Math.sin(y / rows * Math.PI);
          vertices.push({ x: px + offset.x * edge, y: py + offset.y * edge });
        }
        for (let y = 0; y < rows; y++) for (let x = 0; x < columns; x++) {
          const a = vertices[y * (columns + 1) + x], b = vertices[y * (columns + 1) + x + 1], c = vertices[(y + 1) * (columns + 1) + x], d = vertices[(y + 1) * (columns + 1) + x + 1];
          const sx = x / columns * width, sy = y / rows * height, sw = width / columns, sh = height / rows;
          drawTriangle(ctx, texture, [{ x: sx, y: sy }, { x: sx + sw, y: sy }, { x: sx, y: sy + sh }], [a, b, c]);
          drawTriangle(ctx, texture, [{ x: sx + sw, y: sy + sh }, { x: sx, y: sy + sh }, { x: sx + sw, y: sy }], [d, c, b]);
          if (kind === "elastic-mesh") { ctx.strokeStyle = painter.ink; ctx.globalAlpha = .13; ctx.lineWidth = .5; ctx.beginPath(); ctx.moveTo(a.x, a.y); ctx.lineTo(b.x, b.y); ctx.lineTo(d.x, d.y); ctx.stroke(); ctx.globalAlpha = 1; }
        }
      }
      ctx.globalAlpha = 1;
    },
  };
  return painter;
}

function drawTriangle(ctx: CanvasRenderingContext2D, image: HTMLCanvasElement, source: Point[], destination: Point[]) {
  const [s0, s1, s2] = source, [d0, d1, d2] = destination;
  const sx1 = s1.x - s0.x, sy1 = s1.y - s0.y, sx2 = s2.x - s0.x, sy2 = s2.y - s0.y;
  const determinant = sx1 * sy2 - sx2 * sy1;
  if (Math.abs(determinant) < .001) return;
  const dx1 = d1.x - d0.x, dy1 = d1.y - d0.y, dx2 = d2.x - d0.x, dy2 = d2.y - d0.y;
  const a = (dx1 * sy2 - dx2 * sy1) / determinant, b = (dy1 * sy2 - dy2 * sy1) / determinant;
  const c = (dx2 * sx1 - dx1 * sx2) / determinant, d = (dy2 * sx1 - dy1 * sx2) / determinant;
  // Expand every edge by one CSS pixel. Moving vertices by a fixed distance
  // leaves shallow edges partly transparent; the incenter gives equal overdraw.
  const lengths = [Math.hypot(d1.x-d2.x,d1.y-d2.y),Math.hypot(d0.x-d2.x,d0.y-d2.y),Math.hypot(d0.x-d1.x,d0.y-d1.y)];
  const perimeter = lengths.reduce((sum, length) => sum + length, 0);
  const radius = Math.abs(dx1 * dy2 - dx2 * dy1) / perimeter;
  if (radius < .001) return;
  const center = { x: destination.reduce((sum, point, i) => sum + point.x * lengths[i], 0) / perimeter, y: destination.reduce((sum, point, i) => sum + point.y * lengths[i], 0) / perimeter };
  const cover = destination.map(point => ({ x: point.x + (point.x-center.x)/radius, y: point.y + (point.y-center.y)/radius }));
  ctx.save(); ctx.beginPath(); cover.forEach((point, i) => { if (i) ctx.lineTo(point.x, point.y); else ctx.moveTo(point.x, point.y); }); ctx.closePath(); ctx.clip();
  ctx.transform(a, b, c, d, d0.x - a * s0.x - c * s0.y, d0.y - b * s0.x - d * s0.y); ctx.drawImage(image, 0, 0); ctx.restore();
}
