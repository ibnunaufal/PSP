package id.co.solusinegeri.sms_gateway.data.networks

import id.co.solusinegeri.sms_gateway.data.models.modelLogin
import id.co.solusinegeri.sms_gateway.data.models.modelUpdateGateway
import id.co.solusinegeri.sms_gateway.data.responses.CheckCredentialResponse
import id.co.solusinegeri.sms_gateway.data.responses.UpdateGatewayResposes
import id.co.solusinegeri.sms_gateway.data.responses.getResponsesGateway
import id.co.solusinegeri.sms_gateway.data.responses.loginResponse
import okhttp3.ResponseBody
import retrofit2.http.*

interface ServiceApi {
    @Headers("Content-Type: application/json")
    @POST("katalis/login")
    suspend fun login(
        @Body info: modelLogin
    ): loginResponse
    @GET("katalis/user/credential/check")
    suspend fun getUserInfo(): CheckCredentialResponse

    @Headers("content-type: image/png")
    @GET("   /python/notification/get_sms/{companyId}")
    suspend fun GetNotifikasiGateway(
        @Path("companyId") companyId: String
    ): getResponsesGateway
    @Headers("Content-Type: application/json")
    @POST("/python/notification/update_sms")
    suspend fun Updategatewaysms(
        @Body info: modelUpdateGateway
    ): UpdateGatewayResposes
}