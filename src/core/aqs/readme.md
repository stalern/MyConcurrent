### 概念

对于AQS来说，他是一个用来构建锁和同步器的框架，基于CAS，使用AQS能简单且高效地构造出应用广泛的大量的同步器，比如我们提到的ReentrantLock，Semaphore，其他的诸如ReentrantReadWriteLock，SynchronousQueue，FutureTask等等皆是基于AQS的。当然，我们自己也能利用AQS非常轻松容易地构造出符合我们自己需求的同步器

### 使用

从使用上来说，AQS的功能可以分为两种：独占（如ReentrantLock）和共享（如Semaphore/CountDownLatch CyclicBarrier/ReadWriteLock)。ReentrantReadWriteLock可以看成是组合式，它对读共享，写独占

AQS的设计是基于模板方法模式的，如果需要自定义同步器一般的方式，则需要：

1. 使用者继承AbstractQueuedSynchronizer并重写指定的方法。（这些重写方法很简单，无非是对于共享资源state的获取和释放）

2. 将AQS组合在自定义同步组件的实现中，并调用其模板方法，而这些模板方法会调用使用者重写的方法

   ```
   isHeldExclusively()//该线程是否正在独占资源。只有用到condition才需要去实现它。
   tryAcquire(int)//独占方式。尝试获取资源，成功则返回true，失败则返回false。
   tryRelease(int)//独占方式。尝试释放资源，成功则返回true，失败则返回false。
   tryAcquireShared(int)//共享方式。尝试获取资源。负数表示失败；0表示成功，但没有剩余可用资源；正数表示成功，且有剩余资源。
   tryReleaseShared(int)//共享方式。尝试释放资源，成功则返回true，失败则返回false。
   ```

    默认情况下，每个方法都抛出 `UnsupportedOperationException` 。为什么不是abstract的呢？如果是abstract则说明每个使用者都要实现这些方法，要求比较苛刻。

### 原理

* AQS维护一个共享资源`state`，它的语义有响应的子类来实现，譬如在`ReentrantLock`中，`state`初始化为0，表示未锁定状态。A线程`lock()`时，会调用`tryAcquire()`独占该锁并将state+1。此后，其他线程再`tryAcquire()`时就会失败，直到A线程`unlock()`到`state`=0（即释放锁）为止，其它线程才有机会获取该锁。在`CountDownLatch`中，任务分为N个子线程去执行，`state也`初始化为N（注意N要与线程个数一致）。这N个子线程是并行执行的，每个子线程执行完后`countDown()`一次，state会CAS减1。等到所有子线程都执行完后(即state=0)，会`unpark()`主调用线程，然后主调用线程就会从await()函数返回，继续后余动作 

* AQS通过内置的FIFO双端双向链表来完成获取资源线程的排队工作，双端双向链表。该队列由一个一个的`Node`结点组成，每个`Node`结点维护一个`prev`引用和`next`引用，分别指向自己的前驱和后继结点。AQS维护两个指针，分别指向队列头部`head`和尾部`tail`。该队列中的`Node`有五种状态，分别是`CANCELLED`,`SIGNAL`, `CONDITION`,`PROPAGATE`和初始状态

  * **CANCELLED**(1)：表示**当前结点**已取消调度。当timeout或被中断（响应中断的情况下），会触发变更为此状态，进入该状态后的结点将不会再变化
  * **SIGNAL**(-1)：表示**后继结点**在等待当前结点唤醒。后继结点入队时，会将前继结点的状态更新为SIGNAL
  * **CONDITION**(-2)：表示结点等待在Condition上，当其他线程调用了Condition的signal()方法后，CONDITION状态的结点将**从等待队列转移到同步队列中**，等待获取同步锁
  * **PROPAGATE**(-3)：共享模式下，前继结点不仅会唤醒其后继结点，同时也可能会唤醒后继的后继结点
  * **0**：新结点入队时的默认状态

* 当加锁时，子类通过调用AQS的`acquire()`进而调用子类自己实现的`tryAcquire()`方法（该方法是尝试获得锁，即改变state的状态），在调用`tryAcquire()`方法的过程中：先尝试获得锁，如果成功则返回，如果获得不成功则把该线程加入到Node队列中*(节点为空时，自旋创建节点并设置尾点)*，然后自旋尝试获得锁*(只有老二结点，才有机会去tryAcquire，如果不是且当其前驱节点是SINGAL，则阻塞)*，之后返回中断状态

  ```java 
  if (!tryAcquire(arg) 
      && acquireQueued(addWaiter(Node.EXCLUSIVE), arg))            
      selfInterrupt();
  ```

* 当释放锁时，和加锁流程一样。当释放成功后则去唤醒后面的节点

  ```java
  if (tryRelease(arg)) {
      Node h = head;
      if (h != null && h.waitStatus != 0)
          unparkSuccessor(h);
      return true;
  }
  return false;
  ```

### 组件

除了ReentrantLock外，还有三种常用的AQS组件，分别是`Semaphore`,`CountDownLatch`和`CyclicBarrier`

- **Semaphore(信号量)-允许多个线程同时访问：** synchronized 和 ReentrantLock 都是一次只允许一个线程访问某个资源，Semaphore(信号量)可以指定多个线程同时访问某个资源。(PS:学过OS的应该都知道把)
- **CountDownLatch （倒计时器）：** CountDownLatch是一个同步工具类，用来协调多个线程之间的同步。这个工具通常用来控制线程等待，它可以让某一个线程等待直到倒计时结束，再开始执行。
- **CyclicBarrier(循环栅栏)：** CyclicBarrier 和 CountDownLatch 非常类似，它也可以实现线程间的技术等待，但是它的功能比  CountDownLatch 更加复杂和强大。主要应用场景和 CountDownLatch 类似。CyclicBarrier  的字面意思是可循环使用（Cyclic）的屏障（Barrier）。它要做的事情是，让一组线程到达一个屏障（也可以叫同步点）时被阻塞，直到最后一个线程到达屏障时，屏障才会开门，所有被屏障拦截的线程才会继续干活。CyclicBarrier默认的构造方法是 CyclicBarrier(int parties)，其参数表示屏障拦截的线程数量，每个线程调用await()方法告诉  CyclicBarrier 我已经到达了屏障，然后当前线程被阻塞