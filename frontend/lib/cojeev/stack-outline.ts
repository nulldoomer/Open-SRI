export function stackOutline(
  width: number,
  height: number,
  index: number,
  count: number,
  tabHeight: number,
) {
  const w = Math.max(1, width),
    h = Math.max(tabHeight + 40, height);
  const start = (index * w) / count,
    end = ((index + 1) * w) / count;
  const r = Math.min(12, w / count / 4),
    shoulder = Math.min(8, w / count / 8);
  const body = 20,
    y = tabHeight;
  const path = [`M ${start + r} 0`, `H ${end - r}`, `Q ${end} 0 ${end} ${r}`];
  if (index < count - 1)
    path.push(
      `V ${y - shoulder}`,
      `Q ${end} ${y} ${end + shoulder} ${y}`,
      `H ${w - body}`,
      `Q ${w} ${y} ${w} ${y + body}`,
    );
  path.push(
    `V ${h - body}`,
    `Q ${w} ${h} ${w - body} ${h}`,
    `H ${body}`,
    `Q 0 ${h} 0 ${h - body}`,
  );
  if (index > 0)
    path.push(
      `V ${y + body}`,
      `Q 0 ${y} ${body} ${y}`,
      `H ${start - shoulder}`,
      `Q ${start} ${y} ${start} ${y - shoulder}`,
    );
  path.push(`V ${r}`, `Q ${start} 0 ${start + r} 0`, "Z");
  return path.join(" ");
}
