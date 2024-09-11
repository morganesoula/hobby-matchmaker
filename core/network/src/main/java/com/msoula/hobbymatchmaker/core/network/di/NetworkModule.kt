package com.msoula.hobbymatchmaker.core.network.di

import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.msoula.hobbymatchmaker.core.network.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val API_KEY = BuildConfig.TMDB_KEY
private const val BASE_URL = "https://api.themoviedb.org/3/"

val networkModule = module {
    single<Interceptor> {
        Interceptor { chain ->
            val newUrl =
                chain.request().url
                    .newBuilder()
                    .addQueryParameter("api_key", API_KEY)
                    .build()

            val newRequest =
                chain.request()
                    .newBuilder()
                    .url(newUrl)
                    .build()

            chain.proceed(newRequest)
        }
    }

    single<HttpLoggingInterceptor> {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    single<OkHttpClient> {
        OkHttpClient().newBuilder()
            .addInterceptor(get<Interceptor>())
            //.addInterceptor(get<HttpLoggingInterceptor>())
            .build()
    }

    single<FirebaseAuth> { FirebaseAuth.getInstance() }

    single<Retrofit> {
        Retrofit.Builder()
            .client(get<OkHttpClient>())
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<SignInClient> { Identity.getSignInClient(androidContext()) }
}
