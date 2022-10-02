package it.wlp.reactor.router

import it.wlp.reactor.auth.JWTHeadersExchangeMatcher
import it.wlp.reactor.dto.Result
import it.wlp.reactor.dto.ResultSigninDTO
import it.wlp.reactor.entity.Profiles
import it.wlp.reactor.entity.Users
import it.wlp.reactor.handler.ApiHandler
import it.wlp.reactor.jwt.JWTReactiveAuthenticationManager
import it.wlp.reactor.jwt.TokenProvider
import it.wlp.reactor.repository.ProfilesRepository
import it.wlp.reactor.repository.UsersRepository
import it.wlp.reactor.service.AuthService
import it.wlp.reactor.service.UserDetailsServiceProvider
import it.wlp.reactor.util.MockitoHelper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.*
import org.mockito.Mock
import org.mockito.Mockito
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.server.RequestPredicates
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration


@ExtendWith(SpringExtension::class)
internal class ApiRouterTest{

    private val ENDPOINT_LOGIN = "/reactive/login"
    private val ENDPOINT_USERS = "/reactive/users"
    private val ENDPOINT_PROFILES = "/reactive/profiles"
    private val ENDPOINT_SIGNIN = "/reactive/signin"
    private val ENDPOINT_PROFILE = "/reactive/profile"
    private val ENDPOINT_CONFIRM = "/reactive/confirm"

    private val ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmb29AZW1haWwuY29tIiwiZXhwIjoxNjM4ODU1MzA1LCJpYXQiOjE2Mzg4MTkzMDV9.q4FWV7yVDAs_DREiF524VZ-udnqwV81GEOgdCj6QQAs"

    private val MESSAGE =", please confirm your registration and activate your account click on http://%s/reactive/confirm?email="

    private lateinit var apiHandler : ApiHandler

    private lateinit var routerFunction: RouterFunction<ServerResponse>

    lateinit var routerTestClient: WebTestClient

    private lateinit var filter: AuthenticationWebFilter

    @Mock
    private lateinit var usersRepository: UsersRepository

    @Mock
    private lateinit var profilesRepository: ProfilesRepository

    @Mock
    private lateinit var authService: AuthService

    @Mock
    private lateinit var repositoryReactiveAuthenticationManager: JWTReactiveAuthenticationManager

    @Mock
    private lateinit var tokenProvider: TokenProvider

    @Mock
    private lateinit var service: UserDetailsServiceProvider

    @Mock
    private lateinit var authenticationManager: ReactiveAuthenticationManager

    @Mock
    private lateinit var authenticationConverter: ServerAuthenticationConverter


    @BeforeEach
    internal fun setUp() {

        filter = AuthenticationWebFilter(this.authenticationManager);
        filter.setServerAuthenticationConverter(this.authenticationConverter);
        filter.setSecurityContextRepository(WebSessionServerSecurityContextRepository());
        filter.setRequiresAuthenticationMatcher(JWTHeadersExchangeMatcher());

        apiHandler = ApiHandler(usersRepository
            ,profilesRepository
            ,authService
            ,repositoryReactiveAuthenticationManager
            ,tokenProvider)

        routerFunction = RouterFunctions.route(RequestPredicates.POST(ENDPOINT_LOGIN), apiHandler::doLogin)
            .and(RouterFunctions.route(RequestPredicates.GET(ENDPOINT_USERS), apiHandler::listUsers))
            .and(RouterFunctions.route(RequestPredicates.GET(ENDPOINT_PROFILES), apiHandler::listProfiles))
            .and(RouterFunctions.route(RequestPredicates.POST(ENDPOINT_SIGNIN), apiHandler::doSignin))
            .and(RouterFunctions.route(RequestPredicates.GET(ENDPOINT_PROFILE), apiHandler::doProfile))
            .and(RouterFunctions.route(RequestPredicates.GET(ENDPOINT_CONFIRM), apiHandler::doConfirm))

         routerTestClient = WebTestClient.bindToRouterFunction(routerFunction).webFilter<WebTestClient.RouterFunctionSpec>(filter).configureClient()
            .responseTimeout(Duration.ofMillis(30000)).build()
    }

    @Test
    fun routerLoginFunctionTest() {

        Mockito.`when`(repositoryReactiveAuthenticationManager.authenticate(MockitoHelper.anyObject()))
            .thenReturn(Mono.just(UsernamePasswordAuthenticationToken(null, null, arrayListOf())))

        Mockito.`when`(tokenProvider.createToken(MockitoHelper.anyObject()))
            .thenReturn(ACCESS_TOKEN)

        val body = routerTestClient.post().uri(ENDPOINT_LOGIN).bodyValue("{\"username\":\"wlongo\",\"password\":\"admin123\"}")
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.CONTENT_TYPE, "application/json").exchange().expectBody();

        System.out.printf("RESPONSE BODY : %s", body.returnResult().toString());

