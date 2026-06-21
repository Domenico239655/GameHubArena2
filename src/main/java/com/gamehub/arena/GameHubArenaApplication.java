package com.gamehub.arena;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GameHubArenaApplication {

    public static void main(String[] args) {

        SpringApplication.run(GameHubArenaApplication.class, args);
    }

    @org.springframework.context.annotation.Bean
    public org.springframework.boot.CommandLineRunner runDbMigration(org.springframework.jdbc.core.JdbcTemplate jdbcTemplate) {
        return args -> {
            try {
                jdbcTemplate.execute("ALTER TABLE tournament ALTER COLUMN description TYPE TEXT;");
                System.out.println("✅ DATABASE AGGIORNATO: colonna description impostata a TEXT");
            } catch (Exception e) {
                System.out.println("⚠️ Nota database: " + e.getMessage());
            }
        };
    }
}
