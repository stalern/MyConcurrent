package base.basemethod;

/**
 * Thread.interrupt
 * @author stalern
 * @date 2020/01/30~20:22
 */
public class InterruptDemo {

    public static void main(String[] args) {
        Thread thread = new Thread(()->{
            while(true) {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("被中断但是运行");
                } else {
                    System.out.println("未被中断运行");
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    System.out.println("被中断了，准备停止");
                    return;
                }
            }
        });
        thread.start();
        try {
            Thread.sleep(1);
            thread.interrupt();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("主线程结束");

    }
}
