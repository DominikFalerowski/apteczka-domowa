---
project: apteczka-domowa
researched_at: 2026-09-20
recommended_platform: Fly.io (app) + external managed Postgres (Aiven free or Neon free)
runner_up: Render (app + Render Postgres)
context_type: mvp
tech_stack:
  language: Java 25 (backend) + TypeScript (React Router SPA, ssr false)
  framework: Spring Boot 4.1.1 (Maven)
  runtime: JVM in a Docker container; Spring serves the built SPA as static files (one artifact)
---

## Recommendation

**Deploy the Spring Boot app on Fly.io, with PostgreSQL on an external managed provider (Aiven free plan or Neon free plan, Frankfurt).**

Fly.io scored strongest on operations (CLI, VM-based persistent process, GitHub Actions deploy), and it is the starter's default target. Its one penalty, Managed Postgres from $38/month (about $45 all-in), is avoided by keeping the database elsewhere, which fits the cost-minimising answer. The interview answers that drove this: persistent processes required (drops Vercel/Netlify), minimise cost, single region, and a later decision to split services rather than co-locate.

Decision history: the original leader was Render (app + Render Postgres). The user asked for a split setup, then proposed self-hosting on a mini PC behind a Cloudflare Tunnel. That option was cross-checked (uptime, backups, raw-infra operation) and the user chose Fly.io + external Postgres instead.

Caveat on evidence: fly.io, render.com, railway.com and Cloudflare docs returned HTTP 403 to automated fetches during research. Much of the platform data below comes from search snippets and is marked "unverified" where it was not confirmed on an official page.

## Platform Comparison

Hard filter: persistent processes are required, so Vercel and Netlify (no Java runtime, no persistent process) were dropped before scoring. Cloudflare kept, heavily penalised.

| Platform | CLI-first | Managed | Agent docs | Deploy API | MCP / integration | Notes |
|---|---|---|---|---|---|---|
| **Fly.io** | Pass | Pass | Partial (unverified) | Partial (rollback = redeploy earlier image) | Partial (`fly mcp server` experimental) | About $5-6/month app machine. Managed Postgres $38+ avoided via external DB. |
| **Render** | Pass | Pass | Partial (unverified) | Pass (deploy hooks, REST API) | Partial (MCP early access, cannot update services) | About $13/month with Render Postgres. 512 MB Starter is tight for a JVM. |
| **Railway** | Pass | Partial (Postgres unmanaged) | Pass (markdown) | Partial (CLI rollback unconfirmed) | Partial (unverified) | Cheapest ($5-15/month). DB backups are DIY. |
| **Cloudflare** | Pass | Partial | Pass | Partial | Pass | Containers GA (2026-04-13) but sleep when idle, ephemeral disk, need a Worker in front, no managed Postgres. |
| Vercel | dropped | | | | | No Java runtime. Container-as-function is beta, scales to zero. Hobby is non-commercial. |
| Netlify | dropped | | | | | No JVM or persistent processes. Could only host the static frontend. |
| Mini PC + Cloudflare Tunnel | Partial | Fail (raw infra) | n/a | Partial (SSH via tunnel service token) | n/a | Free hosting. Uptime, backups and OS patching all on the owner. Rejected after cross-check. |

### Shortlisted Platforms

#### 1. Fly.io + external Postgres (Recommended)

Full VMs (Machines) run the JVM as a persistent process, deployed with `fly deploy` from GitHub Actions. Cost is about $5-6/month for a 1 GB `shared-cpu-1x` machine (unverified), plus a free-tier external Postgres. Weaknesses: rollback is a redeploy of a previous image, and its official docs were not fetchable during research.

#### 2. Render (app + Render Postgres)

Managed Postgres in Frankfurt plus Docker deploys and a CLI/API give the simplest all-in-one setup at about $13/month. It loses to Fly.io on the 512 MB JVM memory squeeze, an early-access MCP server, and unverified backup retention.

#### 3. Railway (app + external Postgres)

Cheapest app hosting with an Amsterdam region and markdown docs. It gains little from an external database, and its own Postgres is an unmanaged template, so backups are your problem.

## Anti-Bias Cross-Check: Fly.io + external Postgres

### Devil's Advocate — Weaknesses

