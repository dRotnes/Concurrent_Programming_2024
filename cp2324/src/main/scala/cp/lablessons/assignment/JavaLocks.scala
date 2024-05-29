package cp.lablessons
package assignment

import java.util.concurrent.locks.ReentrantLock;

object JavaLocks extends App{

    val lock: ReentrantLock = new ReentrantLock();

    var sharedMem: Int = 0;

    def process(flag: Int) = {
        try{
            for(_<-0 until 10){

                // Non Critical Actions

                lock.lock();
                try {
                    // Critical Actions
                    log(s"P$flag entered critical zone")
                    sharedMem = flag;
                    Thread.sleep(3000)
                    
                    assert(sharedMem == flag, s"P$flag mutual exclusion doesn't hold");

                } finally {
                    log(s"P$flag left critical zone")
                    lock.unlock();
                }
            }
        }
        catch {
            case t: Throwable => {
                t.printStackTrace();
            }
        }
    }

    val p0Thread: Thread = new Thread {
        override def run(): Unit = process(0);

        override def interrupt(): Unit ={
            try{super.interrupt()}
            catch{
                case t:Throwable => {}
            }
        }
    }

    val p1Thread: Thread = new Thread {
        override def run(): Unit = process(1);

        override def interrupt(): Unit ={
            try{super.interrupt()}
            catch{
                case t:Throwable => {}
            }
        }
    }

    p0Thread.start();
    p1Thread.start();

    p0Thread.join();
    p1Thread.join();

    p0Thread.interrupt();
    p1Thread.interrupt();
    
    println("Test completed. If no exceptions were thrown, mutual exclusion holds.");
    
}