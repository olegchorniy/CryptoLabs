package ipt.lab.crypt.lab1.branchbound;

import ipt.lab.crypt.lab1.DiffTableCounter;
import ipt.lab.crypt.lab1.branchbound.strategies.BoundStrategy;
import ipt.lab.crypt.lab1.branchbound.strategies.NoOpStrategy;
import ipt.lab.crypt.lab1.heys.HeysCipher;

import java.util.Arrays;
import java.util.logging.Logger;

import static ipt.lab.crypt.lab1.Constants.BLOCKS_NUMBER;

public class BranchAndBound {

    private static final Logger LOGGER = Logger.getLogger(BranchAndBound.class.getName());
    private static final double blocksNumber = (double) BLOCKS_NUMBER;

    private final long[][] roundDiffProbs;
    private final BoundStrategy boundStrategy;

    public BranchAndBound(long[][] roundDiffProbs) {
        this(roundDiffProbs, new NoOpStrategy());
    }

    public BranchAndBound(long[][] roundDiffProbs, BoundStrategy boundStrategy) {
        this.roundDiffProbs = roundDiffProbs;
        this.boundStrategy = boundStrategy;
    }

    public double[] differentialSearch(int a) {
        //LOGGER.info("differentialSearch started");

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
            //LOGGER.info(round + " round completed, max value = " + Arrays.stream(currentDiffProbs).max());
        }

        return currentDiffProbs;
    }

    private static double[] emptyProbsArray() {
        double[] diffProbs = new double[BLOCKS_NUMBER];
        Arrays.fill(diffProbs, -1);

        return diffProbs;
    }
}
