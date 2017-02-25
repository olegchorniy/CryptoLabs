package ipt.lab.crypt.lab1;

import java.util.Arrays;
import java.util.stream.Collectors;

public abstract class PrintUtils {

    private PrintUtils() {
    }

    public static String toHexAsByte(int value) {
        return padHex(Integer.toHexString(value & Constants.BLOCK_MASK));
    }

    public static String toHexAsByte(int[] bytes) {
        return Arrays.stream(bytes)
                .mapToObj(PrintUtils::toHexAsByte)
                .collect(Collectors.joining(" "));
    }

    private static String padHex(String hex) {
        if (hex.length() == 2) {
            return hex;
        }
        return "0" + hex;
    }
}
