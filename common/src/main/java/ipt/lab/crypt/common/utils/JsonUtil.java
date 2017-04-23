package ipt.lab.crypt.common.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class JsonUtil {

    private static final Gson gson = new Gson();

    private JsonUtil() {
    }

    public static void serialize(Path path, Object object) {
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            gson.toJson(object, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T deserialize(Path path, Class<T> clazz) {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            return gson.fromJson(reader, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T deserialize(Path path, TypeToken<T> typeToken) {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            return gson.fromJson(reader, typeToken.getType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
