package ipt.lab.crypt.lab1.core.branchbound.strategies;

public interface BoundStrategy {

    /**
     * Reduce number of supported differentials by making the value of corresponding cell negative.
     *
     * @param diffProbs array containing probabilities of differentials. Negative value means impossible differential.
     */
    void sieve(double[] diffProbs);
}
