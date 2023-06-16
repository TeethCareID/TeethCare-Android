package com.teethcare.model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.teethcare.data.KeluhanRepository
import com.teethcare.utils.Injection

class ViewModelFactory(private val repo: KeluhanRepository): ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when{
            modelClass.isAssignableFrom(AddKeluhanViewModel::class.java) ->{
                AddKeluhanViewModel(repo) as T
            } else -> throw IllegalArgumentException("Uknown ViewModel class:: " + modelClass.name)
        }
    }

    companion object{
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory{
            return instance ?: synchronized(this){
                instance ?: ViewModelFactory(Injection.provideRepository(context))
            }.also { instance = it }
        }
    }
}