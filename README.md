# ThreadPool-
线程池的种类
1. newSingleThreadExecutor

创建方式：

ExecutorService pool = Executors.newSingleThreadExecutor();
一个单线程的线程池。这个线程池只有一个线程在工作，也就是相当于单线程串行执行所有任务。如果这个唯一的线程因为异常结束，那么会有一个新的线程来替代它。此线程池保证所有任务的执行顺序按照任务的提交顺序执行。

使用方式：

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class ThreadPool {
    public static void main(String[] args) {
        ExecutorService pool = Executors.newSingleThreadExecutor();
        for (int i = 0; i < 10; i++) {
            pool.execute(() -> {
                System.out.println(Thread.currentThread().getName() + "\t开始发车啦....");
            });
        }
    }
}
输出结果如下：

pool-1-thread-1    开始发车啦....
pool-1-thread-1    开始发车啦....
pool-1-thread-1    开始发车啦....
pool-1-thread-1    开始发车啦....
pool-1-thread-1    开始发车啦....
pool-1-thread-1    开始发车啦....
pool-1-thread-1    开始发车啦....
pool-1-thread-1    开始发车啦....
pool-1-thread-1    开始发车啦....
pool-1-thread-1    开始发车啦....
从输出的结果我们可以看出，一直只有一个线程在运行。

2.newFixedThreadPool

创建方式：

ExecutorService pool = Executors.newFixedThreadPool(10);
创建固定大小的线程池。每次提交一个任务就创建一个线程，直到线程达到线程池的最大大小。线程池的大小一旦达到最大值就会保持不变，如果某个线程因为执行异常而结束，那么线程池会补充一个新线程。

使用方式：

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class ThreadPool {
    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            pool.execute(() -> {
                System.out.println(Thread.currentThread().getName() + "\t开始发车啦....");
            });
        }
    }
}
输出结果如下：

pool-1-thread-1    开始发车啦....
pool-1-thread-4    开始发车啦....
pool-1-thread-3    开始发车啦....
pool-1-thread-2    开始发车啦....
pool-1-thread-6    开始发车啦....
pool-1-thread-7    开始发车啦....
pool-1-thread-5    开始发车啦....
pool-1-thread-8    开始发车啦....
pool-1-thread-9    开始发车啦....
pool-1-thread-10 开始发车啦....
3. newCachedThreadPool

创建方式：

ExecutorService pool = Executors.newCachedThreadPool();
创建一个可缓存的线程池。如果线程池的大小超过了处理任务所需要的线程，那么就会回收部分空闲的线程，当任务数增加时，此线程池又添加新线程来处理任务。

使用方式如上2所示。

4.newScheduledThreadPool

创建方式：

ScheduledExecutorService pool = Executors.newScheduledThreadPool(10);
此线程池支持定时以及周期性执行任务的需求。

使用方式：

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
public class ThreadPool {
    public static void main(String[] args) {
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(10);
        for (int i = 0; i < 10; i++) {
            pool.schedule(() -> {
                System.out.println(Thread.currentThread().getName() + "\t开始发车啦....");
            }, 10, TimeUnit.SECONDS);
        }
    }
}
上面演示的是延迟10秒执行任务,如果想要执行周期性的任务可以用下面的方式，每秒执行一次

//pool.scheduleWithFixedDelay也可以
pool.scheduleAtFixedRate(() -> {
                System.out.println(Thread.currentThread().getName() + "\t开始发车啦....");
}, 1, 1, TimeUnit.SECONDS);
5.newWorkStealingPool

newWorkStealingPool是jdk1.8才有的，会根据所需的并行层次来动态创建和关闭线程，通过使用多个队列减少竞争，底层用的ForkJoinPool来实现的。ForkJoinPool的优势在于，可以充分利用多cpu，多核cpu的优势，把一个任务拆分成多个“小任务”，把多个“小任务”放到多个处理器核心上并行执行；当多个“小任务”执行完成之后，再将这些执行结果合并起来即可。

说说线程池的拒绝策略
当请求任务不断的过来，而系统此时又处理不过来的时候，我们需要采取的策略是拒绝服务。RejectedExecutionHandler接口提供了拒绝任务处理的自定义方法的机会。在ThreadPoolExecutor中已经包含四种处理策略。

AbortPolicy策略：该策略会直接抛出异常，阻止系统正常工作。




