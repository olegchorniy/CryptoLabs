package ipt.lab.crypt.lab2;

import ipt.lab.crypt.common.utils.PrintUtils;
import ipt.lab.crypt.common.utils.SerializationUtil;

import java.io.IOException;
import java.util.stream.Collectors;

import static ipt.lab.crypt.lab2.LinearPotentialsSearch.PAIRS_PATH;

public class LinearApproxPairsPrinter {
    public static void main(String[] args) throws IOException {
        LinearApproxList approximations = SerializationUtil.deserialize(PAIRS_PATH, LinearApproxList.class);

        for (LinearApproxList.LinearApprox approx : approximations) {

            String log = approx.getBetas().stream()
                    .map(e -> String.format("\t%s - %.7f", PrintUtils.toHexAsShort(e.getKey()), e.getValue()))
                    .collect(Collectors.joining("\n"));

            System.out.printf("a = %s%n%s%n",
                    PrintUtils.toHexAsShort(approx.getA()),
                    log
            );
        }
    }
}
