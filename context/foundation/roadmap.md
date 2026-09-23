---
project: "Apteczka Domowa"
version: 1
status: draft
created: 2026-09-22
updated: 2026-09-22
prd_version: 1
main_goal: market-feedback
top_blocker: external
milestone_id: mvp-przeplyw-v1
milestone_seq: 1
milestone_status: open
---

# Roadmap: Apteczka Domowa

> Wyprowadzona z `context/foundation/prd.md` (v1) oraz z automatycznej inwentaryzacji bazy kodu.
> Edytuj w miejscu; archiwizuj, gdy zostanie zastąpiona.
> Kawałki poniżej są wypisane w kolejności zależności. Tabela „At a glance" jest indeksem.

## Milestone

**M-1: MVP — przepływ v1 z ostrzeżeniem o duplikacie** — Status: open

- **Intent:** Doprowadzić do końca przepływ v1 z „Kryteriów sukcesu" PRD — od rejestracji, przez dodanie dwóch leków z rejestru, po ostrzeżenie o powtórzonej substancji czynnej, wyróżnienie kończących się terminów i archiwizację — na tyle kompletnie, żeby farmaceutka mogła ocenić wiarygodność reguły duplikatu.
- **Source materials:** `context/foundation/prd.md` (v1)
- **Done when:** każdy `F-NN` i `S-NN` poniżej ma status `done`.
- **Scope anchors:** wszystkie 12 wymagań koniecznych PRD — FR-001, FR-002, FR-007, FR-008, FR-009, FR-010, FR-011, FR-013, FR-014, FR-015, FR-017, FR-019 — oraz obie historyjki: US-01, US-02. Wymagania `nice-to-have` (FR-003–FR-006, FR-012, FR-016, FR-018, FR-020) są poza tym kamieniem milowym, zob. `## Parked`.

## Vision recap

Domowa apteczka leży w szufladzie bez ewidencji: w aptece nie wiadomo, co już jest w domu, więc kupuje się kolejny lek z tą samą substancją czynną pod inną nazwą handlową; gdy ktoś choruje, potrzebny lek okazuje się przeterminowany. Tym, co odróżnia ten produkt od kartki i od aplikacji z przypomnieniami, jest porównywanie leków po substancji czynnej i drodze podania, a nie po nazwie handlowej — i ocena terminu ważności uwzględniająca ważność po otwarciu z ChPL lub ulotki. Aplikacja informuje, nie zaleca leczenia.

## North star

**S-03: Użytkownik dodaje drugi lek z tą samą substancją czynną i dostaje ostrzeżenie o duplikacie** — jeśli ta reguła nie okaże się wiarygodna, reszta produktu jest kolejnym spisem leków, więc sprawdzamy ją najwcześniej, jak pozwalają zależności.

> „Gwiazda przewodnia" znaczy tu: najmniejszy kawałek działający od końca do końca, którego dowiezienie potwierdza, że główne założenie produktu jest prawdziwe. Umieszczamy go tak wcześnie, jak pozwalają wymagania wstępne, bo wszystko inne ma znaczenie dopiero wtedy, gdy on zadziała.

## At a glance

