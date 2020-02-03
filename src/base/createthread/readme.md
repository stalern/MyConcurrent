# Java中创建线程的四种方式
在Java中，共有四种方式可以创建线程，分别是继承Thread类创建线程、实现Runnable接口创建线程、通过Callable和FutureTask创建线程以及通过线程池创建线程

### 实现Runnable和继承Thread的区别
* 我们都知道，Java是不支持多继承的，所以，使用Runnable接口的形式，就可以避免要多继承 。比如有一个类A，已经继承了类B，就无法再继承Thread类了，这时候要想实现多线程，就需要使用Runnable接口了
* 同时，Thread也实现了Runnable接口

### 实现Runnable和实现Callable的区别
* Callable位于java.util.concurrent包下，它也是一个接口，在它里面也只声明了一个方法，只不过这个方法call()，和Runnable接口中的run()方法不同的是，call()方法有返回值 

* FutureTask可用于异步获取执行结果或取消执行任务的场景。通过传入Callable的任务给FutureTask，直接调用其run方法或者放入线程池执行，之后可以在外部通过FutureTask的get方法异步获取执行结果，**因此，FutureTask非常适合用于耗时的计算** ，主线程可以在完成自己的任务后，再去获取结果

* 另外，FutureTask还可以确保即使调用了多次run方法，它都只会执行一次Runnable或者Callable任务，或者通过cancel取消FutureTask的执行等。
  
  值得注意的是，futureTask.get()会阻塞主线程，一直等子线程执行完并返回后才能继续执行主线程后面的代码。
  
  一般，在Callable执行完之前的这段时间，主线程可以先去做一些其他的事情，事情都做完之后，再获取Callable的返回结果。可以通过isDone()来判断子线程是否执行完。