package ipt.lab.crypt.lab2;

public abstract class Utils {
    private Utils() {
    }

    public static int dot(int x, int y) {
        return Integer.bitCount(x & y) & 1;
    }
}
