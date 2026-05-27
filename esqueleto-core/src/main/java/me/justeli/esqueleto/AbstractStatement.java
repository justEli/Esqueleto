package me.justeli.esqueleto;

import me.justeli.esqueleto.binary.Binary;
import me.justeli.esqueleto.handler.SqlConsumer;
import me.justeli.esqueleto.handler.SqlFunction;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author Eli
 * @since December 2, 2022 (creation)
 */
public abstract class AbstractStatement {
    final Esqueleto sql;
    final String statement;
    final Object[] replacements;

    AbstractStatement(Esqueleto sql, String statement, Object... replacements) {
        this.sql = sql;
        this.statement = statement;
        this.replacements = replacements;
    }

    @CheckReturnValue
    abstract ExecutionData execute();

    @CheckReturnValue
    @NotNull
    public <T> Optional<T> complete(@NotNull SqlFunction<Results, T> handler) {
        ExecutionData data = execute();
        try (ResultSet resultSet = data.resultSet()) {
            if (resultSet == null) {
                return Optional.empty();
            }

            return Optional.ofNullable(handler.apply(new Results(resultSet, sql)));
        }
        catch (SQLException exception) {
            Esqueleto.printError(exception, statement);
            return Optional.empty();
        }
    }

    public void complete(@NotNull SqlConsumer<Results> handler) {
        ExecutionData data = execute();
        try (ResultSet resultSet = data.resultSet()) {
            handler.accept(new Results(resultSet, sql));
        }
        catch (SQLException exception) {
            Esqueleto.printError(exception, statement);
        }
    }

    /// Queue onto a queued thread.
    public void queue(@NotNull SqlConsumer<Results> handler) {
        sql.getConfig().getQueueService().submit(() -> complete(handler));
    }

    /// Push onto an async thread.
    public void push(@NotNull SqlConsumer<Results> handler) {
        sql.getConfig().getAsyncService().submit(() -> complete(handler));
    }

    void parseReplacements(@NotNull PreparedStatement statement, Object... replacements) throws SQLException {
        int i = 1;
        for (Object replacement : replacements) {
            if (replacement instanceof Optional<?> optional) {
                replacement = optional.orElse(null);
            }

            if (replacement == null) {
                statement.setNull(i++, Types.NULL);
                continue;
            }

            Binary<?> binary = sql.getConfig().getBinaryTransformer(replacement.getClass());
            if (binary != null) {
                statement.setObject(i++, binary.from(cast(replacement, binary.type())));
            }
            else if (replacement instanceof Collection<?> collection) {
                for (Object object : collection) {
                    statement.setObject(i++, object);
                }
            }
            else {
                statement.setObject(i++, replacement);
            }
        }
    }

    static <T> T cast(Object replacement, Class<?> type) {
        return type.isInstance(replacement)? (T) type.cast(replacement) : null;
    }

    static String checkForIterable(@NotNull String query, Object... replacements) {
        int i = 0;
        for (Object replacement : replacements) {
            if (replacement instanceof Collection<?> collection) {
                int length = collection.size();
                if (length == 0) {
                    continue; // don't be fooled to needing to do i--, cus i++ won't be reached
                }

                List<Integer> indexes = new ArrayList<>();
                for (int j = 0; j < query.length(); j++) {
                    if (query.charAt(j) != '?') {
                        continue;
                    }

                    indexes.add(j);
                }

                String questionMarks = ",?".repeat(length).substring(1);
                query = replaceAtIndex(query, indexes.get(i), questionMarks);
                i += length - 1;
            }
            i++;
        }

        return query;
    }

    static String replaceAtIndex(@NotNull String text, int index, String replacement) {
        return text.substring(0, index) + replacement + text.substring(index + 1);
    }
}
