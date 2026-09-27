export type BloomPoint = { x: number; y: number };
export type BloomParticle = BloomPoint & { vx: number; vy: number; radius: number; radiusFactor: number; angle: number; color: number };

export type BloomOptions = {
  /** Number of particles, 8–100. Default 50. */
  particleCount?: number;
  /** Particle radius in CSS pixels, 6–28. Default 15. */
  radius?: number;
  /** Playback speed, 0–2. Zero freezes the simulation. */
  speed?: number;
  /** Organic wandering force, 0–1. Default 0.5. */
  wander?: number;
  /** Velocity retained per 60Hz step, 0.8–0.98. Default 0.95. */
  damping?: number;
  /** Pointer force multiplier, 0–3. Default 1. */
  pointerAttraction?: number;
  /** Pointer attraction reach in CSS pixels, 0–600. Default 400. */
  pointerRadius?: number;
  /** Text force multiplier, 0–3. Default 1. */
  textAttraction?: number;
  /** Text attraction reach in CSS pixels, 0–400. Default 200. */
  textRadius?: number;
  /** Maximum distance for connections, 0–240 CSS pixels. Default 150. */
  connectionDistance?: number;
};

export function bloomNumber(value: number | undefined, fallback: number, min: number, max: number) {
  return Number.isFinite(value) ? Math.min(max, Math.max(min, value!)) : fallback;
}

export function normalizeBloomOptions(options: BloomOptions = {}): Required<BloomOptions> {
  return {
    particleCount: Math.round(bloomNumber(options.particleCount, 50, 8, 100)),
    radius: bloomNumber(options.radius, 15, 6, 28),
    speed: bloomNumber(options.speed, 1, 0, 2),
    wander: bloomNumber(options.wander, 0.5, 0, 1),
    damping: bloomNumber(options.damping, 0.95, 0.8, 0.98),
    pointerAttraction: bloomNumber(options.pointerAttraction, 1, 0, 3),
    pointerRadius: bloomNumber(options.pointerRadius, 400, 0, 600),
    textAttraction: bloomNumber(options.textAttraction, 1, 0, 3),
    textRadius: bloomNumber(options.textRadius, 200, 0, 400),
    connectionDistance: bloomNumber(options.connectionDistance, 150, 0, 240),
  };
}

/** Source forces integrated at 60Hz, independent of display refresh rate. */
export class BloomSimulation {
  particles: BloomParticle[] = [];
  options: Required<BloomOptions>;
  private randomState: number;
  private accumulator = 0;
  private gathering = 0;

  constructor(public width: number, public height: number, options: BloomOptions = {}, seed = 42) {
    this.options = normalizeBloomOptions(options);
    this.randomState = Number.isFinite(seed) ? seed >>> 0 : 42;
    this.configure(options);
  }

  private random() {
    this.randomState = (Math.imul(this.randomState, 1664525) + 1013904223) >>> 0;
    return this.randomState / 4294967296;
  }

  configure(options: BloomOptions) {
    this.options = normalizeBloomOptions(options);
    this.particles.length = Math.min(this.particles.length, this.options.particleCount);
    while (this.particles.length < this.options.particleCount) {
      const radiusFactor = 0.8 + this.random() * 0.5;
      this.particles.push({
        x: this.width / 2 + (this.random() - 0.5) * Math.min(100, this.width),
        y: this.height / 2 + (this.random() - 0.5) * Math.min(100, this.height),
        vx: (this.random() - 0.5) * 2, vy: (this.random() - 0.5) * 2,
        radius: this.options.radius * radiusFactor, radiusFactor,
        angle: this.random() * Math.PI * 2, color: this.particles.length % 4,
      });
    }
    for (const p of this.particles) p.radius = this.options.radius * p.radiusFactor;
  }

  resize(width: number, height: number) {
    for (const p of this.particles) {
      p.x = p.x / Math.max(1, this.width) * width;
      p.y = p.y / Math.max(1, this.height) * height;
    }
    this.width = width; this.height = height;
  }

  gather() { this.gathering = 240; }

  /** Resolve a direct command without animating, including at speed zero. */
  settle(words: BloomPoint[]) {
    for (let i = 0; i < 180; i++) this.step(words, null);
    this.accumulator = 0;
  }

  scatter() {
    this.gathering = 0;
    for (const p of this.particles) {
      const angle = this.random() * Math.PI * 2;
      const distance = 0.2 + this.random() * 0.24;
      p.x = this.width / 2 + Math.cos(angle) * this.width * distance;
      p.y = this.height / 2 + Math.sin(angle) * this.height * distance;
      p.vx = Math.cos(angle) * 4; p.vy = Math.sin(angle) * 4;
    }
  }

  advance(seconds: number, words: BloomPoint[], pointer: BloomPoint | null) {
    if (this.options.speed === 0 || !Number.isFinite(seconds) || seconds <= 0) return 0;
    this.accumulator += Math.min(seconds * this.options.speed, 1 / 15);
    let steps = 0;
    while (this.accumulator + 1e-10 >= 1 / 60 && steps < 4) {
      this.step(words, pointer);
      this.accumulator = Math.max(0, this.accumulator - 1 / 60);
      steps++;
    }
    return steps;
  }

  private step(words: BloomPoint[], pointer: BloomPoint | null) {
    const o = this.options;
    for (const p of this.particles) {
      p.angle += (this.random() - 0.5) * 0.2;
      p.vx = (p.vx + Math.cos(p.angle) * o.wander) * o.damping;
      p.vy = (p.vy + Math.sin(p.angle) * o.wander) * o.damping;
      if (pointer && Math.hypot(pointer.x - p.x, pointer.y - p.y) < o.pointerRadius) {
        p.vx += (pointer.x - p.x) * 0.002 * o.pointerAttraction;
        p.vy += (pointer.y - p.y) * 0.002 * o.pointerAttraction;
      }
      let nearest: BloomPoint | undefined;
      let distance = Infinity;
      for (const word of words) {
        const d = Math.hypot(word.x - p.x, word.y - p.y);
        if (d < distance) { distance = d; nearest = word; }
      }
      if (this.gathering && !nearest) nearest = { x: this.width / 2, y: this.height / 2 };
      if (nearest && (distance < o.textRadius || this.gathering > 0)) {
        const force = this.gathering ? 0.01 : 0.01 * o.textAttraction;
        p.vx += (nearest.x - p.x) * force;
        p.vy += (nearest.y - p.y) * force;
      }
      p.x += p.vx; p.y += p.vy;
      if (p.x < 0) p.x = this.width;
      if (p.x > this.width) p.x = 0;
      if (p.y < 0) p.y = this.height;
      if (p.y > this.height) p.y = 0;
    }
    this.gathering = Math.max(0, this.gathering - 1);
  }
}
