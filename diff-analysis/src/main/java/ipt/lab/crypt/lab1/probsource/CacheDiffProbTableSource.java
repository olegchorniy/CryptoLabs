package ipt.lab.crypt.lab1.probsource;

import ipt.lab.crypt.lab1.SerializationUtil;
import ipt.lab.crypt.lab1.cache.CachingServer;
import org.apache.commons.lang3.time.StopWatch;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class CacheDiffProbTableSource implements DiffProbTableSource {

    private static final int BUFFER_SIZE = 4096;

    @Override
    public long[][] getDiffProbTable(int sBoxNumber) {
        try {
            StopWatch sw = new StopWatch();

            sw.start();
            ByteArrayOutputStream bos = new ByteArrayOutputStream(500 * (1 << 20));
            System.out.println("Allocation time = " + getAndReset(sw));

            sw.start();
            try (Socket socket = new Socket("localhost", CachingServer.PORT)) {
                copy(socket.getInputStream(), bos);
            }
            System.out.println("Coppying time = " + getAndReset(sw));

            sw.start();
            try {
                byte[] array = bos.toByteArray();
                return SerializationUtil.deserialize(array, long[][].class);
            } finally {
                System.out.println("Deserialization time = " + getAndReset(sw));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static long getAndReset(StopWatch sw) {
        sw.stop();
        long time = sw.getTime();
        sw.reset();

        return time;
    }

    private static int copy(InputStream in, OutputStream out) throws IOException {
        int byteCount = 0;
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = -1;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
            byteCount += bytesRead;
        }
        out.flush();
        return byteCount;
    }
}
