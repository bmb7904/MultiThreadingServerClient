package threadpool2;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A custom class, containing two inner nested classes, that will serve as a ThreadPool
 * for Connection objects. Connection objects are passed into the ThreadPool class and
 * added to a queue. Three worker threads try to get connection objects from the queue
 * and run them. Only three Connection objects will be running at any given time.
 *
 * I had problems initially because I was having WorkerThreads create entirely new
 * Threads and starting them by calling the start method. Now, I am having the
 * WorkerThreads simply call the connection object's run method. After some time to
 * ponder the problem, I now understand that the Worker
 * Threads are actually the threads running the Connection object code inside the run
 * method. And thus, when I have three clients that are active, that means the Worker
 * Threads are actively running the instructions inside the Connection class, and so
 * any new Connections trying to start can't occur. This means what's actually limiting
 * the number of active connections is the number of WorkerThreads in the array.
 * Previously, I was creating entirely new threads and so workerThreads were simply
 * creating these new threads and then continuing in their loop, while the new thread I
 * created was actually running the code in the Connection class. I was, in effect,
 * running 6 different threads for 3 clients.
 *
 * This implementation of the ThreadPool will use Reentrant Locks and Condition objects
 * for synchronization of Threads.
 *
 * @author Brett Bernardi
 */
public class ThreadPool {
    // The maximum number of active threads
    public static final int MAX_THREADS = 3;
    // global workQueue that contains the Queue of Connections
    private WorkQueue workQueue;
    // an array of WorkerThread objects (threads)
    private WorkerThread[] pool;
    // a Lock to lock an object's monitor
    private Lock myLock = new ReentrantLock();
    // A condition object to block threads and wake them
    private Condition condition = myLock.newCondition();

    


    /**
     * Constructor that takes a reference to a Connection Object from the Server. This
     * connection will be added to the Thread Array.
     */
    public ThreadPool() {
        this.pool = new WorkerThread[MAX_THREADS];
        // create WorkQueue object, which creates a Queue of Connections
        this.workQueue = new WorkQueue();

        // Creates and adds the maximum number of WorkerThread threads to the threads[]
        // array and begins running them.
        for (int i = 0; i < MAX_THREADS; i++) {
            this.pool[i] = new WorkerThread();
            this.pool[i].start();
        }
    }

    /**
     * Public method that adds a Connection thread to the Connection Thread queue
     * through the WorkQueue object field and the appropriate method.
     *
     * @param c - A Connection object
     */
    public void addConnection(Connection c) {
        workQueue.addConnection(c);
    }

    /**
     * WorkQueue stores a linked list of Connection objects.
     * queue
     */
    private class WorkQueue {
        // a linked list of Connections
        private LinkedList<Connection> connectionThreadQueue;

        private WorkQueue() {
            this.connectionThreadQueue = new LinkedList<>();

        }

        /**
         * Adds the specified Connection object (thread) to the connectionThreadQueue
         * linked list (queue). After adding a connection, call the notify() method,
         * which will wake a thread in the waitList(). This method uses a Reentrant
         * lock to give the lock on the WorkerThread's monitor to the calling thread.
         * This means that in case any other threads try to call this method while they
         * do not have the lock, they will be put to the sleep until they are woken.
         *
         * @param connection - A Connection object
         */
        private void addConnection(Connection connection) {
            // give the lock to the calling thread
            myLock.lock();
            try {
                connectionThreadQueue.add(connection);
                // wakes up threads that are waiting on the condition.
                condition.signal();
            }
            finally {
                // release the lock
                myLock.unlock();
            }
        }

        /**
         *
         * Blocks the calling thread if queue is empty. Then, remove and return the
         * first element on the queue. Using a Reentrant lock, only one thread at a
         * time can be calling this method, which is necessary because Linked Lists are
         * not Thread Safe.
         *
         * @return Connection - a Connection object
         */
        private Connection getConnection() throws InterruptedException {
            // give the object monitor's lock to the calling thread
            myLock.lock();
                try {
                    // the condition on which threads are put to sleep
                    while (connectionThreadQueue.isEmpty()) {
                        // put the calling thread to sleep
                        condition.await();
                    }
                    return connectionThreadQueue.removeFirst();
                }
                finally {
                        myLock.unlock();
                }
            }
        }


        /**
         * WorkerThread inner class that extends the Thread class and overwrites the run()
         * method. These threads will simply get connections off the queue and run them.
         */
        private class WorkerThread extends Thread {

            @Override
            /**
             * The run method of a WorkerThread that get's a Connection object and
             * run's the thread. This Worker Thread
             */
            public void run() {

                while (true) {
                    try {
                        // Absolutely essential to call the run method from the
                        // Connection class. Do not create a new Thread.
                        Connection c = workQueue.getConnection();
                        c.run();
                    } catch (InterruptedException e) {

                    }
                }
            }
        }


    }

