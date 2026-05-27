package me.justeli.esqueleto.binary;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * BINARY(16)
 * @author Eli
 * @since December 2, 2021 (creation)
 */
public final class UuidBinary implements Binary<UUID> {
    @Override
    public byte[] from(UUID type) {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
        buffer.putLong(type.getMostSignificantBits());
        buffer.putLong(type.getLeastSignificantBits());
        return buffer.array();
    }

    @Override
    public UUID to(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        long msb = buffer.getLong();
        long lsb = buffer.getLong();
        return new UUID(msb, lsb);
    }

    @Override
    public Class<UUID> type() {
        return UUID.class;
    }
}
