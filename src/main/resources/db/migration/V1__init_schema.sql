-- Fundament: schemat aplikacji i uprawnienia dla konta runtime. Bez tabel (dojdą w kolejnych migracjach).
-- Wykonywana jako apteczka_migrator, więc domyślne uprawnienia dotyczą tabel tworzonych przez tę rolę.

CREATE SCHEMA IF NOT EXISTS apteczka;

GRANT USAGE ON SCHEMA apteczka TO apteczka_app;

ALTER DEFAULT PRIVILEGES IN SCHEMA apteczka
    GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO apteczka_app;

ALTER DEFAULT PRIVILEGES IN SCHEMA apteczka
    GRANT USAGE, SELECT ON SEQUENCES TO apteczka_app;
