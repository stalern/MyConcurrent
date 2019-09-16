import java.util.concurrent.locks.ReentrantLock;

/**
 * @author stalern
 * @date 2019年9月16日15:49:34
 * synchronized 的实现方式
 */
class MySynchronized {

    private int a = 0;
    private ReentrantLock lock = new ReentrantLock();

    void writer(int i){
        lock.lock();
        try{
            a += i;
        } finally {
            lock.unlock();
        }
    }

    int reader(){
        lock.lock();
        int i;
        try {
            i = a + 1;
        } finally {
            lock.unlock();
        }
        return i;
    }
}