| ID    | Change ID                            | Rezultat (użytkownik może …)                                          | Prerequisites | PRD refs                          | Status   |
| ----- | ------------------------------------ | --------------------------------------------------------------------- | ------------- | --------------------------------- | -------- |
| F-01  | `szkielet-frontu`                    | (fundament) front buduje się i jest serwowany przez Spring            | —             | NFR (telefon), Access Control     | ready    |
| F-02  | `import-rejestru-lekow`              | (fundament) dane Rejestru Produktów Leczniczych są w bazie            | —             | FR-007, FR-013, FR-014            | ready    |
| F-03  | `waznosc-po-otwarciu-z-chpl`         | (fundament) znana ważność po otwarciu z ChPL/ulotek                   | F-02          | FR-019, US-02                     | proposed |
| S-01  | `konto-i-apteczka`                   | założyć konto, zalogować się i utworzyć własną apteczkę               | F-01          | FR-001, FR-002, Access Control    | proposed |
| S-02  | `dodanie-leku-z-rejestru`            | wyszukać lek w rejestrze, dodać go z terminem i zobaczyć na liście    | S-01, F-02    | FR-007, FR-008, FR-009            | proposed |
| S-03  | `ostrzezenie-o-duplikacie-substancji`| dostać ostrzeżenie o powtórzonej substancji czynnej                   | S-02          | US-01, FR-013, FR-014             | proposed |
| S-04  | `terminy-waznosci-na-liscie`         | zobaczyć, które leki są przeterminowane lub kończą się w 30 dni       | S-02, F-03    | US-02, FR-015, FR-019, FR-010     | proposed |
| S-05  | `archiwizacja-leku`                  | zarchiwizować lek oddany do utylizacji                                | S-03          | FR-011, US-01, US-02              | proposed |
| S-06  | `wyszukiwanie-w-apteczce`            | wyszukać lek w apteczce po nazwie handlowej lub substancji czynnej    | S-02          | FR-017                            | proposed |

## Streams

Pomoc nawigacyjna — grupuje elementy dzielące łańcuch wymagań wstępnych. Kanoniczna kolejność nadal żyje w grafie zależności poniżej; ta tabela to proponowana kolejność czytania w poprzek równoległych torów.

| Stream | Theme                        | Chain                                  | Note                                                                                      |
| ------ | ---------------------------- | -------------------------------------- | ----------------------------------------------------------------------------------------- |
| A      | Powłoka aplikacji i wejście  | `F-01` → `S-01`                        | Czysty scaffolding, zero ryzyka merytorycznego — można prowadzić równolegle do strumienia B. |
| B      | Rejestr, leki i ostrzeżenie  | `F-02` → `S-02` → `S-03` → `S-05`      | Tor gwiazdy przewodniej; dołącza do strumienia A w `S-02`. Tu leży całe ryzyko danych zewnętrznych. |
| C      | Terminy ważności             | `F-03` → `S-04`                        | Odgałęzia się od `F-02`; drugi najbardziej niepewny odczyt danych (PDF ChPL i ulotek).     |
| D      | Wyszukiwanie w apteczce      | `S-06`                                 | Odgałęzienie od `S-02`; nie blokuje i nie jest blokowane przez B i C — dobry kawałek do równoległego agenta. |

## Baseline

Co jest już w bazie kodu na dzień `2026-09-22` (zinwentaryzowane automatycznie, potwierdzone przez użytkownika).
Fundamenty poniżej zakładają, że to istnieje, i tego nie budują ponownie.

- **Frontend:** brak — w repozytorium nie ma żadnego `package.json` ani katalogu frontu. React Router (SPA, `ssr: false`), Tailwind CSS 4 i shadcn/ui są zadeklarowane w `tech-stack.md`, ale nic nie jest zescaffoldowane.
- **Backend / API:** częściowo — Spring Boot 4.1.1 na Javie 25, `spring-boot-starter-webmvc` na classpath, ale jedyna klasa produkcyjna to `src/main/java/com/dominikf/apteczka_domowa/ApteczkaDomowaApplication.java`. Zero kontrolerów, encji, serwisów i repozytoriów.
- **Data:** częściowo — Postgres przez `compose.yaml` i Testcontainers, Flyway wpięty (`spring-boot-starter-flyway`, `flyway-database-postgresql`), `src/main/resources/db/migration/V1__init_schema.sql` zakłada schemat `apteczka` i model dwóch ról (`apteczka_migrator` DDL / `apteczka_app` DML) — ale **nie ma ani jednej tabeli**. JPA na classpath, bez encji.
- **Auth:** częściowo — `spring-boot-starter-security` na classpath (działa domyślne zabezpieczenie Springa), brak `SecurityFilterChain`, encji użytkownika i ekranu logowania.
- **Deploy / infra:** częściowo — `.github/workflows/ci.yml` uruchamia `./mvnw -B verify` przy pushu i PR do `main`. Brak `Dockerfile`, `fly.toml` i kroku wdrożenia; `context/deployment/deploy-plan.md` zapisuje świadomą decyzję `status: no-remote-deployment`.
- **Observability:** brak — bez Actuatora, konfiguracji logowania i śledzenia błędów (odłożone w `deploy-plan.md`).

