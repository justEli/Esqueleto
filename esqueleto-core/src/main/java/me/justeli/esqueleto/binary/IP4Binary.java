package me.justeli.esqueleto.binary;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

/**
 * BINARY(4)
 *
 * @author Eli
 * @since December 2, 2021 (creation)
 */
public final class IP4Binary implements Binary<Inet4Address> {
    @Override
    public byte[] from(Inet4Address type) {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[4]);

        byte[] bytes = type.getAddress();
        for (byte b : bytes) {
            buffer.put(b);
        }

        return buffer.array();
    }

    @Override
    public Inet4Address to(byte[] data) {
        try {
            return Inet4Address.getByAddress(data) instanceof Inet4Address ip4? ip4 : null;
        }
        catch (UnknownHostException exception) {
            return null;
        }
    }

    @Override
    public Class<Inet4Address> type() {
        return Inet4Address.class;
    }
}
