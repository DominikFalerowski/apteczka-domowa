---
project: "Apteczka Domowa"
version: 1
status: draft
created: 2026-09-16
context_type: greenfield
product_type: web-app
target_scale:
  users: small
  qps: low             # obecnie: autor + farmaceutka; docelowo większy wolumen (nieokreślony)
  data_volume: small   # obecnie: autor + farmaceutka; docelowo większy wolumen (nieokreślony)
timeline_budget:
  mvp_weeks: 3
  hard_deadline: 2026-12-06
  after_hours_only: true
---

# PRD: Apteczka Domowa

## Vision & Problem Statement

Domowa apteczka zwykle leży w szufladzie bez żadnej ewidencji. Osoba, która nią zarządza, nie wie dokładnie, co w niej jest: w aptece nie pamięta, jakie leki ma już w domu, i kupuje kolejny z tą samą substancją czynną pod inną nazwą handlową; gdy ktoś w domu choruje, okazuje się, że potrzebny lek jest przeterminowany; przy porządkowaniu szuflady odkrywa zalegające przeterminowane leki i duplikaty. Kosztuje to pieniądze (zakup leków, które już są w domu, i wyrzucanie przeterminowanych), niesie ryzyko podwójnej dawki tej samej substancji oraz sprawia, że lek jest bezużyteczny w chwili, gdy jest potrzebny.

Obecne sposoby (kartka, notatki, aplikacje z przypomnieniami) porównują leki po nazwach handlowych, a nie po substancjach czynnych, więc nie wyłapują duplikatów. Skupiają się też na dawkowaniu, a nie na tym, co jest w apteczce i do kiedy leki są ważne. Przy setkach domostw krytyczna staje się aktualność danych z rejestru, a dane odczytane z ChPL i ulotek (ważność po otwarciu) wymagają kontroli, bo błąd dotyka wielu domów.

## User & Persona

**Primary persona: osoba zarządzająca apteczką w gospodarstwie domowym.** Kupuje leki i pilnuje szuflady z lekami, z której korzystają również pozostali domownicy. Sięga po produkt w trzech momentach: w aptece przy zakupie (czy mam już ten lek / tę substancję?), w domu, gdy ktoś choruje (czy lek jest ważny?), oraz przy porządkowaniu szuflady (co jest przeterminowane, co się dubluje?).

## Success Criteria

### Primary
- Przepływ v1 działa od początku do końca:
  1. Użytkownik otwiera aplikację i loguje się / rejestruje.
  2. Widzi ekran dla użytkownika bez apteczki z opcją „załóż apteczkę” (opcja „dołącz kodem” — v2).
  3. Zakłada apteczkę i zostaje jej właścicielem.
  4. Dodaje lek wybrany z Rejestru Produktów Leczniczych (substancja czynna A, nazwa handlowa B, droga podania X) i wpisuje jego termin ważności.
  5. Dodaje drugi lek z rejestru (substancja czynna A, nazwa handlowa C, ta sama droga podania X).
  6. Po dodaniu dostaje komunikat, że w apteczce jest już lek B z substancją czynną A.
  7. Na liście oba leki mają żółtą ikonę ostrzeżenia (ta sama substancja czynna A i droga podania X).
  8. Lek przeterminowany jest podświetlony na liście na czerwono; lek, którego termin ważności (z uwzględnieniem ważności po otwarciu) kończy się w ciągu 30 dni, jest podświetlony na bardzo jasny czerwony.
  9. Użytkownik oddaje lek do utylizacji i oznacza go w aplikacji jako zarchiwizowany.

### Secondary
- Użytkownik wprowadza całą domową apteczkę (leki dostępne w rejestrze) w jednej sesji.

### Guardrails
- Ostrzeżenia o duplikatach substancji czynnej są wiarygodne — brak fałszywych i pominiętych ostrzeżeń dla leków wybranych z rejestru.
- Aplikacja nie udaje porady medycznej — informuje, ale nie zaleca leczenia.
- Zawartość apteczki widzą wyłącznie jej członkowie.

