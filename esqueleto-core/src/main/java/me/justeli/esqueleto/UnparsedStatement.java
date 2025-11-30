package me.justeli.esqueleto;

import me.justeli.esqueleto.annotation.CheckReturnValue;

/* Eli @ April 13, 2021 (creation) */
public final class UnparsedStatement
{
    private final Esqueleto esqueleto;
    private final String statement;

    UnparsedStatement (Esqueleto esqueleto, String statement)
    {
        this.esqueleto = esqueleto;
        this.statement = statement;
    }

    @CheckReturnValue
    public ExecuteUpdate update ()
    {
        return new ExecuteUpdate(this.esqueleto, this.statement);
    }

    @CheckReturnValue
    public ExecuteQuery query ()
    {
        return new ExecuteQuery(this.esqueleto, this.statement);
    }

    /**
     * @param replacements The replacements that will replace the question marks in the query.
     */
    @CheckReturnValue
    public StatementBind bind (Object... replacements)
    {
        return new StatementBind(this.esqueleto, this.statement, replacements);
    }
}
