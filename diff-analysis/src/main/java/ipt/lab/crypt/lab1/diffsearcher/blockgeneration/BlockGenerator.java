package ipt.lab.crypt.lab1.diffsearcher.blockgeneration;

import java.util.NoSuchElementException;

class BlockGenerator {

    private static final int MAX_NON_ZERO_SUB_BLOCKS = 3;
    private static final int BIT_PER_SUB_BLOCK = 4;
    private static final int SUB_BLOCKS = 4;

    private static final int MIN_SUB_BLOCK_VALUE = 0x1;
    private static final int MAX_SUB_BLOCK_VALUE = 0xF;

    //        nonZeroSubBlocks               combinationGenerator                    tupleGenerator
    // number of non-zero sub-blocks -> indices of non-zero sub-blocks -> concrete values placed at that indices

    private final int maxNonZeroSubBlocks;
    private int nonZeroSubBlocks; //increases up to 3

    private int[] combinationHolder;
    private CombinationGenerator combinationGenerator;

    private int[] tupleHolder;
    private TupleGenerator tupleGenerator;

    public BlockGenerator() {
        this(MAX_NON_ZERO_SUB_BLOCKS);
    }

    public BlockGenerator(int maxNonZeroSubBlocks) {
        this.maxNonZeroSubBlocks = maxNonZeroSubBlocks;
        nextSubBlocksAmount();
    }

    public boolean hasNext() {
        return tupleGenerator.hasNext() ||
                combinationGenerator.hasNext() ||
                nonZeroSubBlocks < maxNonZeroSubBlocks;
    }

    public int next() {
        //1. check the current tuple generator
        if (tupleGenerator.hasNext()) {
            return nextBlock();
        }

        if (combinationGenerator.hasNext()) {
            combinationGenerator.next(combinationHolder);

            tupleGenerator = new TupleGenerator(nonZeroSubBlocks, MIN_SUB_BLOCK_VALUE, MAX_SUB_BLOCK_VALUE);

            return nextBlock();
        }

        if (nonZeroSubBlocks == maxNonZeroSubBlocks) {
            throw new NoSuchElementException();
        }

        nextSubBlocksAmount();
        return nextBlock();
    }

    private int nextBlock() {
        tupleGenerator.next(tupleHolder);
        return createBlock();
    }

    private int createBlock() {
        int block = 0;

        for (int i = 0; i < nonZeroSubBlocks; i++) {
            int subBlockIndex = combinationHolder[i];
            int subBlockValue = tupleHolder[i];

            block |= ((subBlockValue) << (subBlockIndex * BIT_PER_SUB_BLOCK));
        }

        return block;
    }

    private void nextSubBlocksAmount() {
        nonZeroSubBlocks++;

        combinationHolder = new int[nonZeroSubBlocks];
        tupleHolder = new int[nonZeroSubBlocks];

        combinationGenerator = new CombinationGenerator(SUB_BLOCKS, nonZeroSubBlocks);
        tupleGenerator = new TupleGenerator(nonZeroSubBlocks, MIN_SUB_BLOCK_VALUE, MAX_SUB_BLOCK_VALUE);

        combinationGenerator.next(combinationHolder);
    }
}
