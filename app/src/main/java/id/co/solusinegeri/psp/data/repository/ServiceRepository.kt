package id.co.solusinegeri.psp.data.repository

import id.co.solusinegeri.psp.data.models.modelLogin
import id.co.solusinegeri.psp.data.networks.ServiceApi

class ServiceRepository(
    private val api: ServiceApi
) : BaseRepository() {
    suspend fun login(
        username: String,
        password: String,

        ) = safeApiCall {
        api.login(modelLogin(username, password))
    }
    suspend fun getCredentialInfo() = safeApiCall {
        api.getUserInfo()
    }
    suspend fun GetNotifikasiGateway(
        companyId: String,
        category: String?,
        size: String,
        ) = safeApiCall {
        api.GetNotifikasiGateway(companyId, category, size)
    }
    suspend fun Updategatewaysms(
        accountId : String,
        idNotif: String,
        status: String
    ) = safeApiCall {
//        api.Updategatewaysms(modelUpdateGateway(accountId, idNotif))
        api.Updategatewaysms(accountId, idNotif, status)
    }

}