package ipt.lab.crypt.lab2;

import ipt.lab.crypt.common.utils.PrintUtils;

import java.io.Serializable;
import java.util.Formatter;

public class BlockPotential implements Serializable {

    private int block;
    private double potential;

    public BlockPotential() {
    }

    public BlockPotential(int block, double potential) {
        this.block = block;
        this.potential = potential;
    }

    public int getBlock() {
        return block;
    }

    public void setBlock(int block) {
        this.block = block;
    }

    public double getPotential() {
        return potential;
    }

    public void setPotential(double potential) {
        this.potential = potential;
    }

    @Override
    public String toString() {
        return new Formatter(new StringBuilder(20))
                .format("%s, LP = %.8f", PrintUtils.toHexAsShort(block), potential)
                .toString();
    }
}