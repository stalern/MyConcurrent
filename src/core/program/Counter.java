package core.program;


import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author stalern
 * @date 2020/02/04~16:20
 */
public class Counter {

    private AtomicInteger count;

    public Counter(int value) {
        count = new AtomicInteger(value);
    }
    public boolean add() {
        for (;;){
            long time = System.currentTimeMillis();
            int memory = count.get();
            if (count.compareAndSet(memory, memory + 1)) {
                return true;
            }
            if (System.currentTimeMillis() - time > 10L) {
                return false;
            }
        }
    }

    public int get() {
        return count.get();
    }

    public static void main(String[] args) {
        Counter counter = new Counter(0);
        for (int i = 0; i < 10; i++) {
            counter.add();
        }
        System.out.println(counter.get());
    }
}
