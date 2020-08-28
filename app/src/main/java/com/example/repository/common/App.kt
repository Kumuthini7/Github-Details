package com.example.repository.common

import android.app.Application
import com.example.repository.BuildConfig
import com.example.repository.common.di.AppComponent
import com.example.repository.common.di.DaggerAppComponent
import com.facebook.stetho.Stetho

open class App : Application() {

    companion object {
        lateinit var appComponent: AppComponent
        lateinit var app: App
    }

    override fun onCreate() {

        super.onCreate()
        app = this
        appComponent = DaggerAppComponent.builder().build()
        initStetho()
    }

    private fun initStetho() {
        if (BuildConfig.DEBUG ) {
            Stetho.initialize(
                    Stetho.newInitializerBuilder(this)
                            .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                            .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                            .build())
        }
    }
}