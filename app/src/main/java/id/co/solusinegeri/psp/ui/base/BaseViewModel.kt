package id.co.solusinegeri.psp.ui.base

import androidx.lifecycle.ViewModel
import id.co.solusinegeri.psp.data.repository.BaseRepository


abstract class  BaseViewModel(
    private val repository: BaseRepository
) : ViewModel(){
}