package ipt.lab.crypt.common.utils;

import static ipt.lab.crypt.common.heys.HeyConstants.BLOCKS_SIZE;
import static ipt.lab.crypt.common.heys.HeyConstants.BLOCK_MASK;

public abstract class BlockUtils {

    private BlockUtils() {
    }

    public static boolean allSubBlocksActive(int diff) {
        // @formatter:off
        return  ( diff        & 0xF) != 0 &&
                ((diff >>  4) & 0xF) != 0 &&
                ((diff >>  8) & 0xF) != 0 &&
                ((diff >> 12) & 0xF) != 0;
        // @formatter:on
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
