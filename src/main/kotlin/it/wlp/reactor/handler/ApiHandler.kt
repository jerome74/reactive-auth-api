package it.wlp.reactor.handler

import it.wlp.reactor.dto.Result
import it.wlp.reactor.dto.ResultSigninDTO
import it.wlp.reactor.dto.ResultTokenDTO
import it.wlp.reactor.entity.Profiles
import it.wlp.reactor.entity.Users
import it.wlp.reactor.exception.InputException
import it.wlp.reactor.exception.ProcessingException
import it.wlp.reactor.exception.SimpleProcessException
import it.wlp.reactor.jwt.JWTReactiveAuthenticationManager
import it.wlp.reactor.jwt.TokenProvider
import it.wlp.reactor.model.CredentialModel
import it.wlp.reactor.repository.ProfilesRepository
import it.wlp.reactor.repository.UsersRepository
import it.wlp.reactor.service.AuthService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class ApiHandler {

    @Autowired
    lateinit var usersRepository: UsersRepository

    @Autowired
    lateinit var profilesRepository: ProfilesRepository

    @Autowired
    lateinit var authService: AuthService

    @Autowired
    lateinit var repositoryReactiveAuthenticationManager: JWTReactiveAuthenticationManager

    @Autowired
    lateinit var tokenProvider: TokenProvider


    fun listUsers(request: ServerRequest): Mono<ServerResponse> {

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
            .body(usersRepository.findAll(), Users::class.java)
    }


    @Throws(ProcessingException::class)
    fun doProfile(request: ServerRequest): Mono<ServerResponse> {

        return Mono.just(request.queryParam("email"))
            .switchIfEmpty(Mono.defer(this::raiseInputException))
            .map { email -> profilesRepository.findByEmail(email.orElse("empty")) }
            .onErrorResume { Mono.error { SimpleProcessException("Any Profile Found!") } }
            .flatMap {
                ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                    .body(it, Profiles::class.java::class.java)
            }
    }

    @Throws(ProcessingException::class)
    fun doConfirm(request: ServerRequest): Mono<ServerResponse> {

        return Mono.just(request.queryParam("email"))
            .switchIfEmpty(Mono.defer(this::raiseInputException))
            .map { email -> profilesRepository.findByEmail(email.orElse("empty")) }
            .onErrorResume { Mono.error { SimpleProcessException("Any Profile Found!") } }
            .flatMap {
                ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                    .body(it, Profiles::class.java::class.java)
            }
    }

    @Throws(ProcessingException::class)
    fun doSignin(request: ServerRequest): Mono<ServerResponse> {

        return request.bodyToMono(Users::class.java)
            .switchIfEmpty(Mono.defer(this::raiseInputException))
            .flatMap { authService.checkIfUserExist(it) }
            .flatMap { authService.executeSingin(it) }
            .flatMap {
                ServerResponse.ok().bodyValue(it as ResultSigninDTO);
            }.onErrorResume { ServerResponse.status(500).bodyValue(ResultSigninDTO(it.message!!,Result.KO)) }
    }

    /*.onErrorResume(Exception.class, (e) -> {
            e.printStackTrace();
            return Mono.just("Error " + e.getMessage()).flatMap((s) -> {
                return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue(new RestResponse(ErrorCode.INTERNAL_SERVER_ERROR, ErrorMessage.ERROR_DESCRIPTION));
            });
        });*/


    @Throws(BadCredentialsException::class)
    fun doLogin(request: ServerRequest): Mono<ServerResponse> {

        return request.bodyToMono(CredentialModel::class.java)
            .map { UsernamePasswordAuthenticationToken(it.username, it.password) }
            .flatMap { repositoryReactiveAuthenticationManager.authenticate(it); }
            .doOnError { BadCredentialsException("Bad crendentials") }
            .flatMap {
                ReactiveSecurityContextHolder.withAuthentication(it);
                ServerResponse.ok().bodyValue(ResultTokenDTO(tokenProvider.createToken(it)));
            }
    }


    fun <T> raiseInputException(): Mono<T> {
        return Mono.error(InputException("error on input value"));
    }
}
