package it.wlp.reactor

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["it.wlp.reactor"])
class Application

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}
