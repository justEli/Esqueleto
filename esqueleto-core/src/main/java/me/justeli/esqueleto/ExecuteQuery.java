package me.justeli.esqueleto;

import me.justeli.esqueleto.handler.SqlBiConsumer;
import me.justeli.esqueleto.handler.SqlConsumer;
import me.justeli.esqueleto.handler.SqlFunction;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Eli
 * @since April 13, 2021 (creation)
 */
public final class ExecuteQuery extends AbstractStatement<Results> {
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

    // note that totalRows is not implemented for executeQuery. so always use `(resultSet, _) -> ...`
    @CheckReturnValue
    public <T> @NotNull Optional<T> complete(@NotNull SqlFunction<Results, T> handler) {
        AtomicReference<T> result = new AtomicReference<>();
        execute(((resultSet, _) -> result.set(handler.apply(new Results(resultSet, sql)))));
        return Optional.ofNullable(result.get());
    }

    public void complete(@NotNull SqlConsumer<Results> handler) {
        execute((resultSet, _) -> handler.accept(new Results(resultSet, sql)));
    }
}
