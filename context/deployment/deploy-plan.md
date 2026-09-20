---
project: apteczka-domowa
decided_at: 2026-09-20
status: no-remote-deployment
---

# Plan wdrożenia: brak wdrożenia zdalnego

## Decyzja

Na obecnym etapie projekt **nie jest wdrażany na żadne środowisko**. Wystarczy uruchamianie lokalne oraz CI na GitHub Actions. Nic nie działa w produkcji, nie ma sekretów ani kont u dostawców hostingu.

## Stan faktyczny ("co jest wdrożone")

- Środowisko lokalne: `./mvnw spring-boot:run` (Postgres z `compose.yaml`) lub `./mvnw spring-boot:test-run` (Testcontainers).
- CI: `.github/workflows/ci.yml` uruchamia `./mvnw -B verify` przy pushu i PR do `main`. Bez kroku deploy i bez sekretów.
- Baza: schemat `apteczka`, dwie role. `apteczka_migrator` (tylko Flyway, DDL) i `apteczka_app` (aplikacja, tylko DML). Role tworzy `db/init/01-roles.sql` przy starcie kontenera, uprawnienia nadaje migracja `V1__init_schema.sql`.

## Odłożone

Analiza `context/foundation/infrastructure.md` (Fly.io + zewnętrzny Postgres, Aiven/Neon) pozostaje aktualną propozycją na później. Przed wdrożeniem trzeba jeszcze zrobić:

- Dockerfile i `.dockerignore`
- health-check (Actuator + wyjątek w Spring Security) i profil `prod`
- sekrety (`fly secrets`, GitHub Secrets), hasła ról bazy inne niż deweloperskie
- backupy bazy i decyzja o dostawcy (uwaga na PRD: dane o lekach nie trafiają do podmiotów trzecich)
- staging i workflow deploy w CI
