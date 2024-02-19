package interpreter;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

/*
/Users/ravil/experimental/sd-course/sd-p03/src/main/java/interpreter/fib.js
/Users/ravil/experimental/sd-course/sd-p03/src/main/java/interpreter/sum.js
 */
class LoadJs {
    public static void main(String[] args) throws IOException {
        Map<String, Script> commands = loadScripts(args);
        var interpreter = createInterpreter();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.print("> ");
            String input = reader.readLine();
            List<String> list = stream(input.split("\\s+")).toList();
            var command = list.getFirst();

            if (commands.containsKey(command)) {
                List<String> command_args = list.subList(1, list.size());
                Script script = commands.get(command);

                interpreter.getBindings("js").putMember("args", command_args);

                Value result = interpreter.eval("js", script.text());
                System.out.println(result.asString());

            } else if (input.startsWith("help")) {
                System.out.println(commands.keySet().stream().sorted().collect(joining("\n")));
            } else if (input.startsWith("exit")) {
                break;
            } else {
                System.err.println("Unknown command, please use 'help'");
            }
        }

        interpreter.close();
    }

    private static Map<String, Script> loadScripts(String[] args) {
        return stream(args).collect(toMap(
                arg -> name(arg), arg -> new Script(script(arg))));
    }

    private static String name(String arg) {
        return Path.of(arg).getFileName().toString().replaceAll(".js", "");
    }

    private static String script(String arg) {
        try (var lines = Files.lines(Path.of(arg))) {
            return lines.collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Context createInterpreter() {
        System.setProperty("polyglot.engine.WarnInterpreterOnly", "false");
        return Context.newBuilder("js")
                .allowAllAccess(true)
                .build();
    }

    private record Script(String text) {}
}
