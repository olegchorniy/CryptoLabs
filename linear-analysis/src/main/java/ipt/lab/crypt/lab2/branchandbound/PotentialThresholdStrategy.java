package ipt.lab.crypt.lab2.branchandbound;

public class PotentialThresholdStrategy implements BoundStrategy {

    private final double threshold;

    public PotentialThresholdStrategy(double threshold) {
        this.threshold = threshold;
    }

    @Override
    public void sieve(double[] potentials) {
        for (int i = 0; i < potentials.length; i++) {
            if (potentials[i] < threshold) {
                potentials[i] = -1; //any negative value will be suitable
            }
        }
    }
}
