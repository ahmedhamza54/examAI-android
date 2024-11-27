package com.plcoding.pdf_renderercompose

import android.app.appsearch.SearchResults
import android.graphics.Bitmap
import android.graphics.RectF
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun PdfViewerScreen(
    pdfUri: Uri?, // Accept pdfUri as a parameter
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val pdfBitmapConverter = remember {
        PdfBitmapConverter(context)
    }
    var renderedPages by remember {
        mutableStateOf<List<Bitmap>>(emptyList())
    }
    var searchText by remember {
        mutableStateOf("")
    }
    var searchResults by remember {
        mutableStateOf(emptyList<SearchResults>())
    }
    val scope = rememberCoroutineScope()

    LaunchedEffect(pdfUri) {
        pdfUri?.let { uri ->
            renderedPages = pdfBitmapConverter.pdfToBitmaps(uri)
        }
    }

    if (pdfUri == null) {
        Box(
            modifier = modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "No PDF selected.", color = Color.Gray)
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                itemsIndexed(renderedPages) { index, page ->
                    PdfPage(
                        page = page
                    )
                }
            }


        }
    }
}

@Composable
fun PdfPage(
    page: Bitmap,
    modifier: Modifier = Modifier,
    searchResults: SearchResults? = null
) {
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    val containerWidth = LocalContext.current.resources.displayMetrics.widthPixels.toFloat()
    val initialScale = containerWidth / page.width // Calculate scale to fit the width

    LaunchedEffect(Unit) {
        // Set the initial scale to fit the page width
        scale = initialScale
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(page.width.toFloat() / page.height.toFloat())
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(initialScale, 5f) // Limit zoom level
                    offsetX += pan.x
                    offsetY += pan.y
                }
            }
    ) {
        AsyncImage(
            model = page,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offsetX,
                    translationY = offsetY
                )
        )
    }
}
 