## User Stories

### US-01: Ostrzeżenie o powtórzonej substancji czynnej

- **Given** zalogowany użytkownik z apteczką, w której jest aktywny lek o nazwie handlowej B z substancją czynną A i drogą podania X
- **When** dodaje z rejestru lek o nazwie handlowej C z substancją czynną A i drogą podania X
- **Then** lek C zostaje dodany do apteczki, użytkownik dostaje komunikat, że w apteczce jest już lek B z substancją czynną A, a na liście oba leki (B i C) mają żółtą ikonę ostrzeżenia

#### Acceptance Criteria
- Komunikat wskazuje nazwę handlową leku już obecnego w apteczce (B) oraz substancję czynną (A).
- Ostrzeżenie nie blokuje dodania leku — komunikat pojawia się po dodaniu.
- Zarchiwizowane leki nie są brane pod uwagę: jeśli B jest zarchiwizowany, dodanie C nie wywołuje komunikatu ani żółtej ikony.
- Jeśli C ma substancję czynną A, ale inną drogę podania niż B, komunikat i żółta ikona się nie pojawiają.

### US-02: Wyróżnienie leków z kończącym się terminem i archiwizacja

- **Given** użytkownik z apteczką, w której jest lek przeterminowany oraz lek, którego termin ważności kończy się w ciągu 30 dni
- **When** otwiera listę leków
- **Then** lek przeterminowany jest wyróżniony na czerwono, a lek z kończącym się terminem — na bardzo jasny czerwony

#### Acceptance Criteria
- Lek z terminem ważności dalszym niż 30 dni nie jest wyróżniony.
- Termin podany na opakowaniu jako miesiąc/rok (np. „EXP 03/2027”) jest ważny do końca tego miesiąca; lek jest przeterminowany od pierwszego dnia kolejnego miesiąca.
- Termin podany z pełną datą dzienną jest ważny do tego dnia włącznie; lek jest przeterminowany od dnia następnego.
- Jeśli lek ma zapisaną datę otwarcia i znaną ważność po otwarciu, wyróżnienie liczone jest od wcześniejszego z terminów: terminu z opakowania albo daty otwarcia + ważności po otwarciu.
- Po zarchiwizowaniu leku znika on z listy aktywnych leków.
- Po zarchiwizowaniu leku ostrzeżenia o powtórzonej substancji czynnej są przeliczane — zarchiwizowany lek się nie liczy.

## Functional Requirements

### Konto i apteczka
- FR-001: Użytkownik może założyć konto i zalogować się. Priority: must-have
  > Socrates: Rozważono kontrargumenty „konto zniechęca na starcie” i „pełne konta to duży koszt”. Rozstrzygnięcie: bez kontrargumentu — zostaje bez zmian.
- FR-002: Zalogowany użytkownik bez apteczki może założyć apteczkę domostwa i zostać jej właścicielem. Priority: must-have
  > Socrates: Kontrargument: „zbędny krok — apteczka mogłaby powstawać automatycznie przy rejestracji”. Rozstrzygnięcie: zostaje ekran wyboru „załóż / dołącz”, bo zapobiega tworzeniu zbędnych apteczek; w MVP dostępna tylko opcja „załóż” (dołączanie — v2).
- FR-003: Właściciel może wygenerować kod zaproszenia do apteczki. Priority: nice-to-have
  > Socrates: Kontrargument: „zapraszanie nie daje szybkiej wartości — w pierwszych tygodniach wystarczy jeden użytkownik”. Rozstrzygnięcie: zmieniono na nice-to-have; MVP dla jednej osoby, współdzielenie w v2.
