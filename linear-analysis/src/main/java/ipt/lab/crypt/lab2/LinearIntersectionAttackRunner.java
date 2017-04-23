package ipt.lab.crypt.lab2;

import ipt.lab.crypt.common.Constants;
import ipt.lab.crypt.common.utils.PrintUtils;
import ipt.lab.crypt.common.utils.SerializationUtil;
import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

import static ipt.lab.crypt.lab2.LinearPotentialsSearch.PAIRS_PATH;

@SuppressWarnings("Duplicates")
public class LinearIntersectionAttackRunner {

    private static final int ACTUAL_KEY = 0x26e4;
    private static final int CONCURRENCY = 3;

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        LinearApproxList approximations = SerializationUtil.deserialize(PAIRS_PATH, LinearApproxList.class);
        BlockingQueue<Entry<Integer, Integer>> tasks = new LinkedBlockingQueue<>(toPairs(approximations));

        LongAdder approxTried = new LongAdder();

        for (int i = 0; i < CONCURRENCY; i++) {
            new Thread(() -> {

                Map<Integer, Integer> hits = new HashMap<>();

                LinearAttacker attacker = new LinearAttacker(Constants.VARIANT);
                StopWatch sw = new StopWatch();

                while (true) {

                    Entry<Integer, Integer> head = tasks.poll();
                    if (head == null) {
                        break;
                    }

                    int a = head.getKey();
                    int b = head.getValue();

                    sw.start();
                    Map<Integer, Integer> candidates = attacker.attackKey(a, b);
                    sw.stop();

                    NavigableMap<Integer, Set<Integer>> counterToKeys = EntryStream.of(candidates)
                            .invert()
                            .groupingTo(TreeMap::new, HashSet::new);

                    counterToKeys
                            .lastEntry()
                            .getValue()
                            .forEach(key -> incCounter(hits, key));

                    approxTried.increment();

                    System.out.printf("[%s] #%d, a = %s, b = %s, hits map = %s, time = %d%n",
                            Thread.currentThread().getName(),
                            approxTried.sum(),
                            PrintUtils.toHexAsShort(a),
                            PrintUtils.toHexAsShort(b),
                            EntryStream.of(hits)
                                    .sorted(Entry.<Integer, Integer>comparingByValue().reversed())
                                    .limit(100)
                                    .toList(),
                            sw.getTime()
                    );

                    sw.reset();
                }
            }).start();
        }
    }

    private static void incCounter(Map<Integer, Integer> counterMap, int key) {
        counterMap.put(key, counterMap.getOrDefault(key, 0) + 1);
    }

    private static List<Entry<Integer, Integer>> toPairs(LinearApproxList approximations) {
        return EntryStream.of(
                StreamEx.of(approximations).flatMap(approx -> StreamEx.of(approx.getA()).cross(approx.getBetas()))
        )
                .mapValues(Pair::getKey)
                .collect(Collectors.toList());
    }
}
