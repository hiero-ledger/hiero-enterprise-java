# Contributing to the docs

This folder contains the source for the [Hiero Enterprise Java technical documentation](https://hiero-ledger.github.io/hiero-enterprise-java/) (GitHub Pages).

## Structure

- **`index.md`** — Landing page and entry point.
- **`getting-started.md`** — Getting started guide.
- **`architecture.md`** — High-level architecture overview.
- **`spring-boot.md`** — Spring Boot integration.
- **`microprofile.md`** — MicroProfile integration.
- **`managed-services.md`** — Base module and managed services.

The site is built with [MkDocs](https://www.mkdocs.org/) and the [Material for MkDocs](https://squidfunk.github.io/mkdocs-material/) theme. Configuration is in the repository root: `mkdocs.yml`.

## Building locally

From the repository root:

```bash
pip install mkdocs-material "pymdown-extensions"
mkdocs serve
```

Then open http://127.0.0.1:8000 .

## Publishing

The `.github/workflows/docs.yml` workflow:

- **Push to `main`:** Builds and deploys the site to the production URL (root).
- **Pull request:** Builds and deploys a preview to `/pr/<number>/` and comments the preview URL on the PR.

Ensure **GitHub Pages** is enabled and set the source to **Deploy from a branch** → branch: `gh-pages`, folder: `/ (root)`.
