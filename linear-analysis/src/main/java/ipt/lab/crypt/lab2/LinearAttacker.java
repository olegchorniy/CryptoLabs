package ipt.lab.crypt.lab2;

import ipt.lab.crypt.common.Constants;
import ipt.lab.crypt.common.heys.HeyConstants;
import ipt.lab.crypt.common.heys.HeysCipher;
import ipt.lab.crypt.common.heys.HeysConsoleUtility;

import java.util.*;
import java.util.stream.IntStream;

import static ipt.lab.crypt.common.heys.HeyConstants.BLOCKS_NUMBER;
import static ipt.lab.crypt.lab2.Utils.dot;

public class LinearAttacker {

    private final int[] encryptedBlocks;
    private final int[] reversePS;
    private final int[] texts;

    public LinearAttacker(int sBoxNumber) {
        HeysConsoleUtility attackedHeys = new HeysConsoleUtility(Constants.BASE_DIR, sBoxNumber);

        this.encryptedBlocks = attackedHeys.encrypt(IntStream.range(0, BLOCKS_NUMBER).toArray());
        this.reversePS = preComputeReversePSTable(sBoxNumber);

        this.texts = selectBlocksForAttack(16_000);
    }

    public Map<Integer, Integer> attackKey(int a, int b) {

        Map<Integer, Integer> keyAndU = new HashMap<>();

        for (int key = 0; key < BLOCKS_NUMBER; key++) {
            int counter = 0;

            for (int x : this.texts) {

                int c = encryptedBlocks[x];
                int y = reversePS[c ^ key];

                if ((dot(a, x) ^ dot(b, y)) == 0) {
                    counter++;
                }
            }

            int u = Math.abs(2 * counter - HeyConstants.BLOCKS_NUMBER);
            keyAndU.put(key, u);
        }

        return keyAndU;
    }

    private int[] selectBlocksForAttack(int number) {
        Random random = new Random();
        Set<Integer> selectedBlocks = new HashSet<>(number);

        while (selectedBlocks.size() < number) {
            selectedBlocks.add(random.nextInt(BLOCKS_NUMBER));
        }

        return selectedBlocks.stream()
                .mapToInt(i -> i)
                .toArray();
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
