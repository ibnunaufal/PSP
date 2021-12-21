package id.co.solusinegeri.sms_gateway

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import id.co.solusinegeri.sms_gateway.data.networks.Resource
import id.co.solusinegeri.sms_gateway.ui.auth.LoginFragment
import id.co.solusinegeri.sms_gateway.ui.base.BaseFragment

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

        }
        failure.errorCode == 401 -> {
            if (this is LoginFragment) {
                requireView().snackbar("You've entered incorrect username or password")
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
        failure.errorCode == 404 -> {

        }
        else -> {
            val error = failure.errorBody?.string().toString()
            requireView().snackbar(error)
        }

    }

}



