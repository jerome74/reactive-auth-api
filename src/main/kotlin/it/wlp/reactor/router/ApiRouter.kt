package it.wlp.reactor

import it.wlp.reactor.handler.ApiHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router


@Configuration
class ApiRouter {

    @Bean
    fun routerUsersFunction(handler: ApiHandler): RouterFunction<ServerResponse> = router {
        ("/reactive/users").invoke { req -> handler.listUsers(req) }
    }

    @Bean
    fun routerProfilesFunction(handler: ApiHandler): RouterFunction<ServerResponse> = router {
        ("/reactive/profiles").invoke { req -> handler.listProfiles(req) }
    }

    @Bean
    fun routerLoginFunction(handler: ApiHandler): RouterFunction<ServerResponse> = router {
        ("/reactive/login").invoke { req -> handler.doLogin(req) }
    }

    @Bean
    fun routerSigninFunction(handler: ApiHandler): RouterFunction<ServerResponse> = router {
        ("/reactive/signin").invoke { req -> handler.doSignin(req) }
    }

    @Bean
    fun routerProfileFunction(handler: ApiHandler): RouterFunction<ServerResponse> = router {
        ("/reactive/profile").invoke { req -> handler.doProfile(req) }
    }

    @Bean
    fun routerConfirmFunction(handler: ApiHandler): RouterFunction<ServerResponse> = router {
        ("/reactive/confirm").invoke { req -> handler.doConfirm(req) }
    }

}