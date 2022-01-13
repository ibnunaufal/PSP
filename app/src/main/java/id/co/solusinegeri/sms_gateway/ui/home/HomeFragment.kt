package id.co.solusinegeri.sms_gateway.ui.home

import android.Manifest
import android.R.id
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
import androidx.recyclerview.widget.RecyclerView
import com.google.common.hash.HashingOutputStream
import com.squareup.picasso.Picasso
import id.co.solusinegeri.sms_gateway.R
import id.co.solusinegeri.sms_gateway.data.database.adapterlog.DbAdapter
import id.co.solusinegeri.sms_gateway.data.networks.Resource
import id.co.solusinegeri.sms_gateway.data.networks.ServiceApi
import id.co.solusinegeri.sms_gateway.data.repository.ServiceRepository
import id.co.solusinegeri.sms_gateway.data.responses.ContentLog
import id.co.solusinegeri.sms_gateway.databinding.HomeFragmentBinding
import id.co.solusinegeri.sms_gateway.handleApiError
import id.co.solusinegeri.sms_gateway.ui.auth.LoginViewModel
import id.co.solusinegeri.sms_gateway.ui.base.BaseFragment
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.properties.Delegates
import android.R.id.message

import android.content.IntentFilter

import android.app.Activity

import android.content.BroadcastReceiver
import android.content.Context
import java.lang.Exception
import android.R.id.message
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.widget.AdapterView
import id.co.solusinegeri.sms_gateway.snackbar
import id.co.solusinegeri.sms_gateway.visible
import java.text.SimpleDateFormat

class HomeFragment : BaseFragment<HomeViewModel, HomeFragmentBinding, ServiceRepository>() {
    private var adapterTransaction by Delegates.notNull<RecycleViewLog>()
    lateinit var countdown_timer: CountDownTimer
    var isRunning: Boolean = false;
    var time_in_milli_seconds = 0L

    var isPaused = false
    var isCancelled = false
    var isPausedReconnect = false
    var isCancelledReconnect = false
    var resumeFromMillis:Long = 0
    var millisInFuture:Long = 20000
    var countDownInterval:Long = 1000
    var millisInFuture2:Long = 2000
    var countDownInterval2:Long = 1000


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var dbHelper = DbAdapter(requireContext()).TableOfflineAttendance()
        val datatrx = dbHelper.getAlltrxoffline()
        val offlinePending = datatrx.size
        startTimer()

        binding.btnLogout.setOnClickListener {
            stopTimer()
            stopTimerReconnect()
            logout()
        }
        binding.layoutReconnecting.visible(false)
        binding.txtCompanyName.text = runBlocking { userPreferences.getCompanyName() }
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.setRefreshing(false)
        }
