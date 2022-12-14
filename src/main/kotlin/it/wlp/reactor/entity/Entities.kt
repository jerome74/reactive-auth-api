package it.wlp.reactor.entity

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.sql.Timestamp
import java.time.Instant
import java.util.*

@Document
data class Users(@Field("username") var username: String
                 ,@Field("email") var email: String
                 ,@Field("password") var password: String
                 ,@Field("active") var active: Int = 0
                 ,@Field("startdate") var startdate: Date? = Timestamp.from(Instant.now())
                 ,@Field("enddate") var enddate: Date? = null)

@Document
data class Profiles(@Field("nickname") var nickname: String
                    ,@Field("email") var email: String
                    ,@Field("avatarname") var avatarname: String
                    ,@Field("avatarcolor") var avatarcolor: String
                    ,@Field("active") var active: Int = 0
                    ,@Field("startdate") var startdate: Date = Timestamp.from(Instant.now())
                    ,@Field("enddate") var enddate: Date? = null)