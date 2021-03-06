package id.co.solusinegeri.psp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.solusinegeri.psp.data.networks.Resource
import id.co.solusinegeri.psp.data.repository.ServiceRepository
import id.co.solusinegeri.psp.data.responses.UpdateGatewayResposes
import id.co.solusinegeri.psp.data.responses.getResponsesGateway
import id.co.solusinegeri.psp.ui.base.BaseViewModel
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
        idNotif: String,
        status: String
    ) = viewModelScope.launch {
        _updategateway.value = Resource.Loading
        _updategateway.value = repository.Updategatewaysms(accountId, idNotif, status)
    }


}