package ipt.lab.crypt.lab1.datastructures;

import ipt.lab.crypt.lab1.utils.PrintUtils;

import java.io.Serializable;
import java.util.Formatter;

public class DiffProb implements Serializable {

    private int diff;
    private double prob;

    public DiffProb() {
    }

    public DiffProb(int diff, double prob) {
        this.diff = diff;
        this.prob = prob;
    }

    public int getDiff() {
        return diff;
    }

    public void setDiff(int diff) {
        this.diff = diff;
    }

    public double getProb() {
        return prob;
    }

    public void setProb(double prob) {
        this.prob = prob;
    }

    @Override
    public String toString() {
        return new Formatter(new StringBuilder(20))
                .format("%s, p = %.8f", PrintUtils.toHexAsShort(diff), prob)
                .toString();
    }
}