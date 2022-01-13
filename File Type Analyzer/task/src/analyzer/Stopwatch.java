package analyzer;

public class Stopwatch {

    static private long start;

    static void reset() {
        start = System.nanoTime();
    }

    static double getElapsedSeconds() {

        long end = System.nanoTime();
        double elapsedNano = end - start;
        return elapsedNano / 1E9;
    }
}
