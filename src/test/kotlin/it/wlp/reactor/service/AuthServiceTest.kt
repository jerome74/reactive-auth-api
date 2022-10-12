package it.wlp.reactor.service

import it.wlp.reactor.dto.Result
import it.wlp.reactor.entity.Profiles
import it.wlp.reactor.entity.Users
import it.wlp.reactor.repository.ProfilesRepository
import it.wlp.reactor.repository.UsersRepository
import it.wlp.reactor.repository.UsersRepositoryTest
import it.wlp.reactor.util.MockitoHelper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.mockito.InjectMocks
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.eq
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.test.util.ReflectionTestUtils
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

internal class AuthServiceTest {

    inline fun <reified T : Any> mock() = Mockito.mock(T::class.java)

    private var closeable: AutoCloseable? = null

    private val usersRepository: UsersRepository = mock<UsersRepository>()

    private val profilesRepository: ProfilesRepository = mock<ProfilesRepository>()

    private val emailSender: JavaMailSender = mock<JavaMailSender>()

    @InjectMocks
    val authService: AuthService = AuthService(usersRepository, profilesRepository, emailSender)

    @BeforeEach
    fun setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        ReflectionTestUtils.setField(authService, "message", "we have sent a confirmation via email at");
        ReflectionTestUtils.setField(authService, "subject", " auth confirm email for");
        ReflectionTestUtils.setField(authService, "from", "auth.service@tim.it");
        ReflectionTestUtils.setField(authService, "text1", "Hello");
        ReflectionTestUtils.setField(authService, "text2", ", please confirm your registration and activate your account click on http://%s/reactive/confirm?email=");
        ReflectionTestUtils.setField(authService, "color", "'[0.5, 0.5, 0.5, 1]");
    }

    @AfterEach
    @Throws(Exception::class)
    fun tearDown() {
        closeable!!.close()
    }

    @Test
    fun executeConfirm() {

        val email: String = "ken.falco@email.com"

        Mockito.`when`(profilesRepository.findByEmail(eq(email)))
            .thenReturn(Mono.just(Profiles("falco", "ken.falco@email.com", "ken", "[0.5, 0.5, 0.5, 1]", 0)))
        Mockito.`when`(profilesRepository.save(MockitoHelper.anyObject()))
            .thenReturn(Mono.just(Profiles("falco", "ken.falco@email.com", "ken", "[0.5, 0.5, 0.5, 1]", 1)))
        Mockito.`when`(usersRepository.findByEmail(eq(email)))
            .thenReturn(Mono.just(Users("falco", "ken.falco@email.com", "p4ssw0rd", 0)))
        Mockito.`when`(usersRepository.save(MockitoHelper.anyObject()))
            .thenReturn(Mono.just(Users("falco", "ken.falco@email.com", "p4ssw0rd", 1)))
        StepVerifier.create(authService.executeConfirm(email)).consumeNextWith {
            assertEquals(it.name, "OK")

            verify(profilesRepository, times(1)).save(MockitoHelper.anyObject())
            verify(profilesRepository, times(1)).findByEmail(eq(email))
            verify(usersRepository, times(1)).findByEmail(eq(email))
            verify(usersRepository, times(1)).save(MockitoHelper.anyObject())

        }.expectComplete().verify()

    }

    @Test
    fun executeSingin() {

        val username: String = "john"
        val user = Users(username, "ken.falco@email.com", "p4ssw0rd", 1)
        val profile : Profiles = Profiles("falco", "ken.falco@email.com", "ken", "[0.5, 0.5, 0.5, 1]", 1)

        Mockito.`when`(profilesRepository.save(MockitoHelper.anyObject()))
            .thenReturn(Mono.just(profile))
        Mockito.`when`(usersRepository.save(MockitoHelper.anyObject()))
            .thenReturn(Mono.just(user))


        StepVerifier.create(authService.executeSingin(eq(user))).consumeNextWith {
            assertEquals(it.result, Result.OK)
            assertEquals(it.messageError, "we have sent a confirmation via email at[ ken.falco@email.com ]")
            verify(usersRepository, times(1)).save(MockitoHelper.anyObject())
            verify(profilesRepository, times(1)).save(MockitoHelper.anyObject())
        }.expectComplete().verify()
    }

    @Test
    fun checkIfUserExistOK() {

        val username: String = "john"
        val user = Users(username, "john.do@email.com", "p4ssw0rd", 1)
        Mockito.`when`(usersRepository.findByUsername(eq(username)))
            .thenReturn(Mono.empty())

        StepVerifier.create(authService.checkIfUserExist(eq(user))).consumeNextWith {
            assertEquals(it.username, "john")
            verify(usersRepository, times(1)).findByUsername(eq(username))
        }.expectComplete().verify()

    }

    @Test
    fun checkIfUserExistKO() {

        val username: String = "john"
        val user = Users(username, "john.do@email.com", "p4ssw0rd", 1)
        Mockito.`when`(usersRepository.findByUsername(eq(username)))
            .thenReturn(Mono.just(user))

        StepVerifier.create(authService.checkIfUserExist(eq(user))).consumeErrorWith {
            assertEquals(it.message, "email already present: john.do@email.com")
            verify(usersRepository, times(1)).findByUsername(eq(username))
        }.verify()

    }
}