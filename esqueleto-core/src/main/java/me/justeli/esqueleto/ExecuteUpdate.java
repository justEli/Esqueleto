package me.justeli.esqueleto;

import me.justeli.esqueleto.handler.SqlBiConsumer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Eli
 * @since December 13, 2021 (creation)
 */
public final class ExecuteUpdate extends AbstractStatement {
    ExecuteUpdate(Esqueleto sql, String statement, Object... replacements) {
        super(sql, statement, replacements);
    }

    /// @param result The inserted row(s).
    @Override
    void execute(SqlBiConsumer<ResultSet, Integer> result) {
        try (var connection = sql.getConnection()) {
            String replaced = parseIterable(statement, replacements);
            try (var prepared = connection.prepareStatement(replaced, PreparedStatement.RETURN_GENERATED_KEYS)) {
                parseReplacements(prepared, replacements);
                int rows = prepared.executeUpdate();
                try (var data = prepared.getGeneratedKeys()) {
                    result.accept(data, rows);
                }
            }
        }
        catch (SQLException exception) {
            Esqueleto.printError(exception, statement);
        }
    }

    /// @param result The (total amount of) successfully updated or inserted row(s).
    public void complete(SqlBiConsumer<ResultSet, Integer> result) {
        execute(result);
    }

    public int complete() {
        AtomicInteger rows = new AtomicInteger(0);
        execute((_, totalRows) -> rows.set(totalRows));
        return rows.get();
    }

    /// Queue onto a queued thread.
    public void queue() {
        sql.getConfig().getQueueService().submit(() -> execute((_, _) -> {}));
    }

    /// Push onto an async thread.
    public void push() {
        sql.getConfig().getAsyncService().submit(() -> execute((_, _) -> {}));
    }
}
