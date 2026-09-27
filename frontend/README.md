This is a [Next.js](https://nextjs.org) project bootstrapped with [`create-next-app`](https://nextjs.org/docs/app/api-reference/cli/create-next-app).

## Getting Started

First, run the development server:

```bash
npm run dev
# or
yarn dev
# or
pnpm dev
# or
bun dev
```

Open [http://localhost:3000](http://localhost:3000) with your browser to see the result.

You can start editing the page by modifying `app/page.tsx`. The page auto-updates as you edit the file.

This project uses [`next/font`](https://nextjs.org/docs/app/building-your-application/optimizing/fonts) to load Bricolage Grotesque, DM Sans and Geist Mono.

## Learn More

To learn more about Next.js, take a look at the following resources:

- [Next.js Documentation](https://nextjs.org/docs) - learn about Next.js features and API.
- [Learn Next.js](https://nextjs.org/learn) - an interactive Next.js tutorial.

You can check out [the Next.js GitHub repository](https://github.com/vercel/next.js) - your feedback and contributions are welcome!

## Deploy on Vercel

The easiest way to deploy your Next.js app is to use the [Vercel Platform](https://vercel.com/new?utm_medium=default-template&filter=next.js&utm_source=create-next-app&utm_campaign=create-next-app-readme) from the creators of Next.js.

Check out our [Next.js deployment documentation](https://nextjs.org/docs/app/building-your-application/deploying) for more details.

## Design system

The UI is built on [000h by Cojeev](https://000h.cojeev.com), installed from its shadcn registry. Its source is
vendored in `components/ui`, `lib/cojeev`, `lib/cojeev-motion` and `styles/cojeev`, and is used under the MIT
License (copyright © 2026 Sanjay Kumar); the full notice ships in `lib/cojeev/NOTICES.txt`.

OpenSRI customizations live in `app/globals.css`: the Cojeev "graphite" palette, the single `--signal` accent that
replaces Cojeev's pink brand, and the fonts loaded through `next/font` instead of the inline `cojeev-fonts.css`.
Running `shadcn add` for another `@cojeev` item rewrites the token bridge and re-adds that font import, so check
`app/globals.css` afterwards.
