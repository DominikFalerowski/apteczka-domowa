-- Lokalny bootstrap ról bazodanowych (dev/test). Wykonywany przez superużytkownika przy pierwszym
-- starcie kontenera Postgres (docker-entrypoint-initdb.d), nie przez Flyway.
--   apteczka_migrator - używany wyłącznie przez Flyway (DDL, właściciel schematu)
--   apteczka_app      - konto runtime aplikacji (tylko DML, uprawnienia nadaje V1)
-- Hasła są wyłącznie deweloperskie; w innym środowisku nadaj własne przez sekrety.

CREATE ROLE apteczka_migrator LOGIN PASSWORD 'apteczka_migrator_dev';
CREATE ROLE apteczka_app LOGIN PASSWORD 'apteczka_app_dev';

DO $$
BEGIN
    EXECUTE format('GRANT CREATE ON DATABASE %I TO apteczka_migrator', current_database());
END
$$;
