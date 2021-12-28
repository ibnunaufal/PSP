package id.co.solusinegeri.sms_gateway.data.networks

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.xwray.groupie.BuildConfig
import id.co.solusinegeri.sms_gateway.data.preferences.UserPreferences
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RemoteDataSource {
    companion object {
 //        private const val BASE_URL = "http://192.168.10.102:9090/"
      private const val BASE_URL = "https://api.dev.katalis.info/"
//        private const val BASE_URL = "https://api.katalis.info/"
    }

    fun getBaseUrl(): String {
        return BASE_URL;
    }
    fun <Api> buildApi(
        context: Context,
        api: Class<Api>,
        pref: UserPreferences
    ): Api {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(AuthInterceptor(pref))
                    .also { client ->
                        if (BuildConfig.DEBUG) {
                            val logging = HttpLoggingInterceptor()
                            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                            client.addInterceptor(logging)
//                            Log.d("message", client.addInterceptor(logging).toString())
                        }
                    }.addInterceptor(ChuckerInterceptor(context)).build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(api)
    }
}