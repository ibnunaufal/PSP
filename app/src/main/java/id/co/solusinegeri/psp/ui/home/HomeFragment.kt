package id.co.solusinegeri.psp.ui.home

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import id.co.solusinegeri.psp.data.database.adapterlog.DbAdapter
import id.co.solusinegeri.psp.data.networks.Resource
import id.co.solusinegeri.psp.data.networks.ServiceApi
import id.co.solusinegeri.psp.data.repository.ServiceRepository
import id.co.solusinegeri.psp.databinding.HomeFragmentBinding
import id.co.solusinegeri.psp.handleApiError
import id.co.solusinegeri.psp.ui.base.BaseFragment
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.properties.Delegates

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import java.text.SimpleDateFormat

class HomeFragment : BaseFragment<HomeViewModel, HomeFragmentBinding, ServiceRepository>() {
    private var adapterTransaction by Delegates.notNull<RecycleViewLog>()



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var dbHelper = DbAdapter(requireContext()).TableOfflineAttendance()
        val datatrx = dbHelper.getAlltrxoffline()
        val offlinePending = datatrx.size

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.setRefreshing(false)
        }
        adapterTransaction = RecycleViewLog(
            requireContext(),
            datatrx
        )
        adapterTransaction.notifyDataSetChanged()
        rvMain.layoutManager = LinearLayoutManager(requireContext())
        rvMain.adapter = adapterTransaction
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