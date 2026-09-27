export type MarqueeMotion = { phase: number; velocity: number; scrollVelocity: number; scrollDirection: number };

export function marqueeScrollVelocity(distance: number, seconds: number) {
  if (!Number.isFinite(distance + seconds) || seconds <= 0) return 0;
  return Math.max(-2400, Math.min(2400, distance / Math.max(.016, seconds)));
}

/** Bounded integration keeps reversal continuous and background-tab time from accumulating. */
export function advanceMarquee(state: MarqueeMotion, seconds: number, pace: number, period: number, followScroll: boolean): MarqueeMotion {
  if (![seconds, pace, period, state.phase, state.velocity, state.scrollVelocity].every(Number.isFinite) || period <= 0) return { phase: 0, velocity: 0, scrollVelocity: 0, scrollDirection: 1 };
  const dt = Math.max(0, Math.min(.05, seconds));
  const scrollVelocity = followScroll ? state.scrollVelocity * Math.exp(-dt * 5) : 0;
  const scrollDirection = followScroll && Math.abs(scrollVelocity) > 30 ? Math.sign(scrollVelocity) : followScroll ? state.scrollDirection : 1;
  const target = Math.max(-160, Math.min(160, pace)) * scrollDirection * (1 + Math.min(2.2, Math.abs(scrollVelocity) / 800));
  const velocity = state.velocity + (target - state.velocity) * (1 - Math.exp(-dt * 6));
  const phase = ((state.phase + velocity * dt) % period + period) % period;
  return { phase, velocity, scrollVelocity, scrollDirection };
}

export function marqueeDepth(midpoint: number, viewport: number, amount: number) {
  const depth = Number.isFinite(amount) ? Math.max(0, Math.min(1, amount)) : .65;
  const distance = Math.min(1, Math.abs(midpoint - viewport / 2) / Math.max(1, viewport / 2));
  return { blur: distance * distance * depth * 3, opacity: 1 - distance * depth * .28 };
}
