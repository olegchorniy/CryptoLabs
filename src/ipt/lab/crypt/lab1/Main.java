package ipt.lab.crypt.lab1;

import ipt.lab.crypt.lab1.heys.HeysCipher;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Random;

import static java.lang.Integer.toHexString;

public class Main {

    public static final int BLOCKS_SIZE = 16;
    public static final int BLOCKS_NUMBER = 1 << BLOCKS_SIZE; //0x10000
    public static final int BLOCK_MASK = BLOCKS_NUMBER - 1; //0xFFFF

    public static final Random rand = new Random();

    public static void main(String[] args) throws IOException {
        HeysCipher heys = new HeysCipher(1);

        long before = System.currentTimeMillis();
        DiffProb[][] probsTable = differentialProbabilities(heys);
        long time = System.currentTimeMillis() - before;

        for (DiffProb[] probs : probsTable) {
            if (probs == null) {
                continue;
            }
            System.out.println(Arrays.stream(probs).mapToDouble(p -> p.prob).max());
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("D:/probs.bin"))) {
            oos.writeObject(probsTable);
        }

        System.out.println(time);
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

            System.out.println(a + " = " + derivedBlocks.length);

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
