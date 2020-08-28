package com.example.repository.common.di

import com.example.repository.common.App
import dagger.Component
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Singleton
@Component(modules = [(AppModule::class)])
interface AppComponent {

    fun getApp(): App

    @Named("REPOSITORY_API")
    fun getRetrofit(): Retrofit

    // fun getPicasso(): Picasso

    @Component.Builder
    interface Builder {
        fun build(): AppComponent
    }
}