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

    private val _gateway: MutableLiveData<Resource<getResponsesGateway>> = MutableLiveData()
    val _gateways: LiveData<Resource<getResponsesGateway>>
        get() = _gateway

    fun clear(){

    }

    fun GetNotifikasiGateway(
        companyId : String,
        category : String?,
        size : String
    ) = viewModelScope.launch {
        _gateway.value = Resource.Loading
        _gateway.value = repository.GetNotifikasiGateway(companyId, category, size)
    }
    private val _updategateway: MutableLiveData<Resource<UpdateGatewayResposes>> = MutableLiveData()
    val _updategateways: LiveData<Resource<UpdateGatewayResposes>>
        get() = _updategateway

    fun Updategatewaysms(
        accountId : String,
        idNotif: String
    ) = viewModelScope.launch {
        _updategateway.value = Resource.Loading
        _updategateway.value = repository.Updategatewaysms(accountId, idNotif)
    }


}