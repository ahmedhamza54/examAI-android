import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.plcoding.pdf_renderercompose.PdfViewerScreen
import fi.iki.elonen.NanoHTTPD
import java.io.File
import java.io.OutputStream

//loadUrl("file:///android_asset/pdfjs/viewer.html?file=$pdfPath")
// val serverUrl = "http://localhost:8081/pdfjs/viewer.html?file=/storage/emulated/0/Download/dhia.pdf"//$pdfPath
// val serverUrl = "file:///android_asset/pdfjs/viewer.html?file=/storage/emulated/0/Download/dhia.pdf"//$pdfPath
//loadUrl(serverUrl)
//loadUrl("file:///android_asset/test.html")
// Generate the PDF file
//  val pdfUri = remember {
//    generatePdf(context, "Exam_$examId", examText)
//}
//val pdfPath = "file://${context.filesDir}/Exam_$examId.pdf"
//    val pdfPath = pdfUri?.let { getFilePathFromUri(context, it) }

@Composable
fun RegenerateScreen(context: Context, examId: String, examText: String) {
    val pdfUri = remember {
        generatePdf(context, "Exam_$examId", examText)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            "Your Exam",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Display PDF using PdfViewerScreen
        if (pdfUri != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.Gray.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                PdfViewerScreen(
                    pdfUri = pdfUri ,
                    modifier = Modifier.fillMaxSize()
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.Gray.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text("Failed to create PDF.", color = Color.Red)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // View PDF Button
        if (pdfUri != null) {
            Button(
                onClick = { openPdf(context, pdfUri) },
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text("View PDF")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Feedback Section
        FeedbackSection()
    }
}




@Composable
fun FeedbackSection() {
    var feedbackText by remember { mutableStateOf("") }

    TextField(
        value = feedbackText,
        onValueChange = { feedbackText = it },
        placeholder = { Text("Didn't like something?") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Gray.copy(alpha = 0.1f))
    )

    // Regenerate Button
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(Color.Red, Color(0xFF800080))
                ),
                shape = MaterialTheme.shapes.medium
            )
            .clickable { /* Handle Regenerate Action */ }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Regenerate",
            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Share As Button
        Button(
            onClick = { /* Handle Share Action */ },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Red,
                contentColor = Color.White
            ),
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        ) {
            Text(text = "Share As")
        }

        // Edit Button
        Button(
            onClick = { /* Handle Edit Action */ },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.White,
                contentColor = Color.Red
            ),
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        ) {
            Text(text = "Edit")
        }
    }
}

fun generatePdf(context: Context, fileName: String, textContent: String): Uri? {
    val document = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = document.startPage(pageInfo)

    val canvas = page.canvas
    val paint = android.graphics.Paint().apply {
        textSize = 12f
        color = android.graphics.Color.BLACK
    }

    val x = 10f
    var y = 25f
    val lineHeight = paint.textSize + 4

    try {
        // Write text to the PDF canvas
        textContent.split("\n").forEach { line ->
            canvas.drawText(line, x, y, paint)
            y += lineHeight
        }
        document.finishPage(page)

        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$fileName.pdf")
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_DOCUMENTS
            )
        }

        val uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
            ?: throw Exception("Failed to create file URI")

        resolver.openOutputStream(uri)?.use { outputStream ->
            document.writeTo(outputStream)
        }

        Log.d("PDF Generation", "PDF successfully created at: $uri")
        return uri
    } catch (e: Exception) {
        Log.e("PDF Error", "Error generating PDF: ${e.localizedMessage}")
        e.printStackTrace()
        return null
    } finally {
        document.close()
    }
}

fun openPdf(context: Context, pdfUri: Uri) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(pdfUri, "application/pdf")
        flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION
    }
    context.startActivity(intent)
}
fun getFilePathFromUri(context: Context, uri: Uri): String? {
    val projection = arrayOf(MediaStore.Files.FileColumns.DATA)
    context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
        if (cursor.moveToFirst()) {
            return cursor.getString(columnIndex)
        }
    }
    return null
}
class AssetServer(context: Context) : NanoHTTPD(8081) {
    private val assets = context.assets

    override fun serve(session: IHTTPSession?): Response {
        val uri = session?.uri ?: return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Not Found")
        return try {
            val filePath = uri.removePrefix("/")
            val inputStream = assets.open(filePath)
            newChunkedResponse(Response.Status.OK, getMimeType(filePath), inputStream)
        } catch (e: Exception) {
            newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "File Not Found")
        }
    }

    private fun getMimeType(filePath: String): String {
        return when {
            filePath.endsWith(".js") -> "application/javascript"
            filePath.endsWith(".css") -> "text/css"
            filePath.endsWith(".html") -> "text/html"
            filePath.endsWith(".pdf") -> "application/pdf"
            else -> "application/octet-stream"
        }
    }
}



