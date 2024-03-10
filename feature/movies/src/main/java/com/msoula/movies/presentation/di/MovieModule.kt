package com.msoula.movies.presentation.di

import android.content.Context
import com.example.movies.BuildConfig
import com.msoula.database.data.dao.MovieDAO
import com.msoula.movies.data.ImageHelper
import com.msoula.movies.data.MovieRepositoryImpl
import com.msoula.movies.data.MovieService
import com.msoula.movies.data.TMDBService
import com.msoula.movies.data.mapper.MapMovieEntityToMovie
import com.msoula.movies.data.mapper.MapMoviePogoToMovieEntity
import com.msoula.movies.data.mapper.MapMovieToMovieEntity
import com.msoula.movies.domain.MovieRepository
import com.msoula.movies.domain.useCases.DeleteAllMovies
import com.msoula.movies.domain.useCases.InsertMovieUseCase
import com.msoula.movies.domain.useCases.ObserveMoviesUseCase
import com.msoula.movies.domain.useCases.SetMovieFavoriteUseCase
import com.msoula.movies.domain.useCases.deleteAllMovies
import com.msoula.movies.domain.useCases.insertMovie
import com.msoula.movies.domain.useCases.observeMovies
import com.msoula.movies.domain.useCases.setMovieFavorite
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
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
    fun provideImageHelper(
        @ApplicationContext context: Context,
        coroutineDispatcher: CoroutineDispatcher,
    ): ImageHelper = ImageHelper(coroutineDispatcher, context)

    @Provides
    fun provideMovieService(
        movieDAO: MovieDAO,
        tmdbService: TMDBService,
        mapperPogoToMovieEntity: MapMoviePogoToMovieEntity,
        imageHelper: ImageHelper,
    ): MovieService =
        MovieService(
            movieDAO,
            tmdbService,
            mapperPogoToMovieEntity,
            imageHelper,
        )

    @Provides
    @Singleton
    fun provideMovieRepository(
        movieDAO: MovieDAO,
        movieService: MovieService,
        mapperMovieToMovieEntity: MapMovieToMovieEntity,
        mapperMovieEntityToMovie: MapMovieEntityToMovie,
    ): MovieRepository =
        MovieRepositoryImpl(
            movieDAO,
            movieService,
            mapperMovieToMovieEntity,
            mapperMovieEntityToMovie,
        )

    @Provides
    fun provideGetMovieByPopularityUseCase(movieRepository: MovieRepository): ObserveMoviesUseCase =
        ObserveMoviesUseCase {
            observeMovies(movieRepository)
        }

    @Provides
    fun provideDeleteAllMoviesUseCase(movieRepository: MovieRepository): DeleteAllMovies =
        DeleteAllMovies {
            deleteAllMovies(movieRepository)
        }

    @Provides
    fun provideUpdateMovieUseCase(movieRepository: MovieRepository): SetMovieFavoriteUseCase =
        SetMovieFavoriteUseCase { movieId, isFavorite ->
            setMovieFavorite(
                movieRepository,
                movieId,
                isFavorite,
            )
        }

    @Provides
    fun provideInsertMovieUseCase(movieRepository: MovieRepository): InsertMovieUseCase =
        InsertMovieUseCase {
            insertMovie(
                movieRepository,
                it,
            )
        }
}
