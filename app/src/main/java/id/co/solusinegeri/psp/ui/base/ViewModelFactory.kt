package id.co.solusinegeri.psp.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import id.co.solusinegeri.psp.MainViewModel
import id.co.solusinegeri.psp.data.repository.BaseRepository
import id.co.solusinegeri.psp.data.repository.ServiceRepository
import id.co.solusinegeri.psp.ui.auth.LoginViewModel
import id.co.solusinegeri.psp.ui.home.HomeViewModel
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