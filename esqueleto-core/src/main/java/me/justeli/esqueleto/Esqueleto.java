package me.justeli.esqueleto;

import com.zaxxer.hikari.HikariDataSource;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;

/**
 * @author Eli
 * @since November 18, 2020 (creation); April 13, 2021 (rewrite); December 2, 2022 (rewrite); December 26, 2022 (rewrite)
 */
public final class Esqueleto {
    private final HikariDataSource hikari;
    private final SqlConfig config;

    static final Logger LOGGER = LoggerFactory.getLogger(Esqueleto.class);

    public static Esqueleto start(@NotNull Consumer<SqlConfig> consumer) {
        var config = new SqlConfig();
        consumer.accept(config);

        try {
            return new Esqueleto(config);
        }
        catch (RuntimeException exception) {
            Esqueleto.LOGGER.error("No dependency was detected for the SQL driver: {}", exception.getMessage());
            return null;
        }
    }

    Esqueleto(SqlConfig config) {
        long startTime = System.nanoTime();
        this.hikari = new HikariDataSource(config);
        this.config = config;

        config.getQueueService().submit(() -> Thread.currentThread().setName("EsqueletoQueuedService"));
        config.getAsyncService().submit(() -> Thread.currentThread().setName("EsqueletoAsyncService"));

        LOGGER.info("Opening SQL connection...");
        try (Connection connection = hikari.getConnection()) {
            LOGGER.info(
                "Successfully opened SQL connection using {}, in {}ms.",
                connection.getMetaData().getDriverName(),
                (System.nanoTime() - startTime) / 1000000
            );
        }
        catch (SQLException exception) {
            printError(exception, null);
        }
    }

    /// @param statement SQL statement that contains question marks (?) as variables.
    @CheckReturnValue
    public @NotNull UnparsedStatement statement(@Language("SQL") @NotNull String statement) {
        return new UnparsedStatement(this, statement);
    }

    /// Close the SQL connection of the database.
    public void close() {
        if (hikari == null) {
            return;
        }

        hikari.close();
    }

    static void printError(@NotNull SQLException exception, @Nullable String query) {
        if (query == null || query.isEmpty()) {
            LOGGER.error("An error occurred: {}", exception.getMessage());
            return;
        }

        StringBuilder message = new StringBuilder("An error occurred trying to execute an SQL statement: ")
            .append(exception.getMessage());

        message.append("\n").append("```SQL\n").append(query);
        if (!query.endsWith("\n")) {
            message.append("\n");
        }
        message.append("```");

        LOGGER.error(message.toString());
    }

    Connection getConnection() throws SQLException {
        if (hikari == null) {
            throw new SQLException("Unable to get a connection from Hikari pool.");
        }

        return hikari.getConnection();
    }

    SqlConfig getConfig() {
        return config;
    }
}
