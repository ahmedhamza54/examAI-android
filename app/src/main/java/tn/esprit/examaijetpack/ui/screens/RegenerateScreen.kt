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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.plcoding.pdf_renderercompose.PdfViewerScreen
import fi.iki.elonen.NanoHTTPD
import tn.esprit.examaijetpack.ui.viewModels.RegenerateViewModel
import java.io.File
import java.io.OutputStream
var examTextToRegenerate = ""
@Composable
fun RegenerateScreen(context: Context, examId: String, examText: String,regenerateViewModel: RegenerateViewModel = viewModel()) {
if (examTextToRegenerate.isEmpty())
{
    examTextToRegenerate = examText
}
    var regenerateIsClicked = false
    val isLoading by regenerateViewModel.isLoading.collectAsState()
    val regenerateResponse by regenerateViewModel.regenerateResponse.collectAsState()

    var feedbackText by remember { mutableStateOf("") }
    //val pdfUri = remember {
      //  generatePdf(context, "Exam_$examId", examText)
    //}
    val pdfUri = remember(regenerateResponse) {
        regenerateResponse?.let { (newId, newText) ->
            generatePdf(context, "Exam_$newId", newText)
        } ?: generatePdf(context, "Exam_$examId", examText)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Your Exam",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(bottom = 16.dp)
        )
       // lateinit var examTextToRegenerate: String
        if (regenerateResponse != null  )  {
            regenerateIsClicked = false
            val (newId, newText) = regenerateResponse!!
            examTextToRegenerate = newText
           // Log.d("exam text ", examTextToRegenerate)
            //Log.d("exam text ", examTextToRegenerate)

           // val newPdfUri = generatePdf(context, "Exam_$newId", newText)

            // Display regenerated PDF
            if (pdfUri != null) {
                PdfViewerScreen(
                    pdfUri = pdfUri,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        } else if (pdfUri != null) {
            // Display initial PDF
            PdfViewerScreen(
                pdfUri = pdfUri,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
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
            Button(onClick = { openPdf(context, pdfUri) }) {
                Text("View PDF")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        // Feedback Section
        FeedbackSection(
            feedbackText = feedbackText,
            onFeedbackTextChange = { feedbackText = it },
            onRegenerateClick = {
            Log.d("exam text ", examTextToRegenerate)
                regenerateViewModel.regenerateExam(examId, examTextToRegenerate , feedbackText)
                regenerateIsClicked = true
            }

        )


    }
    if (isLoading) {
        var dotsCount by remember { mutableStateOf(1) }

        LaunchedEffect(isLoading) {
            while (isLoading) {
                dotsCount = (dotsCount % 5) + 1 // Cycle from 1 to 5
                kotlinx.coroutines.delay(500) // Adjust delay for speed of animation
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Generating" + ".".repeat(dotsCount),
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
fun FeedbackSection(
    feedbackText: String,
    onFeedbackTextChange: (String) -> Unit,
    onRegenerateClick: () -> Unit
) {
    TextField(
        value = feedbackText,
        onValueChange = onFeedbackTextChange,
        placeholder = { Text("Didn't like something?") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Gray.copy(alpha = 0.1f))
    )

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
            .clickable(onClick = onRegenerateClick)
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
            .padding(top = 16.dp)
           ,
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

