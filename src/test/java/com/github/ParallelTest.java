package com.github;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Fork(3)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 5, time = 1)
public class ParallelTest {

    private static final int SIZE = 1000000;
    private int[] numbers;

    private ExecutorService service;

    @Setup
    public void setup() {
        numbers = new int[SIZE];
        for (int i = 0; i < SIZE; i++) {
            numbers[i] = ThreadLocalRandom
                    .current()
                    .nextInt(100_000);
        }

        service = Executors.newFixedThreadPool(
                Math.max(1, Runtime
                        .getRuntime()
                        .availableProcessors()));
    }

    @TearDown
    public void tearDown() {
        service.shutdown();
    }

    /**
     * 使用循环求最大值
     */
    @Benchmark
    public int primitive() {
        int max = 0;
        for (int number : numbers) {
            if (number > max) {
                max = number;
            }
        }
        return max;
    }

    /**
     * 使用并行流对求最大值
     */
    @Benchmark
    public int parallel() {
        return IntStream
                .of(numbers)
                .parallel()
                .max()
                .orElse(0);
    }

    /**
     * 使用串行流求最大值
     */
    @Benchmark
    public int sequence() {
        return IntStream
                .of(numbers)
                .max()
                .orElse(0);
    }

    /**
     * 自定义多线程并行求最大值
     */
    @Benchmark
    public int custom() throws ExecutionException, InterruptedException {
        int SIZE = numbers.length;
        int threadCount = Runtime.getRuntime().availableProcessors();
        int chunk = (SIZE + threadCount - 1) / threadCount;

        List<Future<Integer>> futures = new ArrayList<>(threadCount);
        for (int start = 0; start < SIZE; start += chunk) {
            int end = Math.min(start + chunk, SIZE);
            int finalStart = start;
            futures.add(service.submit(() -> {
                int localMax = numbers[finalStart];
                for (int i = finalStart + 1; i < end; i++) {
                    if (numbers[i] > localMax) localMax = numbers[i];
                }
                return localMax;
            }));
        }

        int max = Integer.MIN_VALUE;
        for (Future<Integer> f : futures) {
            max = Math.max(max, f.get());
        }
        return max;
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(ParallelTest.class.getSimpleName())
                .result("result.json")
                .resultFormat(ResultFormatType.JSON)
                .build();

        new Runner(opt).run();
    }

}
