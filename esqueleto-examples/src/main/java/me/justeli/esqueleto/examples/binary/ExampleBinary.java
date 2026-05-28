package me.justeli.esqueleto.examples.binary;

import me.justeli.esqueleto.binary.Binary;
import me.justeli.esqueleto.examples.data.Example;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;

/**
 * @author Eli
 * @since December 28, 2022 (creation)
 */
public final class ExampleBinary implements Binary<Example> {
    @Override
    public byte[] from(Example type) {
        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream(); ObjectOutput output = new ObjectOutputStream(bytes)) {
            output.writeObject(type);
            return bytes.toByteArray();
        }
        catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    @Override
    public Example to(byte[] data) {
        try (ByteArrayInputStream bytes = new ByteArrayInputStream(data); ObjectInput input = new ObjectInputStream(bytes)) {
            return (Example) input.readObject();
        }
        catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
        catch (ClassNotFoundException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public Class<Example> type() {
        return Example.class;
    }
}