        body.jsonPath("$.access_token").isEqualTo(ACCESS_TOKEN);
    }


    @Test
    fun routerUsersFunctionTest() {

        Mockito.`when`(usersRepository.findAll())
            .thenReturn(Flux.just(Users("john", "john.do@email.com", "p4ssw0rd")))

        val body = routerTestClient.get().uri(ENDPOINT_USERS)
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.CONTENT_TYPE, "application/json").exchange().expectBody();

        System.out.printf("RESPONSE BODY : %s", body.returnResult().toString());

        body.jsonPath("$.[0].username").isEqualTo("john");
        body.jsonPath("$.[0].email").isEqualTo("john.do@email.com");
        body.jsonPath("$.[0].password").isEqualTo("p4ssw0rd");
    }

    @Test
    fun routerProfilesFunctionTest() {

        Mockito.`when`(profilesRepository.findAll())
            .thenReturn(Flux.just(Profiles("falco", "ken.falco@email.com", "ken", "[0.5, 0.5, 0.5, 1]")))

        val body = routerTestClient.get().uri(ENDPOINT_PROFILES)
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.CONTENT_TYPE, "application/json").exchange().expectBody();

        System.out.printf("RESPONSE BODY : %s", body.returnResult().toString());

        body.jsonPath("$.[0].nickname").isEqualTo("falco");
        body.jsonPath("$.[0].email").isEqualTo("ken.falco@email.com");
        body.jsonPath("$.[0].avatarname").isEqualTo("ken");
        body.jsonPath("$.[0].avatarcolor").isEqualTo("[0.5, 0.5, 0.5, 1]");

    }

    @Test
    fun routerSigninFunctionTest() {

        Mockito.`when`(authService.checkIfUserExist(MockitoHelper.anyObject()))
            .thenReturn(Mono.just(Users("walter.longo74", "walter.longo74@gmail.com", "Em1l14n0!", 0)))

        Mockito.`when`(authService.executeSingin(MockitoHelper.anyObject()))
            .thenReturn(Mono.just(ResultSigninDTO(MESSAGE, Result.OK)))


        val body = routerTestClient.post().uri(ENDPOINT_SIGNIN).bodyValue("{" +
                "\"username\":\"walter.longo74\"," +
                "\"email\":\"walter.longo74@gmail.com\"," +
                "\"password\":\"Em1l14n0!\"}")
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.CONTENT_TYPE, "application/json").exchange().expectBody();

        System.out.printf("RESPONSE BODY : %s", body.returnResult().toString());

        body.jsonPath("$.message").isEqualTo(MESSAGE);
        body.jsonPath("$.result").isEqualTo(Result.OK.name);


    }

    @Test
    fun routerSigninFunctionTestKO() {

        Mockito.`when`(authService.checkIfUserExist(MockitoHelper.anyObject()))
            .thenReturn(Mono.error(Throwable("email already present: walter.longo74@gmail.com")))



        val body = routerTestClient.post().uri(ENDPOINT_SIGNIN).bodyValue("{" +
                "\"username\":\"walter.longo74\"," +
                "\"email\":\"walter.longo74@gmail.com\"," +
                "\"password\":\"Em1l14n0!\"}")
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.CONTENT_TYPE, "application/json").exchange().expectBody();

        System.out.printf("RESPONSE BODY : %s", body.returnResult().toString());

        body.jsonPath("$.message").isEqualTo("email already present: walter.longo74@gmail.com");
        body.jsonPath("$.result").isEqualTo(Result.KO.name);


    }

    @Test
    fun routerConfirmFunctionTest() {

        Mockito.`when`(authService.executeConfirm(MockitoHelper.anyObject()))
            .thenReturn(Mono.just(Result.OK))

        val body = routerTestClient.get().uri { it.path(ENDPOINT_CONFIRM)
            .queryParam("email", "wlongo@minsait.com")
            .build() }
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.CONTENT_TYPE, "application/json").exchange().expectBody();

        System.out.printf("RESPONSE BODY : %s", body.returnResult().toString());

        body.jsonPath("$").isEqualTo(Result.OK.name);


    }

    @Test
    fun routerProfileFunctionTest() {

        val email = "wlongo@minsait.com"

        Mockito.`when`( profilesRepository.findByEmailAndActive(MockitoHelper.anyObject(email), eq(1)))
            .thenReturn(Mono.just(Profiles("falco", "ken.falco@email.com", "ken", "[0.5, 0.5, 0.5, 1]")))

        val body = routerTestClient.get().uri { it.path(ENDPOINT_PROFILE)
            .queryParam("email", email)
            .build() }
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.CONTENT_TYPE, "application/json").exchange().expectBody();

        System.out.printf("RESPONSE BODY : %s", body.returnResult().toString());

        body.jsonPath("$.nickname").isEqualTo("falco");
        body.jsonPath("$.email").isEqualTo("ken.falco@email.com");
        body.jsonPath("$.avatarname").isEqualTo("ken");
        body.jsonPath("$.avatarcolor").isEqualTo("[0.5, 0.5, 0.5, 1]");


    }

}