package com.fillumina.tsid.collision.test;

import com.fillumina.tsid.collision.test.Stats;
import com.github.f4b6a3.tsid.Tsid;
import com.github.f4b6a3.tsid.TsidFactory;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TestPerformer {

    private final int threadCount;
    private final int iterationCount;
    private final int repetitions;
    private int counter = 1;

    public static class Builder {
        private int threadCount = 1;
        private int iterationCount = 10_000;
        private int repetitions = 100;

        private Builder() {
        }

        public Builder threadCount(final int value) {
            this.threadCount = value;
            return this;
        }

        public Builder iterationCount(final int value) {
            this.iterationCount = value;
            return this;
        }

        public Builder repetitions(final int value) {
            this.repetitions = value;
            return this;
        }

        public TestPerformer build() {
            return new TestPerformer(threadCount, iterationCount, repetitions);
        }
    }

    public static TestPerformer.Builder builder() {
        return new TestPerformer.Builder();
    }

    private TestPerformer(final int threadCount, final int iterationCount, final int repetitions) {
        this.threadCount = threadCount;
        this.iterationCount = iterationCount;
        this.repetitions = repetitions;
    }

    public void printTest(String description, String note, String factoryDescription,
            Function<Integer, TsidFactory> factorySupplier) {

        System.out.println("\nTest " + counter + ": "  + description + "\n " +
                factoryDescription + "\n " +
                test(factorySupplier).toString() + "\n " +
                note);

        counter++;
    }

    public Stats test(Function<Integer, TsidFactory> factorySupplier) {
        Stats total = Stats.EMPTY;
        for (int i=0; i<repetitions; i++) {
            long startNanos = System.nanoTime();
            Tsid[][] array = executeTest(factorySupplier);
            final Stats stats = createStats(startNanos, array);
            total = total.add(stats);
        }
        return total;
    }

    private Tsid[][] executeTest(Function<Integer,TsidFactory> factoryFunction) {
        CountDownLatch endLatch = new CountDownLatch(threadCount);
        Tsid[][] array = new Tsid[threadCount][iterationCount];
        for (int thread = 0; thread < threadCount; thread++) {
            final int threadId = thread;
            new Thread(() -> {
                TsidFactory factory = factoryFunction.apply(threadId);
                for (int iteration = 0; iteration < iterationCount; iteration++) {
                    Tsid tsid = factory.create();
                    array[threadId][iteration] = tsid;
                }
                endLatch.countDown();
            }).start();
        }
        try {
            endLatch.await();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        return array;
    }

    private Stats createStats(long startNanos, Tsid[][] array) {
        long elapsedNanos = System.nanoTime() - startNanos;
        final long elapsedMillis = Math.round(elapsedNanos / 1_000_000.0);

        int duplicatedCounter = 0;
        final int size = threadCount * iterationCount;
        Set<Tsid> set = new HashSet<>(size);
        int sequentialCounter = 0;
        for (int thread = 0; thread < threadCount; thread++) {
            Tsid last = null;
            for (int iteration = 0; iteration < iterationCount; iteration++) {
                Tsid tsid = array[thread][iteration];
                if (!set.add(tsid)) {
                    duplicatedCounter++;
                }
                if (last == null || last.toLong() < tsid.toLong()) {
                    sequentialCounter++;
                }
                last = tsid;
            }
        }

        return new Stats(size, duplicatedCounter, elapsedMillis, sequentialCounter);
    }

}
