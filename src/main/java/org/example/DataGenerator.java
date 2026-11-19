package org.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class DataGenerator {
    private static final int N = 1_000_000;
    private static final long SEED = 2026L;

    public static void main(String[] args) throws IOException {
        File dir = new File("data");
        if (!dir.exists()) dir.mkdirs();

        saveArray("data/random_1M.txt", generateRandom(N, SEED));
        saveArray("data/nearly_sorted_1M.txt", generateNearlySorted(N, SEED));
        saveArray("data/reversed_1M.txt", generateReversed(N));
        saveArray("data/duplicates_1M.txt", generateDuplicates(N, SEED));

        System.out.println("Da sinh xong 4 bo du lieu trong thu muc data/");
    }

    private static int[] generateRandom(int n, long seed) {
        Random rand = new Random(seed);
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = rand.nextInt();
        return a;
    }

    private static int[] generateNearlySorted(int n, long seed) {
        Random rand = new Random(seed);
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = i;
        int swaps = (int) (n * 0.05);
        for (int s = 0; s < swaps; s++) {
            int i = rand.nextInt(n);
            int j = rand.nextInt(n);
            int tmp = a[i]; a[i] = a[j]; a[j] = tmp;
        }
        return a;
    }

    private static int[] generateReversed(int n) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = n - i;
        return a;
    }

    private static int[] generateDuplicates(int n, long seed) {
        Random rand = new Random(seed);
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = rand.nextInt(100);
        return a;
    }

    private static void saveArray(String filename, int[] a) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            bw.write(String.valueOf(a.length));
            bw.newLine();
            for (int val : a) {
                bw.write(String.valueOf(val));
                bw.newLine();
            }
        }
        System.out.println("Da tao: " + filename);
    }
}