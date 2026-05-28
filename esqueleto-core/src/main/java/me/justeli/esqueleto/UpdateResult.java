package me.justeli.esqueleto;

/**
 * @author Eli
 * @since May 28, 2026
 */
public final class UpdateResult implements ResultType {
    private final long insertedId;
    private final long rows;

    public UpdateResult(long insertedId, long rows) {
        this.insertedId = insertedId;
        this.rows = rows;
    }

    /// @return the id of the new row. in MariaDB and MySQL: only works for a PRIMARY KEY AUTO_INCREMENT column!
    public long getInsertedId() {
        return insertedId;
    }

    public long getRows() {
        return rows;
    }

    @Override
    public String toString() {
        return "UpdateResult[insertedId=%d, rows=%d]".formatted(insertedId, rows);
    }
}