public static class AbortPolicy implements RejectedExecutionHandler {
    public AbortPolicy() { }
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        throw new RejectedExecutionException("Task " + r.toString() +
                                                 " rejected from " +
                                                 e.toString());
    }
}


CallerRunsPolicy 策略：只要线程池未关闭，该策略直接在调用者线程中，运行当前的被丢弃的任务。





public static class CallerRunsPolicy implements RejectedExecutionHandler {
    public CallerRunsPolicy() { }
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        if (!e.isShutdown()) {
                r.run();
        }
    }
}


DiscardOleddestPolicy策略： 该策略将丢弃最老的一个请求，也就是即将被执行的任务，并尝试再次提交当前任务。



public static class DiscardOldestPolicy implements RejectedExecutionHandler {
    public DiscardOldestPolicy() { }
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        if (!e.isShutdown()) {
            e.getQueue().poll();
            e.execute(r);
        }
    }
}


DiscardPolicy策略：该策略默默的丢弃无法处理的任务，不予任何处理。


public static class DiscardPolicy implements RejectedExecutionHandler {
    public DiscardPolicy() { }
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
    }
}
除了JDK默认为什么提供的四种拒绝策略，我们可以根据自己的业务需求去自定义拒绝策略，自定义的方式很简单，直接实现RejectedExecutionHandler接口即可

比如Spring integration中就有一个自定义的拒绝策略CallerBlocksPolicy，将任务插入到队列中，直到队列中有空闲并插入成功的时候，否则将根据最大等待时间一直阻塞，直到超时。

package org.springframework.integration.util;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
public class CallerBlocksPolicy implements RejectedExecutionHandler {
    private static final Log logger = LogFactory.getLog(CallerBlocksPolicy.class);
    private final long maxWait;
    /**
     * @param maxWait The maximum time to wait for a queue slot to be
     * available, in milliseconds.
     */
    public CallerBlocksPolicy(long maxWait) {
        this.maxWait = maxWait;
    }
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        if (!executor.isShutdown()) {
            try {
                BlockingQueue<Runnable> queue = executor.getQueue();
                if (logger.isDebugEnabled()) {
                    logger.debug("Attempting to queue task execution for " + this.maxWait + " milliseconds");
                }
                if (!queue.offer(r, this.maxWait, TimeUnit.MILLISECONDS)) {
                    throw new RejectedExecutionException("Max wait time expired to queue task");
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("Task execution queued");
                }
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RejectedExecutionException("Interrupted", e);
            }
        }
        else {
            throw new RejectedExecutionException("Executor has been shut down");
        }
    }
}
定义好之后如何使用呢？光定义没用的呀，一定要用到线程池中呀，可以通过下面的方式自定义线程池，指定拒绝策略。

BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(100);
ThreadPoolExecutor executor = new ThreadPoolExecutor(
    10, 100, 10, TimeUnit.SECONDS, workQueue, new CallerBlocksPolicy());
execute和submit的区别？
在前面的讲解中，我们执行任务是用的execute方法，除了execute方法，还有一个submit方法也可以执行我们提交的任务。

这两个方法有什么区别呢？分别适用于在什么场景下呢？我们来做一个简单的分析。

execute适用于不需要关注返回值的场景，只需要将线程丢到线程池中去执行就可以了

public class ThreadPool {
    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(10);
        pool.execute(() -> {
            System.out.println(Thread.currentThread().getName() + "\t开始发车啦....");
        });
    }
}
submit方法适用于需要关注返回值的场景，submit方法的定义如下：

public interface ExecutorService extends Executor {
　　...
　　<T> Future<T> submit(Callable<T> task);
　　<T> Future<T> submit(Runnable task, T result);
　　Future<?> submit(Runnable task);
　　...
}
其子类AbstractExecutorService实现了submit方法,可以看到无论参数是Callable还是Runnable，最终都会被封装成RunnableFuture，然后再调用execute执行。

    /**
     * @throws RejectedExecutionException {@inheritDoc}
     * @throws NullPointerException       {@inheritDoc}
     */
    public Future<?> submit(Runnable task) {
        if (task == null) throw new NullPointerException();
        RunnableFuture<Void> ftask = newTaskFor(task, null);
        execute(ftask);
        return ftask;
    }
    /**
     * @throws RejectedExecutionException {@inheritDoc}
     * @throws NullPointerException       {@inheritDoc}
     */
    public <T> Future<T> submit(Runnable task, T result) {
        if (task == null) throw new NullPointerException();
        RunnableFuture<T> ftask = newTaskFor(task, result);
        execute(ftask);
        return ftask;
    }
    /**
     * @throws RejectedExecutionException {@inheritDoc}
     * @throws NullPointerException       {@inheritDoc}
     */
    public <T> Future<T> submit(Callable<T> task) {
        if (task == null) throw new NullPointerException();
        RunnableFuture<T> ftask = newTaskFor(task);
        execute(ftask);
        return ftask;
    }
