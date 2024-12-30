package tn.esprit.examaijetpack.ui.screens.student

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import tn.esprit.examaijetpack.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import tn.esprit.examaijetpack.ui.navigation.BottomNavigationBarStudent
import tn.esprit.examaijetpack.ui.viewmodels.ExamAttempt
import tn.esprit.examaijetpack.ui.viewmodels.ExamAttemptsViewModel


@Composable
fun ListExamAttemptsScreen(
    navController: NavController,
    studentId: String,
    viewModel: ExamAttemptsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    val examAttempts by viewModel.examAttempts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchExamAttempts(studentId)
    }

    Scaffold(
        topBar = { HomeTopBarStudent1() },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Exam Attempts",
                fontSize = 34.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFD32F2F),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(bottom = 8.dp))

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn {
                    items(examAttempts) { attempt ->
                        ExamAttemptItem(attempt = attempt) { selectedAttempt ->
                            viewModel.correctExam(
                                selectedAttempt.examId,
                                selectedAttempt._id
                            ) { success, correction ->
                                if (success) {
                                   // Toast.makeText(context, "Correction: $correction", Toast.LENGTH_SHORT).show()

                                } else {
                                  //  Toast.makeText(context, "Correction failed: $correction", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Improved ExamAttemptItem Composable with better button behavior
@Composable
fun ExamAttemptItem(
    attempt: ExamAttempt,
    onCorrectExam: (ExamAttempt) -> Unit
) {
    var isCorrecting by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        val scoreColor = if (attempt.score == "not graded") {
            Color.Gray
        } else {
            Color.Blue
        }
        Text(text = "Attempt Date: ${attempt.attemptDate}")
        Text(text = "Score: ${attempt.score}",
            color = scoreColor, // Apply the dynamic color here
            style = MaterialTheme.typography.body1)
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                isCorrecting = true
                onCorrectExam(attempt) // Trigger correction
            },
            enabled = !isCorrecting,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = if (isCorrecting) Color.Gray else Color.Green
            )
        ) {
            Text(
                text = if (isCorrecting) "Correcting..." else "Correct",
                color = Color.White
            )
        }
    }
}

@Composable
fun AnimatedText(isCorrecting: Boolean): String {
    var text by remember { mutableStateOf("Correcting") }
    LaunchedEffect(isCorrecting) {
        if (isCorrecting) {
            while (true) {
                text = when (text) {
                    "Correcting" -> "Correcting."
                    "Correcting." -> "Correcting.."
                    "Correcting.." -> "Correcting..."
                    else -> "Correcting"
                }
                delay(500) // Update every 500ms
            }
        }
    }
    return text
}



@Composable
fun HomeTopBarStudent1() {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.exam_ai_logo),  // Logo image
                    contentDescription = "ExamAI Logo",
                    modifier = Modifier.size(40.dp) // Adjust the size of the logo
                )
                Spacer(modifier = Modifier.width(8.dp)) // Space between logo and title
                Text(
                    text = "ExamAI",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        },
        actions = {
            IconButton(onClick = { /* TODO: Implement search */ }) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Profile Icon",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Black // Optional: Set color
                )
            }
            IconButton(onClick = { /* TODO: Implement menu */ }) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Menu Icon",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Black // Optional: Set color
                )
            }
        },
        backgroundColor = Color.White, // Background color for the top bar
        contentColor = Color.Black
    )
}

// Sample Data
fun sampleExamAttempts(): List<ExamAttempt> {
    return listOf(

    )
}

@Preview(showBackground = true)
@Composable
fun PreviewListExamAttemptsScreen() {
    // ListExamAttemptsScreen(navController = rememberNavController())
}
