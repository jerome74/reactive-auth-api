package it.wlp.reactor.config

import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.core.env.Environment
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component

@Component
@PropertySource("classpath:application.yaml")
class ConfigProperties {

    /**
     * Project
     */

    @Value("\${project.signin}")
    lateinit var signin : String

    @Value("\${project.confirm}")
    lateinit var confirm : String

    @Value("\${project.avatar}")
    lateinit var avatar: String

    @Value("\${project.color}")
    lateinit var color : String

    @Value("\${project.from}")
    lateinit var from : String

    @Value("\${project.apidoc}")
    lateinit var apidoc : String

    @Value("\${project.swaggerui}")
    lateinit var swaggerui : String

    @Value("\${project.loginok}")
    lateinit var loginok : String

    /**
     * Authority
     */

    @Value("\${permission.authorities.useradmin}")
    lateinit var useradmin : String

    @Value("\${permission.authorities.admin}")
    lateinit var admin : String

    @Value("\${permission.authorities.user}")
    lateinit var user : String

    @Value("\${permission.authorities.access}")
    lateinit var access : String

    /**
     * Auth
     */

    @Value("\${auth.apikey}")
    lateinit var apikey : String



    fun getGrantedAuthority(permission: String): Set<SimpleGrantedAuthority> {
        val permissions = mutableListOf<SimpleGrantedAuthority>()

        var role: String

        when (permission) {
            useradmin -> role = admin.substring(1, admin.length - 1).replace("\\", "")
            else -> role = user.substring(1, user.length - 1).replace("\\", "")
        }

        val responseJson = JSONObject("$role!!")

        val name = responseJson.getString("name")
        val actions = responseJson.getJSONObject("actions")

        permissions.add(SimpleGrantedAuthority("ROLE_$name"))


        val read = actions.getString("read")
        if (!read.isNullOrBlank()) permissions.add(SimpleGrantedAuthority(read))

        val write = actions.getString("write")
        if (!write.isNullOrBlank()) permissions.add(SimpleGrantedAuthority(write))

        val delete = actions.getString("delete")
        if (!delete.isNullOrBlank()) permissions.add(SimpleGrantedAuthority(delete))

        return permissions.toSet()
    }

}