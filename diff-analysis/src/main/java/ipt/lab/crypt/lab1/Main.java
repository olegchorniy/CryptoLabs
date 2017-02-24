package ipt.lab.crypt.lab1;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import ipt.lab.crypt.lab1.heys.HeysCipher;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Random;

import static java.lang.Integer.toHexString;

public class Main {

    private static final String file = "D:/probs.bin";

    public static final int BLOCKS_SIZE = 16;
    public static final int BLOCKS_NUMBER = 1 << BLOCKS_SIZE; //0x10000
    public static final int BLOCK_MASK = BLOCKS_NUMBER - 1; //0xFFFF

    public static final Random rand = new Random();

    public static void main(String[] args) throws IOException {
        DiffProb[][] probsTable = deserialize();

        int totalSize = 0;
        for (DiffProb[] diffProbs : probsTable) {
            if (diffProbs != null)
                totalSize += diffProbs.length;
        }

        System.out.println(totalSize);
    }

    private static void serialize(DiffProb[][] probsTable) throws IOException {
        Kryo kryo = new Kryo();

        try (OutputStream out = new FileOutputStream(file);
             Output output = new Output(out, 4096)) {
            kryo.writeObject(output, probsTable);
        }
    }

    private static DiffProb[][] deserialize() throws IOException {
        Kryo kryo = new Kryo();

        try (FileInputStream fis = new FileInputStream(file);
             Input input = new Input(fis, 4096)) {
            return kryo.readObject(input, DiffProb[][].class);
        }
    }

    private static DiffProb[][] differentialProbabilities(HeysCipher heys) {
        int[] SP = precomputeSP(heys);
        DiffProb[][] probabilities = new DiffProb[BLOCKS_NUMBER][];

        for (int a = 1; a < BLOCKS_NUMBER; a++) {
            int[] counters = new int[BLOCKS_NUMBER];

            for (int block = 0; block < BLOCKS_NUMBER; block++) {
                counters[SP[block] ^ SP[block ^ a]]++;
            }

            //count diffs with non-zero probability
            int possibleDiffsNumber = 0;
            for (int counter : counters) {
                if (counter != 0) {
                    possibleDiffsNumber++;
                }
            }

            //retain pairs with non-zero probability
            int currentIndex = 0;
            DiffProb[] derivedBlocks = new DiffProb[possibleDiffsNumber];

            for (int b = 0; b < BLOCKS_NUMBER; b++) {
                if (counters[b] != 0) {
                    derivedBlocks[currentIndex++] = new DiffProb(b, counters[b] / ((double) BLOCKS_NUMBER));
                }
            }

            //System.out.println(a + " = " + derivedBlocks.length);

            probabilities[a] = derivedBlocks;
        }

        return probabilities;
    }

    private static int[] precomputeSP(HeysCipher heysCipher) {
        int[] SP = new int[BLOCKS_NUMBER];

        for (int block = 0; block < BLOCKS_NUMBER; block++) {
            SP[block] = heysCipher.SP(block);
        }

        return SP;
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

    private static void dump(byte[] bytes) {
        for (byte value : bytes) {
            System.out.print(padHex(toHexString(value & 0xFF).toUpperCase()));
            System.out.print(" ");
        }
        System.out.println();
    }

    private static String padHex(String hex) {
        if (hex.length() == 2) {
            return hex;
        }
        return "0" + hex;
    }
}
