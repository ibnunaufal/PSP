package id.co.solusinegeri.sms_gateway.data.responses

data class getResponsesGatewayItem(
    val accountId: String,
    val accountName: String,
    val category: String,
    val content: String?,
    val createdTime: String,
    val id: String,
    val message: String,
    val smsDestination: String,
    val title: String
)