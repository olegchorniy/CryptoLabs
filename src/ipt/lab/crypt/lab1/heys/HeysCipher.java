package ipt.lab.crypt.lab1.heys;

public class HeysCipher {

    public static final int ROUNDS = 6;

    public static final int[][] S_BOXES = {
            {0xA, 0x9, 0xD, 0x6, 0xE, 0xB, 0x4, 0x5, 0xF, 0x1, 0x3, 0xC, 0x7, 0x0, 0x8, 0x2} /* 1 */
    };

    private final int[] sBox;

    public HeysCipher(int sBoxNumber) {
        this.sBox = S_BOXES[sBoxNumber - 1];
    }

    public int encrypt(int block, int[] key) {
        int cipherText = partialEncrypt(block, key, ROUNDS);

        cipherText = mixKey(cipherText, key[ROUNDS]);

        return cipherText;
    }

    public int partialEncrypt(int block, int[] key, int rounds) {
        int cipherText = block;

        for (int i = 0; i < rounds; i++) {
            cipherText = round(cipherText, key[i]);
        }

        return cipherText;
    }

    public int round(int block, int key) {
        int cipherText = mixKey(block, key);
        cipherText = substitute(cipherText);
        return permute(cipherText);
    }

    /* ----------------- Round operations -----------------*/

    private static int mixKey(int block, int key) {
        return block ^ key;
    }

    private int substitute(int block) {
        return sBox[block & 0xF] |
                (sBox[(block >> 4) & 0xF] << 4) |
                (sBox[(block >> 8) & 0xF] << 8) |
                (sBox[(block >> 12) & 0xF] << 12);
    }

    private static int permute(int block) {
        int result = block & 0x8421;

        result |= (block & 0x0842) << 3;
        result |= (block & 0x0084) << 6;
        result |= (block & 0x0008) << 9;

        result |= (block & 0x4210) >> 3;
        result |= (block & 0x2100) >> 6;
        result |= (block & 0x1000) >> 9;

        return result;
    }
}