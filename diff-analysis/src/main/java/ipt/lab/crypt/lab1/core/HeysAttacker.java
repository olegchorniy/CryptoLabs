package ipt.lab.crypt.lab1.core;

import ipt.lab.crypt.lab1.datastructures.DiffPairProb;
import ipt.lab.crypt.lab1.heys.HeysCipher;
import ipt.lab.crypt.lab1.heys.HeysConsoleUtility;
import ipt.lab.crypt.lab1.utils.PrintUtils;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;

import static ipt.lab.crypt.lab1.Constants.BLOCKS_NUMBER;

public class HeysAttacker {

    public int attackAttempt(int sBoxNumber, DiffPairProb differential) {
        int diff = differential.getDiff();
        int inputDiff = differential.getInputDiff();
        double diffProb = differential.getProb();

        int textNumberEstimate = (int) Math.floor(1.0 / (diffProb - 1.0 / BLOCKS_NUMBER));
        System.out.println("textNumberEstimate = " + textNumberEstimate);

        textNumberEstimate = 3000;

        HeysConsoleUtility attackedHeys = new HeysConsoleUtility(sBoxNumber);

        int[] allBlocks = IntStream.range(0, BLOCKS_NUMBER).toArray();
        int[] encryptedBlocks = attackedHeys.encrypt(allBlocks);

        int[] usedBlocks = selectBlocksForAttack(encryptedBlocks, textNumberEstimate);

        int[] reversePS = preComputeReversePSTable(sBoxNumber);

        int keyCandidate = -1;
        int candidateCoincides = 0;

        for (int key = 0; key < BLOCKS_NUMBER; key++) {

            int coincides = 0;

            for (int _block : usedBlocks) {
                int block = _block & 0xFFFF;
                int pairBlock = block ^ inputDiff;

                int cipherText = encryptedBlocks[block];
                int pairCipherText = encryptedBlocks[pairBlock];

                int actualDiff = reversePS[cipherText ^ key] ^ reversePS[pairCipherText ^ key];

                if (actualDiff == diff) {
                    coincides++;
                }
            }

            if (coincides > candidateCoincides) {
                candidateCoincides = coincides;
                keyCandidate = key;
            }

            if (key % 500 == 0) {
                System.out.format("%d done, candidate: %s, candidateCoincides: %d%n",
                        key,
                        PrintUtils.toHexAsShort(keyCandidate),
                        candidateCoincides
                );
            }
        }

        return keyCandidate;
    }

    private int[] selectBlocksForAttack(int[] encryptedBlocks, int textNumberEstimate) {
        Random random = new Random();
        Set<Integer> choosenBlocks = new HashSet<>();

        while (choosenBlocks.size() < textNumberEstimate) {
            choosenBlocks.add(random.nextInt(encryptedBlocks.length));
        }

        int[] texts = new int[textNumberEstimate];
        int i = 0;

        for (int block : choosenBlocks) {
            texts[i++] = block | (encryptedBlocks[block] << 16);
        }

        return texts;
    }

    private static int[] preComputeReversePSTable(int sBoxNumber) {
        HeysCipher heys = new HeysCipher(sBoxNumber);

        int[] reversePS = new int[BLOCKS_NUMBER];

        for (int block = 0; block < BLOCKS_NUMBER; block++) {
            reversePS[block] = heys.inverseSubstitute(HeysCipher.inversePermute(block));
        }

        return reversePS;
    }
}
