package it.wlp.reactor.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSenderImpl




//@Configuration
class ConfigMail {

  /*  @Value("\${mail.host}")
    lateinit var host : String

    @Value("\${mail.port}")
    lateinit var port : String

    @Value("\${mail.password}")
    lateinit var password : String

    @Bean
    fun mailSender(): JavaMailSenderImpl? {
        val javaMailSender = JavaMailSenderImpl()
        javaMailSender.protocol = "SMTP"
        javaMailSender.host = host
        javaMailSender.password = password
        javaMailSender.port = port.toInt()
        return javaMailSender
    }*/
}