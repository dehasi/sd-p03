package reflection.dymanicloading.commands;

import java.util.List;
import java.util.function.Function;

public class Sum implements Function<List<String>, String> {
    @Override public String apply(List<String> strings) {
        int a = Integer.parseInt(strings.get(0));
        int b = Integer.parseInt(strings.get(1));

        int result = a + b;

        return String.valueOf(result);
    }
}
