package id.co.solusinegeri.sms_gateway.data.responses

data class ActiveCompany(
    val banks: List<String>,
    val companyCode: String,
    val id: String,
    val name: String,
    val solution: List<Any>
)