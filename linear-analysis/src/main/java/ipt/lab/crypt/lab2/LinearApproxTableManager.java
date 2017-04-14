package ipt.lab.crypt.lab2;

import ipt.lab.crypt.common.utils.SerializationUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LinearApproxTableManager {

    public static final Path probsDir = Paths.get("D:", "work_dir", "crypt", "approx_tables");

    public static int[][] getTable(int sBoxNumber) {
        try {
            return SerializationUtil.deserialize(resolveFile(sBoxNumber), int[][].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveTable(int sBoxNumber, int[][] newTable) {
        try {
            SerializationUtil.serialize(resolveFile(sBoxNumber), newTable);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Path resolveFile(int sBoxNumber) {
        return probsDir.resolve(sBoxNumber + ".bin");
    }
}
