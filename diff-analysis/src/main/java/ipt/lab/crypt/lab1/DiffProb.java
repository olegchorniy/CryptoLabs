package ipt.lab.crypt.lab1;

import java.io.Serializable;

public class DiffProb implements Serializable {

    private int block;
    private double prob;

    public DiffProb() {
    }

    public DiffProb(int block, double prob) {
        this.block = block;
        this.prob = prob;
    }

    public int getBlock() {
        return block;
    }

    public void setBlock(int block) {
        this.block = block;
    }

    public double getProb() {
        return prob;
    }

    public void setProb(double prob) {
        this.prob = prob;
    }
}