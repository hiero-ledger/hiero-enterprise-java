# Contributing to the docs

This folder contains the source for the [Hiero Enterprise Java technical documentation](https://hiero-ledger.github.io/hiero-enterprise-java/) (GitHub Pages).

## Structure

- **`index.md`** — Landing page and entry point.
- **`getting-started.md`** — Getting started guide.
- **`architecture.md`** — High-level architecture overview.
- **`spring-boot.md`** — Spring Boot integration.
- **`microprofile.md`** — MicroProfile integration.
- **`managed-services.md`** — Base module and managed services.

The site is built with [Zensical](https://zensical.org/docs/get-started/) using the repository root `mkdocs.yml` for configuration.

## Building locally

From the repository root:

```bash
pip install zensical==0.0.36
zensical serve
```

Then open http://127.0.0.1:8000 .

## Publishing

The `.github/workflows/docs.yml` workflow:

- **Push to `main`:** Installs `zensical==0.0.36`, builds the site with `zensical build --clean`, and deploys the production site to the GitHub Pages root.
- **Pull request:** Installs `zensical==0.0.36`, builds the site with `zensical build --clean`, deploys a preview to `/pr/<number>/`, and comments the preview URL on the PR.

Ensure **GitHub Pages** is enabled and set the source to **Deploy from a branch** → branch: `gh-pages`, folder: `/ (root)`.