1. **Cross-provider latency and reliability.** App in Frankfurt on Fly, DB on another provider: every query crosses the public internet or a peering link. Search must answer in under 1 s, so an extra 5-20 ms per query is fine, but a provider outage on either side takes the app down.
2. **Free Postgres tiers sleep.** Neon scales to zero after 5 minutes idle; Aiven free powers off after inactivity. The first query after idle is slow and can exceed the 1 s search target and trip Spring's connection pool or health checks.
3. **JVM memory on small machines.** 256 MB is not enough. 512 MB works and 1 GB is "mostly enough" per community reports. Without `-XX:MaxRAMPercentage` and metaspace limits the machine is OOM-killed.
4. **Rollback is not one command.** `fly releases` plus `fly deploy --image <ref>` is the documented pattern (unverified). A bad Flyway migration will not roll back with the image.
5. **Free database limits are small.** Neon free is 0.5 GB per project. Aiven free is 1 GB, single-node. That is enough for the registry and a small cabinet, but the RPL import may push against it.

### Pre-Mortem — How This Could Fail

The team deployed the Spring app on Fly.io with a free Neon database, assuming both would just work at MVP scale. The app machine was set to auto-stop to save money. After a quiet weekend both the machine and the database were asleep, and the pharmacist's first search on Monday took over 20 seconds while the JVM and the database woke up. She stopped trusting the tool. A one-off RPL import then pushed the free database near its storage limit, and a rushed migration failed halfway through; there was no rollback and the free tier's backups were shorter than assumed. Debugging the deploy meant learning `flyctl` rollback semantics under pressure. The team had also put the JDBC connection string in the Docker image instead of secrets. Three weeks before the deadline they moved to paid tiers, spent the time on operations instead of features, and the cost advantage was gone.

### Unknown Unknowns

- **Spring Boot 4 modularised its starters.** Flyway auto-configuration needs `spring-boot-starter-flyway` (verify in the pinned 4.1.1 BOM). Adding only `flyway-core` can leave migrations silently not running.
- **PgBouncer / pooled endpoints break Flyway.** Neon's pooled endpoint uses transaction pooling, which does not support Flyway's session-level locks. Use the direct (non-pooled) endpoint for migrations.
- **Managed Postgres on Fly has no Warsaw region.** Use Frankfurt (`fra`) for the app so it sits next to the database, even though Warsaw exists for Machines.
- **Auto-stop is a cost trap for a JVM.** Set `min_machines_running = 1` and disable auto-stop; the machine's always-on cost is the price of acceptable latency.
- **Health-check grace period.** A Spring Boot app with Flyway can take 30-60 s to start; the default health-check grace period may kill it in a restart loop.

## Operational Story

- **Preview deploys**: Fly has no built-in PR previews. Options: a second Fly app (`apteczka-domowa-staging`) deployed from a `staging` branch, or ephemeral apps created per PR from GitHub Actions. For a solo MVP, one staging app is enough. The mini PC could host staging if wanted.
- **Secrets**: `fly secrets set` (values encrypted, not readable back via CLI, injected as environment variables). CI uses a scoped deploy token (`fly tokens create deploy`) stored as a GitHub Actions secret `FLY_API_TOKEN`. The database URL, DB password and any Spring AI key live in Fly secrets, never in the image or repo. Rotation: set the new secret (triggers a redeploy), then revoke the old one at the provider.
- **Rollback**: `fly releases` to list, `fly deploy --image <previous image ref>` to revert (unverified; confirm on first deploy). Time-to-revert is roughly the time of one deploy. Database migrations do not roll back with the app, so write backward-compatible Flyway migrations.
- **Approval**: a human approves production deploys via the merge to `main` (the auto-deploy flow) and does all destructive actions by hand: dropping the database, rotating the primary DB credential, deleting the Fly app. The agent may deploy to staging and read logs unattended.
- **Logs**: `fly logs` (add `-a <app>` and filter flags). Agent-readable JSON via `--json` where supported (unverified). `fly status` and `fly machine status` for state. `fly mcp server` exists but is experimental; CLI is the default for the MVP.

## Risk Register

