package ipt.lab.crypt.lab1.core;

import ipt.lab.crypt.lab1.core.branchbound.BranchAndBound;
import ipt.lab.crypt.lab1.datastructures.DiffPairProb;
import ipt.lab.crypt.lab1.datastructures.DiffProb;
import ipt.lab.crypt.lab1.probsource.DiffProbTableSource;
import ipt.lab.crypt.lab1.probsource.FileDiffPropTableSource;
import ipt.lab.crypt.lab1.utils.PrintUtils;
import org.apache.commons.lang3.time.StopWatch;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static ipt.lab.crypt.lab1.Constants.BLOCKS_NUMBER;
import static ipt.lab.crypt.lab1.Constants.BLOCK_MASK;

public class FiveRoundDifferentialsFinder {

    private static final Comparator<DiffProb> descByProbComparator = (lv, rv) -> Double.compare(rv.getProb(), lv.getProb());

    public static final Random rand = new Random();

    public static void main(String[] args) throws IOException {
        DiffProbTableSource source = new FileDiffPropTableSource();
        long[][] roundDiffProbs = source.getDiffProbTable(11);

        System.out.println("Deserialized");

        BranchAndBound bab = new BranchAndBound(roundDiffProbs/*, new ProbabilityThresholdStrategy(4.0 / (65535.0)));*/);

        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                DiffPairProb globalMax = null;

                StopWatch sw = new StopWatch();

                for (int iteration = 1; iteration <= 10_000; iteration++) {
                    int diff = (iteration % 2 == 0) ? randomBlock() : randomSparseBlock();

                    sw.start();
                    double[] diffProbs = bab.differentialSearch(diff);
                    sw.stop();

                    DiffProb max = findMax(diffProbs);

                    if (globalMax == null || max.getProb() > globalMax.getProb()) {
                        globalMax = new DiffPairProb(diff, max.getDiff(), max.getProb());
                    }


                    StringBuilder builder = new StringBuilder(100);
                    new Formatter(builder).format("[Thread = %s] i = %d, a = %s, b = %s, p = %.7f, globalMax = (%s), time = %d%n",
                            Thread.currentThread().getName(),
                            iteration,
                            PrintUtils.toHexAsShort(diff),
                            PrintUtils.toHexAsShort(max.getDiff()),
                            max.getProb(),
                            globalMax,
                            sw.getTime());

                    System.out.println(builder.toString());

                    sw.reset();
                }
            }).start();
        }
    }

    private static DiffProb findMax(double[] diffProbs) {
        int maxDiff = 1;
        double maxProb = diffProbs[1];

        for (int diff = 2; diff < BLOCKS_NUMBER; diff++) {
            if (diffProbs[diff] > maxProb) {
                maxProb = diffProbs[diff];
                maxDiff = diff;
            }
        }

        return new DiffProb(maxDiff, maxProb);
    }

    private static List<DiffProb> toSortedList(double[] diffProbs) {
        List<DiffProb> probsList = new ArrayList<>(diffProbs.length);

        for (int diff = 0; diff < diffProbs.length; diff++) {
            if (diffProbs[diff] > 0) {
                probsList.add(new DiffProb(diff, diffProbs[diff]));
            }
        }

        probsList.sort(descByProbComparator);

        return probsList;
    }

    private static int randomBlock() {
        return ThreadLocalRandom.current().nextInt() & BLOCK_MASK;
    }

    private static int randomSparseBlock() {
        int block;

        do {
            block = 0;
            for (int i = 0; i < 4; i++) {
                if (ThreadLocalRandom.current().nextBoolean()) {
                    block |= (((ThreadLocalRandom.current().nextInt() >> 19) & 0xF) << (4 * i));
                }
            }
        } while (block == 0);

        return block;
    }
}
