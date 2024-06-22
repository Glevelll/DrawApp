package com.example.drawapp.presentation

import android.R
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.drawapp.domain.PathData
import com.example.drawapp.presentation.components.BottomPanel
import com.example.drawapp.presentation.components.DrawCanvas
import com.example.drawapp.ui.theme.DrawAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainActivityViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val coroutineScope = rememberCoroutineScope()
            val pathData = remember { mutableStateOf(PathData()) }
            val pathList = remember { mutableStateListOf<PathData>() }
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
                                        val rootView = findViewById<View>(R.id.content)
                                        viewModel.saveCanvasToBitmap(context, rootView)
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
}