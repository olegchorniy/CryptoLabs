package ipt.lab.crypt.lab2;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class LinearApproxList extends ArrayList<LinearApproxList.LinearApprox> {

    public static class LinearApprox {

        private int a;
        private List<Pair<Integer, Double>> betas;

        public LinearApprox() {
        }

        public LinearApprox(int a, List<Entry<Integer, Double>> betas) {
            this.a = a;
            this.betas = betas.stream()
                    .map(e -> MutablePair.of(e.getKey(), e.getValue()))
                    .collect(Collectors.toList());
        }

        public int getA() {
            return a;
        }

        public List<Pair<Integer, Double>> getBetas() {
            return betas;
        }
    }
}
