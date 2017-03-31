package ipt.lab.crypt.common.utils;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class SerializationUtil {

    private static final Kryo kryo = new Kryo();
    private static final int BUFF_SIZE = 4096;

    private SerializationUtil() {
    }

    public static <T> T deserialize(Path srcFile, Class<T> resultClass) throws IOException {
        try (InputStream fis = Files.newInputStream(srcFile);
             Input input = new Input(fis, BUFF_SIZE)) {
            return kryo.readObject(input, resultClass);
        }
    }

    public static void serialize(Path targetFile, Object object) throws IOException {
        try (OutputStream out = Files.newOutputStream(targetFile);
             Output output = new Output(out, BUFF_SIZE)) {
            kryo.writeObject(output, object);
        }
    }
}
