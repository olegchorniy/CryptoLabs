package ipt.lab.crypt.lab1;

import ipt.lab.crypt.lab1.branchbound.BranchAndBound;
import ipt.lab.crypt.lab1.branchbound.strategies.ProbabilityThresholdStrategy;
import ipt.lab.crypt.lab1.probsource.DiffProbTableSource;
import ipt.lab.crypt.lab1.probsource.FileDiffPropTableSource;
import org.apache.commons.lang3.time.StopWatch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static ipt.lab.crypt.lab1.Constants.BLOCKS_NUMBER;
import static ipt.lab.crypt.lab1.Constants.BLOCK_MASK;

public class Main {

    private static final Comparator<DiffProb> descByProbComparator = (lv, rv) -> Double.compare(rv.getProb(), lv.getProb());

    public static final Random rand = new Random();

    public static void main(String[] args) throws IOException {
        DiffProbTableSource source = new FileDiffPropTableSource();
        long[][] roundDiffProbs = source.getDiffProbTable(1);

        System.out.println("Deserialized");

        BranchAndBound bab = new BranchAndBound(roundDiffProbs, new ProbabilityThresholdStrategy(4.0 / (65535.0)));
        DiffProb globalMax = null;

        StopWatch sw = new StopWatch();

        for (int i = 1; i <= 10_000; i++) {
            int diff = i;//randomBlock();

            sw.start();
            double[] diffProbs = bab.differentialSearch(diff);
            sw.stop();

            DiffProb max = findMax(diffProbs);

            if (globalMax == null || max.getProb() > globalMax.getProb()) {
                globalMax = max;
            }

            if (i % 100 == 0)
                System.out.printf("i = %d, a = %s, b = %s, p = %.7f, globalMax = (%s, %.7f), time = %d%n",
                        i,
                        PrintUtils.toHexAsShort(diff),
                        PrintUtils.toHexAsShort(max.getDiff()),
                        max.getProb(),
                        PrintUtils.toHexAsShort(globalMax.getDiff()),
                        globalMax.getProb(),
                        sw.getTime()
                );

            sw.reset();
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
        return rand.nextInt() & BLOCK_MASK;
    }
}