- FR-004: Zalogowany użytkownik — także taki, który ma już apteczkę — może dołączyć do apteczki, wpisując kod zaproszenia. Priority: nice-to-have
  > Socrates: Kontrargument: „pułapka dla tych, którzy już założyli apteczkę — dołączać może tylko użytkownik bez apteczki”. Rozstrzygnięcie (odpowiedź na pytanie otwarte): użytkownik z apteczką też może dołączyć do innej i wybiera apteczkę z listy (FR-020). Priorytet zmieniony na nice-to-have jako konsekwencja zmiany FR-003.
- FR-005: Właściciel może usunąć domownika z apteczki. Priority: nice-to-have
  > Socrates: Kontrargument: „musi wejść razem z zaproszeniami — bez tego nie da się odciąć dostępu”. Rozstrzygnięcie: zostaje nice-to-have; wchodzi w tym samym wydaniu co FR-003.
- FR-006: Domownik może opuścić apteczkę. Priority: nice-to-have
  > Socrates: Kontrargument: „musi wejść razem z dołączaniem — inaczej osoba w złej apteczce nie ma wyjścia”. Rozstrzygnięcie: zostaje nice-to-have; wchodzi w tym samym wydaniu co FR-004.
- FR-020: Użytkownik należący do kilku apteczek może wybrać apteczkę z listy swoich apteczek. Priority: nice-to-have
  > Socrates: Wymaganie powstało z odpowiedzi na pytanie otwarte o FR-004 (v2).

### Leki
- FR-007: Członek apteczki może wyszukać lek w Rejestrze Produktów Leczniczych po nazwie handlowej i dodać go do apteczki; substancja czynna uzupełnia się z rejestru. Priority: must-have
  > Socrates: Kontrargument: „leki spoza rejestru (suplementy, leki z zagranicy) nie wejdą — kłóci się z celem »cała szuflada w jednej sesji«”. Rozstrzygnięcie: zostaje — w MVP tylko leki z rejestru; „cała szuflada” oznacza leki z rejestru; ręczne dodawanie jako FR-016 (nice-to-have).
- FR-008: Członek apteczki może ręcznie wpisać termin ważności dodawanego leku (miesiąc/rok albo pełną datę dzienną). Priority: must-have
  > Socrates: Rozważono kontrargumenty „żmudne przy całej szufladzie” i „błędna data daje fałszywe poczucie bezpieczeństwa”. Rozstrzygnięcie: bez kontrargumentu — zostaje bez zmian.
- FR-009: Członek apteczki może przeglądać listę aktywnych leków w apteczce. Priority: must-have
  > Socrates: Kontrargument: „sama lista nie wystarczy — bez wyszukiwania trudno szybko sprawdzić substancję w aptece albo w chorobie”. Rozstrzygnięcie: zostaje; dodano FR-017 (wyszukiwanie, must-have).
- FR-010: Członek apteczki może edytować termin ważności i datę otwarcia leku w apteczce; nazwa handlowa i substancja czynna (z rejestru) nie są edytowalne. Priority: must-have
  > Socrates: Kontrargument: „edycja nazwy/substancji psuje wiarygodność ostrzeżeń”. Rozstrzygnięcie: zawężono edycję do terminu ważności i daty otwarcia.
- FR-011: Członek apteczki może zarchiwizować lek zamiast go usuwać; zarchiwizowany lek znika z listy aktywnych leków. Priority: must-have
  > Socrates: Kontrargument: „pomyłki nie da się cofnąć, bo przegląd archiwum jest poza MVP”. Rozstrzygnięcie: zostaje bez zmian — ryzyko zaakceptowane w MVP.
- FR-012: Członek apteczki może przeglądać zarchiwizowane leki (historię). Priority: nice-to-have
  > Socrates: Kontrargument: „historia niewidoczna w MVP”. Rozstrzygnięcie: zostaje nice-to-have — historia gromadzi się od początku, przegląd w v2.
- FR-016: Członek apteczki może ręcznie dodać lek spoza Rejestru Produktów Leczniczych. Priority: nice-to-have
  > Socrates: Wymaganie powstało z rozstrzygnięcia kontrargumentu do FR-007.
