package it.wlp.reactor.config

import it.wlp.reactor.util.UtilCrypt
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Configuration
@Order(1)
class FilterIP: WebFilter{


    override fun filter(serverWebExchange: ServerWebExchange, webFilterChain: WebFilterChain): Mono<Void> {
       IPObject.IP = serverWebExchange.request.remoteAddress!!.address.hostAddress;
        return webFilterChain.filter(serverWebExchange);
    }
}