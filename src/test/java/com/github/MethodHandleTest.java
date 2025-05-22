package com.github;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Fork(3)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 5, time = 1)
public class MethodHandleTest {

    static int add(int a, int b) {
        return a + b;
    }

    @Benchmark
    public int origin(){
        return add(1,2);
    }

    static Method method;
    static MethodHandle methodHandle;
    static {
        try {
            method = MethodHandleTest.class.getDeclaredMethod("add", int.class, int.class);
            methodHandle = MethodHandles
                    .lookup().unreflect(method);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    public Object reflection() throws InvocationTargetException, IllegalAccessException {
        return method.invoke(null, 1, 2);
    }

    @Benchmark
    public Object method() throws Throwable {
        return methodHandle.invoke(1, 2);
    }

    @Benchmark
    public int lambda() {
        return test(Integer::sum, 1, 2);
    }

    static int test(BiFunction<Integer,Integer,Integer> function, int a, int b) {
        return function.apply(a,b);
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(MethodHandleTest.class.getSimpleName())
                .result("result.json")
                .resultFormat(ResultFormatType.JSON)
                .build();

        new Runner(opt).run();
    }

}
