package me.justeli.esqueleto;

import me.justeli.esqueleto.binary.Binary;
import me.justeli.esqueleto.handler.SqlFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Eli
 * @since December 28, 2022 (creation)
 */
public final class Results {
    private final @NotNull ResultSet resultSet;
    private final Esqueleto sql;

    Results(@Nullable ResultSet resultSet, Esqueleto sql) throws SQLException {
        if (resultSet == null) {
            throw new SQLException("No data available");
        }

        this.resultSet = resultSet;
        this.sql = sql;
    }

    public boolean next() throws SQLException {
        return resultSet.next();
    }

    private <T> Optional<T> get(SqlFunction<ResultSet, T> data) throws SQLException {
        if (resultSet.isBeforeFirst()) {
            next();
        }

        T value = data.apply(resultSet);
        return resultSet.wasNull()? Optional.empty() : Optional.of(value);
    }

    public <T> T get(String column) throws SQLException {
        // noinspection unchecked
        return get(data -> (T) data.getObject(column)).orElse(null);
    }

    public <T> T get(String column, Class<T> type) throws SQLException {
        return get(data -> {
            Binary<?> binary = sql.getConfig().getBinaryTransformer(type);
            if (binary == null) {
                return data.getObject(column, type);
            }

            byte[] bytes = data.getBytes(column);
            if (bytes == null) {
                return null;
            }

            // noinspection unchecked
            return (T) binary.to(bytes);
        }).orElse(null);
    }

    public boolean getBoolean(String column) throws SQLException {
        return get(data -> data.getBoolean(column)).orElse(false);
    }

    public Integer getNullableInt(String column) throws SQLException {
        return get(data -> data.getInt(column)).orElse(null);
    }

    public int getInt(String column) throws SQLException {
        return get(data -> data.getInt(column)).orElse(0);
    }

    public String getString(String column) throws SQLException {
        return get(data -> data.getString(column)).orElse(null);
    }

    public UUID getUuid(String column) throws SQLException {
        // todo getBytes or getObject
        return get(data -> data.getString(column)).map(UUID::fromString).orElse(null);
    }

    public Instant getInstant(String column) throws SQLException {
        return get(data -> data.getTimestamp(column)).map(Timestamp::toInstant).orElse(null);
    }

    public Long getTimeMillis(String column) throws SQLException {
        return get(data -> data.getTimestamp(column)).map(Timestamp::getTime).orElse(null);
    }

    public long getLong(String column) throws SQLException {
        return get(data -> data.getLong(column)).orElse(0L);
    }

    public Long getNullableLong(String column) throws SQLException {
        return get(data -> data.getLong(column)).orElse(null);
    }

    public byte[] getBytes(String column) throws SQLException {
        return get(data -> data.getBytes(column)).orElse(null);
    }

    public double getDouble(String column) throws SQLException {
        return get(data -> data.getDouble(column)).orElse(0D);
    }

    public Double getNullableDouble(String column) throws SQLException {
        return get(data -> data.getDouble(column)).orElse(null);
    }

    public float getFloat(String column) throws SQLException {
        return get(data -> data.getFloat(column)).orElse(0F);
    }

    public Float getNullableFloat(String column) throws SQLException {
        return get(data -> data.getFloat(column)).orElse(null);
    }

    public InetAddress getInetAddress(String column) throws SQLException {
        return get(data -> data.getString(column)).map(string -> {
            try {
                return InetAddress.getByName(string);
            }
            catch (UnknownHostException exception) {
                return null;
            }
        }).orElse(null);
    }
}
