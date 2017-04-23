package ipt.lab.crypt.lab2;

import ipt.lab.crypt.common.Constants;
import ipt.lab.crypt.common.blockgeneration.BlocksDistributor;
import ipt.lab.crypt.common.utils.BlockUtils;
import ipt.lab.crypt.common.utils.PrintUtils;
import ipt.lab.crypt.common.utils.SerializationUtil;
import ipt.lab.crypt.lab2.LinearApproxList.LinearApprox;
import ipt.lab.crypt.lab2.branchandbound.LinearBranchAndBound;
import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.time.StopWatch;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LinearPotentialsSearch {

    public static final Path PAIRS_PATH = Constants.BASE_DIR.resolve("pairs.dat");

    public static void main(String[] args) throws InterruptedException, IOException {
        final int concurrency = 3;

        int[][] approx = LinearApproxTableManager.getTable(Constants.VARIANT);

        BlocksDistributor distributor = new BlocksDistributor(1);
        LinearBranchAndBound searcher = new LinearBranchAndBound(approx/*, new PotentialThresholdStrategy(0.00003)*/);

        LinearApproxList result = new LinearApproxList();
        CountDownLatch latch = new CountDownLatch(concurrency);

        for (int i = 0; i < concurrency; i++) {
            runThread(() -> {
                String threadName = Thread.currentThread().getName();

                StopWatch sw = new StopWatch();

                while (true) {
                    Optional<Integer> block = distributor.getIfAvailable();
                    if (!block.isPresent()) {
                        break;
                    }

                    int a = block.get();

                    sw.start();
                    double[] potentials = searcher.approximationsSearch(a);
                    sw.stop();

                    List<Entry<Integer, Double>> topN = topN(potentials, 10);

                    synchronized (LinearPotentialsSearch.class) {
                        result.add(new LinearApprox(a, topN));
                    }

                    String log = topN.stream()
                            .map(e -> String.format("\t%s - %.5f", PrintUtils.toHexAsShort(e.getKey()), e.getValue()))
                            .collect(Collectors.joining("\n"));

                    System.out.printf("[%s] a = %s, time = %d%n%s%n",
                            threadName,
                            PrintUtils.toHexAsShort(a),
                            sw.getTime(),
                            log
                    );

                    sw.reset();
                }

                latch.countDown();
            });
        }

        latch.await();

        SerializationUtil.serialize(PAIRS_PATH, result);
    }

    private static Thread runThread(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.start();

        return thread;
    }

    public static int conditionalArgMax(double[] values) {
        int maxIndex = 0;
        double maxValue = values[0];

        for (int block = 1; block < values.length; block++) {
            double value = values[block];

            if (BlockUtils.allSubBlocksActive(block) && value > maxValue) {
                maxValue = value;
                maxIndex = block;
            }
        }

        return maxIndex;
    }

    public static List<Entry<Integer, Double>> topN(double[] values, int n) {
        //TODO: may be replaced with EntryStream#of
        Stream<Integer> indStream = IntStreamEx.range(0, values.length).boxed();
        Stream<Double> valStream = Arrays.stream(values).boxed();

        return StreamEx.of(indStream).zipWith(valStream)
                .reverseSorted(Entry.comparingByValue())
                .limit(n)
                .collect(Collectors.toList());
    }
}
