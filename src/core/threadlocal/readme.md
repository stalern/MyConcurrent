1. `ThreadLocal`提供了线程本地的实例。它与普通变量的区别在于，每个使用该变量的线程都会初始化一个完全独立的实例副本。`ThreadLocal` 变量通常被`private static`修饰。当一个线程结束时，它所使用的所有 `ThreadLocal `相对的实例副本都可被回收。
2. Spring的事物和服务器请求均用到了`ThreadLocal`，总的来说`ThreadLocal`可以非常完美的满足如下两种场景：
   * 每个线程需要有自己单独的实例 *(可通过在线程内部构件一个实例来实现)*
   * 实例需要在多个方法中共享，但不希望被多线程共享 *(可通过`static`或者通过方法间的参数传递来实现)*

2. 每个`Thread`中都存有一个`ThreadLocalMap`字段，它是`ThreadLocal`的内部类，每个线程实例都有自己的`ThreadLocalMap`。`ThreadLocalMap`实例的key为`ThreadLocal`，value为`T`类型的值。在多线程调用中，会存在和线程实例相同的`ThreadLocalMap`，但只有一个`ThreadLocal`。这三者的调用路径为：

$$
Thread\rightarrow ThreadLocal\rightarrow ThreadLocalMap
$$

3. **读取实例**：线程首先通过`getMap((Thread)t)`方法获取自身的`ThreadLocalMap`，然后通过`map.getEntry((TreadLocal)this)`方法获取该`ThreadLocal`在当前线程的`ThreadLocalMap`中对应的`Entry`，最后从`Entry`中取出值即为所需访问的本线程对应的实例。如果`ThreadLocalMap`或者`Entry`为空，则通过 `setInitialValue()`->`createMap(t,value)`方法设置初始值，此时的`value`则是`initialValue`的返回值。

   *PS：使用`setInitialValue()`而不是`set(T vlaue)`是为了防止用户覆盖`set(T value)`*

4. **设置实例**：除了`initialValue()`初始化的设置实例和私有的`setInitiValue()`，我们主要用`set(T value)`来设置实例。该方法先获取该线程的`ThreadLocalMap`对象，然后直接将`ThreadLocal`对象与目标实例的映射添加进`ThreadLocalMap`中。当然，如果映射已经存在，就直接覆盖。另外，如果获取到的`ThreadLocalMap`为  null，则先创建该`ThreadLocalMap`对象。

5. **防止内存泄漏**：`ThreadLocalMap`的`Entry`对key为`ThreadLocal`的引用为弱引用，避免了`ThreadLocal`对象无法被回收的问题，但是当`ThreadLocal`被回收后为`null`，此时`Entry`无法被GC。所以针对该问题，`ThreadLocalMap`的`set(key, vlaue)`方法中，通过`replaceStaleEntry`方法将所有键为`null`的`Entry`的值设置为`null`从而使得该值可被回收。另外，会在`rehash`方法中通过`expungeStaleEntry`方法将键和值为`null`的`Entry`设置为`null`从而使得该`Entry`可被回收。通过这种方式`ThreadLocal`可防止内存泄漏。 