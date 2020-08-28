package com.example.repository.common.di

import com.example.repository.common.App
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module(includes = [NetworkModule::class, ViewModelModule::class])
class AppModule {

    @Singleton
    @Provides
    fun provideApplication(): App = App.app

    /* @Provides
     @Singleton
     fun providePicasso(app: App): Picasso = Picasso.Builder(app).build()*/

}