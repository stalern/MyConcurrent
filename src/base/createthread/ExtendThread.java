package base.createthread;

/**
 * @author stalern
 * @date 2020/02/02~13:08
 */
public class ExtendThread extends Thread {
    @Override
    public void run() {
        System.out.println("extend Thread");
    }
}
