package jnr.signals.issue

import jnr.constants.platform.Signal
import jnr.posix.LibC
import kotlin.concurrent.thread
import jnr.posix.POSIXFactory
import java.util.concurrent.atomic.AtomicInteger

object SignalHandler : LibC.LibCSignalHandler {
    override fun signal(sig: Int) {
        println("Received it!")
    }
}

fun main(args: Array<String>) {
    val received = AtomicInteger()
    val raised = AtomicInteger()

    POSIXFactory.getNativePOSIX().libc().signal(Signal.SIGWINCH.value(), SignalHandler)

    thread(start = true, isDaemon = true) {
        while (true) {
            try {
                println("Raising SIGWINCH...")
                raised.addAndGet(1)
                POSIXFactory.getNativePOSIX().libc().raise(Signal.SIGWINCH.value())
            } catch (t: Throwable) {
                println("Couldn't raise signal:")
                t.printStackTrace()
            }

            println("Sleeping...")
            Thread.sleep(1000)
        }
    }

    println("My PID is ${POSIXFactory.getNativePOSIX().getpid()}")
    Thread.sleep(5000)

    while (true) {
        println("Invoking GC...")
        System.gc()
        Thread.sleep(1000)
    }
}
