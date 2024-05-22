package com.example.drawapp

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.drawToBitmap
import androidx.room.Room
import com.example.drawapp.ui.theme.DrawAppTheme
import com.example.paintapp.data.Drawing
import com.example.paintapp.data.DrawingDao
import com.example.paintapp.data.DrawingDatabase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class MainActivity : ComponentActivity() {
    private lateinit var drawingDatabase: DrawingDatabase
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        drawingDatabase = Room.databaseBuilder(
            applicationContext,
            DrawingDatabase::class.java, "drawing_database"
        ).build()
        super.onCreate(savedInstanceState)
        setContent {
            val coroutineScope = rememberCoroutineScope()
            val pathData = remember { mutableStateOf(PathData()) }
            val pathList = remember { mutableStateListOf(PathData()) }
            val context = LocalContext.current
            val showBottomSheet = remember { mutableStateOf(true) }

            DrawAppTheme {
                BottomSheetScaffold(
                    sheetContent = {
                        if (showBottomSheet.value) {
                            BottomPanel(
                                onClick = { color -> pathData.value = pathData.value.copy(color = color) },
                                onLineWidthChange = { lineWidth -> pathData.value = pathData.value.copy(lineWidth = lineWidth) },
                                onBackClick = { pathList.removeIf { it == pathList.lastOrNull() } },
                                onCapClick = { cap -> pathData.value = pathData.value.copy(cap = cap) },
                                onAlphaChange = { alpha -> pathData.value = pathData.value.copy(alpha = alpha) },
                                onSaveClick = {
                                    coroutineScope.launch {
                                        showBottomSheet.value = false
                                        delay(1000)
                                        saveCanvasToBitmap(context)
                                        showBottomSheet.value = true
                                    }
                                }
                            )
                        }
                    },
                    sheetPeekHeight = 50.dp
                ) {
                    Column {
                        DrawCanvas(pathData, pathList)
                    }
                }
            }
        }
    }

    //Сохранение в файловое хранилище
    private suspend fun saveCanvasToBitmap(context: Context) {
        val bitmap = context.resources.displayMetrics.let { displayMetrics ->
            with(context) {
                val viewBitmap = findViewById<View>(android.R.id.content).drawToBitmap()
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

    //Сохранение в бд
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



@Composable
fun DrawCanvas(pathData: MutableState<PathData>, pathList: SnapshotStateList<PathData>) {
    var tempPath = Path()

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(true) {
                detectDragGestures(
                    onDragStart = {
                        tempPath = Path()
                    },
                    onDragEnd = {
                        pathList.add(
                            pathData.value.copy(
                                path = tempPath
                            )
                        )
                    }
                ) { change, dragAmount ->
                    tempPath.moveTo(
                        change.position.x - dragAmount.x,
                        change.position.y - dragAmount.y
                    )
                    tempPath.lineTo(
                        change.position.x,
                        change.position.y
                    )

                    // Не удаляем предыдущий путь для решения проблемы с удалением
                    pathList.add(
                        pathData.value.copy(
                            path = tempPath
                        )
                    )
                }
            }
    ){
        pathList.forEach { pathData ->
            drawPath(
                pathData.path,
                color = pathData.color.copy(alpha = pathData.alpha),
                style = Stroke(pathData.lineWidth, cap = pathData.cap)
            )
        }
    }
}