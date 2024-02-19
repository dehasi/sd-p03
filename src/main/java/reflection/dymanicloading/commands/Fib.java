package reflection.dymanicloading.commands;

import java.util.List;
import java.util.function.Function;

public class Fib implements Function<List<String>, String> {

    @Override public String apply(List<String> strings) {
        int num = Integer.parseInt(strings.getFirst());

        int result = fib(num);

        return String.valueOf(result);
    }

    int fib(int num) {
        if (num < 2) return 1;
        return fib(num - 1) + fib(num - 2);
    }
}