- FR-017: Członek apteczki może wyszukać lek na liście apteczki po nazwie handlowej lub substancji czynnej. Priority: must-have
  > Socrates: Wymaganie powstało z rozstrzygnięcia kontrargumentu do FR-009.
- FR-019: Członek apteczki może zapisać datę otwarcia leku; ważność po otwarciu pochodzi z ChPL lub ulotki danego leku, a obowiązującym terminem ważności jest wcześniejszy z dwóch: termin z opakowania albo data otwarcia + ważność po otwarciu. Priority: must-have
  > Socrates: Wymaganie powstało z rozstrzygnięcia kontrargumentu do FR-015 („30 dni nie pasuje do każdego leku — syropy, krople mają krótszą ważność po otwarciu”).

### Ostrzeżenia
- FR-013: Członek apteczki po dodaniu leku dostaje komunikat z nazwą handlową aktywnego leku w apteczce, który ma tę samą substancję czynną i tę samą drogę podania. Priority: must-have
  > Socrates: Kontrargument: „ta sama substancja to nie zawsze problem (np. maść i tabletka)”. Rozstrzygnięcie: zawężono do tej samej drogi podania — do potwierdzenia z farmaceutką.
- FR-014: Członek apteczki widzi na liście żółte oznaczenie aktywnych leków, które mają wspólną substancję czynną i tę samą drogę podania z innym aktywnym lekiem. Priority: must-have
  > Socrates: Kontrargument: „stały szum bez wyciszenia — świadomie trzymane duplikaty zawsze będą żółte”. Rozstrzygnięcie: zostaje; wyciszenie jako FR-018 (nice-to-have). Reguła drogi podania z FR-013 zastosowana dla spójności.
- FR-015: Członek apteczki widzi na liście czerwone wyróżnienie leków przeterminowanych oraz bardzo jasne czerwone wyróżnienie leków, których termin ważności kończy się w ciągu 30 dni. Priority: must-have
  > Socrates: Kontrargument: „30 dni nie pasuje do każdego leku (syropy, krople po otwarciu)”. Rozstrzygnięcie: zostaje; dodano FR-019 (ważność po otwarciu, must-have).
- FR-018: Członek apteczki może oznaczyć duplikat substancji czynnej jako zamierzony, aby wyciszyć jego ostrzeżenie. Priority: nice-to-have
  > Socrates: Wymaganie powstało z rozstrzygnięcia kontrargumentu do FR-014.

## Non-Functional Requirements

- Dane o lekach w apteczce nie są dostępne dla nikogo poza członkami tej apteczki i nie są przekazywane podmiotom trzecim.
- Wyniki wyszukiwania leku w Rejestrze Produktów Leczniczych pojawiają się w czasie < 1 s od wpisania frazy (czas odczuwalny przez użytkownika).
- Każde ostrzeżenie (powtórzona substancja czynna, termin ważności) jest przedstawione jako informacja, a nie zalecenie medyczne.
- MVP jest w pełni używalne w przeglądarce na telefonach z dwoma najpopularniejszymi mobilnymi systemami operacyjnymi.

## Business Logic

Aplikacja wykrywa wśród aktywnych leków apteczki powtórzoną substancję czynną (przy tej samej drodze podania) i ocenia termin ważności leku z uwzględnieniem ważności po otwarciu z ChPL lub ulotki.

**Powtórzona substancja czynna.** Reguła korzysta z substancji czynnych i drogi podania leków wybranych z Rejestru Produktów Leczniczych (rejestr podaje drogę podania każdego leku jako osobną, wyraźnie oznaczoną informację) oraz ze statusu leku (aktywny / zarchiwizowany). Ostrzeżenie powstaje, gdy dwa aktywne leki mają co najmniej jedną wspólną substancję czynną i tę samą drogę podania — dotyczy to również leków złożonych (np. „paracetamol + kofeina” i „paracetamol”). Drogi podania porównuje się wprost według wartości z rejestru, bez dodatkowego grupowania. Leki zarchiwizowane są pomijane. Użytkownik widzi wynik jako komunikat tuż po dodaniu leku (z nazwą handlową leku już obecnego w apteczce) oraz jako żółte oznaczenie obu leków na liście. Ostrzeżenie nie blokuje dodania leku.

