package base.createthread;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * @author stalern
 * @date 2020/02/02~13:24
 */
public class Launcher {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Thread thread = new ExtendThread();
        thread.start();
        Thread thread1 = new Thread(new ImplRunnable());
        thread1.start();

        FutureTask<String> task = new FutureTask<>(new ImplCallable());
        Thread thread2 = new Thread(task);
        thread2.start();
        System.out.println(task.get());
    }
}
