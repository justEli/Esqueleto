package me.justeli.esqueleto;

import me.justeli.esqueleto.binary.Binary;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

/* Eli @ December 28, 2022 (creation) */
public final class Results
{
    private final ResultSet resultSet;
    private final Esqueleto sql;

    private static final String NO_DATA = "No data available";

    Results (@Nullable ResultSet resultSet, Esqueleto sql)
    {
        this.resultSet = resultSet;
        this.sql = sql;
    }

    public boolean next ()
    throws SQLException
    {
        return this.resultSet != null && this.resultSet.next();
    }

    public <T> T get (String column)
    throws SQLException
    {
        if (this.resultSet == null)
            throw new SQLException(NO_DATA);

        if (this.resultSet.isBeforeFirst())
            next();

        T type = (T) this.resultSet.getObject(column);
        return this.resultSet.wasNull()? null : type;
    }

    public <T> T get (String column, Class<T> type)
    throws SQLException
    {
        if (this.resultSet == null)
            throw new SQLException(NO_DATA);

        if (this.resultSet.isBeforeFirst())
            next();

        Binary<?> binary = this.sql.getConfig().getBinaryTransformer(type);
        if (binary != null)
        {
            byte[] bytes = this.resultSet.getBytes(column);
            if (bytes == null)
                return null;

            return (T) binary.to(bytes);
        }

        T object = this.resultSet.getObject(column, type);
        return this.resultSet.wasNull()? null : object;
    }

    public boolean getBoolean (String column)
    throws SQLException
    {
        if (this.resultSet == null)
            throw new SQLException(NO_DATA);

        return this.resultSet.getBoolean(column);
    }

    public Integer getNullableInt (String column)
    throws SQLException
    {
        if (this.resultSet == null)
            throw new SQLException(NO_DATA);

        int integer = this.resultSet.getInt(column);
        return this.resultSet.wasNull()? null : integer;
    }

    public int getInt (String column)
    throws SQLException
    {
        if (this.resultSet == null)
            throw new SQLException(NO_DATA);

        return this.resultSet.getInt(column);
    }

    public String getString (String column)
    throws SQLException
    {
        if (this.resultSet == null)
            throw new SQLException(NO_DATA);

        String string = this.resultSet.getString(column);
        return this.resultSet.wasNull()? null : string;
    }

    public UUID getUuid (String column)
    throws SQLException
    {
        if (this.resultSet == null)
            throw new SQLException(NO_DATA);

        // todo improve
        return this.resultSet.wasNull()? null : UUID.fromString(this.resultSet.getString(column));
    }

    public Instant getInstant (String column)
    throws SQLException
    {
        if (this.resultSet == null)
            throw new SQLException(NO_DATA);

        Timestamp timestamp = this.resultSet.getTimestamp(column);
        return this.resultSet.wasNull()? null : timestamp.toInstant();
    }

    public Long getTimeMillis (String column)
    throws SQLException
    {
        if (this.resultSet == null)
            throw new SQLException(NO_DATA);

        Timestamp timestamp = this.resultSet.getTimestamp(column);
        return this.resultSet.wasNull()? null : timestamp.getTime();
    }

    public long getLong (String column)
    throws SQLException
    {
        if (this.resultSet == null)
            throw new SQLException(NO_DATA);

        return this.resultSet.getLong(column);
    }

    public Long getNullableLong (String column)
    throws SQLException
    {
        if (this.resultSet == null)
            throw new SQLException(NO_DATA);

        long number = this.resultSet.getLong(column);
        return this.resultSet.wasNull()? null : number;
    }

    public byte[] getBytes (String column)
    throws SQLException
    {
        if (this.resultSet == null)
            throw new SQLException(NO_DATA);

        return this.resultSet.getBytes(column);
    }

    public double getDouble (String column)
    throws SQLException
    {
        if (this.resultSet == null)
            throw new SQLException(NO_DATA);

        return this.resultSet.getDouble(column);
    }

    public Double getNullableDouble (String column)
    throws SQLException
    {
        if (this.resultSet == null)
            throw new SQLException(NO_DATA);

        double number = this.resultSet.getDouble(column);
        return this.resultSet.wasNull()? null : number;
    }

    public float getFloat (String column)
    throws SQLException
    {
        if (this.resultSet == null)
            throw new SQLException(NO_DATA);

        return this.resultSet.getFloat(column);
    }

    public Float getNullableFloat (String column)
    throws SQLException
    {
        if (this.resultSet == null)
            throw new SQLException(NO_DATA);

        float number = this.resultSet.getFloat(column);
        return this.resultSet.wasNull()? null : number;
    }
}
