package ipt.lab.crypt.lab1.core.blockgeneration;

import java.util.Arrays;
import java.util.NoSuchElementException;

public class TupleGenerator {

    private final int max;
    private final int min;

    private boolean hasNext;
    private final int[] state;

    public TupleGenerator(int length, int min, int max) {
        this.min = min;
        this.max = max;

        this.state = new int[length];
        Arrays.fill(this.state, min);
        this.hasNext = true;
    }

    public boolean hasNext() {
        return hasNext;
    }

    public void next(int[] holder) {
        if (!hasNext) {
            throw new NoSuchElementException();
        }

        System.arraycopy(this.state, 0, holder, 0, this.state.length);
        nextState();
    }

    private void nextState() {
        int i = this.state.length - 1;

        while (i >= 0) {
            if (this.state[i] < this.max) {
                this.state[i]++;
                return;
            } else {
                this.state[i] = this.min;
                i--;
            }
        }

        if (i < 0) {
            this.hasNext = false;
        }
    }
}
