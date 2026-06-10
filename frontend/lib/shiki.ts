import { getSingletonHighlighter } from "shiki";

const LANGS = [
  "java", "xml", "typescript", "bash", "json", "csharp", "go", "python",
  "php", "groovy", "kotlin",
] as const;

const THEME = "catppuccin-mocha";

export async function highlight(code: string, lang: string): Promise<string> {
  const hl = await getSingletonHighlighter({ themes: [THEME], langs: [...LANGS] });
  return hl.codeToHtml(code, { lang, theme: THEME });
}
