package com.example.repository.common.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.repository.ui.repository.RepositoryViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Module
interface RepositoryViewModule {

    @Binds
    @IntoMap
    @ViewModelKey(RepositoryViewModel::class)
    fun bindRepoViewModel(repositoryViewModel: RepositoryViewModel): ViewModel

    @Binds
    fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}