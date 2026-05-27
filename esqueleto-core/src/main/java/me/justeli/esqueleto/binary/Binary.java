package me.justeli.esqueleto.binary;

/**
 * @author Eli
 * @since December 2, 2021 (creation)
 */
public interface Binary<T> {
    byte[] from(T type);

    T to(byte[] data);

    Class<T> type();
}
