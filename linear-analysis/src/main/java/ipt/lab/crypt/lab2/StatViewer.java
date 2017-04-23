package ipt.lab.crypt.lab2;

import ipt.lab.crypt.common.utils.JsonUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class StatViewer {

    public static void main(String[] args) throws IOException {

        SortedMap<Integer, Integer> mergedPositions = new TreeMap<>();
        SortedMap<Integer, Integer> mergedSkippedKeys = new TreeMap<>();

        Files.walk(LinearAttackRunner.MAPS_DIR)
                .filter(Files::isRegularFile)
                .map(file -> JsonUtil.deserialize(file, LinearAttackRunner.MAPS_PAIR_TYPE))
                .forEach(maps -> {
                    merge(mergedPositions, maps.getLeft());
                    merge(mergedSkippedKeys, maps.getRight());
                });

        mergedPositions.forEach((position, counter) -> System.out.println(position + "\t" + counter));

        System.out.println();

        mergedSkippedKeys.forEach((skippedKeys, counter) -> System.out.println(skippedKeys + "\t" + counter));
    }

    private static void merge(Map<Integer, Integer> aggregator, Map<Integer, Integer> holder) {
        holder.forEach((k, v) -> aggregator.merge(k, v, Integer::sum));
    }
}
