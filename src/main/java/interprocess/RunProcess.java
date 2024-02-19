package interprocess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

class RunProcess {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        Map<String, Function<List<String>, String>> commands = loadCommands(
                "/Users/ravil/experimental/sd-course/sd-p03/src/main/cpp/fib",
                "/Users/ravil/experimental/sd-course/sd-p03/src/main/cpp/sum");

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

    private static Map<String, Function<List<String>, String>> loadCommands(String... args){
        Map<String, Function<List<String>, String>> commands = new HashMap<>();
        for (String arg : args) {
            String name = arg.substring(arg.lastIndexOf('/') + 1);
            Function<List<String>, String> function = params -> {
                List<String> command = new ArrayList<>(List.of(arg));
                command.addAll(params);

                try {
                    Process process = Runtime.getRuntime().exec(command.toArray(new String[0]));
                    try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        return input.lines().collect(joining("\n"));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
            commands.put(name, function);
        }
        return commands;
    }

}
