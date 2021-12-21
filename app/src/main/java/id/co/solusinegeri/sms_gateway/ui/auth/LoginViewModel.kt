package id.co.solusinegeri.sms_gateway.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.co.solusinegeri.sms_gateway.data.networks.Resource
import id.co.solusinegeri.sms_gateway.data.repository.ServiceRepository
import id.co.solusinegeri.sms_gateway.data.responses.loginResponse
import id.co.solusinegeri.sms_gateway.ui.base.BaseViewModel
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
}