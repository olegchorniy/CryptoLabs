package ipt.lab.crypt.lab1;

import ipt.lab.crypt.lab1.heys.HeysCipher;

import java.util.Arrays;
import java.util.Random;

import static java.lang.Integer.toHexString;

public class Main {

    public static final int BLOCKS_SIZE = 16;
    public static final int BLOCKS_NUMBER = 1 << BLOCKS_SIZE; //0x10000
    public static final int BLOCK_MASK = BLOCKS_NUMBER - 1; //0xFFFF

    public static final Random rand = new Random();

    public static void main(String[] args) {
        HeysCipher heys = new HeysCipher(1);

        int key = randomBlock();

        System.out.println(key);
        System.out.println(maxProb(key));
    }

    private static int maxProb(int key) {
        HeysCipher heys = new HeysCipher(1);
        int max = 0;

        for (int a = 1; a < BLOCKS_NUMBER; a++) {
            int[] counters = new int[BLOCKS_NUMBER];
            for (int block = 0; block < BLOCKS_NUMBER; block++) {

                int b = heys.round(block, key) ^ heys.round(block ^ a, key);
                counters[b]++;
            }

            int localMax = max(counters);
            if (localMax > max) {
                max = localMax;
            }
        }

        return max;
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
