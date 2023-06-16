package com.teethcare.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.teethcare.utils.Event

class KeluhanRepository {
    private val _uploadResponse = MutableLiveData<PredictResponse>()
    val uploadResponse: LiveData<PredictResponse> = _uploadResponse

    private val _makeText = MutableLiveData<Event<String>>()
    val makeText: LiveData<Event<String>> = _makeText

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> =_isLoading

    companion object{
        private const val TAG = "KeluhanRepository"

        @Volatile
        private var instance: KeluhanRepository? = null
        fun getInstance(
        ): KeluhanRepository = instance ?: synchronized(this){
            instance ?: KeluhanRepository()
        }.also { instance = it }
    }
}