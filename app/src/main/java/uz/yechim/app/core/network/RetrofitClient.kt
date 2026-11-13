package uz.yechim.app.core.network

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uz.yechim.app.core.security.KeystoreManager
import uz.yechim.app.data.remote.ApiService
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "https://yechimuz-lovat.vercel.app/api/v1/"
    private var apiService: ApiService? = null

    fun getApiService(context: Context): ApiService {
        if (apiService == null) {
            val keystoreManager = KeystoreManager(context)
            val cookieJar = SessionCookieJar(keystoreManager)

            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val okHttpClient = OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .addInterceptor(logging)
                .addInterceptor { chain ->
                    val requestBuilder = chain.request().newBuilder()
                    val token = keystoreManager.getToken()
                    if (token != null) {
                        requestBuilder.addHeader("Authorization", "Bearer $token")
                        requestBuilder.addHeader("Cookie", "yechimuz_token=$token")
                    }
                    chain.proceed(requestBuilder.build())
                }
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            apiService = retrofit.create(ApiService::class.java)
        }
        return apiService!!
    }
}
