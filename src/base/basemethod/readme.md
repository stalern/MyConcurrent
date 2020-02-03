# 线程的基础方法

### join
Thread.join()
* 挂起调用该方法的线程的执行，直到被调用的对象完成执行为止
该方法的内部实现:
```
if (millis == 0) {
    while (isAlive()) {
        wait(0);
    }
} else {
    while (isAlive()) {
        long delay = millis - now;
        if (delay <= 0) {
            break;
        }
        wait(delay);
        now = System.currentTimeMillis() - base;
    }
}
```

### interrupt
Thread.interrupt()
* interrupt()，在一个线程中调用另一个线程的interrupt()方法，即会向那个线程发出信号——线程中断状态已被设置。
    如果线程被中断，则该线程不会立刻抛出中断异常(*如线程调用sleep方法*)，而是等到该线程执行时再抛出异常
* isInterrupted()，用来判断当前线程的中断状态(true or false)。
* interrupted()是个Thread的static方法，用来恢复中断状态

### yield
Thread.yield()

使当前线程从执行状态（运行状态）变为可执行态（就绪状态）
cpu会从众多的可执行态里选择，也就是说，当前也就是刚刚的那个线程还是有可能会被再次执行到的，并不是说一定会执行其他线程而该线程在下一次中不会执行到了

### setDaemon
Thread.setDaemon()
* 守护线程创建出来的线程也是守护线程
* 守护线程必须在线程创建之前设置
* 守护线程无论执行完，系统在用户线程结束会就会结束守护线程进而退出系统

### wait
Object.wait()
调用该方法的线程释放此对象上的监控器锁。因为要释放锁，所以前置条件是该线程必须要先拿到这个对象的锁
* SupportLock.park与wait的作用类似，但是对中断状态的处理并不相同。如果当前线程不是中断的状态，park与wait的效果是一样的；如果一个线程是中断的状态，这时执行wait方法会报java.lang.IllegalMonitorStateException，而执行park时并不会报异常，而是直接返回

### notify
Object.notify
唤醒在此对象监视器锁上被block的线程，之后再次竞争锁