## Foundations

### F-01: Szkielet frontu serwowany przez Spring

- **Outcome:** (fundament) aplikacja frontowa buduje się i jest serwowana przez Spring jako pliki statyczne w jednym artefakcie; w przeglądarce telefonu widać pustą powłokę aplikacji pod adresem głównym.
- **Change ID:** `szkielet-frontu`
- **PRD refs:** NFR „MVP jest w pełni używalne w przeglądarce na telefonach z dwoma najpopularniejszymi mobilnymi systemami operacyjnymi"; Access Control (niezalogowany widzi wyłącznie ekran logowania — potrzebny router)
- **Unlocks:** `S-01` (i przez nią każdy kolejny kawałek — bez powłoki nic nie jest widoczne dla użytkownika); ścieżka weryfikacji „przepływ v1 sprawdzany w przeglądarce telefonu", której wymagają wszystkie `S-NN`
- **Prerequisites:** —
- **Parallel with:** F-02, F-03
- **Blockers:** —
- **Unknowns:** —
- **Risk:** Ułożony pierwszy, bo żaden kawałek użytkowy nie da się zweryfikować bez powłoki. Zakres celowo ścięty do budowania, serwowania i jednej trasy — realny interfejs dochodzi w `S-01`. Ryzyko: rozlanie się do „zbudujmy cały design system" i zjedzenie budżetu, który miał iść na regułę duplikatu.
- **Status:** ready

### F-02: Jednorazowy import Rejestru Produktów Leczniczych

