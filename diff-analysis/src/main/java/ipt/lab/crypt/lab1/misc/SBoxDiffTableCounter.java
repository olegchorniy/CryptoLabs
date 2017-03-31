package ipt.lab.crypt.lab1.misc;

import ipt.lab.crypt.lab1.Constants;
import ipt.lab.crypt.common.heys.HeysCipher;
import ipt.lab.crypt.common.utils.PrintUtils;

public class SBoxDiffTableCounter {

    public static int[][] compute(HeysCipher heys) {
        int[] sBox = heys.getSBox();
        int size = sBox.length;

        int[][] diffTable = new int[size][size];

        for (int a = 0; a < size; a++) {
            for (int x = 0; x < size; x++) {
                diffTable[a][sBox[x] ^ sBox[x ^ a]]++;
            }
        }

        return diffTable;
    }

    public static void print(int[][] diffTable) {
        System.out.print("\t");
        for (int b = 0; b < diffTable.length; b++) {
            System.out.print(PrintUtils.toHexAsSubBlock(b) + "\t");
        }
        System.out.println();

        for (int a = 0; a < diffTable.length; a++) {
            System.out.print(PrintUtils.toHexAsSubBlock(a) + "\t");

            for (int b = 0; b < diffTable[a].length; b++) {
                System.out.print(diffTable[a][b] + "\t");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        print(compute(new HeysCipher(Constants.VARIANT)));
    }
}
