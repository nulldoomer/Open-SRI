import { createHighlighter, type Highlighter } from "shiki";

// Module-level singleton — created once per server process, reused across renders
let _highlighter: Highlighter | null = null;

async function getHighlighter(): Promise<Highlighter> {

  if (!_highlighter) {

    _highlighter = await createHighlighter({

      themes: ["gruvbox-dark-medium"],

      langs: [
        "java", "xml", "typescript", "bash", "json", "csharp", "go", "python",
        "php"
      ],

    });

  }

  return _highlighter;

}

export async function highlight(code: string, lang: string): Promise<string> {

  const hl = await getHighlighter();

  return hl.codeToHtml(code, { lang, theme: "gruvbox-dark-medium" });
}
