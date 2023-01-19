package com.fillumina.tsid.collision.test;

import com.github.f4b6a3.tsid.TsidFactory;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TsidCollisionTestApp {

    public static void main(String[] args) throws InterruptedException {

        TestPerformer tp = TestPerformer.builder()
                .threadCount(32)
                .iterationCount(100_000)
                .repetitions(5)
                .build();

        tp.printTest("Creates a new default TSID factory for each thread",
                "duplication present because of not enough random bits available",
                "i -> TsidFactory.newInstance1024()",
                i -> TsidFactory.newInstance1024());

        TsidFactory sharedInstance1024 = TsidFactory.newInstance1024();
        tp.printTest("Shares the same default TSID factory for each thread",
                "slow because of contention accessing the TSID generator",
                "i -> sharedInstance1024",
                i -> sharedInstance1024);

        tp.printTest("Creates a new node TSID factory for each thread with the same node id",
                "duplications because generators use the same node id",
                "i -> TsidFactory.newInstance1024(0)",
                i -> TsidFactory.newInstance1024(0));

        // THIS IS THE RIGHT STRATEGY FOR SEQUENTIAL TSID ON THE SAME NODE!
        TsidFactory sharedInsstance1024Node0 = TsidFactory.newInstance1024(0);
        tp.printTest("Shares the same node TSID factory for each thread",
                "slow because of contention accessing the TSID generator",
                "i -> sharedInsstance1024Node0",
                i -> sharedInsstance1024Node0);

        tp.printTest("Use a different TSID factory for each thread",
                "fast because node ids are different so no overlapping",
                "i -> TsidFactory.newInstance1024(i)",
                i -> TsidFactory.newInstance1024(i));

        tp.printTest("Use a new thread local random TSID factory for each thread",
                "duplication present because of not enough random bits available",
                "i -> factoryCreator()",
                i -> factoryCreator());

        TsidFactory sharedFactory = factoryCreator();
        tp.printTest("Shares the same thread local random TSID factory for each thread",
                "slow because of contention accessing the TSID generator",
                "i -> sharedFactory",
                i -> sharedFactory);
    }

    private static TsidFactory factoryCreator() {
        return TsidFactory.builder()
                // generates the value of the node part.
                // being random the resulted TSID will NOT BE SEQUENTIAL!
                .withRandomFunction(length -> {
                    final byte[] bytes = new byte[length];
                    ThreadLocalRandom.current().nextBytes(bytes);
                    return bytes;
                }).build();
    }

}
