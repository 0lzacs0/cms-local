# Requirements specification for the implementation of this project, cms-local

## Backend

| Layer | Technology | Purpose |
|---|---|---|
| Language / Runtime | Java 21 (LTS) | Application base |
| Framework | Spring Boot 4.1 (current: 4.1.1) | REST API, dependency injection, configuration |
| FTP | Commons Net (`FTPClient`, `FTPSClient`) — Maven: `commons-net:commons-net` | Connect, download/upload files; FTPS support |
| Data access | Spring Data JPA + Hibernate | Map tables `pages`, `blocks`, `revisions`, `users` |
| Database | PostgreSQL 16 + JDBC driver | Local persistence |
| JSON type | PostgreSQL `jsonb` | Store revision snapshots; enable point queries |
| Validation | Bean Validation (Jakarta): `@Size`, `@NotBlank` + custom rules in Service layer | Character limits, allowed types |
| HTML processing | jsoup | Locate/modify editable blocks in downloaded HTML |
| Image processing | `javax.imageio.ImageIO` | Validate real image type (returns `null` for non-images), read dimensions, enforce size limit. No resizing — uploads outside the expected standard are **rejected**, not transformed |
| Sessions / Auth | Spring Session (or `HttpSession` + signed cookie) | FTP credentials in memory only, session expiry |
| Security | Spring Security | CMS login, basic rate limiting, security headers |
| Password hashing | `BCryptPasswordEncoder` | Hash of the CMS user's password; FTP credentials are **never persisted** (session memory only) |
| Build | Maven | Dependency management and build |

## Database Schema

| Object | Definition |
|---|---|
| Table `users` | `id`, `username` (unique), `password_hash` (BCrypt), `created_at` |
| Table `pages` | `id`, `ftp_path`, `title`, `updated_at` |
| Table `blocks` | `id`, `page_id`, `selector`, `type` (`text`/`image`), `constraints jsonb` — for `text`: `max_length`; for `image`: `expected_width`, `expected_height`, `max_file_size_mb`. These constraints **validate and reject uploads outside the standard** (they do not resize) |
| Table `revisions` | `id`, `page_id`, `payload jsonb`, `created_at` — **max 3 per page**, enforced in the service-layer transaction |
| Index | GIN on `revisions.payload` for efficient point lookups inside versions |
| Migrations | **Flyway** — mandatory from day 1 |

## Frontend

| Item | Technology | Notes |
|---|---|---|
| Framework | React | Chosen for market relevance |
| Block overlay | Custom logic + `data-editable` attributes read by jsoup | Click block → modal opens |
| Edit modals | Custom components | Dialog for text; dialog for image |
| Text sanitization | `DOMPurify` (npm) | Escape before any rendering |
| Image upload | `<input type="file">` + preview + MIME/size validation before send | Modal displays the expected dimensions (consistent with the rejection rule) |
| Communication | `fetch`/`axios` | REST endpoints from Spring |
| Screens | Editor (page with overlay), History (3-version comparison + per-block rollback), FTP Configuration | 3 main views |

## Infrastructure & Tooling

| Item | Technology |
|---|---|
| Local database | PostgreSQL 16 in Docker (official image) |
| Test FTP server | `ftp-srv` (Node) — never test against a real server |
| Tests | JUnit 5 + Mockito (backend), Vitest (frontend) |
| Version control | Git + GitHub |
| Logging | SLF4J / Logback (Spring default) |
| IDE | IntelliJ IDEA Community |

## Core Behavior

- The CMS stores FTP configuration (server URL, page list with ftp_path) in the local database; FTP credentials are re-entered at every session and live in memory only.
- The CMS renders the target page with an overlay on editable blocks (identified by `data-editable` attributes, e.g. `<p data-editable="text" data-max-length="200">`, `<img data-editable="image" ...>`).
- Every save of a page creates one revision snapshot of the page's block contents (all blocks, one jsonb payload); the last 3 are kept; the History screen compares versions side by side and allows per-block rollback.
- If the target pages lack these attributes, the CMS provides a **one-time block-mapping configuration mode**: user clicks elements, the CMS saves a block map (CSS selector + type + constraints per block) as JSON in the local database.
- **Text blocks**: clicking opens a dialog; only the text content is editable, within the `max_length` limit.
- **Image blocks**: clicking opens a dialog; upload a new image **with the same dimensions**, changing only the file (name and type may change). Uploads failing validation (not a real image, wrong dimensions, exceeding `max_file_size_mb`) are **rejected with a clear message**.
- Menus, CSS, layout, and all non-editable page elements remain untouched.

## Security Requirements

1. FTP credentials **in memory only**, never in the database, logs, or config files.
2. Prefer **FTPS** (`secure` mode) when the server supports it; warn the user when falling back to plain FTP.
3. Text input: sanitize client-side with `DOMPurify`; **escape on the server** (treat as text, never inject raw user content into HTML).
4. Image upload: validate the **real file type** (never trust extension or declared MIME alone), enforce per-block size limit.
5. CMS login: BCrypt-hashed password in `users`, session timeout, rate limiting on the login endpoint.
6. The app binds locally; document explicitly if the network interface is ever exposed.

---
