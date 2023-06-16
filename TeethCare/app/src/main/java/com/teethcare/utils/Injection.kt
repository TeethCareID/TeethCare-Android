package com.teethcare.utils

import android.content.Context
import com.teethcare.data.KeluhanRepository

object Injection {
    fun provideRepository(context: Context): KeluhanRepository {

        return KeluhanRepository.getInstance()
    }
}