package me.justeli.esqueleto;

import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* Eli @ December 26, 2022 (creation) */
public final class EsqueletoBuilder
{
    private final SqlConfig config;

    EsqueletoBuilder (SqlConfig config)
    {
        this.config = config;
    }

    /**
     * Create a new SQL connection to database on <i>localhost:3306</i>.
     * @param database The name of the database to connect to.
     * @param username The username of the database.
     * @param password The password of the database.
     */
    public Esqueleto start (@NotNull String database, @NotNull String username, @NotNull String password)
    {
        this.config.setPassword(password);
        return start(database, username);
    }

    public Esqueleto start (@NotNull String database, @NotNull String username)
    {
        this.config.setUsername(username);
        this.config.setDatabase(database);
        return start();
    }

    /**
     * Open an SQL connection without any preset settings.
     */
    @Nullable
    public Esqueleto start ()
    {
        try
        {
            return new Esqueleto(
                this.config,
                new HikariDataSource(this.config)
            );
        }
        catch (RuntimeException exception)
        {
            Esqueleto.LOGGER.error("No dependency was detected for the SQL driver!");
            if (this.config.isDebug())
            {
                Esqueleto.LOGGER.error("[DEBUG]", exception);
            }
            return null;
        }
    }
}
