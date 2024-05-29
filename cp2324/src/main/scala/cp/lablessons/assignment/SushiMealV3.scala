package cp.lablessons
package assignment

import java.util.concurrent.atomic.AtomicReference;
import scala.annotation.tailrec;
import scala.concurrent.duration._
import scala.util.Random

// Classes
class Chopstick(name: String){
    val state: AtomicReference[State] = new AtomicReference[State](new Free);

    @tailrec
    final def getChopstick(user: Friend, side: String):Boolean = {
        val s0 = state.get;
        s0 match {
            case f : Free => if(state.compareAndSet(s0,new Using)) {
                log(s"${user.name} got their $side($name) chopstick");
                true
            } else getChopstick(user, side);
            case u : Using => false;
        }
    }
    
    def dropChopstick(user:Friend, side: String):Unit = {
        log(s"${user.name} dropped their $side($name) chopstick");
        state.set(new Free);
    }
}

class SushiTray(){
    val state: AtomicReference[State] = new AtomicReference[State](new Free);

    @tailrec
    final def getSushiTray(user:Friend):Boolean = {
        val s0 = state.get;
        s0 match {
            case f : Free => {
                if(state.compareAndSet(s0,new Using))  {
                    log(s"${user.name} got the sushitray");
                    true
                } 
                else getSushiTray(user);
            }
            case u : Using => false;
        }
    }
    
    def dropSushiTray(user: Friend):Unit = {
        log(s"${user.name} dropped the sushitray");
        state.set(new Free);
    }
}
sealed trait State
class Using extends State
class Free extends State

class Friend(
    val name: String,
    val leftChopstick: Chopstick,
    val rightChopstick: Chopstick,
    val sushitray: SushiTray
)
{   
    def chat():Unit = {
        log(s"$name is chatting...")
        Thread.sleep(Random.nextInt(10000));
    }
    def eat():Unit = {
        log(s"$name is eating...")
        Thread.sleep(Random.nextInt(10000));
        log(s"$name finished eating");
    }

    @tailrec final def startFriend(): Unit = {
        try{
            // Try to get sushitray if not, busywait
            while(!sushitray.getSushiTray(this)){}
            // Try to get left chopstick if not, busywait
            while(!leftChopstick.getChopstick(this, "left")){}
            // Try to get right chopstick if not, busywait
            while(!rightChopstick.getChopstick(this, "right")){}

            // Drop sushitray
            sushitray.dropSushiTray(this);
            
            // Eat
            eat();

            // Drop chopsticks
            rightChopstick.dropChopstick(this, "right");
            leftChopstick.dropChopstick(this, "left");

            // Chat
            chat()
            //Repeat
        }
        catch{
            case t: Throwable => {}
        }
        startFriend();
    }
}

object SushiMealV3 extends App{

    val c1:Chopstick = new Chopstick("C1");
    val c2:Chopstick = new Chopstick("C2");
    val c3:Chopstick = new Chopstick("C3");
    val c4:Chopstick = new Chopstick("C4");
    val c5:Chopstick = new Chopstick("C5");

    val sushitray: SushiTray = new SushiTray();

    val p1Thread: Thread = new Thread {
        val friend1: Friend = new Friend("Naruto", c2, c1, sushitray);
        override def run(): Unit = friend1.startFriend();

        override def interrupt(): Unit ={
            try{super.interrupt()}
            catch{
                case t:Throwable => {}
            }
        }
    }
    val p2Thread: Thread = new Thread {
        val friend2: Friend = new Friend("Sasuke", c3, c2, sushitray);
    
        override def run(): Unit = friend2.startFriend();

        override def interrupt(): Unit ={
            try{super.interrupt()}
            catch{
                case t:Throwable => {}
            }
        }
    }
    val p3Thread: Thread = new Thread {
        val friend3: Friend = new Friend("Kakashi", c4, c3, sushitray);
    
        override def run(): Unit = friend3.startFriend();

        override def interrupt(): Unit ={
            try{super.interrupt()}
            catch{
                case t:Throwable => {}
            }
        }
    }
    val p4Thread: Thread = new Thread {
        val friend4: Friend = new Friend("Sakura", c5, c4, sushitray);
    
        override def run(): Unit = friend4.startFriend();

        override def interrupt(): Unit ={
            try{super.interrupt()}
            catch{
                case t:Throwable => {}
            }
        }
    }
    val p5Thread: Thread = new Thread {
        val friend5: Friend = new Friend("Hinata", c1, c5, sushitray);

        override def run(): Unit = friend5.startFriend();

        override def interrupt(): Unit ={
            try{super.interrupt()}
            catch{
                case t:Throwable => {}
            }
        }
    }

    p1Thread.setDaemon(true);
    p2Thread.setDaemon(true);
    p3Thread.setDaemon(true);
    p4Thread.setDaemon(true);
    p5Thread.setDaemon(true);

    log("Starting dinner!!\n");

    p1Thread.start();
    p2Thread.start();
    p3Thread.start();
    p4Thread.start();
    p5Thread.start();

    // p1Thread.join();
    // p2Thread.join();
    // p3Thread.join();
    // p4Thread.join();
    // p5Thread.join();

    // Let it run for 40 seconds
    Thread.sleep(40000);

    p1Thread.interrupt();
    p2Thread.interrupt();
    p3Thread.interrupt();
    p4Thread.interrupt();
    p5Thread.interrupt();
    
    println()
    log("Dinner is over!!");
}