**Ocena terminu ważności.** Reguła korzysta z terminu ważności z opakowania (wpisanego ręcznie), opcjonalnej daty otwarcia oraz ważności po otwarciu podanej w Charakterystyce Produktu Leczniczego (ChPL) lub ulotce danego leku — oba dokumenty są dostępne w Rejestrze Produktów Leczniczych. Termin podany na opakowaniu jako miesiąc/rok oznacza ważność do końca tego miesiąca; termin z pełną datą dzienną oznacza ważność do tego dnia włącznie. Obowiązującym terminem jest wcześniejszy z dwóch: termin z opakowania albo data otwarcia + ważność po otwarciu (o ile ważność po otwarciu jest znana — zob. US-02). Wynikiem jest status leku: przeterminowany (wyróżnienie czerwone), kończący się w ciągu 30 dni (wyróżnienie bardzo jasnym czerwonym) albo ważny (bez wyróżnienia). Użytkownik widzi status na liście aktywnych leków.

Obie reguły mają charakter informacyjny — aplikacja nie zaleca leczenia ani nie zastępuje farmaceuty lub lekarza.

## Access Control

- **Dostęp:** logowanie do konta. Niezalogowany użytkownik widzi wyłącznie ekran logowania / rejestracji; żadne dane apteczki nie są dostępne bez zalogowania.
- **Model docelowy:** jedna wspólna apteczka na domostwo. Apteczka należy do domostwa, a nie do pojedynczego użytkownika — wszyscy domownicy korzystają z tej samej apteczki.
- **W MVP:** apteczka ma jednego członka — właściciela, który ją założył. Współdzielenie z domownikami (kod zaproszenia, dołączanie, usuwanie i opuszczanie apteczki) jest poza MVP (FR-003–FR-006).
- **Pierwsze logowanie (użytkownik bez apteczki):** ekran wyboru „załóż apteczkę” (zakładający zostaje właścicielem) / „wpisz kod” (dołącza jako domownik). W MVP dostępna jest tylko opcja „załóż apteczkę”.
- **Dołączanie (v2):** kod zaproszenia generowany przez właściciela, wpisywany po zalogowaniu. Link zaproszenia — później. Do innej apteczki może dołączyć także użytkownik, który ma już własną; użytkownik należący do kilku apteczek wybiera apteczkę z listy (FR-020).
- **Role → uprawnienia:**

| Uprawnienie | Domownik (v2) | Właściciel | W MVP |
| --- | --- | --- | --- |
| Przeglądanie i wyszukiwanie leków w apteczce | ✓ | ✓ | ✓ |
| Dodawanie leków i edycja terminu ważności / daty otwarcia | ✓ | ✓ | ✓ |
| Archiwizowanie leków (zamiast usuwania; historia zostaje) | ✓ | ✓ | ✓ |
| Opuszczenie apteczki (FR-006) | ✓ | — | — |
| Generowanie kodu zaproszenia (FR-003) | — | ✓ | — |
| Usuwanie domowników z apteczki (FR-005) | — | ✓ | — |
| Wybór apteczki z listy swoich apteczek (FR-020) | ✓ | ✓ | — |

## Non-Goals

