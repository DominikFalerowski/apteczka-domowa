---
starter_id: spring
package_manager: maven
project_name: apteczka-domowa
hints:
  language_family: multi
  team_size: solo
  deployment_target: fly
  ci_provider: github-actions
  ci_default_flow: auto-deploy-on-merge
  bootstrapper_confidence: verified
  path_taken: standard
  quality_override: false
  self_check_answers: null
  has_auth: true
  has_payments: false
  has_realtime: false
  has_ai: true
  has_background_jobs: false
---

## Why this stack

Apteczka Domowa to aplikacja webowa małej skali (autor + farmaceutka), budowana solo po godzinach w 3 tygodnie. Wybrano polecany starter dla aplikacji webowej w Javie: Spring Boot spełnia wszystkie cztery kryteria przyjazności dla agentów, a jego scaffolding przeszedł pełny test end-to-end. Wygenerowany projekt trzeba uzupełnić o Spring Security (konta, FR-001), Spring Data JPA z PostgreSQL (rejestr leków, wyszukiwanie poniżej 1 s), Flyway (migracje schematu) i Spring AI (odczyt ważności po otwarciu z ChPL i ulotek podczas jednorazowego importu rejestru). Testy backendu opierają się na JUnit 5 i Mockito (ze spring-boot-starter-test) oraz Testcontainers z PostgreSQL w testach integracyjnych. Frontend to osobna aplikacja React Router w trybie SPA (ssr: false, TypeScript, npm), która w przeciwieństwie do samego Vite + React spełnia kryterium konwencji. Styling to Tailwind CSS 4 (jest w domyślnym szablonie startera, wpięty pluginem Vite) plus komponenty shadcn/ui na wyszukiwarkę rejestru, formularze i ostrzeżenia. Spring serwuje jej zbudowane pliki statyczne, co daje jeden artefakt i eliminuje CORS, dlatego projekt łączy Javę i TypeScript. Wdrożenie trafia na fly (domyślny cel startera), a CI działa na GitHub Actions z automatycznym wdrożeniem po merge'u do main. Płatności, praca w czasie rzeczywistym i zadania w tle są poza zakresem MVP.
