package base;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 观察sleep被中断时的行为
 * @author stalern
 * @date 2020/01/27~18:15
 */
public class Stop {

    public static void main(String[] args) {
        String pattern = "[A-Z]{3}";
        AtomicInteger atomicInteger = new AtomicInteger(1);
        atomicInteger.getAndIncrement();
    }
}


