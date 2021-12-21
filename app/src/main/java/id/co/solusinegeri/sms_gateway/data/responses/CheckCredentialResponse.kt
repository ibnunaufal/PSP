package id.co.solusinegeri.sms_gateway.data.responses

data class CheckCredentialResponse(
    val activeCompany: ActiveCompany,
    val companies: List<Company>,
    val firstLogin: Boolean,
    val tags: List<Any>,
    val user: UserX
)