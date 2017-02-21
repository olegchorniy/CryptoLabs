package ipt.lab.crypt.lab1;

import ipt.lab.crypt.lab1.heys.HeysCipher;

public class Main {

    public static void main(String[] args) {
        HeysCipher heys = new HeysCipher(1);

        byte[] data = {0x12, 0x34};
        byte[] data2 = {0x56, 0x78};

        byte[] key = {
                0x00, 0x01,
                0x02, 0x03,
                0x04, 0x05,
                0x06, 0x07,
                0x08, 0x09,
                0x10, 0x11,
                0x12, 0x13
        };

        dump(heys.encryptBlock(data, key));
        dump(heys.encryptBlock(data2, key));
    }

    private static void dump(byte[] bytes) {
        for (byte value : bytes) {
            System.out.print(padHex(Integer.toHexString(Byte.toUnsignedInt(value)).toUpperCase()));
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
