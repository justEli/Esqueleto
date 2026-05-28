package me.justeli.esqueleto;

import me.justeli.esqueleto.handler.SqlBiConsumer;
import me.justeli.esqueleto.handler.SqlConsumer;
import me.justeli.esqueleto.handler.SqlFunction;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Eli
 * @since December 13, 2021 (creation)
 */
public final class ExecuteUpdate extends AbstractStatement<UpdateResult> {
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

    private UpdateResult getUpdateResult(@NotNull ResultSet resultSet, long totalRows) throws SQLException {
        // does resultSet.getLong(1) also work for other drivers than MariaDB?
        long insertedId = resultSet.next()? resultSet.getLong(1) : 0L;
        return new UpdateResult(insertedId, totalRows);
    }

    @CheckReturnValue
    public <T> @NotNull Optional<T> complete(@NotNull SqlFunction<UpdateResult, T> handler) {
        AtomicReference<T> result = new AtomicReference<>();
        execute(((resultSet, totalRows) -> result.set(handler.apply(getUpdateResult(resultSet, totalRows)))));
        return Optional.ofNullable(result.get());
    }

    public void complete(@NotNull SqlConsumer<UpdateResult> handler) {
        execute((resultSet, totalRows) -> handler.accept(getUpdateResult(resultSet, totalRows)));
    }

    /// @return total rows and inserted id
    public UpdateResult complete() {
        AtomicReference<UpdateResult> result = new AtomicReference<>();
        execute((resultSet, totalRows) -> result.set(getUpdateResult(resultSet, totalRows)));
        return result.get();
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
