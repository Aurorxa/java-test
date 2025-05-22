package com.github;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static java.util.stream.Collectors.*;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Fork(3)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 5, time = 1)
public class ConcurrentTest {

    private static final int SIZE = 1000000 ;
    private int[] numbers;

    @Setup
    public void setup() {
        numbers = new int[SIZE];
        for (int i = 0; i < SIZE; i++) {
            numbers[i] = ThreadLocalRandom
                    .current()
                    .nextInt(100_000);
        }
    }

    /**
     * 不可变的思想，每次都产生新的值，然后将新的值覆盖旧的值
     */
    @Benchmark
    public Map<Integer, Integer> loopMerge() {
        Map<Integer, Integer> map = new HashMap<>();
        for (int number : numbers) {
            // 如果 Map 中没有改元素，则设置为 1
            // 如果 Map 中已经有了该元素，则在上一次的基础上累加
            map.merge(number, 1, Integer::sum);
        }
        return map;
    }

    /**
     * 可变的思想，值始终都是同一个 AtomicInteger 对象，变化的是 AtomicInteger 对象内部维护的数据
     */
    @Benchmark
    public Map<Integer, AtomicInteger> loopComputeIfAbsent() {
        Map<Integer, AtomicInteger> map = new HashMap<>();
        for (int number : numbers) {
            // 如果 Map 中没有该元素，则设置 AtomicInteger 对象
            // 如果 Map 中有改元素，则将 AtomicInteger 对象内部维护的数据 + 1 ，并设置新的 AtomicInteger 对象
            map.computeIfAbsent(number, k -> new AtomicInteger()).getAndIncrement();
        }
        return map;
    }

    /**
     * 单线程进行收集：数字相同的分到一组，然后计算总数
     */
    @Benchmark
    public Map<Integer, Long> sequence() {

        return Arrays.stream(numbers).boxed()
                .collect(groupingBy(Function.identity(), counting()));
    }

    /**
     * 多线程没有使用并发容器进行收集：数字相同的分到一组，然后计算总数
     */
    @Benchmark
    public Map<Integer, Long> parallelNoConcurrent() {
        return Arrays
                .stream(numbers).boxed()
                .parallel()
                .collect(groupingBy(Function.identity(), counting()));
    }

    /**
     * 多线程使用了并发容器进行收集：数字相同的分到一组，然后计算总数
     */
    @Benchmark
    public ConcurrentMap<Integer, Long> parallelConcurrent() {
        return Arrays.stream(numbers).boxed()
                .parallel()
                .collect(groupingByConcurrent(Function.identity(), counting()));
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(ConcurrentTest.class.getSimpleName())
                .result("result.json")
                .resultFormat(ResultFormatType.JSON)
                .build();

        new Runner(opt).run();
    }

}
