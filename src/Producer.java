import java.util.Queue;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class Producer extends Thread
{
    private Queue queue;
    private Lock lock;
    private Condition condition;
    private Random random = new Random();

    public Producer(Queue queue, Lock lock, Condition condition)
    {
        this.queue = queue;
        this.lock = lock;
        this.condition = condition;
    }

    @Override
    public void run()
    {
        while(!isInterrupted()) {
            lock.lock();
            try {
                if (queue.size() < 10) {
                    int ball = random.nextInt(100);
                    queue.add(ball);
                    System.out.printf("[PRODUCER] added ball (%d) to conveyor!%n", ball);
                    condition.signalAll();
                }
                else {
                    System.out.printf("[PRODUCER] Conveyer is full! Going to sleep%n");
                    condition.signalAll();
                    condition.await();
                }
            }
            catch (InterruptedException exception) {
                this.interrupt();
            }
            finally {
                lock.unlock();
            }
            //////////////////////
            //simulated production - no critical section!
            //////////////////////
            try {
                Thread.sleep(random.nextInt(10) + 10);
            }
            catch (InterruptedException exception) {
                this.interrupt();
            }
        }//while not interrupted

        System.out.println("[PRODUCER] Shutting down...");
    }
}
