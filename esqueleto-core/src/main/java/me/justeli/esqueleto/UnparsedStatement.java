package me.justeli.esqueleto;

import org.jetbrains.annotations.CheckReturnValue;

/**
 * @author Eli
 * @since April 13, 2021 (creation)
 */
public final class UnparsedStatement {
    private final Esqueleto sql;
    private final String statement;

    UnparsedStatement(Esqueleto sql, String statement) {
        this.sql = sql;
        this.statement = statement;
    }

    @CheckReturnValue
    public ExecuteUpdate update() {
        return new ExecuteUpdate(sql, statement);
    }

    @CheckReturnValue
    public ExecuteQuery query() {
        return new ExecuteQuery(sql, statement);
    }

    /// @param replacements The replacements that will replace the question marks in the query.
    @CheckReturnValue
    public StatementBind bind(Object... replacements) {
        return new StatementBind(sql, statement, replacements);
    }
}
