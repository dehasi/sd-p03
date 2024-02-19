package actors;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

abstract class AbstractActor implements Runnable {

    String name;
    BlockingQueue<List<String>> queue;
    boolean stop_me;
    Thread thread;

    public AbstractActor() {
        name = this.getClass().getSimpleName();
        queue = new ArrayBlockingQueue<>(5);
        stop_me = false;
        thread = new Thread(this);
    }

    @Override public void run() {
        while (!stop_me) {
            List<String> message = takeFrom(queue);
            dispatch(message);
            if (message.getFirst().equals("die")) {
                stop_me = true;
                System.err.println("stopping " + name);
            }
        }
    }

    abstract void dispatch(List<String> message);

    static void send(AbstractActor receiver, List<String> message) {
        receiver.queue.add(message);
    }

    static <E> E takeFrom(BlockingQueue<E> queue) {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
