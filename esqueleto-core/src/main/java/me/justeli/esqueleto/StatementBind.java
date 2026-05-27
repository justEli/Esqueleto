package me.justeli.esqueleto;

import org.jetbrains.annotations.CheckReturnValue;

/**
 * @author Eli
 * @since July 19, 2024 (creation)
 */
public final class StatementBind {
    private final Esqueleto sql;
    private final String statement;
    private final Object[] replacements;

    StatementBind(Esqueleto sql, String statement, Object... replacements) {
        this.sql = sql;
        this.statement = statement;
        this.replacements = replacements;
    }

    @CheckReturnValue
    public ExecuteUpdate update() {
        return new ExecuteUpdate(sql, statement, replacements);
    }

    @CheckReturnValue
    public ExecuteQuery query() {
        return new ExecuteQuery(sql, statement, replacements);
    }
}
