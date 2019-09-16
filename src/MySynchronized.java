import java.util.concurrent.locks.ReentrantLock;

/**
 * @author stalern
 * @date 2019年9月16日15:49:34
 * synchronized 的实现方式
 */
class MySynchronized {

    private int a = 0;
    private ReentrantLock lock = new ReentrantLock();

    void writer(){
        lock.lock();
        try{
            a ++;
        } finally {
            lock.unlock();
        }
    }

    void reader(){
        lock.lock();
        try {
            int i = a + 1;
        } finally {
            lock.unlock();
        }
    }
}
