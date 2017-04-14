package ipt.lab.crypt.lab2;


import ipt.lab.crypt.common.heys.HeysCipher;
import org.apache.commons.lang3.time.StopWatch;

import java.util.concurrent.TimeUnit;

import static ipt.lab.crypt.common.heys.HeyConstants.BLOCKS_NUMBER;
import static ipt.lab.crypt.lab2.Utils.dot;

public class LinearApproxTableCounter {

    private static final int S_BOX_SIZE = 16;
    private static final int HALF_BLOCKS = BLOCKS_NUMBER >> 1;

    public static int[][] fastRoundLinearApprox(HeysCipher heysCipher) {
        int[][] sBoxApprox = linearApprox(heysCipher.getSBox());
        int[][] roundApprox = new int[BLOCKS_NUMBER][];

        StopWatch watcher = new StopWatch();

        watcher.start();

        for (int a = 0; a < BLOCKS_NUMBER; a++) {

            int a0 = a & 0xF;
            int a1 = (a >> 4) & 0xF;
            int a2 = (a >> 8) & 0xF;
            int a3 = (a >> 12) & 0xF;

            int[] counters = new int[BLOCKS_NUMBER];
            int biasedCounters = 0;

            for (int b = 0; b < BLOCKS_NUMBER; b++) {
                int b0 = b & 0xF;
                int b1 = (b >> 4) & 0xF;
                int b2 = (b >> 8) & 0xF;
                int b3 = (b >> 12) & 0xF;

                int c0 = sBoxApprox[a0][b0];
                int c1 = sBoxApprox[a1][b1];
                int c2 = sBoxApprox[a2][b2];
                int c3 = sBoxApprox[a3][b3];

                int s0 = S_BOX_SIZE - c0;
                int s1 = S_BOX_SIZE - c1;
                int s2 = S_BOX_SIZE - c2;
                int s3 = S_BOX_SIZE - c3;

                int sum = 0;

                sum += c0 * s1 * s2 * s3;
                sum += s0 * c1 * s2 * s3;
                sum += s0 * s1 * c2 * s3;
                sum += s0 * s1 * s2 * c3;

                sum += s0 * c1 * c2 * c3;
                sum += c0 * s1 * c2 * c3;
                sum += c0 * c1 * s2 * c3;
                sum += c0 * c1 * c2 * s3;

                counters[b] = sum;

                if (sum != HALF_BLOCKS) {
                    biasedCounters++;
                }
            }

            if (a % 1000 == 0) {
                System.out.println("a = " + a + ", time passed: " + watcher.getTime(TimeUnit.SECONDS));
            }

            int[] biasedValues = new int[biasedCounters];
            int i = 0;

            for (int b = 0; b < BLOCKS_NUMBER; b++) {
                int counter = counters[b];

                if (counter != HALF_BLOCKS) {
                    // at this point we can apply permutation to the 'b'
                    // to obtain approximations of the whole round
                    biasedValues[i++] = HeysCipher.permute(b) | (counter << 16);
                }
            }

            roundApprox[a] = biasedValues;
        }

        System.out.println("Finished, time passed: " + watcher.getTime(TimeUnit.SECONDS));

        return roundApprox;
    }

    public static int[][] linearApprox(int[] function) {
        final int maxBlock = function.length;

        int[][] approx = new int[maxBlock][maxBlock];

        for (int a = 0; a < maxBlock; a++)
            for (int b = 0; b < maxBlock; b++)
                for (int x = 0; x < maxBlock; x++)
                    approx[a][b] += dot(a, x) ^ dot(b, function[x]);

        return approx;
    }

    public static int[][] slowRoundLinearApprox(HeysCipher heysCipher) {
        int[][] sBoxApprox = linearApprox(heysCipher.getSBox());
        int[][] roundApprox = new int[BLOCKS_NUMBER][];

        long start = System.currentTimeMillis();

        for (int a = 0; a < BLOCKS_NUMBER; a++) {

            int[] counters = new int[BLOCKS_NUMBER];
            int biasedCounters = 0;

            for (int b = 0; b < BLOCKS_NUMBER; b++) {

                for (int i = 0; i < 4; i++) {
                    //i - номер подблока, в котором будет 1 (0)
                    int singleUnit = sBoxApprox[$(a, i)][$(b, i)];
                    int singleZero = S_BOX_SIZE - singleUnit;

                    for (int j = 0; j < 4; j++) {
                        //j - пробегает по всем остальным подблокам
                        if (j != i) {
                            int t = sBoxApprox[$(a, j)][$(b, j)];

                            singleUnit *= (S_BOX_SIZE - t);
                            singleZero *= t;
                        }
                    }

                    counters[b] += singleUnit;
                    counters[b] += singleZero;
                }

                if (counters[b] != HALF_BLOCKS) {
                    biasedCounters++;
                }
            }

            if (a % 1000 == 0) {
                System.out.println("a = " + a + ", time spent: " + (System.currentTimeMillis() - start) / 1000.0);
            }

            int[] biasedValues = new int[biasedCounters];
            int i = 0;

            for (int b = 0; b < BLOCKS_NUMBER; b++) {
                if (counters[b] != HALF_BLOCKS) {

                    if (counters[b] == BLOCKS_NUMBER) {
                        System.err.println("Max value achieved: a = " + a + ", b = " + b);
                    }

                    biasedValues[i++] = b | (counters[b] << 16);
                }
            }

            roundApprox[a] = biasedValues;
        }

        return roundApprox;
    }

    private static int $(int block, int subBlockNum) {
        return (block >> (subBlockNum << 2)) & 0xF;
    }
}