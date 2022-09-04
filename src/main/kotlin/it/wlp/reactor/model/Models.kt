package it.wlp.reactor.model

data class UserModel(
    val username: String, val email: String, val password: String
)

data class UserprofileModel(
    val nickname: String, val email: String, val avatarname: String, val avatarcolor: String
)

data class CredentialModel(
    val username: String, val password: String
)


