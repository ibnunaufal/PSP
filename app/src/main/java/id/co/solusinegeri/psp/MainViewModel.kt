package id.co.solusinegeri.psp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.solusinegeri.psp.data.networks.Resource
import id.co.solusinegeri.psp.data.repository.ServiceRepository
import id.co.solusinegeri.psp.data.responses.CheckCredentialResponse
import id.co.solusinegeri.psp.data.responses.loginResponse
import id.co.solusinegeri.psp.ui.base.BaseViewModel
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