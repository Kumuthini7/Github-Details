package com.example.repository.common.di

import com.example.repository.MainActivity
import com.example.repository.ui.repository.RepoDetailActivity
import com.example.repository.ui.repository.RepoListFragment
import dagger.Component


@FeatureScope
@Component(
    modules = [RepositoryModule::class], dependencies = [(AppComponent::class)]
)
interface RepositoryComponent {
    @Component.Builder
    interface Builder {
        fun appComponent(appComponent: AppComponent): Builder

        fun build(): RepositoryComponent
    }

    fun inject(activity: MainActivity)
    fun inject(activity: RepoDetailActivity)
    fun inject(fragment: RepoListFragment)

}