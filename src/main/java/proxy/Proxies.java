package proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

class Proxies {
    public static void main(String[] args) {
        Fib original_fib = new FibImpl();

        Fib fib = (Fib) Proxy.newProxyInstance(Proxies.class.getClassLoader(),
                new Class[]{Fib.class}, new FibCachingProxy(original_fib));

        System.out.println(fib.fib(4));
        System.out.println(fib.fib(4));
        System.out.println(fib.fib(4));
    }

    static class InvocationLogger implements InvocationHandler {

        final Object original;

        InvocationLogger(Object original) {
            this.original = original;
        }

        @Override public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("before method call : " + method.getName());
            Object result = method.invoke(original, args);
            System.out.println("after method call : " + method.getName());
            return result;
        }
    }

    static class FibCachingProxy implements InvocationHandler {

        final Fib original;
        Map<Object, Object> cache = new HashMap<>();

        private FibCachingProxy(Fib original) {
            this.original = original;
        }

        @Override public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (cache.containsKey(args[0])) {
                System.err.println("Return result from proxy");
                return cache.get(args[0]);
            } else {
                var result = method.invoke(original, args);
                cache.put(args[0], result);
                System.err.println("Save result into proxy");
                return result;
            }
        }
    }

    static class OneArgCachingProxy implements InvocationHandler {

        final Object original;
        final Map<Method, Map<Object, Object>> cache = new HashMap<>();

        private OneArgCachingProxy(Object original) {
            this.original = original;
        }

        @SuppressWarnings("unchecked") static <INTERFACE, TYPE extends INTERFACE> TYPE createFor(TYPE object, Class<INTERFACE> interfaceClass) {
            return (TYPE) Proxy.newProxyInstance(
                    OneArgCachingProxy.class.getClassLoader(),
                    new Class[]{interfaceClass},
                    new OneArgCachingProxy(object));
        }

        @Override public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (args.length == 1) {
                if (cache.containsKey(method) && cache.get(method).containsKey(args[0])) {
                    System.err.println("Return result from proxy");
                    return cache.get(method).get(args[0]);
                } else {
                    var result = method.invoke(original, args);
                    cache.putIfAbsent(method, new HashMap<>());
                    cache.get(method).put(args[0], result);
                    System.err.println("Save result into proxy");
                    return result;
                }
            }
            return method.invoke(original, args);
        }
    }
}
