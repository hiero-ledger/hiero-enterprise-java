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

Two workflows drive publication:

- **`.github/workflows/docs.yml`**
    - **Push to `main`:** builds the site with `zensical build --clean` and publishes it to the GitHub Pages root.
    - **Pull request:** builds the site and uploads it (together with the PR number) as a `docs-preview` artifact. No deploy happens here, so this step is safe for PRs coming from forks.
- **`.github/workflows/docs-preview.yml`**
    - Triggered by the `Docs` workflow completing. It downloads the `docs-preview` artifact, deploys it to `gh-pages/pr/<number>/`, and comments the preview URL on the PR. Because it runs in the base-repo context, fork PRs also get a preview.

Ensure **GitHub Pages** is enabled and set the source to **Deploy from a branch** → branch: `gh-pages`, folder: `/ (root)`.
