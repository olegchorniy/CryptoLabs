package ipt.lab.crypt.lab1;

import ipt.lab.crypt.lab1.heys.HeysCipher;

import static java.lang.Integer.toHexString;

public class Main {

    public static void main(String[] args) {
        HeysCipher heys = new HeysCipher(1);

        short block1 = 0x3412;
        short block2 = 0x7856;

        short[] key = {
                0x0100,
                0x0200,
                0x0300,
                0x0400,
                0x0500,
                0x0600,
                0x0700
        };

        System.out.println(toHexString(heys.encrypt(block1, key) & 0xFFFF));
        System.out.println(toHexString(heys.encrypt(block2, key) & 0xFFFF));
    }

    private static void dump(byte[] bytes) {
        for (byte value : bytes) {
            System.out.print(padHex(toHexString(value & 0xFF).toUpperCase()));
            System.out.print(" ");
        }
        System.out.println();
    }

    private static String padHex(String hex) {
        if (hex.length() == 2) {
            return hex;
        }
        return "0" + hex;
    }
}
