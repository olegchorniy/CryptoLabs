package ipt.lab.crypt.lab1.datastructures;

import ipt.lab.crypt.common.utils.PrintUtils;

public class DiffPairProb extends DiffProb {

    private int inputDiff;

    public DiffPairProb() {
    }

    public DiffPairProb(int inputDiff, int diff, double prob) {
        super(diff, prob);
        this.inputDiff = inputDiff;
    }

    public int getInputDiff() {
        return inputDiff;
    }

    public void setInputDiff(int inputDiff) {
        this.inputDiff = inputDiff;
    }

    @Override
    public String toString() {
        return PrintUtils.toHexAsShort(inputDiff) + " -> " + super.toString();
    }
}
