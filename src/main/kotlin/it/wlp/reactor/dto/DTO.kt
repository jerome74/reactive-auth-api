package it.wlp.reactor.dto

import com.fasterxml.jackson.annotation.JsonProperty

enum class Result {
    OK,
    KO
}

data class ResultTokenDTO(
    @JsonProperty("access_token") var access_token: String,
    @JsonProperty("scope") var scope: String = "default",
    @JsonProperty("token_type") var token_type: String = "Bearer",
    @JsonProperty("expires_in") var expires_in: Long = 30000L
)

data class ProfilesDTO(
    @JsonProperty("nickname") var nickname: String = "",
    @JsonProperty("email") var email: String = "",
    @JsonProperty("avatarname") var avatarname: String = "",
    @JsonProperty("avatarcolor") var avatarcolor: String = "",
    @JsonProperty("active") var active: String = "",
    @JsonProperty("startdate") var startdate: String = "",
    @JsonProperty("enddate") var enddate: String?
)


data class UsersDTO(
    @JsonProperty("username") var username: String = "",
    @JsonProperty("email") var email: String = "",
    @JsonProperty("password") var password: String = "",
    @JsonProperty("active") var active: String = "",
    @JsonProperty("startdate") var startdate: String = "",
    @JsonProperty("enddate") var enddate: String?
)

data class ResultSigninDTO(@JsonProperty("message") var messageError: String, @JsonProperty("result")  var result: Result)
