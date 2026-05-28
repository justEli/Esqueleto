package me.justeli.esqueleto.handler;

import java.sql.SQLException;

/**
 * @author Eli
 * @since May 28, 2026 (creation)
 */
@FunctionalInterface
public interface SqlBiConsumer<T, U> {
    void accept(T t, U u) throws SQLException;
}
