package id.co.solusinegeri.psp.data.responses

data class ActiveCompany(
    val banks: List<String>,
    val companyCode: String,
    val id: String,
    val name: String,
    val solution: List<Any>
)