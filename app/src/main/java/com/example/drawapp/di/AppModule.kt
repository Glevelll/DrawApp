package com.project.listapp.di

import android.content.Context
import androidx.room.Room
import com.example.paintapp.data.DrawingDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDrawingDatabase(@ApplicationContext context: Context): DrawingDatabase {
        return Room.databaseBuilder(
            context,
            DrawingDatabase::class.java,
            "drawing.db"
        ).build()
    }
}