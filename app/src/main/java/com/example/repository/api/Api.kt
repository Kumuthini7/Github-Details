package com.example.repository.api

import androidx.lifecycle.LiveData
import com.example.repository.common.network.ApiResponse
import com.example.repository.models.Repository
import retrofit2.http.GET
import retrofit2.http.Path

interface Api {

    @GET("repositories")
    fun getRepoList(): LiveData<ApiResponse<List<Repository>>>

    @GET("repositories/{id}")
    fun getRepoDetail(@Path("id") id: String): LiveData<ApiResponse<Repository>>

}