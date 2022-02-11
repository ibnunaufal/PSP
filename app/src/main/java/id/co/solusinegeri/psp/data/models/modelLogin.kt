package id.co.solusinegeri.psp.data.models

import androidx.annotation.Keep

@Keep
data class modelLogin(
    val username: String,
    val password: String
)