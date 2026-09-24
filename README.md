# Apteczka Domowa

Aplikacja do prowadzenia domowej apteczki. Leki dodaje się z Rejestru Produktów Leczniczych, a aplikacja pilnuje dwóch rzeczy: ostrzega, gdy w apteczce są dwa leki z tą samą substancją czynną (i tą samą drogą podania), oraz wyróżnia leki przeterminowane lub z kończącym się terminem ważności, także po otwarciu opakowania.

Projekt jest we wczesnej fazie budowy.

## Uruchomienie lokalne

Wymagania: JDK 25 i uruchomiony Docker (Spring sam startuje kontener Postgresa). Node jest potrzebny tylko do `npm run dev` (wersja w `frontend/.nvmrc`) — Maven pobiera własny Node do `frontend/node/`.

Aplikacja razem z frontem (Maven buduje front i Spring serwuje go jako pliki statyczne):

```bash
./mvnw spring-boot:run
```

Aplikacja działa pod `http://localhost:8080`.

Praca nad frontem z przeładowaniem na żywo (w drugim terminalu, obok działającego Springa — żądania `/api` idą przez proxy na port 8080):

```bash
cd frontend && npm run dev
```

Serwer deweloperski działa pod `http://localhost:5173`. Zależności (`frontend/node_modules`) instaluje pierwszy build Mavena albo `npm install` w `frontend/`.

Pełne sprawdzenie przed wypchnięciem zmian (build, testy, formatowanie i lintowanie backendu oraz frontu):

```bash
./mvnw verify
```

Żeby pominąć budowanie frontu (np. przy pracy wyłącznie nad backendem), dodaj `-Dfrontend.skip` do dowolnej komendy Mavena, np. `./mvnw spring-boot:run -Dfrontend.skip`. Spring serwuje wtedy ostatni zbudowany front, jeśli istnieje.
