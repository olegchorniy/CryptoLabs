package ipt.lab.crypt.lab2;

import com.google.gson.reflect.TypeToken;
import ipt.lab.crypt.common.Constants;
import ipt.lab.crypt.common.utils.BlockUtils;
import ipt.lab.crypt.common.utils.PrintUtils;
import ipt.lab.crypt.common.utils.SerializationUtil;
import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

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

    public static final Path MAPS_DIR = Constants.BASE_DIR.resolve("maps");

    public static final TypeToken<? extends Pair<Map<Integer, Integer>, Map<Integer, Integer>>> MAPS_PAIR_TYPE
            = new TypeToken<MutablePair<Map<Integer, Integer>, Map<Integer, Integer>>>() {
    };

    private static final int ACTUAL_KEY = 0x26e4;
    private static final int CONCURRENCY = 3;

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        LinearApproxList approximations = SerializationUtil.deserialize(PAIRS_PATH, LinearApproxList.class);

        for (LinearApproxList.LinearApprox a : approximations) {
            a.getBetas().stream()
                    .filter(p -> BlockUtils.allSubBlocksActive(p.getKey()))
                    .forEach(p -> {
                        System.out.printf(
                                "a = %s, b = %s, LP = %.7f%n",
                                PrintUtils.toHexAsShort(a.getA()),
                                PrintUtils.toHexAsShort(p.getKey()),
                                p.getValue()
                        );
                    });
        }

        if (true) return;

        BlockingQueue<Entry<Integer, Integer>> tasks = new LinkedBlockingQueue<>(toPairs(approximations));

        LongAdder approxTried = new LongAdder();

        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENCY);
        List<Future<Pair<Map<Integer, Integer>, Map<Integer, Integer>>>> results = new ArrayList<>();

        for (int i = 0; i < CONCURRENCY; i++) {
            results.add(executor.submit(() -> {

                Map<Integer, Integer> actualKeyPositions = new HashMap<>();
                Map<Integer, Integer> skippedKeysAmounts = new HashMap<>();

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

                    int actualKeyPosition = 0;
                    int skippedKeysAmount = 0;

                    for (Integer counter : counterToKeys.descendingKeySet()) {
                        actualKeyPosition++;

                        Set<Integer> keys = counterToKeys.get(counter);

                        if (keys.contains(ACTUAL_KEY)) {
                            break;
                        } else {
                            skippedKeysAmount += keys.size();
                        }
                    }

                    incCounter(skippedKeysAmounts, skippedKeysAmount);
                    incCounter(actualKeyPositions, actualKeyPosition);

                    approxTried.increment();

                    System.out.printf("#%d, a = %s, b = %s, candidates = %d, key position = %d, skipped keys = %d, time = %d%n",
                            approxTried.sum(),
                            PrintUtils.toHexAsShort(a),
                            PrintUtils.toHexAsShort(b),
                            candidates.size(),
                            actualKeyPosition,
                            skippedKeysAmount,
                            sw.getTime()
                    );

                    sw.reset();
                }

                return MutablePair.of(actualKeyPositions, skippedKeysAmounts);
            }));
        }

        for (Future<Pair<Map<Integer, Integer>, Map<Integer, Integer>>> future : results) {
            //JsonUtil.serialize(tempFile(), future.get());
            System.out.println(future.get());
        }

        executor.shutdown();
    }

    private static Path tempFile() throws IOException {
        return Files.createTempFile(MAPS_DIR, null, ".json");
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
