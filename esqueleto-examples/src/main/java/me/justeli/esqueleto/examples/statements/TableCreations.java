package me.justeli.esqueleto.examples.statements;

import me.justeli.esqueleto.examples.Main;

/**
 * @author Eli
 * @since December 28, 2022 (creation)
 */
public final class TableCreations {
    public TableCreations(Main main) {
        main.sql().statement("""
            CREATE TABLE IF NOT EXISTS TestTable (
                id   INT,
                data BLOB NOT NULL
            )"""
        ).update().queue();

        main.sql().statement("""
            CREATE TABLE IF NOT EXISTS UuidTable (
                added_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                uuid     BINARY(16) NOT NULL,
                message  VARCHAR(32) NOT NULL
            )"""
        ).update().complete();
    }
}
