package base.createthread;

import java.util.concurrent.Callable;

/**
 * @author stalern
 * @date 2020/02/02~13:13
 */
public class ImplCallable implements Callable<String> {
    @Override
    public String call() throws Exception {
        return "Impl callable";
    }
}
