package id.co.solusinegeri.sms_gateway.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.co.solusinegeri.sms_gateway.data.networks.Resource
import id.co.solusinegeri.sms_gateway.data.repository.ServiceRepository
import id.co.solusinegeri.sms_gateway.data.responses.CheckCredentialResponse
import id.co.solusinegeri.sms_gateway.data.responses.UpdateGatewayResposes
import id.co.solusinegeri.sms_gateway.data.responses.getResponsesGateway
import id.co.solusinegeri.sms_gateway.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class HomeViewModel (
    private val repository: ServiceRepository
) : BaseViewModel(repository) {
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
        companyId : String
    ) = viewModelScope.launch {
        _gateway.value = Resource.Loading
        _gateway.value = repository.GetNotifikasiGateway(companyId)
    }
    private val _updategateway: MutableLiveData<Resource<UpdateGatewayResposes>> = MutableLiveData()
    val _updategateways: LiveData<Resource<UpdateGatewayResposes>>
        get() = _updategateway

    fun Updategatewaysms(
        _id : String
    ) = viewModelScope.launch {
        _updategateway.value = Resource.Loading
        _updategateway.value = repository.Updategatewaysms(_id)
    }


}