package id.co.solusinegeri.psp.data.networks

import android.util.Log
import id.co.solusinegeri.psp.data.preferences.UserPreferences
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(pref: UserPreferences) : Interceptor {
    private val sharedPref = pref

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        Log.d("request : ",requestBuilder.toString())
        sharedPref.getAuthToken()?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")

        }
        val response = chain.proceed(requestBuilder.build())
        if (response.code == 200) {
            val allHeaders = response.headers
            Log.d("token : ",allHeaders.toString())
            val tokenBearer = allHeaders.get("Authorization")

            if (tokenBearer != null) {
                val tmp = tokenBearer.split(" ");

                runBlocking { sharedPref.saveAuthToken(tmp[1]) }
            }
        }
        Log.d("Respone Message : ",response.message +"\n" +response.body + "\n"+"Respone Code :" +response.code+ "\n"+ "Requet :" +response.request+"\n")
        return response
    }
}