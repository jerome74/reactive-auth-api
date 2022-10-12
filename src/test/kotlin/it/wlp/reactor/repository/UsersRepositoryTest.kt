package it.wlp.reactor.repository

import it.wlp.reactor.entity.Profiles
import it.wlp.reactor.entity.Users
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@ExtendWith(SpringExtension::class)
@DataMongoTest
class UsersRepositoryTest {

    @BeforeEach
    fun setUp() {

    }

    @Autowired
    lateinit var usersRepository: UsersRepository

    @Autowired
    lateinit var profilesRepository: ProfilesRepository


    @Test
    fun usersRepositorySaveTest() {

        usersRepository.save( Users("falco", "ken.falco@email.com", "p4ssw0rd", 0)).`as`(StepVerifier::create)
            .consumeNextWith {
                Assertions.assertEquals(it.username, "falco")
                Assertions.assertEquals(it.email, "ken.falco@email.com")
            }
            .verifyComplete()
    }

    @Test
    fun usersRepositoryFindByEmailTest() {

        usersRepository.save( Users("falco", "ken.falco@email.com", "p4ssw0rd", 0)).map {
            usersRepository.findByEmail(it.email).`as`(StepVerifier::create) .expectNextCount(1)
                .verifyComplete()
        }
    }

    @Test
    fun usersRepositoryFindByUsernameTest() {

        usersRepository.save( Users("falco", "ken.falco@email.com", "p4ssw0rd", 0)).map {
            usersRepository.findByUsername(it.username).`as`(StepVerifier::create) .expectNextCount(1)
                .verifyComplete()
        }
    }

    @Test
    fun profilesRepositorySaveTest() {

        profilesRepository.save(Profiles("falco", "ken.falco@email.com", "ken", "[0.5, 0.5, 0.5, 1]", 1)).`as`(StepVerifier::create)
            .consumeNextWith {
                Assertions.assertEquals(it.nickname, "falco")
                Assertions.assertEquals(it.email, "ken.falco@email.com")
            }
            .verifyComplete()
    }


    @Test
    fun profilesRepositoryFindByUsernameTest() {

        profilesRepository.save(Profiles("falco", "ken.falco@email.com", "ken", "[0.5, 0.5, 0.5, 1]", 1)).map {
            profilesRepository.findByEmail(it.email).`as`(StepVerifier::create) .expectNextCount(1)
                .verifyComplete()
        }
    }

    @Test
    fun profilesRepositoryFindByEmailAndActiveeTest() {

        profilesRepository.save( Profiles("falco", "ken.falco@email.com", "ken", "[0.5, 0.5, 0.5, 1]", 1)).map {
            profilesRepository.findByEmailAndActive(it.email,1).`as`(StepVerifier::create) .expectNextCount(1)
                .verifyComplete()
        }
    }

}