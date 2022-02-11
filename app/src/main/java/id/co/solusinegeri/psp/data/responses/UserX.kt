package id.co.solusinegeri.psp.data.responses

data class UserX(
    val accounts: List<Account>,
    val address: String,
    val banks: List<Any>,
    val dateOfBirth: String,
    val email: String,
    val firebase: Firebase,
    val gender: String,
    val id: String,
    val maritalStatus: String,
    val name: String,
    val nik: String,
    val openfire: Openfire,
    val phone: Any,
    val photoUrl: String,
    val placeOfBirth: String,
    val regDate: String,
    val religion: String,
    val socmedAccounts: List<SocmedAccount>,
    val validationStatus: Boolean
)