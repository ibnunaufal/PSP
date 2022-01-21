package id.co.solusinegeri.sms_gateway.data.repository

import id.co.solusinegeri.sms_gateway.data.models.modelLogin
import id.co.solusinegeri.sms_gateway.data.models.modelUpdateGateway
import id.co.solusinegeri.sms_gateway.data.networks.ServiceApi

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