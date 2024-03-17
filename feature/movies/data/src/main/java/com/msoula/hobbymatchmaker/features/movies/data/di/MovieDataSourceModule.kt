package com.msoula.hobbymatchmaker.features.movies.data.di

import com.msoula.hobbymatchmaker.core.dao.MovieDAO
import com.msoula.hobbymatchmaker.features.movies.data.BuildConfig
import com.msoula.hobbymatchmaker.features.movies.data.data_sources.local.MovieLocalDataSourceImpl
import com.msoula.hobbymatchmaker.features.movies.data.data_sources.remote.MovieRemoteDataSourceImpl
import com.msoula.hobbymatchmaker.features.movies.data.data_sources.remote.services.TMDBService
import com.msoula.hobbymatchmaker.features.movies.domain.data_sources.MovieLocalDataSource
import com.msoula.hobbymatchmaker.features.movies.domain.data_sources.MovieRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

private const val API_KEY = BuildConfig.tmdb_key
private const val BASE_URL = "https://api.themoviedb.org/3/"

@Module
@InstallIn(SingletonComponent::class)
object MovieDataSourceModule {

    @Provides
    @Singleton
    fun providesAuthInterceptor(): Interceptor {
        return Interceptor { chain ->
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

    @Provides
    @Singleton
    fun providesOkHttpClient(authInterceptor: Interceptor): OkHttpClient {
        return OkHttpClient().newBuilder()
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun providesRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(client)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideTMDBService(retrofit: Retrofit): TMDBService {
        return retrofit.create(TMDBService::class.java)
    }

    @Provides
    @Singleton
    fun provideMovieLocalDataSource(movieDAO: MovieDAO): MovieLocalDataSource =
        MovieLocalDataSourceImpl(movieDAO)

    @Provides
    @Singleton
    fun provideMovieRemoteDataSource(
        tmdbService: TMDBService,
        movieLocalDataSource: MovieLocalDataSource
    ): MovieRemoteDataSource =
        MovieRemoteDataSourceImpl(tmdbService, movieLocalDataSource)
}