下面我们来看看这三个方法分别如何去使用：

submit(Callable task);

public class ThreadPool {
    public static void main(String[] args) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(10);
        Future<String> future = pool.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "Hello";
            }
        });
        String result = future.get();
        System.out.println(result);
    }
}
submit(Runnable task, T result);

public class ThreadPool {
    public static void main(String[] args) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(10);
        Data data = new Data();
        Future<Data> future = pool.submit(new MyRunnable(data), data);
        String result = future.get().getName();
        System.out.println(result);
    }
}
class Data {
    String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
class MyRunnable implements Runnable {
    private Data data;
    public MyRunnable(Data data) {
        this.data = data;
    }
    @Override
    public void run() {
        data.setName("yinjihuan");
    }
}
Future submit(Runnable task);
直接submit一个Runnable是拿不到返回值的，返回值就是null.

五种线程池的使用场景
newSingleThreadExecutor：一个单线程的线程池，可以用于需要保证顺序执行的场景，并且只有一个线程在执行。

newFixedThreadPool：一个固定大小的线程池，可以用于已知并发压力的情况下，对线程数做限制。

newCachedThreadPool：一个可以无限扩大的线程池，比较适合处理执行时间比较小的任务。

newScheduledThreadPool：可以延时启动，定时启动的线程池，适用于需要多个后台线程执行周期任务的场景。

newWorkStealingPool：一个拥有多个任务队列的线程池，可以减少连接数，创建当前可用cpu数量的线程来并行执行。

线程池的关闭
关闭线程池可以调用shutdownNow和shutdown两个方法来实现

shutdownNow：对正在执行的任务全部发出interrupt()，停止执行，对还未开始执行的任务全部取消，并且返回还没开始的任务列表

public class ThreadPool {
    public static void main(String[] args) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(1);
        for (int i = 0; i < 5; i++) {
            System.err.println(i);
            pool.execute(() -> {
                try {
                    Thread.sleep(30000);
                    System.out.println("--");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        Thread.sleep(1000);
        List<Runnable> runs = pool.shutdownNow();
    }
}
上面的代码模拟了立即取消的场景，往线程池里添加5个线程任务，然后sleep一段时间，线程池只有一个线程，如果此时调用shutdownNow后应该需要中断一个正在执行的任务和返回4个还未执行的任务，控制台输出下面的内容：

0
1
2
3
4
[fs.ThreadPool$$Lambda$1/990368553@682a0b20, 
fs.ThreadPool$$Lambda$1/990368553@682a0b20, 
fs.ThreadPool$$Lambda$1/990368553@682a0b20, 
fs.ThreadPool$$Lambda$1/990368553@682a0b20]
java.lang.InterruptedException: sleep interrupted
    at java.lang.Thread.sleep(Native Method)
    at fs.ThreadPool.lambda$0(ThreadPool.java:15)
    at fs.ThreadPool$$Lambda$1/990368553.run(Unknown Source)
    at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
    at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
    at java.lang.Thread.run(Thread.java:745)
shutdown：当我们调用shutdown后，线程池将不再接受新的任务，但也不会去强制终止已经提交或者正在执行中的任务

public class ThreadPool {
    public static void main(String[] args) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(1);
        for (int i = 0; i < 5; i++) {
            System.err.println(i);
            pool.execute(() -> {
                try {
                    Thread.sleep(30000);
                    System.out.println("--");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        Thread.sleep(1000);
        pool.shutdown();
        pool.execute(() -> {
            try {
                Thread.sleep(30000);
                System.out.println("--");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
上面的代码模拟了正在运行的状态，然后调用shutdown，接着再往里面添加任务，肯定是拒绝添加的，请看输出结果：

0
1
2
3
4
Exception in thread "main" java.util.concurrent.RejectedExecutionException: Task fs.ThreadPool$$Lambda$2/1747585824@3d075dc0 rejected from java.util.concurrent.ThreadPoolExecutor@214c265e[Shutting down, pool size = 1, active threads = 1, queued tasks = 4, completed tasks = 0]
    at java.util.concurrent.ThreadPoolExecutor$AbortPolicy.rejectedExecution(ThreadPoolExecutor.java:2047)
    at java.util.concurrent.ThreadPoolExecutor.reject(ThreadPoolExecutor.java:823)
    at java.util.concurrent.ThreadPoolExecutor.execute(ThreadPoolExecutor.java:1369)
    at fs.ThreadPool.main(ThreadPool.java:24)
还有一些业务场景下需要知道线程池中的任务是否全部执行完成，当我们关闭线程池之后，可以用isTerminated来判断所有的线程是否执行完成，千万不要用isShutdown，isShutdown只是返回你是否调用过shutdown的结果。

public class ThreadPool {
    public static void main(String[] args) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(1);
        for (int i = 0; i < 5; i++) {
            System.err.println(i);
            pool.execute(() -> {
                try {
                    Thread.sleep(3000);
                    System.out.println("--");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        Thread.sleep(1000);
        pool.shutdown();
        while(true){  
            if(pool.isTerminated()){  
                System.out.println("所有的子线程都结束了！");  
                break;  
            }  
            Thread.sleep(1000);    
        }  
    }
}
自定义线程池
在实际的使用过程中，大部分我们都是用Executors去创建线程池直接使用，如果有一些其他的需求，比如指定线程池的拒绝策略，阻塞队列的类型，线程名称的前缀等等，我们可以采用自定义线程池的方式来解决。

如果只是简单的想要改变线程名称的前缀的话可以自定义ThreadFactory来实现，在Executors.new…中有一个ThreadFactory的参数，如果没有指定则用的是DefaultThreadFactory。

自定义线程池核心在于创建一个ThreadPoolExecutor对象，指定参数

下面我们看下ThreadPoolExecutor构造函数的定义：

public ThreadPoolExecutor(int corePoolSize,
                              int maximumPoolSize,
                              long keepAliveTime,
                              TimeUnit unit,
                              BlockingQueue<Runnable> workQueue,
                              ThreadFactory threadFactory,
                              RejectedExecutionHandler handler) ;
corePoolSize
线程池大小，决定着新提交的任务是新开线程去执行还是放到任务队列中，也是线程池的最最核心的参数。一般线程池开始时是没有线程的，只有当任务来了并且线程数量小于corePoolSize才会创建线程。

maximumPoolSize
最大线程数，线程池能创建的最大线程数量。

keepAliveTime
在线程数量超过corePoolSize后，多余空闲线程的最大存活时间。

unit
时间单位

workQueue
存放来不及处理的任务的队列，是一个BlockingQueue。

threadFactory
生产线程的工厂类，可以定义线程名，优先级等。

handler
拒绝策略，当任务来不及处理的时候，如何处理, 前面有讲解。

了解上面的参数信息后我们就可以定义自己的线程池了，我这边用ArrayBlockingQueue替换了LinkedBlockingQueue，指定了队列的大小，当任务超出队列大小之后使用CallerRunsPolicy拒绝策略处理。

这样做的好处是严格控制了队列的大小，不会出现一直往里面添加任务的情况，有的时候任务处理的比较慢，任务数量过多会占用大量内存，导致内存溢出。

当然你也可以在提交到线程池的入口进行控制，比如用CountDownLatch, Semaphore等。

/**
 * 自定义线程池<br>
 * 默认的newFixedThreadPool里的LinkedBlockingQueue是一个无边界队列，如果不断的往里加任务，最终会导致内存的不可控<br>
 * 增加了有边界的队列，使用了CallerRunsPolicy拒绝策略
 * @author yinjihuan
 *
 */
public class FangjiaThreadPoolExecutor {
    private static ExecutorService executorService = newFixedThreadPool(50);
    private static ExecutorService newFixedThreadPool(int nThreads) {
        return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(10000), new DefaultThreadFactory(), new CallerRunsPolicy());
    }
    public static void execute(Runnable command) {
        executorService.execute(command);
    }
    public static void shutdown() {
        executorService.shutdown();
    }
    static class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;
        DefaultThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                                  Thread.currentThread().getThreadGroup();
            namePrefix = "FSH-pool-" +
                          poolNumber.getAndIncrement() +
                         "-thread-";
        }
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                                  namePrefix + threadNumber.getAndIncrement(),
                                  0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}
