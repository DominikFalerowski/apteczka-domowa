package com.dominikf.apteczka_domowa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class FlywayMigrationTests {

    @Autowired
    private Flyway flyway;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void appliesInitialSchemaMigration() {
        assertThat(flyway.info().current().getVersion().getVersion()).isEqualTo("1");
    }

    @Test
    void applicationConnectsAsRestrictedAppRole() {
        assertThat(jdbcTemplate.queryForObject("select current_user", String.class))
                .isEqualTo("apteczka_app");
        assertThat(jdbcTemplate.queryForObject(
                        "select count(*) from information_schema.schemata where schema_name = 'apteczka'",
                        Integer.class))
                .isEqualTo(1);
    }

    @Test
    void applicationRoleCannotRunDdl() {
        assertThatThrownBy(() -> jdbcTemplate.execute("create table apteczka.forbidden (id int)"))
                .rootCause()
                .hasMessageContaining("permission denied");
    }
}
