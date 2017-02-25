package ipt.lab.crypt.lab1.probsource;

import ipt.lab.crypt.lab1.DiffTableCounter;
import ipt.lab.crypt.lab1.heys.HeysCipher;

public class CountDiffProbTableSource implements DiffProbTableSource {

    @Override
    public long[][] getDiffProbTable(int sBoxNumber) {
        return DiffTableCounter.differentialProbabilities(new HeysCipher(1));
    }
}
