package com.fillumina.tsid.collision.test;



/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
record Stats(int size, int duplicatedCounter, long elapsedMillis, int sequentialCounter) {

    public static final Stats EMPTY = new Stats(0, 0, 0, 0);

    public Stats add(Stats stats) {
        return new Stats(
                this.size + stats.size,
                this.duplicatedCounter + stats.duplicatedCounter,
                this.elapsedMillis + stats.elapsedMillis,
                this.sequentialCounter + stats.sequentialCounter);
    }

    public double getOperationPerMillis() {
        return Math.round(size * 1.0 / elapsedMillis);
    }

    public boolean isSequential() {
        return sequentialCounter == size;
    }

    @Override
    public String toString() {
        return "duplicates: " + duplicatedCounter + ", op/ms: " + getOperationPerMillis() +
                ", sequential: " + isSequential();
    }
}
