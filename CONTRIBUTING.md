# Contributing to Hiero Hiero Enterprise Java

Thank you for your interest in contributing to Hiero Hiero Enterprise Java!

## Table of Contents

- [Ways to Contribute](#ways-to-contribute)
  - [Code Contributions](#-code-contributions)
  - [Bug Reports](#-bug-reports)
  - [Feature Requests](#-feature-requests)
  - [Blog Posts](#-blog-posts)
- [Developer Resources](#developer-resources)
- [Cheatsheet](#cheatsheet)

---

## Ways to Contribute

### 💻 Code Contributions

**Get Started By Reading:**

- [Readme](README.md)
- [Getting Started](docs/getting-started.md)

**Quick Start:**

1. Find/create an issue → [Issues](https://github.com/hiero-ledger/hiero-enterprise-java/issues)
2. Get assigned (comment "I'd like to work on this")
3. Read [Getting Started](docs/getting-started.md)
5. GPG and DCO sign commits [Quickstart Signing](https://github.com/hiero-ledger/sdk-collaboration-hub/blob/main/guides/issue-progression/for-developers/signing.md)

**Detailed Docs:**

- [Signing Guide](https://github.com/hiero-ledger/sdk-collaboration-hub/blob/main/guides/issue-progression/for-developers/signing.md)
- [Rebasing Guide](https://github.com/hiero-ledger/sdk-collaboration-hub/blob/main/guides/issue-progression/for-developers/rebasing.md)
- [Merge Conflicts Guide](https://github.com/hiero-ledger/sdk-collaboration-hub/blob/main/guides/issue-progression/for-developers/merge_conflicts.md)
- [Testing Guide](docs/testing-forks.md)


#### ⚠️ A Note on Breaking Changes

**Avoid breaking changes** when possible. If necessary:
1. Create a new issue explaining the benefits
2. Wait for approval
3. Submit as a separate PR with:
   - Reasons for the change
   - Backwards compatibility plan
   - Tests
   - Changelog documentation

---

### 🐛 Bug Reports

Found a bug? Help us fix it!

**Click here to submit a bug** → [Bug Reports](https://github.com/hiero-ledger/hiero-enterprise-java/issues)

---

### 💡 Feature Requests

Have an idea? We'd love to hear it!

1. **Search existing requests** - Avoid duplicates
2. **[Create a Feature Request](https://github.com/hiero-ledger/hiero-enterprise-java/issues/new)**
3. **Describe:**
   - What problem does it solve?
   - How should it work?
   - Example code (if applicable)

**Want to implement it yourself?** Comment on the issue and we'll assign you!

---

### 📝 Blog Posts

Want to write about Hiero Enterprise Java?

We welcome blog posts! Whether you're sharing a tutorial, case study, or your experience building with the SDK, we'd love to feature your content.

**Quick overview:**
- Blog posts are submitted to the [Hiero Website Repository](https://github.com/hiero-ledger/hiero-website) in a Pull Request
- Written in Markdown

**Full guide with step-by-step instructions:** [Blog Post Guide](https://github.com/hiero-ledger/sdk-collaboration-hub/blob/main/guides/issue-progression/for-developers/blogs.md)

---

## Developer Resources

### Essential Guides

| Guide | What It Covers |
|-------|----------------|
| [Signing](https://github.com/hiero-ledger/sdk-collaboration-hub/blob/main/guides/issue-progression/for-developers/signing.md) | GPG + DCO commit signing |
| [Rebasing](https://github.com/hiero-ledger/sdk-collaboration-hub/blob/main/guides/issue-progression/for-developers/rebasing.md) | Keeping branch updated |
| [Merge Conflicts](https://github.com/hiero-ledger/sdk-collaboration-hub/blob/main/guides/issue-progression/for-developers/merge_conflicts.md) | Resolving conflicts |

---

## Cheatsheet

### First-Time Setup
```bash
# Fork on GitHub, then:
git clone https://github.com/YOUR_USERNAME/hiero-enterprise-java.git
cd hiero-enterprise-java
git remote add upstream https://github.com/hiero-ledger/hiero-enterprise-java.git

```

### Making a Contribution
```bash
# Start new work
git checkout main
git pull upstream main
git checkout -b "name-of-your-issue"

# Make changes, then commit (signed!)
git add .
git commit -S -s -m "feat: add new feature"

# Push and create PR
git push origin "name-of-your-issue"
```

### Keeping Branch Updated
```bash
git checkout main
git pull upstream main
git checkout your-branch
git rebase main -S
```

**Full guide:** [Rebasing Guide](https://github.com/hiero-ledger/sdk-collaboration-hub/blob/main/guides/issue-progression/for-developers/rebasing.md)


---

Thank you for contributing to Hiero Enterprise Java! 🎉

- **Need help or want to connect?** Join our community on Discord! See the **[Discord Joining Guide](https://github.com/hiero-ledger/sdk-collaboration-hub/blob/main/guides/issue-progression/for-developers/discord.md)** for detailed steps on how to join the LFDT server
- **Quick Links:**
    - Join the main [Linux Foundation Decentralized Trust (LFDT) Discord Server](https://discord.gg/hyperledger).
    - Go directly to the [#hiero-enterprise-java channel](https://discord.com/channels/905194001349627914/1370373361942663178)