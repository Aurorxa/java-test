package com.github;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Fork(3)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 5, time = 1)
public class SumTest {

    private static final int SIZE = 100;
    private int[] numbers;
    private List<Integer> numberList;

    @Setup
    public void setup() {
        numbers = new int[SIZE];
        for (int i = 0; i < SIZE; i++) {
            numbers[i] = i;
        }
        numberList = new ArrayList<>(SIZE);
        for (int i = 0; i < SIZE; i++) {
            numberList.add(i);
        }
    }

    /**
     * 使用循环对 int 求和
     */
    @Benchmark
    public int primitive() {
        int sum = 0;
        for (int number : numbers) {
            sum += number;
        }
        return sum;
    }

    /**
     * 使用循环对 Integer 求和
     */
    @Benchmark
    public int boxed() {
        int sum = 0;
        for (Integer i : numberList) {
            sum += i;
        }
        return sum;
    }

    /**
     * 使用 Stream 对 Integer 求和
     */
    @Benchmark
    public int stream() {
        return numberList
                .stream()
                .reduce(0, Integer::sum);
    }

    /**
     * 使用 IntStream 对 int 求和
     */
    @Benchmark
    public int intStream() {
        return IntStream
                .of(numbers)
                .sum();
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(SumTest.class.getSimpleName())
                .result("result.json")
                .resultFormat(ResultFormatType.JSON)
                .build();

        new Runner(opt).run();
    }

}
