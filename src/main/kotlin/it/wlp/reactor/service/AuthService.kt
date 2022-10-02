package it.wlp.reactor.service

import it.wlp.reactor.config.IPObject
import it.wlp.reactor.dto.Result
import it.wlp.reactor.dto.ResultSigninDTO
import it.wlp.reactor.entity.Profiles
import it.wlp.reactor.entity.Users
import it.wlp.reactor.exception.ProcessingException
import it.wlp.reactor.repository.ProfilesRepository
import it.wlp.reactor.repository.UsersRepository
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.net.InetAddress
import java.net.NetworkInterface
import java.sql.Timestamp
import java.time.Instant
import java.util.function.Consumer

@Suppress("UNREACHABLE_CODE")
@Service
class AuthService {

    @Autowired
    lateinit var usersRepository: UsersRepository

    @Autowired
    lateinit var profilesRepository: ProfilesRepository

    @Autowired
    lateinit var emailSender: JavaMailSender

    @Value("\${email.message}")
    lateinit var message: String

    @Value("\${email.subject}")
    lateinit var subject: String

    @Value("\${email.from}")
    lateinit var from: String

    @Value("\${email.text1}")
    lateinit var text1: String

    @Value("\${email.text2}")
    lateinit var text2: String

    @Value("\${project.color}")
    lateinit var color: String

    val log = LogManager.getLogger(AuthService::class.java.getName());

    @Throws(ProcessingException::class)
    fun executeConfirm(email: String): Mono<Result> {

        return profilesRepository.findByEmail(email).flatMap {
            it.active = 1
            profilesRepository.save(it).flatMap {
                usersRepository.findByEmail(email).flatMap {
                    it.active = 1
                    usersRepository.save(it).map {
                        Result.OK
                    }
                }
            }
        }.doOnError { Mono.error<ProcessingException> { ProcessingException("Confirm,", it) } }
    }

    @Throws(ProcessingException::class)
    fun executeSingin(user: Users): Mono<ResultSigninDTO> {


        val profileMono = profilesRepository.insert(
            Mono.just(
                Profiles(
                    user.username, user.email, user.username, color, 0, Timestamp.from(
                        Instant.now()
                    ), null
                )
            )
        )
            .map { it }.take(1).single()
            .doOnError { Mono.error<ProcessingException> { ProcessingException("Singin on profile,", it) } }

        val userMono = usersRepository.save(user)

        return Mono.zip(profileMono, userMono).map {

            val simpleMailMessage = SimpleMailMessage();

            simpleMailMessage.setFrom(from);
            simpleMailMessage.setTo(it.t2.email);
            simpleMailMessage.setSubject(subject + it.t2.username);

            val host = InetAddress.getLocalHost().toString()

            simpleMailMessage.setText(
                String.format("$text1 ${it.t2.username}$text2${it.t2.email}", "${IPObject.IP}:8082")
            )

            emailSender.send(simpleMailMessage)

            return@map ResultSigninDTO(message.plus("[ ${it.t2.email} ]"), Result.OK)

        }.doOnError { Mono.error<ProcessingException> { ProcessingException("on Singin,", it) } }

    }

    @Throws(ProcessingException::class)
    fun checkIfUserExist(user: Users): Mono<Users> {
        return usersRepository.findByUsername(user.username)
            .switchIfEmpty {
                user.active = 2
                return@switchIfEmpty Mono.just(user)
            }
            .map {
                if (it.active == 2) {
                    user.active = 0
                    return@map user
                } else
                    throw Throwable("email already present: ${user.email}")
            }
    }
}
