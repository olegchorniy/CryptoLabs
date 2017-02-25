package ipt.lab.crypt.lab1.probsource;

import ipt.lab.crypt.lab1.Constants;
import ipt.lab.crypt.lab1.SerializationUtil;

import java.io.IOException;
import java.nio.file.Path;

public class FileDiffPropTableSource implements DiffProbTableSource {

    public static final Path probsFile = Constants.BASE_DIR.resolve("probs.bin");

    @Override
    public long[][] getDiffProbTable(int sBoxNumber) {
        try {
            return deserialize();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateTable(long[][] newTable) {
        try {
            serialize(newTable);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void serialize(long[][] probsTable) throws IOException {
        SerializationUtil.serialize(probsFile, probsTable);
    }

    private static long[][] deserialize() throws IOException {
        return SerializationUtil.deserialize(probsFile, long[][].class);
    }
}
