package com.example.paintapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DrawingDao {
    @Insert
    suspend fun insertDrawing(drawing: Drawing)

    @Query("SELECT * FROM drawings")
    suspend fun getAllDrawings(): List<Drawing>
}
