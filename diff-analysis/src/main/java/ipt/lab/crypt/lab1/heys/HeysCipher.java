package ipt.lab.crypt.lab1.heys;

public class HeysCipher {

    public static final int ROUNDS = 6;

    public static final int[][] S_BOXES = {
            {0xA, 0x9, 0xD, 0x6, 0xE, 0xB, 0x4, 0x5, 0xF, 0x1, 0x3, 0xC, 0x7, 0x0, 0x8, 0x2}, /* 1 */
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            {0x4, 0xB, 0x1, 0xF, 0x9, 0x2, 0xE, 0xC, 0x6, 0xA, 0x8, 0x7, 0x3, 0x5, 0x0, 0xD} /* 11 */
    };

    private final int[] sBox;
    private final int[] inverseSBox;

    public HeysCipher(int sBoxNumber) {
        this.sBox = S_BOXES[sBoxNumber - 1];
        this.inverseSBox = new int[this.sBox.length];

        for (int i = 0; i < this.inverseSBox.length; i++) {
            this.inverseSBox[this.sBox[i]] = i;
        }
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
        return SP(mixKey(block, key));
    }

    public int SP(int block) {
        return permute(substitute(block));
    }

    /* ----------------- Round operations -----------------*/

    public static int mixKey(int block, int key) {
        return block ^ key;
    }

    public int substitute(int block) {
        return applySBox(block, this.sBox);
    }

    public int inverseSubstitute(int block) {
        return applySBox(block, this.inverseSBox);
    }

    private static int applySBox(int block, int[] sBox) {
        int result = sBox[block & 0xF];

        result |= sBox[(block >> 4) & 0xF] << 4;
        result |= sBox[(block >> 8) & 0xF] << 8;
        result |= sBox[(block >> 12) & 0xF] << 12;

        return result;
    }

    public static int permute(int block) {
        int result = block & 0x8421;

        result |= (block & 0x0842) << 3;
        result |= (block & 0x0084) << 6;
        result |= (block & 0x0008) << 9;

        result |= (block & 0x4210) >> 3;
        result |= (block & 0x2100) >> 6;
        result |= (block & 0x1000) >> 9;

        return result;
    }

    public static int inversePermute(int block) {
        return permute(block);
    }
}