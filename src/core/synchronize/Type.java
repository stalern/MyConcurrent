package core.synchronize;

/**
 * 用synchronized实现对象锁和类锁
 * @author stalern
 * @date 2020/01/29~17:13
 */
public class Type {
    /**
     * 对象锁：形式1(方法锁)
     */
    public synchronized void objectMethodLock() {
        System.out.println("我是对象锁也是方法锁");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * 对象锁：形式2（代码块形式）
     */
    public void objectBlockLock() {
        synchronized (this) {
            System.out.println("我是对象锁");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 类锁：形式1
     */
    public static synchronized void classMethodLock() {
        System.out.println("我是类锁一号");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 类锁：形式2
     */
    public void classBlockLock() {
        synchronized (Type.class) {
            System.out.println("我是类锁二号");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
