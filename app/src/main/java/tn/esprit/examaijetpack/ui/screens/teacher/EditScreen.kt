package tn.esprit.examaijetpack.ui.screens.teacher

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import tn.esprit.examaijetpack.R
import tn.esprit.examaijetpack.ui.viewModels.EditViewModel

@Composable
fun EditScreen(
    examId: String,
    initialText: String,
    viewModel: EditViewModel = viewModel(),
    onSaveSuccess: (String) -> Unit
) {
    val examText = viewModel.examText.collectAsState().value

    // Initialize the ViewModel's text
    LaunchedEffect(Unit) {
        viewModel.updateText(initialText)
    }

    var showError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = { HomeTopBar() } // Include HomeTopBar as the top bar
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Adjust content padding for the Scaffold
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Edit Exam", fontSize = 20.sp)
                Text(
                    text = "Save",
                    fontSize = 16.sp,
                    color = Color.Blue,
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable {
                            viewModel.saveExam(
                                examId,
                                onSuccess = { updatedText ->
                                    showError = ""
                                    onSaveSuccess(updatedText) // Pass updated text
                                },
                                onError = { errorMessage ->
                                    showError = errorMessage
                                }
                            )
                        }
                )
            }

            BasicTextField(
                value = examText,
                onValueChange = { viewModel.updateText(it) },
                textStyle = TextStyle(
                    textAlign = TextAlign.Right // Right-align text for Arabic writing
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp)
            )

            if (showError != null) {
                Text(
                    text = "Error: ${showError}",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}
