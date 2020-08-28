package com.example.repository.common.di

import com.example.repository.common.App

class ComponentsProvider {

    companion object {
        fun getComponent() =
            DaggerRepositoryComponent.builder().appComponent(App.appComponent).build()
    }
}