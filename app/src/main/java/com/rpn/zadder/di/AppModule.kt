package com.rpn.zadder.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.work.WorkManager
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.rpn.zadder.data.local.ZadderDatabase
import com.rpn.zadder.data.remote.UnsplashApiService
import com.rpn.zadder.data.helpers.DefaultWallpaperSetter
import com.rpn.zadder.data.repository.ImageRepositoryImpl
import com.rpn.zadder.data.repository.NetworkConnectivityObserverImpl
import com.rpn.zadder.data.util.Constants
import com.rpn.zadder.data.util.Constants.API_KEY
import com.rpn.zadder.data.util.Constants.ZADDER_DATABASE
import com.rpn.zadder.domain.repository.ImageRepository
import com.rpn.zadder.domain.repository.NetworkConnectivityObserver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val newRequest = originalRequest.newBuilder()
                    .addHeader("Authorization", "Client-ID $API_KEY")
                    .build()
                // Log the request URL
                Log.d(Constants.IV_LOG_TAG, newRequest.url.toString())
                chain.proceed(newRequest)
            }
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideUnsplashApiService(okHttpClient: OkHttpClient): UnsplashApiService {
        val contentType = "application/json".toMediaType()
        val json = Json { ignoreUnknownKeys = true }
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(json.asConverterFactory(contentType))
            .client(okHttpClient) // Set the OkHttpClient
            .build()
        return retrofit.create(UnsplashApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideZadderDatabase(
        @ApplicationContext context: Context
    ): ZadderDatabase {
        return Room
            .databaseBuilder(
                context,
                ZadderDatabase::class.java,
                ZADDER_DATABASE
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideImageRepository(
        apiService: UnsplashApiService,
        database: ZadderDatabase
    ): ImageRepository {
        return ImageRepositoryImpl(apiService, database)
    }

    @Provides
    @Singleton
    fun provideApplicationScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }

    @Provides
    @Singleton
    fun provideNetworkConnectivityObserver(
        @ApplicationContext context: Context,
        scope: CoroutineScope
    ): NetworkConnectivityObserver {
        return NetworkConnectivityObserverImpl(context, scope)
    }


    @Provides
    @Singleton
    fun provideDefaultWallpaperSetter(@ApplicationContext context: Context): DefaultWallpaperSetter {
        return DefaultWallpaperSetter(context)
    }

    @Provides
    @Singleton
    fun createConverterFactory(): MoshiConverterFactory {
        return MoshiConverterFactory.create()
    }


    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext appContext: Context): WorkManager =
        WorkManager.getInstance(appContext)

}