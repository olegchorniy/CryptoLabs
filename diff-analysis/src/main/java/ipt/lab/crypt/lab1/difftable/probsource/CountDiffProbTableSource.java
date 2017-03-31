package ipt.lab.crypt.lab1.difftable.probsource;

import ipt.lab.crypt.lab1.difftable.DiffTableCounter;
import ipt.lab.crypt.common.heys.HeysCipher;

public class CountDiffProbTableSource implements DiffProbTableSource {

    @Override
    public long[][] getDiffProbTable(int sBoxNumber) {
        return DiffTableCounter.differentialProbabilities(new HeysCipher(sBoxNumber));
    }
}
