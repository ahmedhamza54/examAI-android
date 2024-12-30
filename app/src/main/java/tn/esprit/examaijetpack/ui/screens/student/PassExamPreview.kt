package tn.esprit.examaijetpack.ui.screens.student

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.plcoding.pdf_renderercompose.PdfViewerScreen
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


@Composable
fun SimplePdfViewerScreen(
    examText: String,
    examId: String,
    context: Context,
    navController: NavController,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // PDF Viewer with Elevation
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(8.dp),
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            // Placeholder for PDF Viewer
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp),
                contentAlignment = Alignment.TopStart // Ensure text starts from the top
            ) {
                Text(
                    text = examText,
                    style = MaterialTheme.typography.body1,
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Pass Exam Button
        Button(
            { navController.navigate("examTextArea/$examId/${Uri.encode(examText)}") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Pass Exam", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Share Button with Icon
        Button(
            onClick = { /* Share exam logic */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
            shape = MaterialTheme.shapes.medium
        ) {
            Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Share", color = Color.White)
        }
    }
}



@Composable
fun PassExamPreview(navController: NavHostController, examId: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Pass Exam Preview for Exam ID: $examId", style = MaterialTheme.typography.h5)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text(text = "Back")
        }
    }
}

@Composable
fun AnswerExamScreen(navController: NavHostController, examId: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Display Exam ID
        Text(
            text = "Answer Exam for Exam ID: $examId",
            style = MaterialTheme.typography.h5,
            color = Color(0xFF2196F3) // Blue color for emphasis
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Placeholder for Exam Answers
        Text(
            text = "This is where you will answer the exam questions.",
            style = MaterialTheme.typography.body1,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Submit Button
        Button(
            onClick = {
                // Logic for submitting answers can be added here
                navController.popBackStack() // Navigates back to the previous screen
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)) // Green button
        ) {
            Text(
                text = "Submit Answers",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Cancel Button
        OutlinedButton(
            onClick = { navController.popBackStack() }, // Navigate back
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(48.dp),
            border = BorderStroke(2.dp, Color(0xFFE53935)), // Red border
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = "Cancel",
                color = Color(0xFFE53935),
                fontWeight = FontWeight.Bold
            )
        }
    }
}






@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
fun PreviewSimplePdfViewerScreen() {
    val context = LocalContext.current
    val samplePdfUri: Uri? = null
    val navController = androidx.navigation.compose.rememberNavController()

   /* SimplePdfViewerScreen(
        pdfUri = samplePdfUri,
        context = context,
        navController = navController
    )*/
}
