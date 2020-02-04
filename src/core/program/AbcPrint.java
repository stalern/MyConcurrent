package core.program;

/**
 * 循环打印ABC
 *  两种思路，一个是开三种不同的线程，分别代表打印ABC，当A打印完后唤醒B，B打印完后唤醒C，C打印完后唤醒A
 *  另外一种如下所示
 * @author stalern
 * @date 2020/02/04~14:58
 */
public class AbcPrint {
    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            new Thread(new Print()).start();
        }
    }

    static int i = 0;

    private static class Print implements Runnable {

        private static int BASE_NUMBER = 65;

        @Override
        public void run() {
            synchronized (Print.class) {
                System.out.println(Thread.currentThread().getName() + " "+ (char) (BASE_NUMBER + i));
                i = (i + 1) % 3;
            }
        }
    }
}
