package reflection.dymanicloading;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;


/*

java -cp 'fib.jar:sum.jar:.'\
DynamicLoading.java  'reflection.dymanicloading.commands.Fib' 'reflection.dymanicloading.commands.Sum'

* */
class DynamicLoading {
    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        Map<String, Function<List<String>, String>> commands = loadCommands(
                "reflection.dymanicloading.commands.Fib",
                "reflection.dymanicloading.commands.Sum");

        while (true) {
            System.out.print("> ");
            String input = reader.readLine();
            List<String> list = stream(input.split("\\s+")).toList();
            var command = list.getFirst();

            if (commands.containsKey(command)) {
                List<String> params = list.subList(1, list.size());
                Function<List<String>, String> function = commands.get(command);
                String result = function.apply(params);
                System.out.println(result);

            } else if (input.startsWith("help")) {
                System.out.println(commands.keySet().stream().sorted().collect(joining("\n")));
            } else if (input.startsWith("exit")) {
                break;
            } else {
                System.err.println("Unknown command, please use 'help'");
            }
        }
    }

    private static Map<String, Function<List<String>, String>> loadCommands(String... args) throws Exception {
        Map<String, Function<List<String>, String>> commands = new HashMap<>();
        for (String arg : args) {
            Class<?> clazz = Class.forName(arg);
            @SuppressWarnings("unchecked") // class implements Function<> by convention
            var command = (Function<List<String>, String>) clazz.getDeclaredConstructor().newInstance();
            commands.put(clazz.getSimpleName().toLowerCase(), command);
        }
        return commands;
    }
}
