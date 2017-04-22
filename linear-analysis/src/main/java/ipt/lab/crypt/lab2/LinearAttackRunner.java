package ipt.lab.crypt.lab2;

import ipt.lab.crypt.common.Constants;
import ipt.lab.crypt.common.utils.PrintUtils;
import ipt.lab.crypt.common.utils.SerializationUtil;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;

import static ipt.lab.crypt.lab2.LinearPotentialsSearch.PAIRS_PATH;

public class LinearAttackRunner {
    /*
  + a = 00c0, b = 1011, LP = 0,00032777
  + a = 0020, b = 0110, LP = 0,00037890
  - a = 0010, b = 0110, LP = 0,00040228

    a = 00c0, b = 1111, LP = 0,00016018
    a = 00e0, b = 1111, LP = 0,00016155
    a = 0090, b = 1111, LP = 0,00013384
  + a = 00d0, b = 1111, LP = 0,00015632

    a = 8000, b = 2222

  - a = 000c, b = 2222, LP = 0,00013905
 -- a = 0c0c, b = 4264, LP = 0,00003
     */

    private static final ThreadLocal<LinearAttacker> attackerThreadLocal = new ThreadLocal<LinearAttacker>() {

        @Override
        protected LinearAttacker initialValue() {
            System.out.println("New attacker instance, thread = " + Thread.currentThread().getName());
            return new LinearAttacker(Constants.VARIANT);
        }
    };

    public static void main(String[] args) {
        List<Pair<Integer, Integer>> candidates = attackerThreadLocal.get().attackKey(0x8000, 0x2222);

        System.out.println(candidates);
    }

    public static void bruteForce(String[] args) throws IOException {
        LinearApproxList approximations = SerializationUtil.deserialize(PAIRS_PATH, LinearApproxList.class);

        int actualKey = 0x26e4;

        LongAdder hits = new LongAdder();
        LongAdder approxTried = new LongAdder();

        //LinearAttacker attacker = new LinearAttacker(Constants.VARIANT);

        approximations.stream()
                .flatMap(approx -> StreamEx.of(approx.getA()).cross(approx.getBetas()))
                //.parallel()
                .skip(345)
                .forEach(e -> {
                    int a = e.getKey();
                    int b = e.getValue().getKey();

                    StopWatch sw = new StopWatch();

                    sw.start();
                    List<Pair<Integer, Integer>> candidates = attackerThreadLocal.get().attackKey(a, b);
                    sw.stop();

                    approxTried.increment();
                    if (candidates.stream().anyMatch(candidate -> candidate.getKey() == actualKey)) {
                        hits.increment();
                    }

                    System.out.printf("Try #%d, a = %s, b = %s, candidates = %d, time = %d | hits = %d%n",
                            approxTried.sum(),
                            PrintUtils.toHexAsShort(a),
                            PrintUtils.toHexAsShort(b),
                            candidates.size(),
                            sw.getTime(),
                            hits.sum()
                    );

                    sw.reset();
                });

        /*for (LinearApprox approx : approximations) {
            int a = approx.getA();

            for (Pair<Integer, Double> pair : approx.getBetas()) {
                int b = pair.getKey();

                sw.start();
                List<Pair<Integer, Integer>> candidates = attacker.attackKey(a, b);
                sw.stop();

                approxTried++;
                if (candidates.stream().anyMatch(candidate -> candidate.getKey() == actualKey)) {
                    hits++;
                }

                System.out.printf("Trie #%d, a = %s, b = %s, time = %d | hits = %d%n",
                        approxTried,
                        PrintUtils.toHexAsShort(a),
                        PrintUtils.toHexAsShort(b),
                        sw.getTime(),
                        hits
                );

                sw.reset();
            }
        }*/

        System.out.printf("Tries: %d, hits: %d%n", approxTried.sum(), hits.sum());
    }
}
