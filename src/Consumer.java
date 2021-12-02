import java.util.Queue;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class Consumer extends Thread
{
    private Queue queue;
    private Lock lock;
    private Condition condition;
    private int number;
    private Random random = new Random();

    public Consumer(Queue queue, Lock lock, Condition condition, int number)
    {
        this.queue = queue;
        this.lock = lock;
        this.condition = condition;
        this.number = number;
    }

    @Override
    public void run()
    {
        while (!isInterrupted()) {
            consumeIt();
        }//while not interrupted

        // do rest if anything left on belt
        while (!queue.isEmpty()) {
            consumeIt();
        }

        System.out.printf("[C-%d] Shutting down...%n", number);
    }

    private void consumeIt() {
        Integer ball = null;
        lock.lock();
        try {
            if(queue.size() > 0) {
                ball = (int) queue.remove();
                System.out.printf("[C-%d] Took 1 Ball%n", number);
                //signalAll - maybe we took the last ball, so we wake up producer
                condition.signalAll();
            }
            else
            {
                System.out.printf("[C-%d] Conveyer Belt is empty! Going to sleep%n", number);
                condition.signalAll();
                condition.await();
                System.out.printf("[C-%d] Waking up!%n", number);
            }
        }
        catch (InterruptedException exception) {
            this.interrupt();
        }
        finally {
            lock.unlock();
        }
        //////////////////////
        //simulated packaging - no critical section!
        //////////////////////
        if(ball != null) {
            try {
                System.out.printf("[C-%d] Packing Ball (%d)!%n", number, ball);
                Thread.sleep(random.nextInt(100) + 100);
            } catch (InterruptedException exception) {
                this.interrupt();
            }
        }
    }
}
