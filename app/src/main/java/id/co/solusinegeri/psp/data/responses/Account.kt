package id.co.solusinegeri.psp.data.responses

data class Account(
    val accountNumber: String,
    val active: Boolean,
    val callerIdentities: List<Any>,
    val companyId: String,
    val id: String,
    val lastLogin: String,
    val note: String,
    val roles: List<String>,
    val sourceOfFund: SourceOfFund,
    val transactionUnlimited: Boolean,
    val vaNumbers: List<VaNumber>
)