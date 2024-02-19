package proxy;

class FibImpl implements Fib {

    @Override public int fib(int num) {
        System.out.printf("fib(%s) is called\n", num);
        if (num < 2) return 1;
        return fib(num - 1) + fib(num - 2);

    }
}
