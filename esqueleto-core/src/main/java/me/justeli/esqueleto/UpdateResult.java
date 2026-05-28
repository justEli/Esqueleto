package me.justeli.esqueleto;

import java.util.OptionalLong;

/**
 * @author Eli
 * @since May 28, 2026
 */
public final class UpdateResult implements ResultType {
    private final Long insertedId;
    private final long rows;

    public UpdateResult(Long insertedId, long rows) {
        this.insertedId = insertedId;
        this.rows = rows;
    }

    /// @return the id of the new row. in MariaDB and MySQL: only works for a PRIMARY KEY AUTO_INCREMENT column!
    public OptionalLong getInsertedId() {
        return insertedId == null? OptionalLong.empty() : OptionalLong.of(insertedId);
    }

    public long getRows() {
        return rows;
    }

    @Override
    public String toString() {
        return "UpdateResult[insertedId=%d, rows=%d]".formatted(insertedId, rows);
    }
}
