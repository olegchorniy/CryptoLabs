package ipt.lab.crypt.lab2;

import ipt.lab.crypt.common.Constants;
import ipt.lab.crypt.common.blockgeneration.BlocksDistributor;
import ipt.lab.crypt.common.utils.BlockUtils;
import ipt.lab.crypt.common.utils.PrintUtils;
import ipt.lab.crypt.lab2.branchandbound.LinearBranchAndBound;
import ipt.lab.crypt.lab2.branchandbound.NoOpStrategy;
import ipt.lab.crypt.lab2.branchandbound.PotentialThresholdStrategy;
import org.apache.commons.lang3.time.StopWatch;

import java.util.Formatter;
import java.util.Optional;

public class LinearPotentialsSearch {

    public static void main(String[] args) {

        int[][] approx = LinearApproxTableManager.getTable(Constants.VARIANT);

        BlocksDistributor distributor = new BlocksDistributor();
        LinearBranchAndBound searcher = new LinearBranchAndBound(approx, new PotentialThresholdStrategy(0.00003));

        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                String threadName = Thread.currentThread().getName();

                StopWatch sw = new StopWatch();

                int threadMaxInput = 0;
                BlockPotential threadMaxOutput = null;

                while (true) {
                    Optional<Integer> block = distributor.getIfAvailable();
                    if (!block.isPresent()) {
                        break;
                    }

                    int a = block.get();

                    sw.start();
                    double[] potentials = searcher.approximationsSearch(a);
                    sw.stop();

                    int b = conditionalArgMax(potentials);
                    double localMaxPotential = potentials[b];

                    if (threadMaxOutput == null || localMaxPotential > threadMaxOutput.getPotential()) {
                        threadMaxInput = a;
                        threadMaxOutput = new BlockPotential(b, localMaxPotential);
                    }

                    String log = new Formatter().format(
                            "[%s] a = %s, b = %s, LP = %.5f, time = %d | thread max = (a = %s, b = %s)",
                            threadName,
                            PrintUtils.toHexAsShort(a),
                            PrintUtils.toHexAsShort(b),
                            localMaxPotential,
                            sw.getTime(),
                            PrintUtils.toHexAsShort(threadMaxInput),
                            threadMaxOutput
                    ).toString();

                    System.out.println(log);

                    sw.reset();
                }
            }).start();
        }
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
}
