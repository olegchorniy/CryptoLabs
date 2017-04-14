package ipt.lab.crypt.lab2.branchandbound;

import ipt.lab.crypt.common.heys.HeysCipher;
import ipt.lab.crypt.lab2.BlockPotential;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ipt.lab.crypt.common.heys.HeyConstants.BLOCKS_NUMBER;
import static ipt.lab.crypt.common.heys.HeyConstants.BLOCK_MASK;

public class LinearBranchAndBound {

    private static final double blocksNumber = (double) BLOCKS_NUMBER;

    private final int[][] roundApprox;
    private final BoundStrategy boundStrategy;
    private final boolean verbose;

    public LinearBranchAndBound(int[][] roundApprox) {
        this(roundApprox, new NoOpStrategy(), false);
    }

    public LinearBranchAndBound(int[][] roundApprox, BoundStrategy boundStrategy) {
        this(roundApprox, boundStrategy, false);
    }

    public LinearBranchAndBound(int[][] roundApprox, boolean verbose) {
        this(roundApprox, new NoOpStrategy(), verbose);
    }

    public LinearBranchAndBound(int[][] roundApprox, BoundStrategy boundStrategy, boolean verbose) {
        this.roundApprox = roundApprox;
        this.boundStrategy = boundStrategy;
        this.verbose = verbose;
    }

    public double[] approximationsSearch(int a) {
        double[] currentPotentials = emptyPotentialsArray();

        currentPotentials[a] = 1.0;

        for (int round = 1; round <= HeysCipher.ROUNDS - 1; round++) {
            double[] nextPotentials = emptyPotentialsArray();

            for (int inputBlock = 0; inputBlock < BLOCKS_NUMBER; inputBlock++) {
                double inputPotential = currentPotentials[inputBlock];

                if (inputPotential < 0) {
                    continue;
                }

                for (int blockAndCounter : roundApprox[inputBlock]) {
                    int block = blockAndCounter & BLOCK_MASK;
                    int counter = blockAndCounter >>> 16;

                    double currentPotential = nextPotentials[block];
                    if (currentPotential < 0) {
                        currentPotential = 0;
                    }

                    double correlation = 1 - 2 * (counter / blocksNumber);
                    double potential = Math.pow(correlation, 2);

                    nextPotentials[block] = currentPotential + potential * inputPotential;
                }
            }

            boundStrategy.sieve(nextPotentials);

            currentPotentials = nextPotentials;

            if (verbose) {
                List<BlockPotential> blockPotentials = new ArrayList<>();
                for (int i = 0; i < currentPotentials.length; i++) {
                    if (currentPotentials[i] >= 0) {
                        blockPotentials.add(new BlockPotential(i, currentPotentials[i]));
                    }
                }

                String info = blockPotentials.stream()
                        .sorted((l, r) -> Double.compare(r.getPotential(), l.getPotential()))
                        .limit(15)
                        .map(BlockPotential::toString)
                        .collect(Collectors.joining("\n\t"));

                System.out.format("Round: %d, total: %d%n\t%s%n", round, blockPotentials.size(), info);
            }
        }

        return currentPotentials;
    }

    private static double[] emptyPotentialsArray() {
        double[] potentials = new double[BLOCKS_NUMBER];
        Arrays.fill(potentials, -1);

        return potentials;
    }
}
