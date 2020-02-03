package base.basemethod;

/**
 * Thread.join
 * @author stalern
 * @date 2020/01/30~20:00
 */
public class JoinDemo {
    /**
     * 打印当前线程的消息
     * @param message 要打印的消息
     */
    static void threadMessage(String message) {
        String threadName = Thread.currentThread().getName();
        System.out.format("%s: %s%n", threadName, message);
    }

    private static class MessageLoop implements Runnable {
        @Override
        public void run() {
            String[] importantInfo = {
                    "Mares eat oats",
                    "Does eat oats",
                    "Little lambs eat ivy",
                    "A kid will eat ivy too"
            };
            try {
                for (String s : importantInfo) {
                    // 睡眠4s
                    Thread.sleep(4000);
                    // 打印消息
                    threadMessage(s);
                }
            } catch (InterruptedException e) {
                threadMessage("I wasn't done!");
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {

        // main线程耐心时间，默认1hour，可以通过args修改
        long patience = 1000 * 60 * 60;

//        args = new String[]{"1"};

        // 如果存在命令行参数
        if (args.length > 0) {
            try {
                patience = Long.parseLong(args[0]) * 1000;
            } catch (NumberFormatException e) {
                System.err.println("Argument must be an integer.");
                System.exit(1);
            }
        }

        threadMessage("Starting MessageLoop thread");
        long startTime = System.currentTimeMillis();
        Thread t = new Thread(new MessageLoop());
        t.start();

        threadMessage("Waiting for MessageLoop thread to finish");
        // 直到t线程退出
        while (t.isAlive()) {
            threadMessage("Still waiting...");
            // 最长main线程让出1s
            t.join(1000);
            // 时间间隔比耐心长
            if (((System.currentTimeMillis() - startTime) > patience)
                    && t.isAlive()) {
                threadMessage("Tired of waiting!");
                t.interrupt();
                // 不会等待太长时间
                // 无限期等待
                t.join();
            }
        }
        threadMessage("Finally!");
    }
}
