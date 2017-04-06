package ipt.lab.crypt.lab1;

import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class Constants {

    private Constants() {
    }

    public static final Path BASE_DIR = Paths.get("D:", "work_dir", "crypt");

    public static final int VARIANT = 11;
}
