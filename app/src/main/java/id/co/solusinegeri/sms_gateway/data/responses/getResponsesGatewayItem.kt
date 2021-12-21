package id.co.solusinegeri.sms_gateway.data.responses

data class getResponsesGatewayItem(
    val _id: String,
    val accountName: String,
    val category: String,
    val message: String,
    val smsDestination: String,
    val title: String
)