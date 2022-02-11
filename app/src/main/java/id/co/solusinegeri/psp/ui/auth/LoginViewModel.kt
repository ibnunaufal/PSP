package id.co.solusinegeri.psp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.solusinegeri.psp.data.networks.Resource
import id.co.solusinegeri.psp.data.repository.ServiceRepository
import id.co.solusinegeri.psp.data.responses.CheckCredentialResponse
import id.co.solusinegeri.psp.data.responses.getResponsesGateway
import id.co.solusinegeri.psp.data.responses.loginResponse
import id.co.solusinegeri.psp.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: ServiceRepository
) : BaseViewModel(repository) {
    private val _loginResponse: MutableLiveData<Resource<loginResponse>> = MutableLiveData()
    val loginResponse: LiveData<Resource<loginResponse>>
        get() = _loginResponse

    fun login(
        username: String,
        password: String
    ) = viewModelScope.launch {
        _loginResponse.value = Resource.Loading
        _loginResponse.value = repository.login(username, password)
    }

    private val _user: MutableLiveData<Resource<CheckCredentialResponse>> = MutableLiveData()
    val _users: LiveData<Resource<CheckCredentialResponse>>
        get() = _user

    fun getUser() = viewModelScope.launch {
        _user.value = Resource.Loading
        _user.value = repository.getCredentialInfo()
    }

    private val _gateway: MutableLiveData<Resource<getResponsesGateway>> = MutableLiveData()
    val _gateways: LiveData<Resource<getResponsesGateway>>
        get() = _gateway

    fun GetNotifikasiGateway(
        companyId : String,
        category : String?,
        size : String
    ) = viewModelScope.launch {
        _gateway.value = Resource.Loading
        _gateway.value = repository.GetNotifikasiGateway(companyId, category, size)
    }
}