//        binding.btnRefresh.visible(false)
//        binding.btnRefresh.setOnClickListener {
//            startTimer()
//        }

        adapterTransaction = RecycleViewLog(
            requireContext(),
            datatrx
        )
        adapterTransaction.notifyDataSetChanged()
        rvMain.layoutManager = LinearLayoutManager(requireContext())
        rvMain.adapter = adapterTransaction
    }
    fun startTimer(){
        if(isNetworkAvailable(activity)){
            timer(millisInFuture,countDownInterval).start()
//            binding.btnRefresh.visible(false)
            getNotifikasiGateway()

            isCancelled = false
            isPaused = false
        }else{
            stopTimer()
            startTimerReconnect()
            timerReconnect(millisInFuture2, countDownInterval2)
//            requireView().snackbar("Mohon periksa kembali koneksi internet anda.")
//            binding.btnRefresh.visible(true)
        }

    }
    fun stopTimer(){
        Log.d("network", isCancelled.toString())
        isCancelled = true
        Log.d("network", isCancelled.toString())
        isPaused = false
    }

    fun startTimerReconnect(){
        binding.layoutReconnecting.visible(true)
        timerReconnect(millisInFuture2,countDownInterval2).start()
        isCancelledReconnect = false
//        requireView().snackbar("Mohon periksa kembali koneksi internet anda.")
    }

    fun stopTimerReconnect(){
        binding.layoutReconnecting.visible(false)
        Log.d("network", isCancelledReconnect.toString())
        isCancelledReconnect = true
        Log.d("network", isCancelledReconnect.toString())
        isPausedReconnect = false
    }

    // Method to configure and return an instance of CountDownTimer object
    private fun timer(millisInFuture:Long,countDownInterval:Long):CountDownTimer{
        return object: CountDownTimer(millisInFuture,countDownInterval){
            override fun onTick(millisUntilFinished: Long){
                val timeRemaining = "Seconds remaining ${millisUntilFinished/1000} $isCancelled"

                if (isPaused){
                    Log.d("timer ", "Paused ${millisUntilFinished/1000} $isCancelled")
                    resumeFromMillis = millisUntilFinished
                    cancel()
                }else if (isCancelled){
                    Log.d("timer ", "Canceled ${millisUntilFinished/1000} $isCancelled")
                    cancel()
                }else{
                    Log.d("timer", timeRemaining)
//                    binding.txtTimer.text = (millisUntilFinished/1000).toString()
                }
            }

            override fun onFinish() {
                Log.d("timer", "Finish")
                Log.d("timer", "Restarting...")
//                binding.txtTimer.text = "Restarting..."
                startTimer()
            }
        }

    }

    // Method to configure and return an instance of CountDownTimer object
    private fun timerReconnect(millisInFuture:Long,countDownInterval:Long):CountDownTimer{
        return object: CountDownTimer(millisInFuture,countDownInterval){
            override fun onTick(millisUntilFinished: Long){
                val timeRemaining = "Seconds remaining ${millisUntilFinished/1000} $isCancelled"

                if (isPausedReconnect){
                    Log.d("timer reconnect", "Paused ${millisUntilFinished/1000} $isCancelled")
                    resumeFromMillis = millisUntilFinished
                    cancel()
                }else if (isCancelledReconnect){
                    Log.d("timer Reconnect Stop", "Canceled ${millisUntilFinished/1000} $isCancelled")
                    cancel()
                }else{
                    Log.d("timer Reconnecting....", timeRemaining)
                    if(isNetworkAvailable(activity)){
                        Log.d("timer Reconnect Success", timeRemaining)
                        binding.layoutReconnecting.visible(false)
                        stopTimerReconnect()
                        startTimer()
                        Toast.makeText(context, "Terhubung ke Internet", Toast.LENGTH_SHORT).show()
                    }
//                    binding.txtTimer.text = (millisUntilFinished/1000).toString()
                }
            }

            override fun onFinish() {
                Log.d("timer Reconnect", "Finish")
                Log.d("timer Reconnect", "Restarting...")
//                binding.txtTimer.text = "Restarting..."
                startTimerReconnect()
            }
        }

    }

    private fun getNotifikasiGateway(){
        var companyId: String = runBlocking { userPreferences.getCompanyId().toString()}
        if( viewLifecycleOwner != null ){
            viewModel._gateways.removeObservers(viewLifecycleOwner)
        }
        viewModel.GetNotifikasiGateway(companyId, "", "10")
        viewModel._gateways.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when(it){
                is Resource.Success -> {
                    Log.d("getNotif", it.value.toString())
                    Log.d("getNotifSize", it.value.size.toString())
                    var companyName = runBlocking { userPreferences.getCompanyName() }
                    var dbHelper = DbAdapter(requireContext()).TableOfflineAttendance()
                    for(data in it.value){
                        var newPhoneNumber = data.smsDestination
                        if(data.smsDestination.take(1) == "0"){
                            newPhoneNumber = "+62" + data.smsDestination.substring(1)
                        }
                        var message = data.message
                        var contentt:String? = data.content
                        if(data.message.takeLast(1) == "*"){
                            if (contentt != null) {
                                message = message.replaceRange(
                                    message.length- contentt.length,
                                    message.length,
                                    contentt.toString())
                            }
                        }
                        Log.d("contentt :", contentt.toString())
                        Log.d("messagee :", message)
                        message = "${data.title}\n\n${message}\n\n-$companyName-"
                        Log.d("sms_status:","trying to send...")

                        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                        val currentDate = sdf.format(Date()).toString()
                        System.out.println(" C DATE is  "+currentDate)

                        dbHelper.addAttoffline(data.id, data.accountId, data.accountName, data.category, data.title, message, newPhoneNumber, currentDate)

                        // trying to send SMS without delivered confirmation
                        val sentPI: PendingIntent = PendingIntent.getBroadcast(requireContext(), 0, Intent("SMS_SENT"), 0)
                        val deliveredPI: PendingIntent = PendingIntent.getBroadcast(requireContext(), 0, Intent("SMS_DELIVERED"), 0)
                        SmsManager.getDefault().sendTextMessage(newPhoneNumber, null, message, sentPI, deliveredPI)

                        postupdategateway(data.id, data.id, data.accountId)
                    }
                    var datatrx = dbHelper.getAlltrxoffline()

                    adapterTransaction = RecycleViewLog(
                        requireContext(),
                        datatrx
                    )
                    adapterTransaction.notifyDataSetChanged()
                    rvMain.layoutManager = LinearLayoutManager(requireContext())
                    rvMain.adapter = adapterTransaction


                }
                is Resource.Failure -> handleApiError(it)
            }
        })
    }

    private fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null){
            Log.d("context","null")
            return false
        }
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }
    private fun postupdategateway(id : String, idNotif: String, accountId: String ){
//        sendSMS(phone, title, message)
        viewModel.Updategatewaysms(accountId, idNotif)
        viewModel._updategateways.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Success-> {
                    var dbHelper = DbAdapter(requireContext()).TableOfflineAttendance()
                    dbHelper.deleteData(id)
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