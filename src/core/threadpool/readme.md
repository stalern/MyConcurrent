# Java线程池
做为创建线程的第四中方式，线程池有如下几个特点需要我们掌握

### 线程池的优点
线程池是池化技术的一种应用，包括数据库连接池，HTTP连接池都是利用了线程池技术。**线程池**提供了一种限制和管理资源（包括执行一个任务）。每个**线程池**还维护一些基本统计信息，例如已完成任务的数量。 

- **降低资源消耗**。通过重复利用已创建的线程降低线程创建和销毁造成的消耗。
- **提高响应速度**。当任务到达时，任务可以不需要的等到线程创建就能立即执行。
- **提高线程的可管理性**。线程是稀缺资源，如果无限制的创建，不仅会消耗系统资源，还会降低系统的稳定性，使用线程池可以进行统一的分配，调优和监控。
### 线程池的种类
狭义上来说，线程池的种类是指有`Executors`创建的并且底层都是由`ThreadPoolExecutor`实现的四种线程，分别是：

1. `newFixedThreadPool(int nThread)`： 被称为可重用固定线程数的线程池。采用 `ThreadPoolExecutor(nThread,nThread,0L,TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());` 从这里我们可以看出来线程池的核心线程和最大线程数是相同的。同时当任务超过线程数时，其等待队列是无限长的(`INTEGER.MAX_VALUE`)，此时当任务过多时会产生内存溢出。**适用于任务量比较固定但耗时长的任务**

2. `newSingleThreadExecutor`：采用 `new FinalizableDelegatedExecutorService    (new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>()) );` 可以看出来，核心线程和最大线程数都是1，同时等待队列也是无限长的。至于`FinalizableDlegatedExecutorService()`，它继承了一个关于`ExecutorService`的wrapper类，被包装之后只能调用`ExecutorService`的方法，但是调用不了`ThreadPoolExecutor`类的方法。**适用于一个任务一个任务执行的场景**

3. `newCacheThreadPool`：采用 `new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), threadFactory);` 可以看出，核心线程是0，最大线程是`MAX_VALUE`，`KeepAlive`是60s，线程采用的队列是同步队列。当有新任务到来，则插入到`SynchronousQueue`中，由于`SynchronousQueue`是同步队列，因此会在池中寻找可用线程来执行，若有可以线程则执行，若没有可用线程则创建一个线程来执行该任务；若池中线程空闲时间超过60s，则该线程会被销毁。**适用于执行很多短期异步的小程序或者负载较轻的服务器**

4. `newScheduledThreadPool`: 采用`new ThreadPoolExecutor(corePoolSize, Integer.MAX_VALUE, 0, NANOSECONDS, new DelayedWorkQueue());` 可以看出，最大线程为`Integer.MAX_VALUE`，存活时间无限制。等待队列为`DelayedWorkQueue`，是`ScheduledThreadPoolExecutor`的内部类，它是一种按照超时时间排序的队列结构。当运行的线程数没有达到`corePoolSize`的时候，就新建线程去`DelayedWorkQueue`中取`ScheduledFutureTask`然后才去执行任务，否则就把任务添加到`DelayedWorkQueue`，`DelayedWorkQueue`会将任务排序，按新建一个非核心线程顺序执行，执行完线程就回收，然后循环。任务队列采用的`DelayedWorkQueue`是个无界的队列，延时执行队列任务。**适用于执行定时任务和具体固定周期的重复任务**

但其实还有第五种：`newWorkStealingPool`： 创建一个拥有多个任务队列的线程池，可以减少连接数，创建当前可用cpu数量的线程来并行执行，**适用于大耗时的操作，可以并行来执行** 
### 线程池的参数
其中有6个参数，分别是

1. `corePoolSize`：核心线程数。在创建了线程池后，默认情况下，线程池中并没有任何线程，而是等待有任务到来才创建线程去执行任务，除非调用了`prestartAllCoreThreads()`或者`prestartCoreThread()`方法，在没有任务到来之前就创建`corePoolSize`个线程或者一个线程。当线程数小于核心线程数时，即使现有的线程空闲，线程池也会优先创建新线程来处理任务，而不是直接交给现有的线程处理。核心线程在`allowCoreThreadTimeout`被设置为true时会超时退出，默认情况下不会退出
2. `maximumPoolSize`：最大线程池数。即当需要的线程数超过核心线程数并且任务等待队列已满时，此时线程池可以再创建一批线程来满足需求。但是创建后线程池中的线程数不能超过`maximumPoolSize`

