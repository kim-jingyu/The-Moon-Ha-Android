package com.innerpeace.themoonha.data.network

import com.innerpeace.themoonha.Constants.url
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

//object ApiClient {
//    fun getClient(): Retrofit = createRetrofit()
//
//    private fun createRetrofit(): Retrofit {
//        return Retrofit.Builder()
//            .baseUrl(url)
//            .client(OkHttpClient.Builder().build())
//            .addConverterFactory(ScalarsConverterFactory.create())
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//    }
//}

// 로그 추가 (임시)
object ApiClient {
    fun getClient(): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(url)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}