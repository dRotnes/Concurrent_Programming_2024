package cp.lablessons
package assignment

object Hyman extends App{

    @volatile var b = Array(false, false);
    @volatile var k = 1;
    @volatile var sharedMem: Int = 0;

    def hyman(turn: Int, flag: Int) = { 
        try{
            for (_<- 0 until 10) {

                // Non-critical section

                b(flag) = true;
                while (k != turn) {
                    while(b(1 - flag)){}
                    k = turn;
                }
                
                // Critical section
                
                log(s"P$flag entered critical zone");
                sharedMem = flag;
                Thread.sleep(3000)
                assert(k == turn, throw new Exception(s"P$flag mutual exclusion doesn't hold"));
                assert(sharedMem == flag, throw new Exception(s"P$flag mutual exclusion doesn't hold"));
                log(s"P$flag left critical zone")

                b(flag) = false;
            }
        }
        catch {
            case t: Throwable => {
                t.printStackTrace();
            }
        }

    }

    val p0Thread: Thread = new Thread {
        override def run(): Unit = hyman(1,0);

        override def interrupt(): Unit ={
            try{super.interrupt()}
            catch{
                case t:Throwable => {}
            }
        }
    }

    val p1Thread: Thread = new Thread {
        override def run(): Unit = hyman(0,1);

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