当然也可以在创建完线程池之后通过`setCorePoolSize()`/`setMaximumPoolSize()`修改这些值

3. `keepAliveTime`：当线程空闲时间达到`keepAliveTime`，该线程会退出，直到线程数量等于`corePoolSize`。如果`allowCoreThreadTimeout`设置为true，则所有线程均会退出直到线程数量为0
4. `unit`：`keepAliveTime`的单位，有7种属性，分别是：`DAYS`,`HOURS`,`MINUTES`,`SECONDS`, `MILLISECONDS`, `MICROSECONDS`,`NANOSECONDS`

5. `workQueue`：任务缓存队列及排队策略。当任务超过`maximumPoolSize`后，回到`workQueue`中等待，它有如下三种取值：
   * `ArrayBlockingQueue`：基于数组的先进先出队列，此队列创建时必须指定大小
   * `LinkedBlockingQueue`：基于链表的先进先出队列，如果创建时没有指定此队列大小，则默认为`Integer.MAX_VALUE`
   * `synchronousQueue`：这个队列比较特殊，它不会保存提交的任务，而是将直接新建一个线程来执行新来的任务
6. `threadFactory`：创建线程的工厂，这里可以统一创建线程以及规定线程的属性。推荐使用guava包
7. `handler`：任务拒绝策略。 当线程池的任务缓存队列已满并且线程池中的线程数目达到`maximumPoolSize`，如果还有任务到来就会采取任务拒绝策略，通常有以下四种策略 ：

* `ThreadPoolExecutor.AbortPolicy`:丢弃任务并抛出`RejectedExecutionException`异常
  * `ThreadPoolExecutor.DiscardPolicy`：也是丢弃任务，但是不抛出异常
  * `ThreadPoolExecutor.DiscardOldestPolicy`：丢弃队列最前面的任务，然后重新尝试执行任务（重复此过程）
  * `ThreadPoolExecutor.CallerRunsPolicy`：由调用线程处理该任务.。因此这种策略会降低对于新任务提交速度，影响程序的整体性能。另外，这个策略喜欢增加队列容量。如果您的应用程序可以承受此延迟并且你**不能任务丢弃任何一个任务请求的话**，你可以选择这个策略。 
### execute和submit
* submit可以执行实现Callable和Runnable，但是execute只能执行实现Runnable的类
* 工具类 Executors 可以实现 `Runnable`对象到`Callable`对象之间的转换。Runnable没有返回值，Callable有返回值且可以抛出异常
* execute()方法用于提交不需要返回值的任务，所以无法判断任务是否被线程池执行成功与否；
* submit()方法用于提交需要返回值的任务。线程池会返回一个 Future 类型的对象，通过这个 Future 对象可以判断任务是否执行成功，并且可以通过 Future 的 get()方法来获取返回值，get()方法会阻塞当前线程直到任务完成，而使用 get(long timeout，TimeUnit unit)方法则会阻塞当前线程一段时间后立即返回，这时候有可能任务没有执行完
* submit()也是通过execute实现
### 线程池的原理
整个流程为：
$$
execute() \rightarrow addWorker()\rightarrow Thread.start() \rightarrow runWorker() \rightarrow getTask() \rightarrow processWorkerExit()
$$
四种线程池都是通过`ThreadPoolExecutor`的构造方法实现的，执行任务流程主要是说的该类下的主要方法的具体原理：

* `ThreadPoolExecutor`类中有一个`AtomicInteger`类型的`final`变量`ctl`，该变量有两重意思，一个是表示工作的线程数，另一个是表示当前线程池的五种状态。其实`Integer`类型，共32位，高3位保存runState，低29位保存workerCount 

