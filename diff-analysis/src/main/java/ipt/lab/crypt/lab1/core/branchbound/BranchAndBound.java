package ipt.lab.crypt.lab1.core.branchbound;

import ipt.lab.crypt.lab1.core.DiffTableCounter;
import ipt.lab.crypt.lab1.core.branchbound.strategies.BoundStrategy;
import ipt.lab.crypt.lab1.core.branchbound.strategies.NoOpStrategy;
import ipt.lab.crypt.lab1.datastructures.DiffProb;
import ipt.lab.crypt.lab1.heys.HeysCipher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ipt.lab.crypt.lab1.Constants.BLOCKS_NUMBER;

public class BranchAndBound {

    private static final double blocksNumber = (double) BLOCKS_NUMBER;

    private final long[][] roundDiffProbs;
    private final BoundStrategy boundStrategy;
    private final boolean verbose;

    public BranchAndBound(long[][] roundDiffProbs) {
        this(roundDiffProbs, new NoOpStrategy(), false);
    }

    public BranchAndBound(long[][] roundDiffProbs, BoundStrategy boundStrategy) {
        this(roundDiffProbs, boundStrategy, false);
    }

    public BranchAndBound(long[][] roundDiffProbs, boolean verbose) {
        this(roundDiffProbs, new NoOpStrategy(), verbose);
    }

    public BranchAndBound(long[][] roundDiffProbs, BoundStrategy boundStrategy, boolean verbose) {
        this.roundDiffProbs = roundDiffProbs;
        this.boundStrategy = boundStrategy;
        this.verbose = verbose;
    }

    public double[] differentialSearch(int a) {
        //indices of this array serve as differentials
        double[] currentDiffProbs = emptyProbsArray();

        //1. initial value
        currentDiffProbs[a] = 1.0;

        for (int round = 1; round <= HeysCipher.ROUNDS - 1; round++) {
            double[] nextDiffProbs = emptyProbsArray();

            for (int inputDiff = 0; inputDiff < BLOCKS_NUMBER; inputDiff++) {
                double inputDiffProb = currentDiffProbs[inputDiff];

                if (inputDiffProb < 0) {
                    continue;
                }

                for (long blockAndProb : roundDiffProbs[inputDiff]) {
                    int diff = DiffTableCounter.unpackBlock(blockAndProb);
                    int freq = DiffTableCounter.unpackCounter(blockAndProb);

                    double currentProb = nextDiffProbs[diff];
                    if (currentProb < 0) {
                        currentProb = 0;
                    }

                    nextDiffProbs[diff] = currentProb + (freq / blocksNumber) * inputDiffProb;
                }
            }

            boundStrategy.sieve(nextDiffProbs);

            currentDiffProbs = nextDiffProbs;

            if (verbose) {
                List<DiffProb> diffProbs = new ArrayList<>();
                for (int i = 0; i < currentDiffProbs.length; i++) {
                    if (currentDiffProbs[i] >= 0) {
                        diffProbs.add(new DiffProb(i, currentDiffProbs[i]));
                    }
                }

                String info = diffProbs.stream()
                        .sorted((l, r) -> Double.compare(r.getProb(), l.getProb()))
                        .limit(15)
                        .map(DiffProb::toString)
                        .collect(Collectors.joining("\n\t"));

                System.out.format("Round: %d, total: %d%n\t%s%n", round, diffProbs.size(), info);
            }
        }

        return currentDiffProbs;
    }

    private static double[] emptyProbsArray() {
        double[] diffProbs = new double[BLOCKS_NUMBER];
        Arrays.fill(diffProbs, -1);

        return diffProbs;
    }
}
