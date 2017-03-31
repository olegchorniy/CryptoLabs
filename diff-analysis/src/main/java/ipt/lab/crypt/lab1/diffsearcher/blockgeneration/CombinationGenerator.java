package ipt.lab.crypt.lab1.diffsearcher.blockgeneration;

import java.util.NoSuchElementException;

class CombinationGenerator {

    private final int n;
    private final int k;
    private final int[] state;
    private boolean hasNext;

    public CombinationGenerator(int n, int k) {
        if ((n > 32 || n <= 0) || (k < 0 || k > n)) {
            throw new IllegalArgumentException("n = " + n + ", k = " + k);
        }
        this.n = n;
        this.k = k;
        state = new int[k];
        for (int i = 0; i < state.length; i++) {
            state[i] = i;
        }
        hasNext = true;
    }

    public boolean hasNext() {
        return hasNext;
    }

    public void next(int[] stateHolder) {
        if (!hasNext) {
            throw new NoSuchElementException();
        }
        System.arraycopy(state, 0, stateHolder, 0, state.length);
        hasNext = nextState();
    }

    private boolean nextState() {
        int i = k - 1;
        while (i >= 0 && state[i] == n - k + i) {
            i--;
        }
        if (i == -1) {
            return false;
        }
        state[i]++;
        for (i++; i < k; i++) {
            state[i] = state[i - 1] + 1;
        }
        return true;
    }
}
