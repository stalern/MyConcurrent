package core.program;

import java.util.concurrent.Semaphore;

/**
 * 生产者消费者模型
 * @author stalern
 * @date 2020/02/03~23:43
 */
public class ProAndCon {

    private static final int BUFFER = 1024;
    private static Semaphore empty = new Semaphore(BUFFER);
    private static Semaphore full = new Semaphore(0);
    private static Semaphore mutex = new Semaphore(1);
    private static int in = 1;
    private static int out = 1;

    private static class Producer implements Runnable {

        @Override
        public void run() {
            try {
                empty.acquire();
                mutex.acquire();
                System.out.println("生产" + in);
                in = (in + 1) % BUFFER;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                mutex.release();
                full.release();
            }
        }
    }
    private static class Consumer implements Runnable {

        @Override
        public void run() {
            try {
                full.acquire();
                mutex.acquire();
                System.out.println("消费" + out);
                out = (out + 1) % BUFFER;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                mutex.release();
                empty.release();
            }
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            new Thread(new Producer()).start();
            new Thread(new Consumer()).start();
        }
    }
}