- **Outcome:** (fundament) dane rejestru — nazwa handlowa, substancje czynne i droga podania — są wczytane do bazy i da się je wyszukać po nazwie handlowej poniżej 1 s.
- **Change ID:** `import-rejestru-lekow`
- **PRD refs:** FR-007 (wyszukanie i uzupełnienie substancji z rejestru), FR-013 i FR-014 (reguła duplikatu czyta substancję i drogę podania z rejestru); NFR „wyniki wyszukiwania < 1 s"
- **Unlocks:** `S-02`, `S-03`, `F-03`; redukuje główne ryzyko tego kamienia milowego — czy dane rejestru w ogóle udźwigną regułę duplikatu
- **Prerequisites:** —
- **Parallel with:** F-01, S-01
- **Blockers:** —
- **Unknowns:**
  - Czy XML rejestru niesie drogę podania dla każdego leku jako osobny atrybut porównywalny wprost, bez grupowania wartości? PRD zapisuje to jako rozstrzygnięte, ale rozstrzygnięcie nie było sprawdzone na prawdziwym pliku. — Owner: użytkownik. Block: no.
  - Jak w danych rejestru reprezentowane są leki złożone (np. „paracetamol + kofeina"), skoro reguła duplikatu ma je obejmować? — Owner: użytkownik. Block: no.
- **Risk:** Ułożony równolegle do F-01 i przed resztą, bo to jedyne miejsce, gdzie ryzyko danych zewnętrznych — wskazane jako główne ograniczenie projektu — może zostać zdjęte. Jeśli droga podania okaże się nieporównywalna wprost albo leki złożone rozjadą się z modelem, zmienia się kształt gwiazdy przewodniej, a nie tylko szczegół implementacji. Lepiej dowiedzieć się tego przed `S-02`, nie po.
- **Status:** ready

### F-03: Odczyt ważności po otwarciu z ChPL i ulotek

- **Outcome:** (fundament) dla leków z rejestru znana jest ważność po otwarciu tam, gdzie ChPL lub ulotka ją podaje; brak danych jest jawnie odróżniony od zera, żeby reguła terminu mogła się na niego bezpiecznie cofnąć.
- **Change ID:** `waznosc-po-otwarciu-z-chpl`
- **PRD refs:** FR-019 (ważność po otwarciu pochodzi z ChPL lub ulotki), US-02 (obowiązuje wcześniejszy z dwóch terminów, „o ile ważność po otwarciu jest znana")
- **Unlocks:** `S-04`; ścieżka weryfikacji dla kryterium akceptacji US-02 „wyróżnienie liczone od wcześniejszego z terminów" — bez tych danych kryterium nie da się sprawdzić
- **Prerequisites:** F-02
- **Parallel with:** F-01, S-01, S-02, S-03
- **Blockers:** —
- **Unknowns:**
  - Dla jakiej części leków ChPL lub ulotka podaje ważność po otwarciu w postaci nadającej się do automatycznego odczytu, i czy ta część wystarczy, żeby US-02 dało się w ogóle pokazać? — Owner: użytkownik. Block: no.
  - Czy odczyt z PDF ma działać jednorazowo na całym rejestrze, czy tylko dla leków faktycznie dodanych do apteczki? Pierwsze jest kosztowne, drugie przesuwa koszt na moment dodania leku i może kolidować z NFR o czasie odpowiedzi. — Owner: użytkownik. Block: no.
- **Risk:** Ułożony po F-02, bo korzysta z linków z tego samego XML-a, i osobno od `S-04`, żeby `S-04` nie urosło do kawałka zawierającego jednocześnie potok odczytu PDF i całą semantykę dat. To drugi najbardziej niepewny element danych: PRD świadomie przyjmuje ryzyko błędnego odczytu i nie przewiduje jego kontroli w MVP, więc błąd tutaj jest cichy — nie wywróci budowania, tylko da złą datę.
- **Status:** proposed

## Slices

### S-01: Konto i założenie apteczki

- **Outcome:** użytkownik może założyć konto, zalogować się i — nie mając jeszcze apteczki — utworzyć własną, stając się jej właścicielem, po czym widzi jej pustą listę leków.
- **Change ID:** `konto-i-apteczka`
- **PRD refs:** FR-001, FR-002; Access Control (niezalogowany widzi wyłącznie ekran logowania/rejestracji; w MVP dostępna tylko opcja „załóż apteczkę"); Kryteria sukcesu kroki 1–3
- **Prerequisites:** F-01
- **Parallel with:** F-02, F-03
- **Blockers:** —
- **Unknowns:** —
- **Risk:** Dwa wymagania w jednym kawałku, bo rejestracja bez wyboru „załóż / dołącz" zostawia użytkownika na ślepym ekranie — to jedno wejście do produktu, nie dwa. Ryzyko: ekran wyboru z jedną czynną opcją (dołączanie to v2) wygląda na niedokończony; w MVP to akceptowane, bo zapobiega tworzeniu zbędnych apteczek przy późniejszym dołączaniu.
- **Status:** proposed

### S-02: Dodanie leku z rejestru z terminem ważności

- **Outcome:** użytkownik może wyszukać lek w Rejestrze Produktów Leczniczych po nazwie handlowej, dodać go do apteczki z ręcznie wpisanym terminem ważności (miesiąc/rok albo pełna data) i zobaczyć go na liście aktywnych leków z uzupełnioną z rejestru substancją czynną.
- **Change ID:** `dodanie-leku-z-rejestru`
- **PRD refs:** FR-007, FR-008, FR-009; NFR „wyniki wyszukiwania < 1 s"; Kryteria sukcesu krok 4
- **Prerequisites:** S-01, F-02
- **Parallel with:** F-03
- **Blockers:** —
- **Unknowns:**
  - Czy lek zapisujemy jako odniesienie do wpisu rejestru, czy jako kopię jego danych w chwili dodania? PRD zakłada brak automatycznej aktualizacji rejestru, więc obie odpowiedzi są spójne z zakresem, ale różnią się tym, co się stanie przy kolejnym imporcie. — Owner: użytkownik. Block: no.
- **Risk:** Pierwszy kawałek dotykający danych zewnętrznych w sposób widoczny dla użytkownika, więc to tutaj wyjdzie, czy import z F-02 rzeczywiście wystarcza. Ułożony przed gwiazdą przewodnią, bo bez dwóch leków w apteczce nie ma czego porównywać. Ryzyko: wpisywanie terminu ręcznie w dwóch formatach jest najbardziej żmudną częścią produktu, a Kryteria sukcesu (Secondary) zakładają wprowadzenie całej szuflady w jednej sesji.
- **Status:** proposed

### S-03: Ostrzeżenie o powtórzonej substancji czynnej

- **Outcome:** użytkownik po dodaniu leku o tej samej substancji czynnej i tej samej drodze podania co inny aktywny lek dostaje komunikat z nazwą handlową leku już obecnego w apteczce, a oba leki dostają na liście żółte oznaczenie.
- **Change ID:** `ostrzezenie-o-duplikacie-substancji`
- **PRD refs:** US-01, FR-013, FR-014; Business Logic („Powtórzona substancja czynna"); Kryteria sukcesu kroki 5–7
- **Prerequisites:** S-02
- **Parallel with:** F-03, S-04, S-06
- **Blockers:** —
- **Unknowns:**
  - Czy zawężenie do tej samej drogi podania jest regułą wystarczająco ostrożną? PRD zapisuje przy FR-013 „do potwierdzenia z farmaceutką" i to potwierdzenie jest właśnie celem tego kawałka. — Owner: użytkownik (z farmaceutką). Block: no — ten kawałek istnieje po to, żeby pytanie dało się rozstrzygnąć na działającej rzeczy.
- **Risk:** To jest gwiazda przewodnia — ułożona tak wcześnie, jak pozwalają zależności (potrzebuje apteczki i dwóch leków z rejestru). Reguła odrzucania leków zarchiwizowanych jest częścią modelu od początku, ale jej kryterium akceptacji z US-01 („jeśli B jest zarchiwizowany, dodanie C nie wywołuje komunikatu") da się sprawdzić dopiero po `S-05` — do tego czasu US-01 jest domknięte częściowo. Ryzyko: fałszywe albo pominięte ostrzeżenie uderza wprost w Guardrail PRD o wiarygodności.
- **Status:** proposed

### S-04: Wyróżnienie kończących się terminów ważności

- **Outcome:** użytkownik widzi na liście lek przeterminowany na czerwono, a lek z terminem kończącym się w ciągu 30 dni na bardzo jasny czerwony, i może uzupełnić lub poprawić termin ważności oraz datę otwarcia leku.
- **Change ID:** `terminy-waznosci-na-liscie`
- **PRD refs:** US-02, FR-015, FR-019, FR-010; Business Logic („Ocena terminu ważności"); Kryteria sukcesu krok 8
- **Prerequisites:** S-02, F-03
- **Parallel with:** S-03, S-05, S-06
- **Blockers:** —
- **Unknowns:**
  - Co pokazujemy, gdy lek ma zapisaną datę otwarcia, ale ważność po otwarciu jest nieznana? US-02 mówi „o ile ważność po otwarciu jest znana", czyli liczy się sam termin z opakowania — pytanie, czy użytkownik ma się o tym dowiedzieć, czy ma to zostać niewidoczne. — Owner: użytkownik. Block: no.
- **Risk:** Ułożony po gwieździe przewodniej, bo kolejność łamie remisy na korzyść najbardziej niepewnego założenia, a reguła duplikatu jest bardziej niepewna niż arytmetyka dat. Edycja terminu i daty otwarcia siedzi tutaj, a nie osobno, bo bez niej nie da się w ogóle ustawić daty otwarcia, od której zależy cała reguła. Ryzyko: semantyka dat ma cztery przypadki brzegowe (miesiąc/rok do końca miesiąca, pełna data włącznie, wcześniejszy z dwóch terminów, brak ważności po otwarciu) i każdy z nich myli się cicho — zły kolor zamiast błędu.
- **Status:** proposed

### S-05: Archiwizacja leku

- **Outcome:** użytkownik może zarchiwizować lek oddany do utylizacji zamiast go usuwać; lek znika z listy aktywnych, a ostrzeżenia o powtórzonej substancji czynnej są przeliczane bez niego.
- **Change ID:** `archiwizacja-leku`
- **PRD refs:** FR-011; US-01 (kryterium „zarchiwizowane leki nie są brane pod uwagę"), US-02 (kryteria „lek znika z listy aktywnych" i „ostrzeżenia są przeliczane"); Kryteria sukcesu krok 9
- **Prerequisites:** S-03
- **Parallel with:** S-04, S-06
- **Blockers:** —
- **Unknowns:** —
- **Risk:** Zależy od `S-03`, bo domyka kryterium akceptacji US-01, którego bez istniejących ostrzeżeń nie da się sprawdzić. To ten kawałek zamyka przepływ v1 z Kryteriów sukcesu. Ryzyko przyjęte świadomie w PRD: pomyłki nie da się cofnąć, bo przegląd archiwum (FR-012) jest poza MVP — zarchiwizowany lek znika bez drogi powrotnej.
- **Status:** proposed

### S-06: Wyszukiwanie leku w apteczce

- **Outcome:** użytkownik może wyszukać lek na liście swojej apteczki po nazwie handlowej albo po substancji czynnej.
- **Change ID:** `wyszukiwanie-w-apteczce`
- **PRD refs:** FR-017; User & Persona (moment „w aptece przy zakupie — czy mam już tę substancję?")
- **Prerequisites:** S-02
- **Parallel with:** S-03, S-04, S-05
- **Blockers:** —
- **Unknowns:** —
- **Risk:** Jedyny kawałek niezależny od gwiazdy przewodniej i od terminów ważności — dobry kandydat na równoległego agenta, gdy tor B czeka na rozstrzygnięcie z farmaceutką. Ułożony jako ostatni, bo z trzech momentów użycia persony (apteka, choroba, porządkowanie) tylko pierwszy go wymaga, a pozostałe dwa obsługuje sama lista. Ryzyko: wyszukiwanie po substancji czynnej wygląda jak reguła duplikatu, ale nią nie jest — nie uwzględnia drogi podania ani statusu leku i nie powinno udawać ostrzeżenia.
- **Status:** proposed

## Backlog Handoff

| Roadmap ID | Change ID                             | Suggested issue title                                              | Ready for `/10x-plan` | Notes |
| ---------- | ------------------------------------- | ------------------------------------------------------------------ | --------------------- | ----- |
| F-01       | `szkielet-frontu`                     | Szkielet frontu budowany i serwowany przez Spring                  | yes                   | Brak wymagań wstępnych; można prowadzić równolegle z F-02 |
| F-02       | `import-rejestru-lekow`               | Jednorazowy import Rejestru Produktów Leczniczych do bazy          | yes                   | Zdejmuje główne ryzyko projektu — rekomendowany pierwszy |
| F-03       | `waznosc-po-otwarciu-z-chpl`          | Odczyt ważności po otwarciu z ChPL i ulotek                        | no                    | Czeka na F-02 (linki do dokumentów z XML rejestru) |
| S-01       | `konto-i-apteczka`                    | Rejestracja, logowanie i założenie własnej apteczki                | no                    | Czeka na F-01 |
| S-02       | `dodanie-leku-z-rejestru`             | Dodanie leku z rejestru wraz z terminem ważności                   | no                    | Czeka na S-01 i F-02 |
| S-03       | `ostrzezenie-o-duplikacie-substancji` | Ostrzeżenie o powtórzonej substancji czynnej i drodze podania      | no                    | Gwiazda przewodnia; czeka na S-02 |
| S-04       | `terminy-waznosci-na-liscie`          | Wyróżnienie terminów ważności z uwzględnieniem daty otwarcia       | no                    | Czeka na S-02 i F-03 |
| S-05       | `archiwizacja-leku`                   | Archiwizacja leku i przeliczenie ostrzeżeń                         | no                    | Czeka na S-03 |
| S-06       | `wyszukiwanie-w-apteczce`             | Wyszukiwanie w apteczce po nazwie handlowej i substancji czynnej    | no                    | Czeka na S-02; niezależne od S-03/S-04/S-05 |

## Open Roadmap Questions

1. **Jak farmaceutka dostanie dostęp do aplikacji, żeby ocenić regułę duplikatu?** Celem tego kamienia milowego jest jej informacja zwrotna, ale `context/deployment/deploy-plan.md` ustala `status: no-remote-deployment` — działa tylko środowisko lokalne i CI. Trzeba rozstrzygnąć, czy ocena odbywa się przy Twoim komputerze, czy wraca temat wdrożenia z `infrastructure.md` (Fly.io + zewnętrzny Postgres). — Owner: użytkownik. Block: roadmap-wide (nie blokuje planowania żadnego kawałka, blokuje domknięcie celu kamienia milowego).
2. **Docelowa skala produktu.** Obecnie autor + farmaceutka; docelowo większy wolumen, na razie nieokreślony. Przy większej skali kontrola danych odczytanych z ChPL i ulotek staje się krytyczna (zob. Vision), co zmieniłoby kształt F-03. — Owner: użytkownik. Block: roadmap-wide.

## Parked

- **Współdzielenie apteczki z domownikami** (FR-003–FR-006, FR-020: kod zaproszenia, dołączanie, usuwanie i opuszczanie apteczki, wybór apteczki z listy) — Non-Goals PRD: MVP dla jednej osoby, współdzielenie w v2.
- **Dołączanie przez link zaproszenia** — Non-Goals PRD: docelowo wystarczy kod.
- **Przegląd archiwum leków** (FR-012) — Non-Goals PRD: historia gromadzi się od początku, przegląd w v2.
- **Ręczne dodawanie leków spoza rejestru** (FR-016) — Non-Goals PRD: w MVP tylko leki z rejestru, żeby ostrzeżenia były wiarygodne.
- **Wyciszanie zamierzonych duplikatów** (FR-018) — Non-Goals PRD: ostrzeżenie zawsze widoczne w MVP.
- **Powiadomienia o terminie ważności** — Non-Goals PRD: w MVP status widać tylko na liście.
- **Automatyczna aktualizacja danych z rejestru** — Non-Goals PRD: dane wczytane jednorazowo.
- **Sprawdzanie interakcji między lekami, dziennik przyjętych dawek, rekomendacje zamienników, skanowanie opakowań** — Non-Goals PRD: produkt skupia się na zawartości apteczki, nie na dawkowaniu i nie na doradzaniu.
- **Kontrola danych odczytanych z ChPL i ulotek** — Non-Goals PRD: ryzyko błędnego odczytu przyjęte w MVP; przy większej skali wraca (zob. Open Roadmap Questions 2).
- **Wdrożenie zdalne, Dockerfile, health-check, sekrety, staging** — `context/deployment/deploy-plan.md`: świadoma decyzja o braku wdrożenia zdalnego na tym etapie; analiza z `infrastructure.md` pozostaje aktualną propozycją na później.
- **Obserwowalność (Actuator, śledzenie błędów, metryki)** — odłożona w `deploy-plan.md`; żaden NFR PRD jej nie wymusza, a cel tego kamienia milowego to informacja zwrotna, nie gotowość operacyjna.
- **Działanie offline, aplikacja natywna w sklepach, wiele wersji językowych, status wyrobu medycznego** — Non-Goals PRD (niefunkcjonalne).

## Milestone History

(Pusta — to pierwszy kamień milowy. Wpisy dopisuje zamknięcie kamienia milowego.)

## Done

(Pusta na starcie. `/10x-archive` dopisuje tu wpis — i przestawia status elementu na `done` — gdy archiwizowana zmiana ma pasujący `Change ID`.)
