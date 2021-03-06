package id.co.solusinegeri.psp

import android.app.Activity
import android.content.Intent
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import id.co.solusinegeri.psp.data.networks.Resource
import id.co.solusinegeri.psp.ui.auth.LoginFragment
import id.co.solusinegeri.psp.ui.base.BaseFragment
import id.co.solusinegeri.psp.ui.home.HomeFragment

lateinit var countdown_timer: CountDownTimer
var isRunning: Boolean = false;
var time_in_milli_seconds = 0L

fun <A : Activity> Activity.startNewActivity(activity: Class<A>) {
    Intent(this, activity).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }
}

fun View.visible(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun View.enable(enabled: Boolean) {
    isEnabled = enabled
    alpha = if (enabled) 1f else 0.5f
}

fun View.snackbar(message: String, action: (() -> Unit)? = null) {
    val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
    action?.let {
        snackbar.setAction("Retry") {
            it()
        }
    }
    snackbar.show()
}

fun Fragment.handleApiError(
    failure: Resource.Failure,
    retry: (() -> Unit)? = null

) {
    when {
        failure.isNetworkError -> {
            if(this is MainFragment){
                directAutoLogin()
            }else if(this is HomeFragment){
                requireView().snackbar("Mohon periksa kembali koneksi internet anda")
            }
        }
        failure.errorCode == 401 -> {
            if (this is LoginFragment) {
                requireView().snackbar("Username atau Password yang anda masukkan salah")
                progressDialog.dialog.dismiss()

            } else {
                (this as BaseFragment<*, *, *>).logout()
            }
        }
        failure.errorCode == 500 -> {
            requireView().snackbar("Sistem sedang diperbaharui~")
        }
        failure.errorCode == 400 -> {

        }
        failure.errorCode == 403 -> {
            if(this is MainFragment){
                progressDialog.dialog.dismiss()
                directAutoLogin()
            }
        }
        failure.errorCode == 404 -> {

        }

        failure.errorCode == 304 -> {
            if (this is HomeFragment){
                requireView().snackbar("")
            }
        }
        else -> {
            val error = failure.errorBody?.string().toString()
            requireView().snackbar(error)
            Log.e("error", error)
        }

    }

}



