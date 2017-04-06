package ipt.lab.crypt.common.heys;

public abstract class HeyConstants {
    private HeyConstants() {
    }

    public static final int BLOCKS_SIZE = 16;
    public static final int BLOCKS_NUMBER = 1 << BLOCKS_SIZE; //0x10000
    public static final int BLOCK_MASK = BLOCKS_NUMBER - 1; //0xFFFF
}
