package ipt.lab.crypt.lab1;

import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class Constants {

    private Constants() {
    }

    public static final Path BASE_DIR = Paths.get("D:", "work_dir", "crypt");

    public static final int BLOCKS_SIZE = 16;
    public static final int BLOCKS_NUMBER = 1 << BLOCKS_SIZE; //0x10000
    public static final int BLOCK_MASK = BLOCKS_NUMBER - 1; //0xFFFF

    public static final int VARIANT = 11;
}
