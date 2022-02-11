package id.co.solusinegeri.psp.data.networks

import id.co.solusinegeri.psp.data.models.modelLogin
import id.co.solusinegeri.psp.data.responses.CheckCredentialResponse
import id.co.solusinegeri.psp.data.responses.UpdateGatewayResposes
import id.co.solusinegeri.psp.data.responses.getResponsesGateway
import id.co.solusinegeri.psp.data.responses.loginResponse
import retrofit2.http.*

interface ServiceApi {
    @Headers("Content-Type: application/json")
    @POST("katalis/login")
    suspend fun login(
        @Body info: modelLogin
    ): loginResponse
    @GET("katalis/user/credential/check")
    suspend fun getUserInfo(): CheckCredentialResponse

    @Headers("content-type: application/json")
    @GET("   /python/notification/get_sms/{companyId}")
    suspend fun GetNotifikasiGateway(
        @Path("companyId") companyId: String?,
        @Query("category") category: String?,
        @Query("size") size: String
    ): getResponsesGateway
    @Headers("Content-Type: application/json")
    @POST("/python/notification/update_sms/{accountId}/{idNotif}")
    suspend fun Updategatewaysms(
//        @Body info: modelUpdateGateway
        @Path("accountId") accountId: String?,
        @Path("idNotif") idNotif: String?,
        @Query("status") status: String?
    ): UpdateGatewayResposes
}