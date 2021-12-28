package id.co.solusinegeri.sms_gateway.ui.home

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.CountDownTimer
import android.telephony.SmsManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.common.hash.HashingOutputStream
import com.squareup.picasso.Picasso
import id.co.solusinegeri.sms_gateway.R
import id.co.solusinegeri.sms_gateway.data.database.adapterlog.DbAdapter
import id.co.solusinegeri.sms_gateway.data.networks.Resource
import id.co.solusinegeri.sms_gateway.data.networks.ServiceApi
import id.co.solusinegeri.sms_gateway.data.repository.ServiceRepository
import id.co.solusinegeri.sms_gateway.databinding.HomeFragmentBinding
import id.co.solusinegeri.sms_gateway.handleApiError
import id.co.solusinegeri.sms_gateway.ui.auth.LoginViewModel
import id.co.solusinegeri.sms_gateway.ui.base.BaseFragment
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.properties.Delegates

class HomeFragment : BaseFragment<HomeViewModel, HomeFragmentBinding, ServiceRepository>() {
    private var adapterTransaction by Delegates.notNull<RecycleViewLog>()
    lateinit var countdown_timer: CountDownTimer
    var isRunning: Boolean = false;
    var time_in_milli_seconds = 0L
    var companyName = "PT TEKNOLOGI KARTU INDONESIA"
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getUserCredentialInfo()
        var dbHelper = DbAdapter(requireContext()).TableOfflineAttendance()
        val datatrx = dbHelper.getAlltrxoffline()
        val offlinePending = datatrx.size
        adapterTransaction = RecycleViewLog(
            requireContext(),
            datatrx
        )
        rvMain.layoutManager = LinearLayoutManager(requireContext())
        rvMain.adapter = adapterTransaction
        val companyId = runBlocking { userPreferences.getCompanyId()}
        GetNotifikasiGateway(companyId.toString())
        binding.btnLogout.setOnClickListener {
            countdown_timer.cancel()
            logout()
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.setRefreshing(false)
            getUserCredentialInfo()

        }
    }

    private fun getUserCredentialInfo() {
        viewModel.getUser()
        viewModel._users.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Success-> {
                    val companyId = it.value.user.accounts[0].companyId
                    val creatorId = it.value.user.accounts[0].id
                    val username = it.value.user.name
                    val deviceId = it.value.user.accounts[0].note
                    val nama = it.value.user.name
                    companyName = it.value.companies[0].name
                    val email = it.value.user.email

                    binding.txtCompanyName.text = companyName

//                    binding.txtGmail.setText(email)
                    runBlocking { userPreferences.saveDeviceId(deviceId) }
                    runBlocking { userPreferences.saveCreatorId(creatorId) }
                    runBlocking { userPreferences.saveCompanyId(companyId) }
                    val companyID = "5f80685c6860831471d5237d"
                    TimersGet()
                }
                is Resource.Failure -> handleApiError(it) {
                }

            }
        })
    }
    private fun TimersGet(){
        val time = "1"
        time_in_milli_seconds = time.toLong() * 30000L
        countdown_timer = object : CountDownTimer(time_in_milli_seconds, 1000) {
            override fun onFinish() {
                val companyId = runBlocking { userPreferences.getCompanyId()}
                GetNotifikasiGateway(companyId.toString())
            }

            override fun onTick(p0: Long) {
                time_in_milli_seconds = p0
                binding.txtTimer.setText(time_in_milli_seconds.toString().substring(0,2))
                Log.d("timer", time_in_milli_seconds.toString())
            }
        }
        countdown_timer.start()

        isRunning = true
    }
    private fun sendSMS(phoneNumber: String, title: String, message: String) {
        var newPhoneNumber = phoneNumber
        if(phoneNumber.take(1).toString() == "0"){
            newPhoneNumber = "+62" + phoneNumber.substring(1)
        }
        val syntax = "$title\n\n$message\n\n-$companyName-"
        Log.d("sms_status:","trying to send...")
        val sentPI: PendingIntent = PendingIntent.getBroadcast(requireContext(), 0, Intent("SMS_SENT"), 0)
        SmsManager.getDefault().sendTextMessage(newPhoneNumber, null, syntax, sentPI, null)
    }
    private fun GetNotifikasiGateway(companyId : String){
        TimersGet()
        viewModel.GetNotifikasiGateway(companyId)
        viewModel._gateways.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Success-> {
                    Log.d("data gateway : ", it.toString())
                    val id = it.value
                    for(_id in id){
                        val accountId = _id._id
                        val accountName = _id.accountName
                        val category = _id.category
                        val title = _id.title
                        val phone = _id.smsDestination
                        val message = _id.message
                        Log.d("data id :",accountId)


                        postupdategateway(accountId,accountName,category, phone, title, message)
                    }

                }
                is Resource.Failure -> handleApiError(it) {
                }

            }
        })
    }
    private fun postupdategateway(id : String,accountName: String,category: String,phone : String,title : String, message: String ){
        sendSMS(phone, title, message)
        viewModel.Updategatewaysms(id)
        viewModel._updategateways.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Success-> {
                    var dbHelper = DbAdapter(requireContext()).TableOfflineAttendance()
                    Log.d("data gateway : ", it.toString())
                    val status = it.value.status
                    dbHelper.addAttoffline(accountName, category, title,phone,status)
                    var datatrx = dbHelper.getAlltrxoffline()
                    val offlinePending = datatrx.size
                    adapterTransaction = RecycleViewLog(
                        requireContext(),
                        datatrx
                    )
                    rvMain.layoutManager = LinearLayoutManager(requireContext())
                    rvMain.adapter = adapterTransaction
                }
                is Resource.Failure -> handleApiError(it) {
                }

            }
        })
    }

    override fun getViewModel()= HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    )= HomeFragmentBinding.inflate(inflater,container,false)

    override fun getFragmentRepository()= ServiceRepository(
        remoteDataSource.buildApi(requireContext(), ServiceApi::class.java, userPreferences)
    )
}