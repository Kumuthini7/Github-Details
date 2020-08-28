package com.example.repository.common.di

import com.example.repository.BuildConfig
import com.example.repository.common.adapter.LiveDataCallAdapterFactory
import com.example.repository.common.constant.BaseUrl.Companion.REPOSITORY_URL
import com.example.repository.common.network.HttpErrorInterceptor
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
open class NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        networkTimeoutSecond: Long,
        logger: HttpLoggingInterceptor,
        stethoInterceptor: StethoInterceptor
    ): OkHttpClient {
        val okHttpClientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
        okHttpClientBuilder.readTimeout(networkTimeoutSecond, TimeUnit.SECONDS)
        okHttpClientBuilder.connectTimeout(networkTimeoutSecond, TimeUnit.SECONDS)
        okHttpClientBuilder.writeTimeout(networkTimeoutSecond, TimeUnit.SECONDS)
        if (BuildConfig.DEBUG) {
            logger.level = HttpLoggingInterceptor.Level.BASIC
            okHttpClientBuilder.addInterceptor(logger)
            okHttpClientBuilder.addNetworkInterceptor(stethoInterceptor)
        }

        okHttpClientBuilder.addInterceptor(HttpErrorInterceptor())
        return okHttpClientBuilder.build()
    }

    @Provides
    @Singleton
    fun provideConverter(gson: Gson): Converter.Factory = GsonConverterFactory.create(gson)


    @Provides
    @Singleton
    fun provideCallAdapterFactory(): LiveDataCallAdapterFactory = LiveDataCallAdapterFactory()

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().create()
    }

    @Provides
    @Singleton
    fun provideNetworkTimeout(): Long = 30L

    @Provides
    fun provideLogger(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message -> Timber.d(message); })
    }

    @Provides
    @Singleton
    fun provideStethoInterceptor(): StethoInterceptor = StethoInterceptor()


    @Provides
    @Singleton
    @Named("REPOSITORY_API")
    fun provideListingRetrofit(
        httpClient: OkHttpClient,
        converter: Converter.Factory,
        liveDataCallAdapterFactory: LiveDataCallAdapterFactory
    ): Retrofit {
        return getRetrofit(
            httpClient,
            converter,
            liveDataCallAdapterFactory,
            REPOSITORY_URL
        )
    }

    private fun getRetrofit(
        httpClient: OkHttpClient,
        converter: Converter.Factory,
        liveDataCallAdapterFactory: LiveDataCallAdapterFactory,
        baseUrl: String
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(REPOSITORY_URL)
            .addCallAdapterFactory(liveDataCallAdapterFactory)
            .addConverterFactory(converter)
            .client(httpClient).build()
    }
}