### Funkcjonalne
- **Brak powiadomień o terminie ważności** — w MVP status terminu widać tylko na liście; powiadomienia w v2 (cięcie zakresu z etapu 3).
- **Brak współdzielenia apteczki z domownikami** (zaproszenia, dołączanie, usuwanie i opuszczanie apteczki, przynależność do kilku apteczek; FR-003–FR-006, FR-020) — MVP dla jednej osoby; współdzielenie w v2.
- **Brak dołączania przez link zaproszenia** — docelowo wystarczy kod (cięcie zakresu z etapu 3).
- **Brak automatycznej aktualizacji danych z rejestru** — dane wczytane jednorazowo (cięcie zakresu z etapu 3).
- **Brak ręcznego dodawania leków spoza rejestru** (FR-016) — w MVP tylko leki z Rejestru Produktów Leczniczych, żeby ostrzeżenia były wiarygodne.
- **Brak wyciszania zamierzonych duplikatów** (FR-018) — ostrzeżenie zawsze widoczne w MVP.
- **Brak przeglądu archiwum** (FR-012) — historia gromadzi się od początku, przegląd w v2.
- **Brak sprawdzania interakcji między lekami** — wymaga wiarygodnego źródła danych; poza MVP.
- **Brak dziennika przyjętych leków i przypomnień o dawkach** — produkt skupia się na zawartości apteczki, nie na dawkowaniu.
- **Brak rekomendowania leków i zamienników** — aplikacja nie podpowiada, co kupić ani czym zastąpić lek.
- **Brak skanowania opakowań** (kod kreskowy, data z aparatu) — termin ważności wpisywany ręcznie.

### Niefunkcjonalne
- **Brak gwarancji pełnego działania offline** — MVP może wymagać połączenia z internetem.
- **Brak natywnej aplikacji publikowanej w sklepach z aplikacjami mobilnymi** — MVP to aplikacja webowa używana w przeglądarce telefonu; obecnie brak planów budowy natywnej aplikacji.
- **Brak wielu wersji językowych** — MVP tylko po polsku.
- **Brak statusu wyrobu medycznego** — bez certyfikacji i zgodności z regulacjami dla wyrobów medycznych; komunikaty mają charakter informacyjny.
- **Brak kontroli danych odczytanych z ChPL i ulotek** — w MVP ważność po otwarciu nie jest weryfikowana; ryzyko błędnego odczytu zaakceptowane (przy większej skali kontrola staje się krytyczna — zob. Vision).

## Open Questions

1. **Docelowa skala produktu.** Obecnie: autor + farmaceutka; docelowo większy wolumen, na razie nieokreślony. — Owner: użytkownik.

### Rozstrzygnięte (2026-09-16)

- Lek z terminem kończącym się w ciągu 30 dni oznaczamy bardzo jasnym czerwonym.
- Termin podany jako miesiąc/rok jest ważny do końca podanego miesiąca.
- Termin z pełną datą dzienną jest ważny do tego dnia włącznie (aplikacja przyjmuje oba formaty).
- (v2) Użytkownik, który ma już apteczkę, może dołączyć do innej; wybiera apteczkę z listy (FR-020).
- Rejestr Produktów Leczniczych podaje drogę podania dla każdego leku. _(Wcześniejsze ustalenie o grupowaniu wartości dróg podania — nieaktualne, zob. dawne pytanie 2.)_
- _(Nieaktualne, zob. dawne pytanie 1.)_ Rejestr Produktów Leczniczych podaje postać leku; ważność po otwarciu wynika z zestawienia postać → ważność po otwarciu (np. krople do nosa — 3 miesiące).
- Natywna aplikacja mobilna — obecnie brak planów.
- Skala: obecnie autor + farmaceutka.
- (dawne pytanie 1) Ważność po otwarciu pochodzi z ChPL i ulotki danego leku — zestawienie postać → ważność po otwarciu od farmaceutki nie jest potrzebne.
- (dawne pytanie 2) Droga podania jest wyraźnie oznaczona w danych rejestru i porównywana wprost — lista grup dróg podania nie jest potrzebna.
- Dane odczytane z ChPL i ulotek nie są kontrolowane w MVP — ryzyko błędnego odczytu zaakceptowane.
