package com.msoula.movies.presentation.di

import com.example.movies.BuildConfig
import com.msoula.database.data.dao.MovieDAO
import com.msoula.movies.data.MovieRepositoryImpl
import com.msoula.movies.data.TMDBService
import com.msoula.movies.data.mapper.MapMovieEntityToMovie
import com.msoula.movies.data.mapper.MapMoviePogoToMovieEntity
import com.msoula.movies.domain.MovieRepository
import com.msoula.movies.domain.use_case.GetMovieUseCase
import com.msoula.movies.domain.use_case.getMovies
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
object MovieModule {

    @Provides
    @Singleton
    fun providesAuthInterceptor(): Interceptor {
        return Interceptor { chain ->
            val newUrl = chain.request().url
                .newBuilder()
                .addQueryParameter("api_key", API_KEY)
                .build()

            val newRequest = chain.request()
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
    fun provideMovieRepository(
        tmdbService: TMDBService,
        movieDAO: MovieDAO,
        mapperPogoToMovieEntity: MapMoviePogoToMovieEntity
    ): MovieRepository = MovieRepositoryImpl(
        tmdbService,
        movieDAO,
        mapperPogoToMovieEntity
    )

    @Provides
    fun provideGetMovieUseCase(
        movieRepository: MovieRepository,
        mapper: MapMovieEntityToMovie
    ): GetMovieUseCase =
        GetMovieUseCase {
            getMovies(movieRepository, mapper)
        }
}
