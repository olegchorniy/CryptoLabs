package ipt.lab.crypt.lab1;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import ipt.lab.crypt.lab1.heys.HeysCipher;
import ipt.lab.crypt.lab1.heys.HeysConsoleUtility;
import org.apache.commons.lang3.time.StopWatch;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Random;

import static ipt.lab.crypt.lab1.Constants.*;

public class Main {

    private static final Path probsFile = Constants.BASE_DIR.resolve("probs.bin");

    public static final Random rand = new Random();

    public static void main(String[] args) throws IOException {

        int testBlock = 0x1234;

        int key[] = new int[7];
        for (int i = 0; i < key.length; i++) {
            key[i] = randomBlock();
        }

        HeysCipher heys = new HeysCipher(1);
        HeysConsoleUtility consoleHeys = new HeysConsoleUtility(1);

        System.out.println(Integer.toHexString(consoleHeys.encrypt(testBlock, key)));
        System.out.println(Integer.toHexString(heys.encrypt(testBlock, key)));
    }

    public static void read() throws IOException {
        StopWatch sw = new StopWatch();

        sw.start();
        long[][] probsTable = deserialize();
        sw.stop();

        System.out.println("Deserialization: " + sw.getTime());

        int totalSize = 0;
        for (long[] probs : probsTable) {
            totalSize += (probs == null ? 0 : probs.length);
        }

        System.out.println("totalSize = " + totalSize);
    }

    private static void evaluateAndSerialize() throws IOException {

        StopWatch sw = new StopWatch();

        sw.start();
        long[][] probsTable = DiffTableCounter.differentialProbabilities(new HeysCipher(1));
        sw.stop();

        System.out.println("Count: " + sw.getTime());

        sw.reset();

        sw.start();
        serialize(probsTable);
        sw.stop();

        System.out.println("Serialization: " + sw.getTime());
    }

    private static void serialize(long[][] probsTable) throws IOException {
        Kryo kryo = new Kryo();

        try (OutputStream out = Files.newOutputStream(probsFile);
             Output output = new Output(out, 4096)) {
            kryo.writeObject(output, probsTable);
        }
    }

    private static long[][] deserialize() throws IOException {
        Kryo kryo = new Kryo();

        try (InputStream fis = Files.newInputStream(probsFile);
             Input input = new Input(fis, 4096)) {
            return kryo.readObject(input, long[][].class);
        }
    }

    private static int max(int[][] values) {
        return Arrays.stream(values)
                .mapToInt(Main::max)
                .max()
                .orElseThrow(() -> new IllegalArgumentException("array is empty"));
    }

    private static int max(int[] values) {
        return Arrays.stream(values)
                .max()
                .orElseThrow(() -> new IllegalArgumentException("array is empty"));
    }

    private static int randomBlock() {
        return rand.nextInt() & BLOCK_MASK;
    }
}
