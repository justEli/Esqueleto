package me.justeli.esqueleto;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Eli
 * @since December 13, 2021 (creation)
 */
public final class ExecuteUpdate extends AbstractStatement {
    ExecuteUpdate(Esqueleto sql, String statement, Object... replacements) {
        super(sql, statement, replacements);
    }

    /// @return The inserted row(s).
    @Override
    ExecutionData execute() {
        try (
            var connection = sql.getConnection();
            var prepared = connection.prepareStatement(
                checkForIterable(statement, replacements),
                PreparedStatement.RETURN_GENERATED_KEYS
            )
        ) {
            parseReplacements(prepared, replacements);
            int rows = prepared.executeUpdate();

            return new ExecutionData(prepared.getGeneratedKeys(), rows);
        }
        catch (SQLException exception) {
            Esqueleto.printError(exception, statement);
            return new ExecutionData(null, 0);
        }
    }

    /// @return The total amount of successfully updated or inserted row(s).
    public int complete() {
        return execute().rows();
    }

    /// Queue onto a queued thread.
    public void queue() {
        sql.getConfig().getQueueService().submit(() -> complete(results -> {}));
    }

    /// Push onto an async thread.
    public void push() {
        sql.getConfig().getAsyncService().submit(() -> complete(results -> {}));
    }
}
