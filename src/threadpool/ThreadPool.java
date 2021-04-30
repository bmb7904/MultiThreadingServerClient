package threadpool;

import java.util.LinkedList;

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
 * This implementation of the ThreadPool will use synchronized methods and wait() and
 * notify() for Thread synchronization.
 *
 * @author Brett Bernardi
 */
public class ThreadPool {
    // The maximum number of active threads
    public static final int MAX_THREADS = 3;
    // global workQueue that contains the Queue of Connections
    private WorkQueue workQueue;
    // an array of WorkerThread objects (threads)
    private WorkerThread pool[];

    /**
     * Constructor that takes a reference to a Connection Object from the Server. This
     * connection will be added to the WorkerThread Array.
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
         * which will wake a thread in the waitList().This method is synchronized,
         * meaning that whatever thread calls this method gets the lock on the WorkQueue
         * object's monitor, and thus any other threads that try to call this method are
         * put to sleep until the lock is released (the synchronized method ends).
         *
         * @param connection - A Connection object
         */
        private synchronized void addConnection(Connection connection) {
            connectionThreadQueue.add(connection);
            // wakes up a single thread that is currently blocked on the WorkerQueue
            // object.
            notify();
        }

        /**
         *
         * Blocks the calling thread if queue is empty. Then, remove and return the
         * first element on the queue. This method is synchronized,
         * meaning that whatever thread calls this method gets the lock on the WorkQueue
         * object's monitor, and thus any other threads that try to call this method are
         * put to sleep until the lock is released (the synchronized method ends).
         *
         * @return Connection - a Connection object from the queue
         */
        private synchronized Connection getConnection() throws InterruptedException {
                while(connectionThreadQueue.isEmpty()) {
                    // put the calling thread to sleep until it notified
                    wait();
                }
                return connectionThreadQueue.removeFirst();
            }
        }


        /**
         * WorkerThread inner class that extends the Thread class and overwrites the run()
         * method. These threads will simply pop connections off the queue.
         */
        private class WorkerThread extends Thread {

            @Override
            /**
             * The run method of a WorkerThread that get's a Connection object ( a
             * thread) from the queue using the getConnection method, and runs it.
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

