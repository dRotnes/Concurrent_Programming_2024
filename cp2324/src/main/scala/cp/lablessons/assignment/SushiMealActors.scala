package cp.lablessons
package assignment

import akka.pattern.gracefulStop
import akka.actor.{Actor, ActorRef, Props, Terminated}
import akka.util.Timeout
import scala.util.{Failure, Success}
import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.annotation.tailrec
import scala.concurrent.duration._
import scala.util.Random

class ChopstickActor extends Actor {
    import ChopstickActor._
    import CommonMessages._
    // Chopstick name
    val name = self.path.name

    // Chopstick in use
    def inUse(friend: ActorRef): Receive = gracefulStopBehavior orElse{
        case Drop if sender == friend => {
            sender ! ChopstickDropped
            context.become(free);
        }
        case Take => {
            sender ! ChopstickInUse
        }
    }

    // Chopstick free
    def free: Receive = gracefulStopBehavior orElse{
        case Take => {
            sender ! ChopstickTaken
            context.become(inUse(sender))
        }
    }

    def gracefulStopBehavior: Receive ={
        case GracefulStop => {
            context.stop(self)
        }
    } 

    override def postStop () = log(s"$name graceful shutdown")


    override def unhandled(msg: Any): Unit = {
        msg match {
        case m =>
            // super.unhandled(m)
        }
    }
    
    override def receive: Receive = gracefulStopBehavior orElse free
}

object ChopstickActor {
    case object Take
    case object Drop

    def props() = Props(new ChopstickActor())
}

class FriendActor(
    leftChopstick: ActorRef, 
    rightChopstick: ActorRef
) extends Actor {
    import ChopstickActor._
    import FriendActor._
    import CommonMessages._

    // Friend name
    val name = self.path.name
    
    // Execution context 
    implicit private val executionContext = context.dispatcher

    // Time of each activity
    private val eatingTime = Random.nextInt(10000).millis
    private val chattingTime = Random.nextInt(10000).millis
    private val retryTime = 10.millis
    
    // Take left chopstick
    private def takeLeftChopstick = {
        leftChopstick ! Take
    }

    // Take right chopstick
    private def takeRightChopstick = {
        rightChopstick ! Take
    }

    // Drop left Chopstick
    private def dropLeftChopstick = {
        leftChopstick ! Drop
        context.become(leftChopstickFunc)
    }

    // Drop right Chopstick
    private def dropRightChopstick = {
        rightChopstick ! Drop
        context.become(rightChopstickFunc)
    }

    // Waiting time to pick up Left chopstick
    private def waitLeftChopstick(duration: FiniteDuration) = {
        context.system.scheduler.scheduleOnce(duration, self, TakeChopstick)
        context.become(chatting)
    }

    // Waiting time to pick up Right chopstick
    private def waitRightChopstick(duration: FiniteDuration) = {
        context.system.scheduler.scheduleOnce(duration, self, TakeChopstick)
        context.become(rightChopstickFunc)
    }

    def leftChopstickFunc: Receive = gracefulStopBehavior orElse{
        case ChopstickInUse => waitLeftChopstick(retryTime)
        case ChopstickTaken =>{
            log(s"$name got their left(${sender.path.name}) chopstick")
            waitRightChopstick(retryTime)
        }
        case ChopstickDropped => {
            log(s"$name dropped their left(${sender.path.name}) chopstick");
            dropRightChopstick
        }
    }

    def rightChopstickFunc: Receive =  gracefulStopBehavior orElse{
        case TakeChopstick => takeRightChopstick
        case ChopstickInUse => waitRightChopstick(retryTime)
        case ChopstickTaken =>{
            log(s"$name got their right(${sender.path.name}) chopstick")
            context.system.scheduler.scheduleOnce(Random.nextInt(10000).millis, self, Chat)
            log(s"$name is eating...")
            context.become(eating)
        }
        case ChopstickDropped => {
            log(s"$name dropped their right(${sender.path.name}) chopstick");
            log(s"$name is chatting...")
            waitLeftChopstick(Random.nextInt(10000).millis)
        }
    }

    def chatting: Receive = gracefulStopBehavior orElse{
        case TakeChopstick =>{
            takeLeftChopstick
            context.become(leftChopstickFunc)
        }
    }

    def eating: Receive = gracefulStopBehavior orElse{
        case Chat => {
            log(s"$name finished eating")
            dropLeftChopstick
        }
    }

    def gracefulStopBehavior: Receive ={
        case GracefulStop => {
            context.stop(self)
        }
    } 

    override def postStop () = log(s"$name graceful shutdown")


    override def unhandled(msg: Any): Unit = {
        msg match {
        case m =>
            // // super.unhandled(m)
        }
    }

    override def receive: Receive = gracefulStopBehavior orElse{
        case Chat =>{
            log(s"$name is ready to eat!")
            waitLeftChopstick(Random.nextInt(10000).millis)
        }
    }
}

