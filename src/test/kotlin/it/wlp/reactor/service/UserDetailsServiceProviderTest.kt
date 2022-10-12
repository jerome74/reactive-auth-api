package it.wlp.reactor.service

import it.wlp.reactor.config.ConfigProperties
import it.wlp.reactor.entity.Users
import it.wlp.reactor.repository.UsersRepository
import it.wlp.reactor.repository.UsersRepositoryTest
import it.wlp.reactor.util.MockitoHelper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.eq
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@ExtendWith(SpringExtension::class)
internal class UserDetailsServiceProviderTest {

    private var closeable: AutoCloseable? = null

    inline fun <reified T : Any> mock() = Mockito.mock(T::class.java)

    private val usersRepository: UsersRepository = mock<UsersRepository>()


    private val configProperties: ConfigProperties = mock<ConfigProperties>()

    @InjectMocks
    val userDetailsServiceProvider: UserDetailsServiceProvider =
        UserDetailsServiceProvider(usersRepository, configProperties)

    @BeforeEach
    fun setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    @Throws(Exception::class)
    fun tearDown() {
        closeable!!.close()
    }

    @Test
    fun routerUsersFunctionTest() {

        val username = "john"

        Mockito.`when`(usersRepository.findByUsername(eq(username)))
            .thenReturn(Mono.just(Users("john", "john.do@email.com", "p4ssw0rd", 1)))

        StepVerifier.create(userDetailsServiceProvider.findByUsername(username)).consumeNextWith {

            assertEquals(it.username, "john")
            assertNotNull(it.password)

            verify(usersRepository, times(1)).findByUsername(MockitoHelper.anyObject(username))

        }.expectComplete().verify()
    }
}