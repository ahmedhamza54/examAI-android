package tn.esprit.examaijetpack.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import tn.esprit.examaijetpack.ui.viewmodels.AnswerExamViewModel

@Composable
fun ExamTextAreaScreen(
    examId: String,
    examTextContent: String,
    navController: NavController,
    viewModel: AnswerExamViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var examText by remember { mutableStateOf("") }
    var showExamDialog by remember { mutableStateOf(false) } // State to control dialog visibility
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val StudentId = "67588f48560d1cdec95fc73f"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title Text
        Text(
            text = "Exam Answer",
            style = MaterialTheme.typography.headlineSmall,
            color = Color(0xFF333333),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Framed Text Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .border(2.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                .background(Color.Gray.copy(alpha = 0.05f))
                .padding(8.dp)
        ) {
            TextField(
                value = examText,
                onValueChange = { examText = it },
                placeholder = {
                    Text(
                        text = "Write your exam answers here...",
                        color = Color.Gray
                    )
                },
                modifier = Modifier.fillMaxSize(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                maxLines = Int.MAX_VALUE
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // "View Exam" Button
            Button(
                onClick = { showExamDialog = true }, // Show the pop-up dialog
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF607D8B) // Subtle blue-gray color
                )
            ) {
                Text(text = "View Exam", color = Color.White)
            }

            // "Save" Button
            Button(
                onClick = {
                    isSaving = true
                    viewModel.saveExamAttempt(
                        studentId = StudentId,
                        examId = examId,
                        answerText = examText,
                        onSuccess = {
                            isSaving = false
                        },
                        onError = {
                            isSaving = false
                            errorMessage = it // Show error message
                        }
                    )
                    navController.navigate("attempts")
                },
                enabled = !isSaving,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                )
            ) {
                if (isSaving) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp))
                } else {
                    Text(text = "Save", color = Color.White)
                }
            }
        }

        // Pop-up Dialog for Viewing the Exam Text
        if (showExamDialog) {
            AlertDialog(
                onDismissRequest = { showExamDialog = false }, // Close the dialog
                confirmButton = {
                    Button(
                        onClick = { showExamDialog = false } // Close button
                    ) {
                        Text("Close")
                    }
                },
                title = {
                    Text(
                        text = "Exam Content",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF2196F3) // Blue Title
                    )
                },
                text = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp) // Restrict height to make it scrollable
                            .verticalScroll(rememberScrollState())
                            .background(Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = examTextContent,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            textAlign = TextAlign.Justify
                        )
                    }
                }
            )
        }
    }
}
