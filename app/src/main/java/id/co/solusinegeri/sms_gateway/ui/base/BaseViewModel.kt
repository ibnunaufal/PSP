package id.co.solusinegeri.sms_gateway.ui.base

import androidx.lifecycle.ViewModel
import id.co.solusinegeri.sms_gateway.data.repository.BaseRepository


abstract class  BaseViewModel(
    private val repository: BaseRepository
) : ViewModel(){
}