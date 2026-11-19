package org.example;

import java.io.*;
import java.util.Arrays;

public class Experiment {
    private static final int TRIALS = 10;
    private static final int[] CUTOFFS = {1, 2, 4, 6, 7, 8, 10, 12, 15, 20, 25, 30, 40, 50};

    private static Integer[] loadArray(String filename) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            int n = Integer.parseInt(br.readLine().trim());
            Integer[] a = new Integer[n];
            for (int i = 0; i < n; i++) {
                a[i] = Integer.parseInt(br.readLine().trim());
            }
            return a;
        }
    }

    private static boolean isSorted(Comparable[] a) {
        for (int i = 1; i < a.length; i++) {
            if (a[i].compareTo(a[i - 1]) < 0) return false;
        }
        return true;
    }

    private static void printSystemInfo() {
        System.out.println("=== THONG TIN MOI TRUONG ===");
        System.out.println("OS: " + System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ")");
        System.out.println("Java Version: " + System.getProperty("java.version"));
        System.out.println("CPU Cores (Logical): " + Runtime.getRuntime().availableProcessors());
        System.out.println("Max Heap (MB): " + (Runtime.getRuntime().maxMemory() / (1024 * 1024)));
        System.out.println("============================\n");
    }

    public static void main(String[] args) throws IOException {
        printSystemInfo();

        File resDir = new File("results");
        if (!resDir.exists()) resDir.mkdirs();

        String[][] datasets = {
                {"Random",       "data/random_1M.txt"},
                {"NearlySorted", "data/nearly_sorted_1M.txt"},
                {"Reversed",     "data/reversed_1M.txt"},
                {"Duplicates",   "data/duplicates_1M.txt"}
        };

        // Warm-up JVM để JIT Compiler tối ưu mã trước khi đo thật
        System.out.println("Dang chay Warm-up JVM...");
        Integer[] warmup = loadArray(datasets[0][1]);
        Merge.CUTOFF = 7;
        for (int w = 0; w < 5; w++) {
            Merge.sort(Arrays.copyOf(warmup, warmup.length));
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter("results/results.csv"))) {
            pw.println("Dataset,N,Cutoff,Mean_ms,StdDev_ms,Min_ms,Max_ms");

            for (String[] ds : datasets) {
                String name = ds[0];
                Integer[] baseData = loadArray(ds[1]);
                System.out.println("\n--- Bo du lieu: " + name + " (N = " + baseData.length + ") ---");

                for (int c : CUTOFFS) {
                    Merge.CUTOFF = c;
                    double[] times = new double[TRIALS];

                    for (int t = 0; t < TRIALS; t++) {
                        Integer[] arr = Arrays.copyOf(baseData, baseData.length);
                        long start = System.nanoTime();
                        Merge.sort(arr);
                        long end = System.nanoTime();

                        if (!isSorted(arr)) {
                            throw new RuntimeException("Loi sap xep sai tai CUTOFF = " + c);
                        }
                        times[t] = (end - start) / 1_000_000.0;
                    }

                    double sum = 0, min = Double.MAX_VALUE, max = Double.MIN_VALUE;
                    for (double time : times) {
                        sum += time;
                        if (time < min) min = time;
                        if (time > max) max = time;
                    }
                    double mean = sum / TRIALS;

                    double sqDiff = 0;
                    for (double time : times) sqDiff += Math.pow(time - mean, 2);
                    double stdDev = Math.sqrt(sqDiff / (TRIALS - 1));

                    System.out.printf("CUTOFF = %2d | Mean: %8.3f ms | StdDev: %6.3f ms | Min: %8.3f | Max: %8.3f%n",
                            c, mean, stdDev, min, max);
                    pw.printf("%s,%d,%d,%.4f,%.4f,%.4f,%.4f%n",
                            name, baseData.length, c, mean, stdDev, min, max);
                }
            }
        }
        System.out.println("\nHoan thanh! Ket qua da luu tai results/results.csv");
    }
}