object FriendActor {
    case object Eat
    case object Chat
    case object TakeChopstick

    def props(leftChopstick: ActorRef, rightChopstick: ActorRef) = Props(new FriendActor(leftChopstick, rightChopstick))
}

object CommonMessages {
    case object GracefulStop
    case object ChopstickInUse
    case object ChopstickTaken
    case object ChopstickDropped
}

object SushiMealActors extends App {
    import FriendActor._
    import CommonMessages._
    import globalActorSystem.dispatcher

    implicit val timeout: Timeout = 5.seconds
    

    val c1: ActorRef = globalActorSystem.actorOf(ChopstickActor.props(), name="c1");
    val c2: ActorRef = globalActorSystem.actorOf(ChopstickActor.props(), name="c2");
    val c3: ActorRef = globalActorSystem.actorOf(ChopstickActor.props(), name="c3");
    val c4: ActorRef = globalActorSystem.actorOf(ChopstickActor.props(), name="c4");
    val c5: ActorRef = globalActorSystem.actorOf(ChopstickActor.props(), name="c5");


    val friend1: ActorRef = globalActorSystem.actorOf(FriendActor.props(c2, c1), name="Naruto");
    val friend2: ActorRef = globalActorSystem.actorOf(FriendActor.props(c3, c2), name="Sasuke");
    val friend3: ActorRef = globalActorSystem.actorOf(FriendActor.props(c4, c3), name="Kakashi");
    val friend4: ActorRef = globalActorSystem.actorOf(FriendActor.props(c5, c4), name="Sakura");
    val friend5: ActorRef = globalActorSystem.actorOf(FriendActor.props(c1, c5), name="Hinata");


    log("Starting dinner!!\n")

    friend1 ! Chat
    friend2 ! Chat
    friend3 ! Chat
    friend4 ! Chat
    friend5 ! Chat 

    // Watch actors to handle their termination
    val watcher = globalActorSystem.actorOf(Props(new Actor {
        context.watch(c1)
        context.watch(c2)
        context.watch(c3)
        context.watch(c4)
        context.watch(c5)
        context.watch(friend1)
        context.watch(friend2)
        context.watch(friend3)
        context.watch(friend4)
        context.watch(friend5)

        var terminatedCount = 0
        val expectedTerminations = 10

        def receive: Receive = {
            case Terminated(_) =>
                terminatedCount += 1
                if (terminatedCount == expectedTerminations) {
                    globalActorSystem.terminate()
                }
            case "startShutdown" => startShutdown
        }

        def startShutdown(): Unit = {
            val chopsticks = List(c1, c2, c3, c4, c5)
            val friends = List(friend1, friend2, friend3, friend4, friend5)

            chopsticks.foreach(_ ! GracefulStop)
            friends.foreach(_ ! GracefulStop)
        }
    }))

    globalActorSystem.scheduler.scheduleOnce(40.seconds) {
        println()
        log("Dinner is over!!")
        // Send GracefulStop to all actors
        watcher ! "startShutdown"
    }

}
