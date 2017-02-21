package ipt.lab.crypt.lab1.heys;

import java.util.Arrays;

public class HeysCipher {

    public static final int SUB_BLOCKS = 4;
    public static final int SUB_BLOCKS_SUB_ONE = SUB_BLOCKS - 1;
    public static final int ROUNDS = 6;

    public static final byte[][] S_BLOCKS = {
            {0xA, 0x9, 0xD, 0x6, 0xE, 0xB, 0x4, 0x5, 0xF, 0x1, 0x3, 0xC, 0x7, 0x0, 0x8, 0x2} /* 1 */
    };

    private byte[] sBlock;

    public HeysCipher(int sBlockNumber) {
        this.sBlock = S_BLOCKS[sBlockNumber - 1];
    }

    public byte[] encryptBlock(byte[] data, byte[] key) {
        int[] result = bytesToSubBlocks(data);

        /* 6 rounds */
        for (int round = 0; round < ROUNDS; round++) {
            mixKey(result, roundKey(key, round));
            substitution(result);
            permutation(result);
        }

        /* final key mixing */
        mixKey(result, roundKey(key, ROUNDS));

        return subBlocksToBytes(result);
    }

    private static int[] roundKey(byte[] key, int round) {
        int startIndex = round << 1;
        byte[] roundKey = {
                key[startIndex],
                key[startIndex + 1]
        };

        return bytesToSubBlocks(roundKey);
    }

    /* ----------------- Round operations  ----------------- */

    public static void mixKey(int[] data, int[] key) {
        for (int i = 0; i < SUB_BLOCKS; i++) {
            data[i] ^= key[i];
        }
    }

    public void substitution(int[] data) {
        for (int i = 0; i < SUB_BLOCKS; i++) {
            data[i] = sBlock[data[i]];
        }
    }

    public static void permutation(int[] data) {
        int[] temp = Arrays.copyOf(data, data.length); //create temporary copy
        Arrays.fill(data, 0); //clear main array

        //the output i of S-box j is connected to input j of S-box i
        for (int j = 0; j < SUB_BLOCKS; j++) {
            for (int i = 0; i < SUB_BLOCKS; i++) {
                int revI = SUB_BLOCKS_SUB_ONE - i;
                int revJ = SUB_BLOCKS_SUB_ONE - j;

                data[i] |= lowerBit(temp[j] >> revI) << revJ;
            }
        }
    }

    //guaranteed correct version
    public static void permutation2(int[] data) {
        int[] temp = new int[SUB_BLOCKS];

        for (int i = 0; i < SUB_BLOCKS; i++) {
            //reverse sub blocks
            temp[SUB_BLOCKS - 1 - i] = data[i];
            //clear main array
            data[i] = 0;
        }

        //the output i of S-box j is connected to input j of S-box i
        for (int j = 0; j < SUB_BLOCKS; j++) {
            for (int i = 0; i < SUB_BLOCKS; i++) {
                data[i] |= lowerBit(temp[j] >> i) << j;
            }
        }

        //reverse main array again
        for (int i = 0, j = SUB_BLOCKS - 1; i < j; i++, j--) {
            swap(data, i, j);
        }
    }

    private static void swap(int[] array, int i, int j) {
        int t = array[i];
        array[i] = array[j];
        array[j] = t;
    }

    /* ----------------- Conversion utilities ----------------- */

    private static int[] bytesToSubBlocks(byte[] bytes) {
        int[] subBlocks = new int[SUB_BLOCKS];

        subBlocks[0] = lowerHalf(bytes[1] >> 4);
        subBlocks[1] = lowerHalf(bytes[1]);

        subBlocks[2] = lowerHalf(bytes[0] >> 4);
        subBlocks[3] = lowerHalf(bytes[0]);

        return subBlocks;
    }

    private static byte[] subBlocksToBytes(int[] subBlocks) {
        byte[] bytes = new byte[2];

        bytes[0] = (byte) ((subBlocks[2] << 4) | subBlocks[3]);
        bytes[1] = (byte) ((subBlocks[0] << 4) | subBlocks[1]);

        return bytes;
    }

    /* ----------------- Bytes & bits utilities ----------------- */

    private static int lowerHalf(int value) {
        return value & 0xF;
    }

    private static int lowerBit(int value) {
        return value & 0x1;
    }
}
