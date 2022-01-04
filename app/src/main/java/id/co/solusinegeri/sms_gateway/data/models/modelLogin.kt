package id.co.solusinegeri.sms_gateway.data.models

import androidx.annotation.Keep

@Keep
data class modelLogin(
    val username: String,
    val password: String
)