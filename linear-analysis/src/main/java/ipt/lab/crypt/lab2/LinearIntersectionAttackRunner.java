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
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static ipt.lab.crypt.lab2.LinearPotentialsSearch.PAIRS_PATH;

@SuppressWarnings("Duplicates")
public class LinearIntersectionAttackRunner {

    private static final int ACTUAL_KEY = 0x26e4;
    private static final int CONCURRENCY = 3;

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        LinearApproxList approximations = SerializationUtil.deserialize(PAIRS_PATH, LinearApproxList.class);

        List<Entry<Integer, Integer>> pairs = toPairs(approximations);
        Collections.shuffle(pairs);

        BlockingQueue<Entry<Integer, Integer>> tasks = new LinkedBlockingQueue<>(pairs);
        BlockingQueue<NavigableMap<Integer, Set<Integer>>> intermediateResults = new LinkedBlockingQueue<>();

        for (int i = 0; i < CONCURRENCY; i++) {
            //worker threads
            new Thread(() -> {

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

                    intermediateResults.add(counterToKeys);

                    System.out.printf("[%s] a = %s, b = %s, time = %d%n",
                            Thread.currentThread().getName(),
                            PrintUtils.toHexAsShort(a),
                            PrintUtils.toHexAsShort(b),
                            sw.getTime()
                    );

                    sw.reset();
                }
            }).start();
        }

        // updater thread
        new Thread(() -> {

            Map<Integer, Integer> hits = new HashMap<>();
            Consumer<Integer> incrementer = key -> incCounter(hits, key);

            int approxTried = 0;

            try {
                while (true) {

                    Iterator<Entry<Integer, Set<Integer>>> keyCountersIt = intermediateResults.take()
                            .descendingMap()
                            .entrySet()
                            .iterator();

                    for (int i = 0; i < 15 && keyCountersIt.hasNext(); i++) {
                        keyCountersIt.next().getValue().forEach(incrementer);
                    }

                    System.out.printf("[%s] #%d, hits = %s%n",
                            "Updater thread",
                            ++approxTried,
                            EntryStream.of(hits)
                                    .sorted(Entry.<Integer, Integer>comparingByValue().reversed())
                                    .limit(100)
                                    .map(e -> String.format("%s = %d", PrintUtils.toHexAsShort(e.getKey()), e.getValue()))
                                    .collect(Collectors.joining(", ", "[", "]"))
                    );
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
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
