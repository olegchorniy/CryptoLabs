package ipt.lab.crypt.lab1.misc;

import ipt.lab.crypt.common.Constants;
import ipt.lab.crypt.lab1.branchbound.BranchAndBound;
import ipt.lab.crypt.lab1.difftable.probsource.DiffProbTableSource;
import ipt.lab.crypt.lab1.difftable.probsource.FileDiffPropTableSource;

public class VerboseBranchAndBounds {

    public static void main(String[] args) {
        DiffProbTableSource source = new FileDiffPropTableSource();
        long[][] roundDiffProbs = source.getDiffProbTable(Constants.VARIANT);

        BranchAndBound branchAndBound = new BranchAndBound(roundDiffProbs, true);
        branchAndBound.differentialSearch(0xE000);
    }
}
