package id.co.solusinegeri.sms_gateway.data.responses

data class loginResponse(
    val firstLogin: Boolean,
    val user: User
)