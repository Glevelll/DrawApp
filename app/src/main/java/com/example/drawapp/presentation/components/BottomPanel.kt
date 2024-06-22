package com.example.drawapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

@Composable
fun BottomPanel(onClick: (Color) -> Unit, onLineWidthChange: (Float) -> Unit, onBackClick: () -> Unit, onCapClick: (StrokeCap) -> Unit, onAlphaChange: (Float) -> Unit, onSaveClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ColorList{ color ->
            onClick(color)
        }
        CustomSlider{ lineWidth ->
            onLineWidthChange(lineWidth)
        }
        ButtonPanel({
            onBackClick()
        }, {
            onCapClick(it)
        } ) {
            onSaveClick()
        }
        Spacer(modifier = Modifier.height(10.dp))
        AlphaSlider { alpha ->
            onAlphaChange(alpha)
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun ColorList(onClick: (Color) -> Unit) {
    val colors = listOf(
        Color.Blue,
        Color.Red,
        Color.Black,
        Color.Green,
        Color.Yellow,
        Color.Gray,
        Color.Magenta,
        Color.Cyan,
    )

    LazyRow(
        modifier = Modifier.padding(vertical = 10.dp)
    ) {
        items(colors) { color ->
            Box(
                modifier = Modifier
                    .padding(end = 10.dp)
                    .clickable {
                        onClick(color)
                    }
                    .size(40.dp)
                    .background(color, CircleShape)
            )
        }
    }
}

@Composable
fun AlphaSlider(onChange: (Float) -> Unit) {
    var alpha by remember { mutableStateOf(1f) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Резкость: ${(alpha * 100).toInt()}")
        Slider(
            value = alpha,
            onValueChange = {
                alpha = it
                onChange(it)
            }
        )
    }
}

@Composable
fun CustomSlider(onChange: (Float) -> Unit){
    var position by remember {
        mutableFloatStateOf(0.05f)
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Ширина линии: ${(position * 100).toInt()}")
        Slider(
            value = position,
            onValueChange = {
                val tempPos = if (it > 0) it else 0.01f
                position = tempPos
                onChange(tempPos * 100)
            }
        )
    }
}

@Composable
fun ButtonPanel(onClick: () -> Unit, onCapClick: (StrokeCap) -> Unit, onSaveClick: () -> Unit){
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(
            modifier = Modifier
                .clip(CircleShape)
                .background(Color.White)
                .padding(horizontal = 5.dp),
            onClick = {
                onClick()
            }
        ) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = null
            )
        }

        Spacer(modifier = Modifier.width(5.dp))

        IconButton(
            modifier = Modifier
                .clip(CircleShape)
                .background(Color.White)
                .padding(horizontal = 5.dp),
            onClick = {
                onCapClick(StrokeCap.Round)
            }
        ) {
            Icon(
                Icons.Default.Build,
                contentDescription = null
            )
        }

        Spacer(modifier = Modifier.width(5.dp))

        IconButton(
            modifier = Modifier
                .clip(CircleShape)
                .background(Color.White)
                .padding(horizontal = 5.dp),
            onClick = {
                onCapClick(StrokeCap.Butt)
            }
        ) {
            Icon(
                Icons.Default.Create,
                contentDescription = null
            )
        }

        Spacer(modifier = Modifier.width(5.dp))

        IconButton(
            modifier = Modifier
                .clip(CircleShape)
                .background(Color.White)
                .padding(horizontal = 5.dp),
            onClick = {
                onCapClick(StrokeCap.Square)
            }
        ) {
            Icon(
                Icons.Default.Menu,
                contentDescription = null
            )
        }

        Spacer(modifier = Modifier.width(5.dp))

        IconButton(
            modifier = Modifier
                .clip(CircleShape)
                .background(Color.White)
                .padding(horizontal = 5.dp),
            onClick = {
                onSaveClick()
            }
        ) {
            Icon(
                Icons.Default.ExitToApp,
                contentDescription = null
            )
        }
    }
}
