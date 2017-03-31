package ipt.lab.crypt.lab1.diffsearcher.blockgeneration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class BlocksDistributor {

    private final List<Integer> blocks = new ArrayList<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    public BlocksDistributor() {
        retrieveAllBlocks(new BlockGenerator());
    }

    public BlocksDistributor(int maxNonZeroSubBlocks) {
        retrieveAllBlocks(new BlockGenerator(maxNonZeroSubBlocks));
    }

    private void retrieveAllBlocks(BlockGenerator generator) {
        while (generator.hasNext()) {
            this.blocks.add(generator.next());
        }
    }

    public Optional<Integer> getIfAvailable() {
        int index = this.counter.incrementAndGet();
        if (index < blocks.size()) {
            return Optional.of(blocks.get(index));
        }

        return Optional.empty();
    }
}
