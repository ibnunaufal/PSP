package id.co.solusinegeri.sms_gateway.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import id.co.solusinegeri.sms_gateway.MainViewModel
import id.co.solusinegeri.sms_gateway.data.repository.BaseRepository
import id.co.solusinegeri.sms_gateway.data.repository.ServiceRepository
import id.co.solusinegeri.sms_gateway.ui.auth.LoginViewModel
import id.co.solusinegeri.sms_gateway.ui.home.HomeViewModel
import java.lang.IllegalArgumentException

class ViewModelFactory(
    private val repository: BaseRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> MainViewModel(repository as ServiceRepository) as T
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(repository as ServiceRepository) as T
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(repository as ServiceRepository) as T
            else -> throw IllegalArgumentException("ViewModelClass Not Found")
        }
    }
}