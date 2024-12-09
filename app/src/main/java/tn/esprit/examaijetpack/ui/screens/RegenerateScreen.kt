import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.plcoding.pdf_renderercompose.PdfViewerScreen
import tn.esprit.examaijetpack.ui.navigation.NavGraph
import tn.esprit.examaijetpack.ui.navigation.encode
import tn.esprit.examaijetpack.ui.viewModels.RegenerateViewModel

var examTextToRegenerate = ""

@Composable
fun RegenerateScreen(context: Context, examId: String, examText: String,regenerateViewModel: RegenerateViewModel = viewModel(),navController: NavHostController) {
    if (examTextToRegenerate.isEmpty())
    {
        examTextToRegenerate = examText
    }
    val isLoading by regenerateViewModel.isLoading.collectAsState()
    val regenerateResponse by regenerateViewModel.regenerateResponse.collectAsState()

    var feedbackText by remember { mutableStateOf("") }

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
            context = context,
            feedbackText = feedbackText,
            pdfUri = pdfUri, // Pass pdfUri here
            onFeedbackTextChange = { feedbackText = it },
            onRegenerateClick = {
                Log.d("exam text ", examTextToRegenerate)
                regenerateViewModel.regenerateExam(examId, examTextToRegenerate, feedbackText)
            },
            onShareClick = {
                pdfUri?.let { sharePdf(context, it) } // Handle the share action
            },
            examId,
            examTextToRegenerate,
            navController
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
    context: Context,
    feedbackText: String,
    pdfUri: Uri?,
    onFeedbackTextChange: (String) -> Unit,
    onRegenerateClick: () -> Unit,
    onShareClick: () -> Unit,
    examId: String,
    examText: String,
    navController: NavHostController
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
            onClick = {
                pdfUri?.let { sharePdf(context, it) }
            },
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
            onClick = { navController.navigate("Edit/$examId/${examText.encode()}")
            },
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
        textAlign = android.graphics.Paint.Align.RIGHT // Right-align the text
    }

    val pageWidth = pageInfo.pageWidth.toFloat()
    var y = 25f
    val lineHeight = paint.textSize + 4

    try {
        // Write text to the PDF canvas
        textContent.split("\n").forEach { line ->
            val adjustedLine = line.replace("+", " ") // Replace "+" with spaces
            canvas.drawText(adjustedLine, pageWidth - 10f, y, paint) // Align text to the right
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
fun sharePdf(context: Context, pdfUri: Uri) {
    try {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, pdfUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Grant temporary permission to access the URI
        }
        context.startActivity(
            Intent.createChooser(shareIntent, "Share PDF via")
        )
    } catch (e: Exception) {
        Log.e("Share PDF", "Error sharing PDF: ${e.localizedMessage}")
        e.printStackTrace()
    }
}
