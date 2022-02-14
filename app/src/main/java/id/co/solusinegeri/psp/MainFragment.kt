package id.co.solusinegeri.psp

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import id.co.solusinegeri.psp.data.networks.Resource
import id.co.solusinegeri.psp.data.networks.ServiceApi
import id.co.solusinegeri.psp.data.repository.ServiceRepository
import id.co.solusinegeri.psp.databinding.MainFragmentBinding
import id.co.solusinegeri.psp.ui.auth.LoginActivity
import id.co.solusinegeri.psp.ui.base.BaseFragment
import id.co.solusinegeri.psp.ui.home.HomeActivity
import id.co.solusinegeri.psp.ui.tab.TabActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainFragment : BaseFragment<MainViewModel,MainFragmentBinding,ServiceRepository>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog.show(view.context)
        val token = runBlocking { userPreferences.authToken.first()}
        val username = runBlocking { userPreferences.getUsername().toString() }
        val password = runBlocking { userPreferences.getPassword().toString() }

        if (token == null){
            if((username.isNullOrEmpty() && password.isNullOrEmpty()) || (username == ("") && password == (""))){
                requireActivity().startNewActivity(LoginActivity::class.java)
                activity?.overridePendingTransition(0, 0)
                progressDialog.dialog.dismiss()
            }else{
                tryLogin(username, password)
            }
        }else  {
//            Log.d("network", isNetworkAvailable(context).toString())
//            if(isNetworkAvailable(context)){
                viewModel.getUser()
//            }else{
//                requireActivity().startNewActivity(LoginActivity::class.java)
//                activity?.overridePendingTransition(0, 0)
//                progressDialog.dialog.dismiss()
//                requireView().snackbar("Mohon periksa kembali koneksi internet anda")
//            }
        }
        viewModel.user.observe(viewLifecycleOwner, Observer {
            when(it){
                is  Resource.Success -> {
                    Log.d("data user : ", it.toString())
                    val companyId = it.value.user.accounts[0].companyId
                    val creatorId = it.value.user.accounts[0].id
                    val username = it.value.user.name
                    val deviceId = it.value.user.accounts[0].note
                    Log.d("device main : ", deviceId.toString())
                    runBlocking { userPreferences.saveDeviceId(deviceId.toString())}
                    runBlocking { userPreferences.saveCreatorId(creatorId.toString())}
                    runBlocking { userPreferences.saveCompanyId(companyId.toString())}


                        requireActivity().startNewActivity(TabActivity::class.java)
                        activity?.overridePendingTransition(0, 0)
                        progressDialog.dialog.dismiss()

                }
                is Resource.Failure -> handleApiError(it) {
                }

            }
        })


    }

    private fun tryLogin(username: String, password: String){
        viewModel.login(username, password)
        viewModel.loginResponse.observe(viewLifecycleOwner, Observer {
            when(it){
                is  Resource.Success -> {
                    Log.d("data login",it.toString())
                    val firstLogin = it.value.firstLogin
                    requireActivity().startNewActivity(HomeActivity::class.java)
                    activity?.overridePendingTransition(0, 0)
                    progressDialog.dialog.dismiss()
                }
                is Resource.Failure -> handleApiError(it)

            }
        })
    }

    fun directAutoLogin(){
        requireActivity().startNewActivity(LoginActivity::class.java)
        activity?.overridePendingTransition(0, 0)
    }


    private fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) return false
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


     override fun getViewModel()= MainViewModel::class.java

     override fun getFragmentBinding(
         inflater: LayoutInflater,
         container: ViewGroup?
     )=MainFragmentBinding.inflate(inflater, container, false)

     override fun getFragmentRepository() = ServiceRepository(
         remoteDataSource.buildApi(requireContext(),ServiceApi::class.java, userPreferences)
     )
}