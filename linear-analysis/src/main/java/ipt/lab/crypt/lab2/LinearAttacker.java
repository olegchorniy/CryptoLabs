package ipt.lab.crypt.lab2;

import ipt.lab.crypt.common.Constants;
import ipt.lab.crypt.common.heys.HeyConstants;
import ipt.lab.crypt.common.heys.HeysCipher;
import ipt.lab.crypt.common.heys.HeysConsoleUtility;
import ipt.lab.crypt.common.utils.PrintUtils;

import java.util.stream.IntStream;

import static ipt.lab.crypt.common.heys.HeyConstants.BLOCKS_NUMBER;
import static ipt.lab.crypt.lab2.Utils.dot;

public class LinearAttacker {

    private final int sBoxNumber;

    public LinearAttacker(int sBoxNumber) {
        this.sBoxNumber = sBoxNumber;
    }

    public int attackKey(int a, int b) {

        HeysConsoleUtility attackedHeys = new HeysConsoleUtility(Constants.BASE_DIR, sBoxNumber);

        int[] encryptedBlocks = attackedHeys.encrypt(IntStream.range(0, BLOCKS_NUMBER).toArray());
        int[] reversePS = preComputeReversePSTable(sBoxNumber);

        int keyCandidate = -1;
        int maxU = 0;

        for (int key = 0; key < BLOCKS_NUMBER; key++) {
            int zeros = 0;

            for (int x = 0; x < encryptedBlocks.length; x++) {

                int c = encryptedBlocks[x];
                int y = reversePS[c ^ key];

                if ((dot(a, x) ^ dot(b, y)) == 0) {
                    zeros++;
                }
            }

            int u = Math.abs(2 * zeros - HeyConstants.BLOCKS_NUMBER);

            if (u >= maxU) {
                maxU = u;
                keyCandidate = key;

                System.out.format("candidate: %s, maxU: %d%n",
                        PrintUtils.toHexAsShort(keyCandidate),
                        maxU);
            }

            if (key == 0x26e4) {
                System.out.println("U for real key: " + u);
            }
        }

        return keyCandidate;
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
