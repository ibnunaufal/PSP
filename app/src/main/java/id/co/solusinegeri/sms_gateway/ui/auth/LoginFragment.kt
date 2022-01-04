package id.co.solusinegeri.sms_gateway.ui.auth

import android.Manifest
import android.content.pm.PackageManager
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

            login()
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
        Log.d("data login :", username + password)
        viewModel.login(username, password)
        viewModel.loginResponse.observe(viewLifecycleOwner, Observer {
            binding.btnLogin.enable(true)
            when(it){
                is  Resource.Success -> {
                    progressDialog.dialog.dismiss()
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
                    lifecycleScope.launch {
                        requireActivity().startNewActivity(HomeActivity::class.java)
                        activity?.overridePendingTransition(0, 0)

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