package id.co.solusinegeri.sms_gateway.data.models

import androidx.annotation.Keep

@Keep
data class modelUpdateGateway(
    val accountId : String,
    val idNotif: String
)