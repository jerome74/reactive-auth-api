package it.wlp.reactor

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
@EnableEncryptableProperties
@SpringBootApplication(scanBasePackages = ["it.wlp.reactor"])
class Application

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}
