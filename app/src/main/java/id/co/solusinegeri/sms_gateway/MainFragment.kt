package id.co.solusinegeri.sms_gateway

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import id.co.solusinegeri.sms_gateway.data.networks.Resource
import id.co.solusinegeri.sms_gateway.data.networks.ServiceApi
import id.co.solusinegeri.sms_gateway.data.repository.ServiceRepository
import id.co.solusinegeri.sms_gateway.databinding.MainFragmentBinding
import id.co.solusinegeri.sms_gateway.ui.auth.LoginActivity
import id.co.solusinegeri.sms_gateway.ui.base.BaseFragment
import id.co.solusinegeri.sms_gateway.ui.home.HomeActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainFragment : BaseFragment<MainViewModel,MainFragmentBinding,ServiceRepository>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog.show(view.context)
        val token = runBlocking { userPreferences.authToken.first()}

        if (token == null){
            requireActivity().startNewActivity(LoginActivity::class.java)
            activity?.overridePendingTransition(0, 0)
            progressDialog.dialog.dismiss()
        }else  {
            viewModel.getUser()
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


                        requireActivity().startNewActivity(HomeActivity::class.java)
                        activity?.overridePendingTransition(0, 0)
                        progressDialog.dialog.dismiss()

                }
                is Resource.Failure -> handleApiError(it) {
                }

            }
        })


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