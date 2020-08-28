package com.example.repository.ui.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.repository.api.Api
import com.example.repository.common.network.ApiResponse
import com.example.repository.common.network.NetworkOnlyResource
import com.example.repository.common.network.Resource
import com.example.repository.models.Repository
import javax.inject.Inject

class RepositoryViewModel @Inject constructor(private val api: Api) : ViewModel() {

    fun getRepoList(): LiveData<Resource<List<Repository>>> {
        return object : NetworkOnlyResource<List<Repository>>() {
            override fun createCall(): LiveData<ApiResponse<List<Repository>>> {
                return api.getRepoList()
            }
        }.asLiveData()
    }

    fun getRepoDetails(id: String): LiveData<Resource<Repository>> {
        return object : NetworkOnlyResource<Repository>() {
            override fun createCall(): LiveData<ApiResponse<Repository>> {
                return api.getRepoDetail(id)
            }
        }.asLiveData()
    }
}