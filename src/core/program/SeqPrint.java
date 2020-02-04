package core.program;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * N个线程顺序打印1-100
 * @author stalern
 * @date 2020/02/04~11:33
 */
public class SeqPrint {
    /**
     * 工作线程数量
     */
    private static final int WORKER_COUNT = 3;
    /**
     * 是计数器
     */
    private static int countIndex = 0;

    public static void main(String[] args){
        //可重入锁 此处使用默认非公平锁即可
        final ReentrantLock reentrantLock = new ReentrantLock();
        final List<Condition> conditions = new ArrayList<>();
        for(int i=0; i< WORKER_COUNT; i++){
            //给每个工人分配一个用于通知干活的令牌 没喊你的时候都等着 喊你时再干
            Condition condition = reentrantLock.newCondition();
            conditions.add(condition);
            //创建工人 i是工人序号 锁是保证同时只有一人在操作间 令牌主要用来通知下一位工人
            Worker worker = new Worker(i, reentrantLock, conditions);
            worker.start();
        }

        //部分IDE不能在主线程 上锁喔 所以起新线程通知大家开工了
        new Thread(() -> {
            reentrantLock.lock();
            //Condition的 通知方法必须在持有锁时才可调用
            try {
                conditions.get(0).signal();
            } finally {
                reentrantLock.unlock();
            }
        }).start();

    }

    static class Worker extends Thread{
        //工人的序号
        int index;
        ReentrantLock lock;
        //记录所有工人的令牌
        List<Condition> conditions;

        public Worker(int index, ReentrantLock lock, List<Condition> conditions){
            super("Worker:"+index);
            this.index = index;
            this.lock = lock;
            this.conditions = conditions;
        }

        /**
         * 当前工人工作结束时用来通知下一个工人
         */
        private void signalNext(){
            int nextIndex = (index + 1) % conditions.size();
            //signal喊到对应的工人时 代码会从 await后继续执行
            conditions.get(nextIndex).signal();
        }

        @Override
        public void run(){
            //上班喽 不数完100个数不能 下班喔
            while(true) {
                //锁住 保证操作间同时只有一位工人
                lock.lock();
                try {
//                    System.out.println(this.getName() + " wait");
                    //进入工作准备状态 等待喊号 直到收到signal（喊号）时代码才会继续执行
                    conditions.get(index).await();

                    //先看看当前工作进度
//                    final int currentIndex = countIndex.get();
                    if (countIndex > 100) {
                        //全部工作已经完成 下班啦！！！ 记得告诉后面一位兄弟喔
                        signalNext();
//                        System.out.println(this.getName() + " exit");
                        //退出循环 线程运行结束 下班
                        return;
                    }
                    //核心工作 打印数字
                    System.out.println((this.getName() + " " + countIndex));
                    //计数器+1
                    countIndex ++;
                    //自己的任务完成 通知下一位干活
                    signalNext();
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    lock.unlock();
                }
            }
        }
    }
}
