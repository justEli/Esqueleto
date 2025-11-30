package me.justeli.esqueleto;

import com.zaxxer.hikari.HikariDataSource;
import me.justeli.esqueleto.annotation.CheckReturnValue;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;

/* Eli @ November 18, 2020 (creation) */
/* Eli @ April 13, 2021 (rewrite) */
/* Eli @ December 2, 2022 (rewrite) */
/* Eli @ December 26, 2022 (rewrite) */
public final class Esqueleto
{
    private final HikariDataSource hikari;
    private final SqlConfig config;

    static final Logger LOGGER = LoggerFactory.getLogger(Esqueleto.class);

    public static EsqueletoBuilder create (@NotNull Consumer<SqlConfig> consumer)
    {
        var config = new SqlConfig();
        consumer.accept(config);

        return new EsqueletoBuilder(config);
    }

    public static Esqueleto start (@NotNull String database, @NotNull String username, @NotNull String password)
    {
        return new EsqueletoBuilder(new SqlConfig()).start(database, username, password);
    }

    public static Esqueleto start (@NotNull String database, @NotNull String username)
    {
        return new EsqueletoBuilder(new SqlConfig()).start(database, username);
    }

    Esqueleto (SqlConfig config, HikariDataSource source)
    {
        long startTime = System.nanoTime();
        this.hikari = source;
        this.config = config;

        config.getQueueService().submit(() -> Thread.currentThread().setName("EsqueletoQueuedService"));
        config.getAsyncService().submit(() -> Thread.currentThread().setName("EsqueletoAsyncService"));

        LOGGER.info("Opening SQL connection...");
        try (Connection connection = this.hikari.getConnection())
        {
            LOGGER.info(
                "Successfully opened SQL connection using {}, in {}ms.",
                connection.getMetaData().getDriverName(),
                (System.nanoTime() - startTime) / 1000000
            );
        }
        catch (SQLException exception)
        {
            printError(exception, null);
        }
    }

    /**
     * @param statement SQL statement that contains question marks (?) as variables.
     */
    @CheckReturnValue
    @NotNull
    public UnparsedStatement statement (@Language("SQL") @NotNull String statement)
    {
        return new UnparsedStatement(this, statement);
    }

    /**
     * Close the SQL connection of the database.
     */
    public void close ()
    {
        if (this.hikari == null)
            return;

        this.hikari.close();
    }

    static void printError (@NotNull SQLException exception, @Nullable String query)
    {
        boolean validQuery = query != null && !query.isEmpty();

        if (!validQuery)
        {
            LOGGER.error("An error occurred: {}", exception.getMessage());
            return;
        }

        StringBuilder message = new StringBuilder("An error occurred trying to execute an SQL statement: ")
            .append(exception.getMessage());

        message.append("\n")
            .append("```SQL\n")
            .append(query);

        if (!query.endsWith("\n"))
        {
            message.append("\n");
        }

        message.append("```");

        LOGGER.error(message.toString());
    }

    Connection getConnection ()
    throws SQLException
    {
        if (this.hikari == null)
        {
            throw new SQLException("Unable to get a connection from Hikari pool.");
        }

        return this.hikari.getConnection();
    }

    SqlConfig getConfig ()
    {
        return this.config;
    }
}
