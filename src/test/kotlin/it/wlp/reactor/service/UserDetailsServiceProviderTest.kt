package it.wlp.reactor.service

import it.wlp.reactor.config.ConfigProperties
import it.wlp.reactor.entity.Users
import it.wlp.reactor.repository.ProfilesRepository
import it.wlp.reactor.repository.UsersRepository
import it.wlp.reactor.util.MockitoHelper
import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.Spy
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@ExtendWith(SpringExtension::class)
internal class UserDetailsServiceProviderTest {

    @Mock
    private lateinit var usersRepository: UsersRepository

    @Mock
    lateinit var configProperties: ConfigProperties

    @InjectMocks
     val userDetailsServiceProvider: UserDetailsServiceProvider = UserDetailsServiceProvider()

    @BeforeEach
    fun setUp() {
    }

    @Test
    fun routerUsersFunctionTest() {

        val email = "john.do@email.com"

        Mockito.`when`(usersRepository.findByEmail(MockitoHelper.anyObject(email)))
            .thenReturn(Mono.just(Users("john", "john.do@email.com", "p4ssw0rd", 1)))

        StepVerifier.create(userDetailsServiceProvider.findByUsername(MockitoHelper.anyObject(email))).consumeNextWith {

            assertEquals(it.password , "p4ssw0rd" )
            assertEquals(it.username , "john" )

            verify(usersRepository, times(1)).findByEmail(MockitoHelper.anyObject(email))

        }.expectComplete().verify()
    }
}