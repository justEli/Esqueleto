package me.justeli.esqueleto;

import me.justeli.esqueleto.binary.Binary;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

/**
 * @author Eli
 * @since December 28, 2022 (creation)
 */
public final class Results {
    private final ResultSet resultSet;
    private final Esqueleto sql;

    private static final String NO_DATA = "No data available";

    Results(@Nullable ResultSet resultSet, Esqueleto sql) {
        this.resultSet = resultSet;
        this.sql = sql;
    }

    public boolean next() throws SQLException {
        return resultSet != null && resultSet.next();
    }

    public <T> T get(String column) throws SQLException {
        if (resultSet == null) {
            throw new SQLException(NO_DATA);
        }

        if (resultSet.isBeforeFirst()) {
            next();
        }

        T type = (T) resultSet.getObject(column);
        return resultSet.wasNull()? null : type;
    }

    public <T> T get(String column, Class<T> type) throws SQLException {
        if (resultSet == null) {
            throw new SQLException(NO_DATA);
        }

        if (resultSet.isBeforeFirst()) {
            next();
        }

        Binary<?> binary = sql.getConfig().getBinaryTransformer(type);
        if (binary != null) {
            byte[] bytes = resultSet.getBytes(column);
            if (bytes == null) {
                return null;
            }

            return (T) binary.to(bytes);
        }

        T object = resultSet.getObject(column, type);
        return resultSet.wasNull()? null : object;
    }

    public boolean getBoolean(String column) throws SQLException {
        if (resultSet == null) {
            throw new SQLException(NO_DATA);
        }

        return resultSet.getBoolean(column);
    }

    public Integer getNullableInt(String column) throws SQLException {
        if (resultSet == null) {
            throw new SQLException(NO_DATA);
        }

        int integer = resultSet.getInt(column);
        return resultSet.wasNull()? null : integer;
    }

    public int getInt(String column) throws SQLException {
        if (resultSet == null) {
            throw new SQLException(NO_DATA);
        }

        return resultSet.getInt(column);
    }

    public String getString(String column) throws SQLException {
        if (resultSet == null) {
            throw new SQLException(NO_DATA);
        }

        String string = resultSet.getString(column);
        return resultSet.wasNull()? null : string;
    }

    public UUID getUuid(String column) throws SQLException {
        if (resultSet == null) {
            throw new SQLException(NO_DATA);
        }

        String uuid = resultSet.getString(column); // todo getBytes or getObject
        return resultSet.wasNull()? null : UUID.fromString(uuid);
    }

    public Instant getInstant(String column) throws SQLException {
        if (resultSet == null) {
            throw new SQLException(NO_DATA);
        }

        Timestamp timestamp = resultSet.getTimestamp(column);
        return resultSet.wasNull()? null : timestamp.toInstant();
    }

    public Long getTimeMillis(String column) throws SQLException {
        if (resultSet == null) {
            throw new SQLException(NO_DATA);
        }

        Timestamp timestamp = resultSet.getTimestamp(column);
        return resultSet.wasNull()? null : timestamp.getTime();
    }

    public long getLong(String column) throws SQLException {
        if (resultSet == null) {
            throw new SQLException(NO_DATA);
        }

        return resultSet.getLong(column);
    }

    public Long getNullableLong(String column) throws SQLException {
        if (resultSet == null) {
            throw new SQLException(NO_DATA);
        }

        long number = resultSet.getLong(column);
        return resultSet.wasNull()? null : number;
    }

    public byte[] getBytes(String column) throws SQLException {
        if (resultSet == null) {
            throw new SQLException(NO_DATA);
        }

        return resultSet.getBytes(column);
    }

    public double getDouble(String column) throws SQLException {
        if (resultSet == null) {
            throw new SQLException(NO_DATA);
        }

        return resultSet.getDouble(column);
    }

    public Double getNullableDouble(String column) throws SQLException {
        if (resultSet == null) {
            throw new SQLException(NO_DATA);
        }

        double number = resultSet.getDouble(column);
        return resultSet.wasNull()? null : number;
    }

    public float getFloat(String column) throws SQLException {
        if (resultSet == null) {
            throw new SQLException(NO_DATA);
        }

        return resultSet.getFloat(column);
    }

    public Float getNullableFloat(String column) throws SQLException {
        if (resultSet == null) {
            throw new SQLException(NO_DATA);
        }

        float number = resultSet.getFloat(column);
        return resultSet.wasNull()? null : number;
    }

    public InetAddress getInetAddress(String column) throws SQLException {
        if (resultSet == null) {
            throw new SQLException(NO_DATA);
        }

        String address = resultSet.getString(column);
        if (resultSet.wasNull()) {
            return null;
        }

        try {
            return InetAddress.getByName(address);
        }
        catch (UnknownHostException exception) {
            return null;
        }
    }
}
