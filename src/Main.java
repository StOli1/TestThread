import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main
{
    private static Lock lock = new ReentrantLock();
    private static Condition condition = lock.newCondition();

    public static void main(String[] args) throws Exception
    {
        Queue<Integer> queue = new LinkedList<>();

        //Create threads
        Producer producer = new Producer(queue, lock, condition);
        Consumer consumer1 = new Consumer(queue, lock, condition, 1);
        Consumer consumer2 = new Consumer(queue, lock, condition, 2);
        Consumer consumer3 = new Consumer(queue, lock, condition, 3);

        //execute threads
        producer.start();
        consumer1.start();
        consumer2.start();
        consumer3.start();

        try { Thread.sleep(10000); }
        catch (Exception exception) { exception.printStackTrace(); }

        producer.interrupt();

        consumer1.interrupt();
        consumer2.interrupt();
        consumer3.interrupt();

        consumer1.join();
        consumer2.join();
        consumer3.join();
    }
}