package com.teethcare.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.teethcare.data.KeluhanRepository
import com.teethcare.data.PredictResponse
import com.teethcare.utils.Event

class AddKeluhanViewModel (private val repo: KeluhanRepository) : ViewModel() {
    val uploadResponse: LiveData<PredictResponse> = repo.uploadResponse
    val isLoading: LiveData<Boolean> = repo.isLoading
    val makeText: LiveData<Event<String>> = repo.makeText


}