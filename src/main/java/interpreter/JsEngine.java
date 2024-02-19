package interpreter;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import javax.script.ScriptException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class JsEngine {
    public static void main(String[] args) throws ScriptException {
        var interpreter = createInterpreter();

        // language=JavaScript
        var script = """
                // we expect that variable 'num' is set from Java
                console.log('Hello from JS')
                const fib = (n) => {
                    if(n < 2) return 1
                    else return fib(n-1) + fib(n-2)
                }
                console.log(`calculating fib(${num})`)
                fib(num);
                """;

        interpreter.getBindings("js")
                .putMember("num", 9);

        Value result = interpreter.eval("js", script);
        System.out.println(result.asInt());

        interpreter.close();
    }

    private static Context createInterpreter() {
        System.setProperty("polyglot.engine.WarnInterpreterOnly", "false");
        return Context.newBuilder("js")
                .allowAllAccess(true) // if not allow, you still cannot access the array/list/object members
                .build();
    }
}
