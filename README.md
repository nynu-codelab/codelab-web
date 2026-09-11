<div align="center">

<img src="admin-web/src/assets/brand/nynu-code-lab-mark.svg" width="96" alt="NYNU CodeLab" />

# NYNU CodeLab Website

**The official website and recruitment management system of NYNU CodeLab.**

A full-stack platform that runs the lab's public presence and its member recruitment end to end — public site, eight-state application review pipeline, and an admin console for content, members and site configuration.

[![CI](https://github.com/nynu-codelab/codelab-web/actions/workflows/ci.yml/badge.svg)](https://github.com/nynu-codelab/codelab-web/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/Java-17-3776AB?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Vue](https://img.shields.io/badge/Vue-3-4FC08D?style=flat-square&logo=vuedotjs&logoColor=white)
![TypeScript](https://img.shields.io/badge/TypeScript-3178C6?style=flat-square&logo=typescript&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8-4479A1?style=flat-square&logo=mysql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-7-DC382D?style=flat-square&logo=redis&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=flat-square&logo=docker&logoColor=white)
[![License: MIT](https://img.shields.io/badge/License-MIT-4EB1BA?style=flat-square)](LICENSE)

English | [简体中文](./README.zh-CN.md)

[Quick start](#quick-start) · [Architecture](#architecture) · [Verification](#verification) · [Contributing](#contributing)

</div>

---

## What this is

NYNU CodeLab is a student lab, and recruiting new members is one of the things it does every year. This repository is the system that runs that process — and it doubles as a reference full-stack codebase that lab members can read, extend and be reviewed against.

Three surfaces, one deployment:

- **Public site** — lab introduction, articles, project showcases and an online application form.
- **Admin console** — reviewing applications through an eight-state pipeline, publishing articles, managing members, projects, technical directions and site configuration.
- **API** — a documented REST surface shared by both front ends.

## Features

**Recruitment pipeline**

- Online application with a full review workflow: submitted → viewed → contacted → preliminary review → interview → final decision, plus withdrawal.
- Applicants see their own status at any time; reviewers see the queue with state transitions and history.
- Token blacklist and login rate limiting on the auth surface.

**Content management**

- Markdown article editing and publishing with draft/published states.
- Project showcase and member profiles managed as first-class entities.
- Technical directions (the lab's tracks) managed separately from content.
- Key-value site configuration driving contact details and feature switches, without a redeploy.

**Admin console**

- Vue 3 + Element Plus console with dashboard, per-entity views and upload management.
- Role-scoped: everything admin-facing lives behind JWT auth.

**Platform**

- Hardened file upload: magic-byte validation, path-traversal protection and UUID renaming — seven defensive layers in total.
- Prometheus-style operational hygiene: health endpoints, structured logging and a documented environment contract.
- PC-first immersive front end: Three.js WebGL spatial layer, canvas particle network and a terminal-console visual style.
- Scripted end-to-end verification — **62 automated checks** across the three core flows (see [Verification](#verification)).

## Architecture

```text
┌──────────────┐     ┌──────────────────┐     ┌─────────────┐
│  web (5173)  │     │  backend (8080)  │     │  MySQL 8    │
│  Vue 3 + TS  │◄────┤  Spring Boot 3   │────►│  Redis 7    │
└──────────────┘     │  MyBatis-Plus    │     └─────────────┘
┌──────────────┐     │  Sa-Token (JWT)  │            ▲
│ admin-web    │◄────┘                  │            │
│ (5174)       │     ┌──────────────────┐     ┌────┴────────┐
│ Element Plus │     │  nginx (80)      │────►│ uploads /   │
└──────────────┘     │  reverse proxy   │     │ static      │
                     └──────────────────┘     └─────────────┘
```

| Layer | Technologies |
| --- | --- |
| Backend | Java 17, Spring Boot 3.x, Maven, MyBatis-Plus, Sa-Token (JWT) |
| Database / cache | MySQL 8, Redis 7 (token blacklist + login rate limiting) |
| Public front end | Vue 3, TypeScript, Vite, Pinia, Vue Router 4 |
| Admin console | Vue 3, TypeScript, Vite, Element Plus |
| Delivery | Docker Compose, Nginx, GitHub Actions CI |

## Quick start

### Docker Compose (recommended)

```bash
git clone https://github.com/nynu-codelab/codelab-web.git
cd codelab-web/deploy
cp .env.example .env          # set the passwords and JWT secret
docker compose --env-file .env up -d --build
```

| Surface | URL |
| --- | --- |
| Public site | http://localhost |
| Admin console | http://localhost/admin/ |
| API docs | http://localhost/doc.html |

### Local development

Prerequisites: Java 17, Maven 3.9+, Node.js 20+, MySQL 8.

```bash
# 1. Initialise the database
mysql -u root -p < deploy/mysql/init/01-init.sql

# 2. Backend (port 8080)
cd backend && mvn spring-boot:run

# 3. Public front end (port 5173)
cd web && npm install && npm run dev

# 4. Admin console (port 5174)
cd admin-web && npm install && npm run dev
```

## Configuration

Copy `deploy/.env.example` to `deploy/.env` and fill in the required values:

| Variable | Purpose | How to generate |
| --- | --- | --- |
| `MYSQL_ROOT_PASSWORD` | MySQL root password | `openssl rand -base64 24` |
| `MYSQL_PASSWORD` | Application database password | `openssl rand -base64 20` |
| `JWT_SECRET` | JWT signing secret | `openssl rand -base64 64` |

Spring profiles: `local` (default — `localhost:3306`, in-memory Redis behaviour) and `docker` (`mysql:3306`, real Redis).

## Verification

Three scripts exercise the core flows end to end against a running deployment — **62 checks in total**:

```bash
bash scripts/verify-recruitment-flow.sh   # recruitment pipeline, 18 checks
bash scripts/verify-article-flow.sh       # article lifecycle, 23 checks
bash scripts/verify-project-flow.sh       # project showcase, 21 checks
```

Every check asserts a specific behaviour, and a failure fails the script — the suite is meant to be a regression gate, not a demo.

## Project structure

```text
codelab-web/
├── backend/        # Spring Boot 3 API (port 8080)
├── web/            # public Vue 3 SPA (port 5173)
├── admin-web/      # admin Vue 3 + Element Plus (port 5174)
├── deploy/         # Docker Compose, Nginx, MySQL init scripts
├── scripts/        # ops and end-to-end verification scripts
└── docs/           # project, deployment, API and design documents
```

## Documentation

| Document | Contents |
| --- | --- |
| [docs/项目说明.md](docs/项目说明.md) | Background, users, roles, feature list, business flows |
| [docs/部署运行说明.md](docs/部署运行说明.md) | Environments, local/Docker startup, database init, backup, HTTPS, production |
| [docs/接口说明.md](docs/接口说明.md) | API conventions, auth, response format, endpoint list |
| [docs/变更记录.md](docs/变更记录.md) | Change log and known issues |
| [docs/前端视觉组件规范.md](docs/前端视觉组件规范.md) | Visual direction and component usage rules |

> The documentation is written in Chinese — it is the working language of the lab.

## Security

- The default administrator account (`admin` / `admin123`) exists **for local development only**; change it before any real deployment.
- `deploy/.env` is never committed, and production requires strong passwords.
- Production deployments must serve over HTTPS; certificates and private keys never enter the repository.
- See [SECURITY policy](https://github.com/nynu-codelab/.github/blob/main/SECURITY.md) for reporting a vulnerability.

## Contributing

Contributions are welcome — from lab members and from outside. The lab runs the same pipeline for every change:

> requirement → technical design → interface contract → pull request → CI → code review → merge

1. Pick or open an [issue](https://github.com/nynu-codelab/codelab-web/issues).
2. Create a branch (`feature/...` or `fix/...`).
3. Follow [Conventional Commits](https://www.conventionalcommits.org) for commit messages.
4. Open a pull request and make CI pass — `Backend Test`, `Web Build` and `Admin Web Build` are required.
5. A code owner reviews and merges.

Standards and conventions: [nynu-codelab/docs](https://github.com/nynu-codelab/docs) ·
[CONTRIBUTING](https://github.com/nynu-codelab/.github/blob/main/CONTRIBUTING.md)

## Disclaimer

This project is maintained by the CodeLab student lab. It is **not** an official portal of Nanyang Normal University.

## License

[MIT](LICENSE)
