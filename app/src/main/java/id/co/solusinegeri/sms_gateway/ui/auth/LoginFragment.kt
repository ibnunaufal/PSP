package id.co.solusinegeri.sms_gateway.ui.auth

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import id.co.solusinegeri.sms_gateway.*
import id.co.solusinegeri.sms_gateway.data.database.adapterlog.DbAdapter
import id.co.solusinegeri.sms_gateway.data.networks.Resource
import id.co.solusinegeri.sms_gateway.data.networks.ServiceApi
import id.co.solusinegeri.sms_gateway.data.repository.ServiceRepository
import id.co.solusinegeri.sms_gateway.databinding.LoginFragmentBinding
import id.co.solusinegeri.sms_gateway.ui.base.BaseFragment
import id.co.solusinegeri.sms_gateway.ui.home.HomeActivity
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class LoginFragment: BaseFragment<LoginViewModel, LoginFragmentBinding, ServiceRepository>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLogin.enable(false)
        binding.edPassword.addTextChangedListener {
            val user = binding.edUsername.text.toString().trim()
            val password = binding.edPassword.text.toString().trim()
            binding.btnLogin.enable(
                password.isNotEmpty() && user.isNotEmpty() && it.toString()
                    .isNotEmpty()
            )
        }
        binding.edUsername.addTextChangedListener {
            val user = binding.edUsername.text.toString().trim()
            val password = binding.edPassword.text.toString().trim()
            binding.btnLogin.enable(
                password.isNotEmpty() && user.isNotEmpty() && it.toString()
                    .isNotEmpty()
            )
        }
        binding.btnForgotPassword.setOnClickListener {
//            val intent = Intent(activity, ForgotPassActivity::class.java);
//            startActivity(intent)
        }
        binding.btnLogin.setOnClickListener {

            if(isNetworkAvailable(activity)){
                login()
            }else{
                requireView().snackbar("Mohon periksa kembali koneksi internet anda.")
            }
        }
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.SEND_SMS) !==
            PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                    Manifest.permission.SEND_SMS)) {
                ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(Manifest.permission.SEND_SMS), 1)
            } else {
                ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(Manifest.permission.SEND_SMS), 1)
            }
        }

        var username = runBlocking { userPreferences.getUsername().toString() }
        var password = runBlocking { userPreferences.getPassword().toString() }

        if(username == "" || username.isNullOrEmpty()){
            if(password == "" || password.isNullOrEmpty()){
                autoLogin()
            }
        }
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
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(requireContext(),
                            Manifest.permission.SEND_SMS) ===
                                PackageManager.PERMISSION_GRANTED)) {
                        Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    private fun login(){
        view?.let { progressDialog.show(it.context) }
        binding.btnLogin.enable(false)
        var username =   view?.findViewById<EditText>(R.id.ed_username)?.text.toString().trim()
        var password =   view?.findViewById<EditText>(R.id.ed_password)?.text.toString().trim()
        runBlocking { userPreferences.saveUsername(username) }
        runBlocking { userPreferences.savePassword(password) }
        Log.d("data login :", username + password)
        viewModel.login(username, password)
        viewModel.loginResponse.observe(viewLifecycleOwner, Observer {
            binding.btnLogin.enable(true)
            when(it){
                is  Resource.Success -> {
                    Log.d("data login",it.toString())
                    val firstLogin = it.value.firstLogin
                    getUserCredentialInfo()
                }
                is Resource.Failure -> handleApiError(it)

            }
        })

    }

    fun autoLogin(){
        var username = runBlocking { userPreferences.getUsername().toString() }
        var password = runBlocking { userPreferences.getPassword().toString() }
        Log.d("data login :", username + password)
        viewModel.login(username, password)
        viewModel.loginResponse.observe(viewLifecycleOwner, Observer {
            binding.btnLogin.enable(true)
            when(it){
                is  Resource.Success -> {
                    Log.d("data login",it.toString())
                    val firstLogin = it.value.firstLogin
                    getUserCredentialInfo()
                }
                is Resource.Failure -> handleApiError(it)

            }
        })
    }

    private fun getUserCredentialInfo() {
        viewModel.getUser()
        viewModel._users.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Success-> {
                    val companyId = it.value.activeCompany.id
                    val creatorId = it.value.user.accounts[0].id
                    val username = it.value.user.name
                    val deviceId = it.value.user.accounts[0].note
                    val nama = it.value.user.name
                    val companyName = it.value.companies[0].name
                    val email = it.value.user.email

//                    binding.txtGmail.setText(email)
                    runBlocking { userPreferences.saveDeviceId(deviceId) }
                    runBlocking { userPreferences.saveCreatorId(creatorId) }
                    runBlocking { userPreferences.saveCompanyId(companyId) }
                    runBlocking { userPreferences.saveCompanyName(companyName) }
                    val companyID = "5f80685c6860831471d5237d"

                    if(it.value.user.accounts[0].roles.contains("ROLE_DEVICE")){
                        lifecycleScope.launch {
                            requireActivity().startNewActivity(HomeActivity::class.java)
                            activity?.overridePendingTransition(0, 0)
                            progressDialog.dialog.dismiss()

                        }
                    }else{
                        requireView().snackbar("Akun anda tidak memiliki akses login.")
                        progressDialog.dialog.dismiss()
                    }
                }
                is Resource.Failure -> handleApiError(it) {
                }

            }
        })
    }
    override fun getViewModel()=LoginViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    )= LoginFragmentBinding.inflate(inflater,container,false)

    override fun getFragmentRepository()= ServiceRepository(
        remoteDataSource.buildApi(requireContext(), ServiceApi::class.java, userPreferences)
    )

}