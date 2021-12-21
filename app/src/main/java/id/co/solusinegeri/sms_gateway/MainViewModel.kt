package id.co.solusinegeri.sms_gateway

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.co.solusinegeri.sms_gateway.data.networks.Resource
import id.co.solusinegeri.sms_gateway.data.networks.ServiceApi
import id.co.solusinegeri.sms_gateway.data.repository.ServiceRepository
import id.co.solusinegeri.sms_gateway.data.responses.CheckCredentialResponse
import id.co.solusinegeri.sms_gateway.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: ServiceRepository
) : BaseViewModel(repository) {

    private val _user: MutableLiveData<Resource<CheckCredentialResponse>> = MutableLiveData()
    val user: LiveData<Resource<CheckCredentialResponse>>
        get() = _user

    fun getUser() = viewModelScope.launch {
        _user.value = Resource.Loading
        _user.value = repository.getCredentialInfo()
    }
}