package com.dominikf.apteczka_domowa;

import java.nio.file.Path;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

/**
 * Postgres z tym samym bootstrapem ról co compose.yaml. Celowo bez {@code @ServiceConnection}: ten
 * wstrzyknąłby superużytkownika kontenera i zatarł rozdział apteczka_app / apteczka_migrator, więc
 * podajemy tylko URL, a użytkownicy pochodzą z application.properties.
 */
@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    @Bean
    PostgreSQLContainer postgresContainer() {
        return new PostgreSQLContainer(DockerImageName.parse("postgres:18"))
                .withCopyFileToContainer(
                        MountableFile.forHostPath(Path.of("db/init/01-roles.sql")),
                        "/docker-entrypoint-initdb.d/01-roles.sql")
                .waitingFor(Wait.forLogMessage(".*database system is ready to accept connections.*\\n", 2));
    }

    @Bean
    DynamicPropertyRegistrar databaseUrl(PostgreSQLContainer postgres) {
        return registry -> {
            registry.add("spring.datasource.url", postgres::getJdbcUrl);
            registry.add("spring.flyway.url", postgres::getJdbcUrl);
        };
    }
}
