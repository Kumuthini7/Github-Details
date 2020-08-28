package com.example.repository.common.di

import com.example.repository.api.Api
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Named

@Module(includes = [(RepositoryViewModule::class)])
class RepositoryModule {

    @Provides
    @FeatureScope
    fun provideRepoApi(@Named("REPOSITORY_API") retrofit: Retrofit): Api {
        return retrofit.create(Api::class.java)
    }


}