* 对于`execute(Runnable)`来说：
  * 整体过程是：先提交任务，如果`workerCount`<`corePoolSize`，则新建线程并执行该任务；如果` workerCount` >= `corePoolSize` &&`阻塞队列未满`，则把任务添加到阻塞队列中；如果`workerCount` >= `corePoolSize` && `workerCount` < `maximumPoolSize `&&`阻塞队列已满`，则创建线程并执行该任务；如果 `workerCount` >=  `maximumPoolSize`，则拒绝该任务
  * 从源码上看：首先获得`ctl`的值，如果`workerCount`<`corePoolSize`，则通过`addWorker`提交任务。如果提交任务失败*(有两个原因，一个是线程数超过最大值，另外一个是线程状态不是Running，这也是为何后面要recheck的原因)*，则重新获得`ctl`的值，然后判断线程池的状态，如果为`Running`则把任务插入队列。再次获得`ctl`对线程数目和线程池状态进行`recheck`，如果此时线程池状态不是`Running`则把已经插入队列的任务移出，并调用拒绝策略，如果此时线程池中线程数目为0，则通过`addWorker`新建线程*(此处保证线程池最少有一个线程执行任务)*。之后的情况说明队列已满并且`workCount`>`corePoolSize`，此时再次调用`addWorker`提交任务，提交失败，则拒绝该任务

* 对于`addWorker(Runnable,Boolean)`来说，从源码来看，分为3部分：

  * 检查是否`runState`>=`Shutdown`&&(`rs == SHUTDOWN`, `firsTask为空`,`阻塞队列不为空`都成立)，否则返回false
  * 通过CAS增加线程数（并不是实际增加，只是通过增加`ctl`）。其中，`break retry`说明增加成功，`continue retry`说明runState改变，重新开始goto和循环
  * 真正创建新的worker，开始新线程(worker实现了Runnable，也是线程)。创建worker的时候需要通过`ReentrantLock`加锁，创建成功后(`workers.add(worker)`)设置线程池的大小`largestPoolSize`，之后释放锁，然后启动线程，并执行任务


* 对于`Worker`类来说，它是`ThreadPoolExecutor`的内部类，实现了`Runnable`接口，继承了`AQS`，同时线程池底层就是`HashSet<Worker>`这个类

  * 其有两个重要的变量`firstTask`保存了要执行的任务，`thread`通过构造方法中的 `getThreadFactory().newThread(this)`来新建一个线程。所以`t.start()`就等于说调用了`worker.run()`，而`worker.run()`又调用了`worker.runWorker()`方法
  * `Worker`继承自AQS，用于判断线程是否空闲以及是否可以被中断。为什么不用`ReentrantLock`呢？通过`Worker`的`tryAcquire`可以看出该锁是不可重入的，但是`ReentrantLock`却是可重入锁。之所以设置为不可重入锁，主要是不希望当执行譬如`setCorePoolSize()`这样的线程池控制方法时中断正在执行任务的线程*(这种线程池控制方法还有shutdown()等，会调用interruptIdleWorkers()方法来中断空闲的线程，interruptIdleWorkers()方法会使用tryLock()方法来判断线程池中的线程是否是空闲状态，而tryLock()又会通过tryAcquire()来判断线程池中的线程是否是空闲状态。查看源码可得用到了AQS的方法，这也能解释为什么构造方法将state设为-1，即为了禁止在执行任务前对线程进行中断  )*，然后重新获取锁。如果该线程现在不是独占锁的状态，也就是空闲的状态，说明它没有在处理任务，这时可以对该线程进行中断
* 对于`runWorker()`来说：

  * 先通过while循环不断通过`getTask()`去获得任务
  * 如果线程池正在停止，那么要保证当前线程是中断状态，否则要保证当前线程不是中断状态
  * 调用`task.run()`执行任务
  * 如果task为null则跳出循环，执行`processWorkerExit()`方法
  * `runWorker`方法执行完毕，也代表着Worker中的`run`方法执行完毕，销毁线程
  * 注意两个钩子`beforeExecute()`和`afterExecute()`，这两个类是空的，留给子类实现
* 对于`getTask()`来说，它从阻塞队列中获取任务。通过workQueue.poll和take来获取任务，前者是如果在keepAliveTime时间内没有获取到任务，则返回null，后者是一直阻塞直到获取任务