| Risk | Source | Likelihood | Impact | Mitigation |
|---|---|---|---|---|
| Free Postgres sleeps, first query after idle exceeds 1 s | Devil's advocate | High | Medium | Keep-alive query from the app (scheduled ping), or pay for a small always-on tier; measure search latency after idle before launch. |
| JVM OOM-killed on 512 MB machine | Devil's advocate | Medium | High | Use a 1 GB machine, set `-XX:MaxRAMPercentage=70`, cap metaspace, watch `fly logs` for OOM. |
| Auto-stop causes long cold starts | Pre-mortem | High | Medium | `min_machines_running = 1`, auto-stop disabled in `fly.toml`. |
| Bad Flyway migration cannot be rolled back with the image | Devil's advocate | Medium | High | Backward-compatible migrations only; snapshot or export the database before each migration; test migrations against the Testcontainers Postgres first. |
| Free database backups shorter than assumed, or storage limit hit | Pre-mortem | Medium | High | Check provider backup retention before launch. Take a scheduled `pg_dump` to a second location (the mini PC is a good target). Upgrade tier before the RPL import if it approaches the limit. |
| Flyway auto-config missing in Spring Boot 4 | Unknown unknowns | Medium | High | Confirm `spring-boot-starter-flyway` is on the classpath and a migration runs on the first deploy. |
| Pooled DB endpoint breaks Flyway locks | Unknown unknowns | Medium | Medium | Use the provider's direct endpoint for the app and migrations, or a separate migration datasource. |
| Health-check kills a slow-starting JVM | Unknown unknowns | Medium | Medium | Raise health-check grace period in `fly.toml`. |
| Provider outage on either app or DB side | Devil's advocate | Low | Medium | Accepted for MVP. Status pages for both providers; a hosted fallback is out of scope. |
| Medicine data held by a third-party DB processor conflicts with PRD privacy rule | Research finding | Medium | Medium | Decide explicitly: EU region (Frankfurt), TLS-required connections, note in the PRD that the DB provider is a processor. Neon is US-headquartered (CLOUD Act); Aiven is EU-headquartered. |
| Official docs (fly.io, render.com, railway.com) not fetchable by agents | Research finding | High | Low | Use the GitHub-hosted `superfly/docs` and `flyctl` `--help` output, and confirm commands on first deploy. |
| Fly Managed Postgres pricing, region and status may have changed | Research finding | Medium | Low | Not used in this plan. Re-check only if the external DB is replaced with MPG. |

## Getting Started

The commands below are checked against the pinned stack (Spring Boot 4.1.1, Java 25, Maven wrapper). Fly docs were not fetchable, so confirm `fly` flags with `fly <command> --help` on first use.

1. **Install and log in**: install `flyctl` (see https://fly.io/docs/flyctl/install/), then `fly auth login`.
2. **Dockerfile**: multi-stage build, Maven wrapper in a Temurin 25 JDK image, run stage on a Temurin 25 JRE image. Start with `-XX:MaxRAMPercentage=70` and expose port 8080. Confirm a Java 25 image tag exists before relying on it. Build the SPA in the same build (or a Node stage) and copy its static output into Spring's static resources so it stays one artifact.
3. **Create the app**: `fly launch --no-deploy --region fra` and edit `fly.toml`: `internal_port = 8080`, `min_machines_running = 1`, auto-stop off, a 1 GB machine, and a health-check grace period of 60 s or more.
4. **Database**: create the free Postgres in Frankfurt (Aiven or Neon), require TLS, and pick the direct endpoint. Set `fly secrets set SPRING_DATASOURCE_URL=... SPRING_DATASOURCE_USERNAME=... SPRING_DATASOURCE_PASSWORD=...`. Verify Flyway ran on the first boot.
5. **Deploy**: `fly deploy`, then `fly status` and `fly logs`. Create a deploy token with `fly tokens create deploy` and store it as `FLY_API_TOKEN` in GitHub Actions secrets for the auto-deploy-on-merge workflow.

## Out of Scope

The following were not evaluated in this research:
- Docker image configuration (only sketched in Getting Started)
- CI/CD pipeline setup (the GitHub Actions workflow itself)
- Production-scale architecture (multi-region, HA, DR)
- Choice between Aiven and Neon for the database (both recorded as candidates; decide before first deploy)
