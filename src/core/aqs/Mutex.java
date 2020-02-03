package core.aqs;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 通过aqs实现自定义锁
 * @author stalern
 * @date 2020/02/03~13:57
 */
public class Mutex {
    private static class Sync extends AbstractQueuedSynchronizer {

        @Override
        protected boolean tryAcquire(int arg) {
            if (compareAndSetState(0,1)) {
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        @Override
        protected boolean tryRelease(int arg) {
            if (getState() == 0) {
                throw new IllegalMonitorStateException();
            }
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }

        boolean isLocked() {
            return getState() == 1;
        }
    }

    private Sync sync = new Sync();

    public void lock() {
        sync.acquire(1);
    }

    public void unlock() {
        sync.release(1);
    }

    static Mutex mutex = new Mutex();
    static int k = 0;
    static int f = 0;

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            new Thread(()-> increase()).start();
            new Thread(()-> lockIncrease()).start();

        }

        System.out.println("未加锁：" + k);
        System.out.println("加锁：" + f);
    }

    static void increase() {
        k ++;
    }

    static void lockIncrease() {
        mutex.lock();
        f ++;
        mutex.unlock();
    }
}
