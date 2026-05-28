package me.justeli.esqueleto;

import me.justeli.esqueleto.handler.SqlBiConsumer;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Eli
 * @since April 13, 2021 (creation)
 */
public final class ExecuteQuery extends AbstractStatement {
    ExecuteQuery(Esqueleto sql, String statement, Object... replacements) {
        super(sql, statement, replacements);
    }

    /// @param result The row(s) requested in the query.
    @Override
    void execute(SqlBiConsumer<ResultSet, Integer> result) {
        try (var connection = sql.getConnection()) {
            String replaced = parseIterable(statement, replacements);
            try (var prepared = connection.prepareStatement(replaced)) {
                parseReplacements(prepared, replacements);
                try (var data = prepared.executeQuery()) {
                    result.accept(data, 0);
                }
            }
        }
        catch (SQLException exception) {
            Esqueleto.printError(exception, statement);
        }
    }
}
