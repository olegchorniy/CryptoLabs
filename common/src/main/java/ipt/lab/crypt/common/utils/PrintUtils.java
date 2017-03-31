package ipt.lab.crypt.common.utils;

import java.util.Arrays;
import java.util.stream.Collectors;

public abstract class PrintUtils {

    private PrintUtils() {
    }

    public static String toHexAsSubBlock(int value) {
        return Integer.toHexString(value & 0xF);
    }

    public static String toHexAsByte(int value) {
        return padHex(Integer.toHexString(value & 0xFF), 2);
    }

    public static String toHexAsShort(int value) {
        return padHex(Integer.toHexString(value & 0xFFFF), 4);
    }

    public static String toHexAsByte(int[] bytes) {
        return Arrays.stream(bytes)
                .mapToObj(PrintUtils::toHexAsByte)
                .collect(Collectors.joining(" "));
    }

    private static String padHex(String hex, int length) {
        while (hex.length() < length) {
            hex = "0" + hex;
        }

        return hex;
    }
}
