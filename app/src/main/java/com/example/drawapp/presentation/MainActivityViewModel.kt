package com.example.drawapp.presentation

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.view.drawToBitmap
import androidx.lifecycle.ViewModel
import com.example.paintapp.data.Drawing
import com.example.paintapp.data.DrawingDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val drawingDatabase: DrawingDatabase
) : ViewModel() {

    suspend fun saveCanvasToBitmap(context: Context, view: View) {
        val bitmap = context.resources.displayMetrics.let { displayMetrics ->
            with(context) {
                val viewBitmap = view.drawToBitmap()
                Bitmap.createBitmap(viewBitmap, 0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
            }
        }

        saveBitmapToFile(context, bitmap)
        saveBitmapToDatabase(bitmap)
    }

    private fun saveBitmapToFile(context: Context, bitmap: Bitmap) {
        val fileName = "painting_${System.currentTimeMillis()}.jpg"
        val resolver = context.contentResolver
        val imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

        val imageDetails = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
            put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
        }

        resolver.insert(imageCollection, imageDetails)?.also { uri ->
            resolver.openOutputStream(uri).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream!!)
            }
        } ?: run {
            Toast.makeText(context, "Ошибка", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(context, "Сохранено", Toast.LENGTH_SHORT).show()
    }

    private suspend fun saveBitmapToDatabase(bitmap: Bitmap) {
        val byteArray = bitmap.toByteArray()
        val drawing = Drawing(filePath = byteArray)
        drawingDatabase.drawingDao().insertDrawing(drawing)
    }

    private fun Bitmap.toByteArray(): ByteArray {
        val baos = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.PNG, 100, baos)
        return baos.toByteArray()
    }
}
