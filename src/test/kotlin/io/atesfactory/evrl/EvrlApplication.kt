package io.atesfactory.evrl

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EvrlApplication

fun main(args: Array<String>) {
    runApplication<EvrlApplication>(*args)
}
