package ipt.lab.crypt.lab1.branchbound.strategies;

public class ProbabilityThresholdStrategy implements BoundStrategy {

    private final double threshold;

    public ProbabilityThresholdStrategy(double threshold) {
        this.threshold = threshold;
    }

    @Override
    public void sieve(double[] diffProbs) {
        for (int i = 0; i < diffProbs.length; i++) {
            if (diffProbs[i] < threshold) {
                diffProbs[i] = -1; //any negative value will be suitable
            }
        }
    }
}
