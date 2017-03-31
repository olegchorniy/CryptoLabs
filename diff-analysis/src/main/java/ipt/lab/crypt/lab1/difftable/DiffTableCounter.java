package ipt.lab.crypt.lab1.difftable;

import ipt.lab.crypt.common.heys.HeysCipher;

import static ipt.lab.crypt.lab1.Constants.*;

public class DiffTableCounter {

    public static long[][] differentialProbabilities(HeysCipher heys) {
        //speedup probability compution with pre-computed SP-table
        int[] SP = preComputeSP(heys);
        long[][] probabilities = new long[BLOCKS_NUMBER][];

        //to prevent null-checks all over the code
        probabilities[0] = new long[0];

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
            long[] derivedBlocks = new long[possibleDiffsNumber];

            for (int b = 0; b < BLOCKS_NUMBER; b++) {
                if (counters[b] != 0) {
                    //pack block and counter into single long variable
                    derivedBlocks[currentIndex++] = pack(b, counters[b]);
                }
            }

            probabilities[a] = derivedBlocks;
        }

        return probabilities;
    }

    private static int[] preComputeSP(HeysCipher heysCipher) {
        int[] SP = new int[BLOCKS_NUMBER];

        for (int block = 0; block < BLOCKS_NUMBER; block++) {
            SP[block] = heysCipher.SP(block);
        }

        return SP;
    }

    public static long pack(int block, int counter) {
        return block | (counter << BLOCKS_SIZE);
    }

    public static int unpackBlock(long packedValue) {
        return (int) (packedValue & BLOCK_MASK);
    }

    public static int unpackCounter(long packedValue) {
        return (int) (packedValue >>> BLOCKS_SIZE);
    }
}
