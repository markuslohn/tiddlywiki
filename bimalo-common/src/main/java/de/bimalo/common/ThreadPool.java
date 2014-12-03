package de.bimalo.common;

import java.util.Stack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p> Implements a pool for threads. The maximum number of thread in the
 * ThreadPool can be defined when the ThreadPool gets created. In the
 * <code>run</code> function a
 * <code>Runnable</code> implementation can be provided that will be executed by
 * a thread in this ThreadPool. When the
 * <code>shutdown</code> function was called all threads in this ThreadPool will
 * be shutdown when the task was processed.</p> <p> Example:<br>
 * <pre>
 * ...
 *   ThreadPool tp = new ThreadPool(threads);
 *   for (int i = 0; i < 5; i++) {
 *       tp.run(new ArchivelinkTesterDownloadThread(properties));
 *   }
 *  tp.shutdown();
 *  ...
 * </pre> </p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @version $Rev$ $LastChangedDate$
 * @since 1.0
 * @see java.lang.Runnable
 * @see java.lang.Thread
 */
public final class ThreadPool {

    /**
     * Default capacity for this ThreadPool.
     */
    private static final int DEFAULT_THREADPOOL_CAPACITY = 10;
    /**
     * Logger for this class.
     */
    final Logger logger = LoggerFactory.getLogger(ThreadPool.class);
    /**
     * The internal pool for all WorkerThreads.
     */
    private Stack waitingThreads = null;
    /**
     * The number of active threads started by this ThreadPool.
     */
    private int activeThreads = 0;
    /**
     * The maximum number of WorkerThreads in the pool.
     */
    private int capacity = DEFAULT_THREADPOOL_CAPACITY;
    /**
     * Indicates whether the pool has to be shutdown.
     */
    private boolean shutdown = false;

    /**
     * Constructs a new
     * <code>ThreadPool</code>.
     *
     * @param capacity how many threads in the thread pool will be initially
     * started.
     */
    public ThreadPool(int capacity) {
        if (capacity > 0) {
            this.capacity = capacity;
        }

        waitingThreads = new Stack();
        for (int i = 0; i < this.capacity; i++) {
            WorkerThread w = new WorkerThread("WorkerThread_" + i, this);
            w.start();
            waitingThreads.push(w);
        }
        this.activeThreads = this.capacity;

        logger.info("ThreadPool with {} WorkerThreads initialized.",
                String.valueOf(this.capacity));
    }

    /**
     * <p> Executes a task provided by a class that implements the
     * <code>Runnable</code> interface.</p> <p> It uses an already started
     * thread from this ThreadPool to execute the task. If currently no thread
     * is available in the ThreadPool it creates a new thread to execute the
     * task.</p>
     *
     * @param target the task to execute by a thread from this ThreadPool
     * @exception IllegalArgumentException if target is null
     */
    public void run(Runnable target) {
        Assert.notNull(target);

        WorkerThread w = null;
        synchronized (waitingThreads) {
            if (waitingThreads.empty()) {
                if (!shutdown) {
                    w =
                            new WorkerThread("WorkerThread_" + this.activeThreads, this);
                    this.activeThreads++;
                } else {
                    logger.info("ThreadPool has been shutdown. Due to this new worker threads cannot be started!");
                }
            } else {
                w = (WorkerThread) waitingThreads.pop();
            }
            logger.trace("Active WorkerThread count= {}.",
                    String.valueOf(this.activeThreads));
        }
        if (w != null) {
            w.activate(target);
        }
    }

    /**
     * Shutdowns the ThreadPool. This means all started threads by this
     * ThreadPool will be shutdown when the task has been processed.
     */
    public synchronized void shutdown() {
        logger.trace("Active WorkerThread count= {}.",
                String.valueOf(this.activeThreads));
        shutdown = true;
        try {
            while (this.activeThreads > 0) {
                wait();
            }
        }
        catch (InterruptedException e) {
            logger.warn(e.getMessage());
        }
    }

    /**
     * Push back the finished thread into this ThreadPool when the capacity of
     * the pool has not reached.
     *
     * @param w the WorkerThread finished processing and can be pushed back in
     * the pool.
     * @return true if true if pushed back in the pool otherwise false
     * @IllegalArgumentException if w is null
     */
    private synchronized boolean push(WorkerThread w) {
        Assert.notNull(w);
        boolean result = false;
        if (this.waitingThreads.size() < this.capacity && !shutdown) {
            this.waitingThreads.push(w);
            notify();
            result = true;
        } else {
            this.activeThreads--;
            logger.trace("Active WorkerThread count= {}.",
                    String.valueOf(this.activeThreads));
            notify();
            result = false;
        }
        return result;
    }

    /**
     * The WorkerThreads that make up this ThreadPool.
     */
    private class WorkerThread extends Thread {

        /**
         * The unique identifier of this thread.
         */
        private String identifier = null;
        /**
         * The thread pool that this object belongs to.
         */
        private ThreadPool owner;
        /**
         * The task to execute when this WorkerThread gets activated.
         */
        private Runnable target = null;
        
        /**
         * Creates a new WorkerThread.
         *
         * @param identifier unique identifier of this WorkerThread
         * @param owner the thread pool
         * @excpetion IllegalArgumentException if identifier or owner is null
         */
        public WorkerThread(String identifier, ThreadPool owner) {
            Assert.notNull(identifier);
            Assert.notNull(owner);

            this.identifier = identifier;
            this.owner = owner;
        }

        /**
         * Activates this WorkerThread to execute the run-method of target.
         *
         * @param target the task to execute
         * @exception IllegalArgumentException if target is null
         */
        public synchronized void activate(Runnable target) {
            Assert.notNull(target);
            this.target = target;
            notify();
            logger.trace("WorkerThread {} has been activated.",
                    this.identifier);
        }

        /**
         * Executes the task of this WorkerThread.
         */
        @Override
        public synchronized void run() {
            boolean stop = false;
            while (!stop) {
                if (target == null) {
                    try {
                        wait();
                    }
                    catch (InterruptedException e) {
                        logger.error("WorkerThread " + this.identifier
                                + " reports an error.", e);
                        continue;
                    }
                }
                if (target != null) {
                    target.run();
                }
                target = null;
                stop = !( owner.push(this) );
            }
            logger.info("WorkerThread {} has been closed.", this.identifier);
        }

        /**
         * Indicates whether this WorkerThread is working.
         *
         * @return true if it is working otherwise false
         */
        public boolean isBusy() {
            return ( target != null );
        }
    }
}
