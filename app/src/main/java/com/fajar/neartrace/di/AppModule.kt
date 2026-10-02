package com.fajar.neartrace.di

import android.content.Context
import androidx.room.Room
import com.fajar.neartrace.data.local.DeviceDao
import com.fajar.neartrace.data.local.NearTraceDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides @Singleton
    fun database(@ApplicationContext context: Context): NearTraceDatabase =
        Room.databaseBuilder(context, NearTraceDatabase::class.java, "neartrace.db").build()

    @Provides fun deviceDao(database: NearTraceDatabase): DeviceDao = database.deviceDao()
}
