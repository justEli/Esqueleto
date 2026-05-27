package me.justeli.esqueleto;

import java.sql.SQLException;

/**
 * @author Eli
 * @since April 13, 2021 (creation)
 */
public final class ExecuteQuery extends AbstractStatement {
    ExecuteQuery(Esqueleto sql, String statement, Object... replacements) {
        super(sql, statement, replacements);
    }

    /**
     * @return The row(s) requested in the query.
     */
    @Override
    ExecutionData execute() {
        try (
            var connection = sql.getConnection();
            var prepared = connection.prepareStatement(checkForIterable(statement, replacements))
        ) {
            parseReplacements(prepared, replacements);
            return new ExecutionData(prepared.executeQuery(), 0);
        }
        catch (SQLException exception) {
            Esqueleto.printError(exception, statement);
            return new ExecutionData(null, 0);
        }
    }
}
