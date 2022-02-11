package id.co.solusinegeri.psp.ui.home

import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import id.co.solusinegeri.psp.R
import id.co.solusinegeri.psp.data.database.adapterlog.DbAdapter
import id.co.solusinegeri.psp.data.responses.getResponsesGateway
import kotlinx.android.synthetic.main.home_fragment.*
import java.util.ArrayList
import kotlin.properties.Delegates

class HomeActivity : AppCompatActivity() {
    var dbHelper = DbAdapter(this).TableOfflineAttendance()
    var adapterTransaction by Delegates.notNull<RecycleViewLog>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }
    fun sendingSms(datas:getResponsesGateway){
        var data = dbHelper.getSynced()
        if(data.size > 0){
            Log.d("sms_status:","Activity trying to send 36 ...")
            for (data in data){
                val notif = dbHelper.getById(data.id)
                val accountId = data.accountId
                val newPhoneNumber = data.phone
                val message = data.message
                sendSMS(data.idNotif, accountId, newPhoneNumber, message)
                Log.d("sms_status:","Activity trying to send...")
            }
        }
    }
    private fun updateView(){
        var datatrx = dbHelper.getAlltrxoffline()
        adapterTransaction = RecycleViewLog(
            this,
            datatrx
        )
        adapterTransaction.notifyDataSetChanged()
        rvMain.layoutManager = LinearLayoutManager(this)
        rvMain.adapter = adapterTransaction
    }
    private fun sendSMS(idNotif: String?, accountId: String?, phoneNumber: String?, message: String?) {
        var returnSentStatus: String = "QUEUE"
        var returnDelivStatus: String = "QUEUE"
//        var dbHelper = DbAdapter(requireContext()).TableOfflineAttendance()
        var dbHelper = DbAdapter(this).TableOfflineAttendance()
        val smsManager = SmsManager.getDefault()
        val SENT = "SMS_SENT"
        val DELIVERED = "SMS_DELIVERED"
        val sms = SmsManager.getDefault()
        val parts = sms.divideMessage(message)
        val messageCount: Int = parts.size
        Log.i("Message Count", "Message Count: $messageCount")
        val deliveryIntents = ArrayList<PendingIntent>()
        val sentIntents = ArrayList<PendingIntent>()
        val sentPI = PendingIntent.getBroadcast(this, 0, Intent(SENT), 0)
        val deliveredPI = PendingIntent.getBroadcast(this, 1, Intent(DELIVERED), 0)
        for (j in 0 until messageCount) {
            sentIntents.add(sentPI)
            deliveryIntents.add(deliveredPI)
        }

        // ---when the SMS has been sent---
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(arg0: Context, arg1: Intent) {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        returnSentStatus = "SMS_SENT"
                        update(idNotif.toString(),accountId.toString(),returnSentStatus)
                    }
                    SmsManager.RESULT_ERROR_GENERIC_FAILURE -> {
                        returnSentStatus = "GENERIC_FAILURE"
                        update(idNotif.toString(),accountId.toString(),returnSentStatus)
                    }
                    SmsManager.RESULT_ERROR_NO_SERVICE -> {
                        returnSentStatus = "NO_SERVICE"
                        update(idNotif.toString(),accountId.toString(),returnSentStatus)
                    }
                    SmsManager.RESULT_ERROR_NULL_PDU -> {
                        returnSentStatus = "NULL_PDU"
                        update(idNotif.toString(),accountId.toString(),returnSentStatus)
                    }
                    SmsManager.RESULT_ERROR_RADIO_OFF -> {
                        returnSentStatus = "RADIO_OFF"
                        update(idNotif.toString(),accountId.toString(),returnSentStatus)
                    }
                }
            }
        }, IntentFilter(SENT))

        // ---when the SMS has been delivered---
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(arg0: Context, arg1: Intent) {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        returnDelivStatus = "SMS_DELIVERED"
                        update(idNotif.toString(),accountId.toString(),returnDelivStatus)
                    }
                    Activity.RESULT_CANCELED -> {
                        returnDelivStatus = "SMS_NOT_DELIVERED"
                        update(idNotif.toString(),accountId.toString(),returnDelivStatus)
                    }
                }
            }
        }, IntentFilter(DELIVERED))
        smsManager.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI)
        /* sms.sendMultipartTextMessage(phoneNumber, null, parts, sentIntents, deliveryIntents); */
    }

    fun update(idNotif: String,accountId: String, status: String){
        Log.d("Update", "status: $status $idNotif")
        dbHelper.updateStatus(idNotif.toString(), status)
        if(status == "SMS_SENT" || status == "SMS_DELIVERED"){
//            var homeFragment: HomeFragment = HomeFragment()
//            homeFragment.postupdategateway(idNotif,accountId)

        }
    }
}