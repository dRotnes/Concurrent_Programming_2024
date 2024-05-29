package cp.lablessons
package assignment

trait DummyTasks {

    def noncritical(flag: Int, sleepValue: Int)= {
        log(s"P$flag non critical")
        Thread.sleep(sleepValue)

    }

    def critical(flag: Int, sleepValue: Int)= {
        log(s"P$flag entered critical section")
        Thread.sleep(sleepValue)
        // assert(k == turn, s"P$flag Mutual exclusion error: k is $k")
        log(s"P$flag left critical section")

    }
}