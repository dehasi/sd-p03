package actors;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

class actors {
    public static void main(String[] args) throws Exception {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        PrintActor printActor = new PrintActor();
        SumActor sumActor = new SumActor(printActor);
        FibActor fibActor = new FibActor(printActor);
        Map<String, AbstractActor> commands = Map.of("sum", sumActor, "fib", fibActor);

        List<AbstractActor> actors = List.of(printActor, sumActor, fibActor);
        actors.forEach(actor -> actor.thread.start());

        while (true) {
            System.out.print("> ");
            String input = reader.readLine();
            List<String> list = stream(input.split("\\s+")).toList();
            var command = list.getFirst();

            if (commands.containsKey(command)) {
                var actor = commands.get(command);
                AbstractActor.send(actor, /*message=*/list);

            } else if (input.startsWith("help")) {
                System.out.println(commands.keySet().stream().sorted().collect(joining("\n")));
            } else if (input.startsWith("exit")) {
                actors.forEach(actor -> {
                    AbstractActor.send(actor, List.of("die"));
                    //  actor.thread.join(); skipped
                });
                break;
            } else {
                System.err.println("Unknown command, please use 'help'");
            }
        }
    }

    static class SumActor extends AbstractActor {
        final AbstractActor printer;

        SumActor(AbstractActor printActor) {
            this.printer = printActor;
        }

        @Override void dispatch(List<String> message) {
            String command = message.getFirst();
            if (command.equals("sum")) {
                int a = Integer.parseInt(message.get(1));
                int b = Integer.parseInt(message.get(2));
                String result = String.format("sum(%s, %s) = %s", a, b, a + b);
                send(printer, List.of("print", result));
            }
        }
    }

    static class FibActor extends AbstractActor {
        final AbstractActor printer;

        FibActor(AbstractActor printActor) {
            this.printer = printActor;
        }

        @Override void dispatch(List<String> message) {
            String command = message.getFirst();
            if (command.equals("fib")) {
                int num = Integer.parseInt(message.get(1));
                String result = String.format("fib(%s) = %s", num, fib(num));
                send(printer, List.of("print", result));
            }
        }

        int fib(int n) {
            if (n < 2) return 1;
            return fib(n - 1) + fib(n - 2);
        }
    }

    static class PrintActor extends AbstractActor {
        @Override void dispatch(List<String> message) {
            String command = message.getFirst();
            if (command.equals("print")) {
                System.out.println(message.stream().skip(1).collect(joining("\n")));
            }
        }
    }
}
