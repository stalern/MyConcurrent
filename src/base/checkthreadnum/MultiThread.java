package base.checkthreadnum;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/**
 * 查看多少线程
 * @author stalern
 * @date 2020/01/27~17:58
 */
public class MultiThread {
    public static void main(String[] args) {

        // 获取 Java 线程管理 MXBean
        ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean();
        // 不需要获取同步的 monitor 和 synchronized 信息，仅获取线程和线程堆栈信息
        ThreadInfo[] threadInfos = threadMxBean.dumpAllThreads(false, false);
        // 遍历线程信息，仅打印线程 ID 和线程名称信息
        for (ThreadInfo threadInfo : threadInfos) {
            System.out.println("[" + threadInfo.getThreadId() + "] " + threadInfo.getThreadName());
        }
    }
}
