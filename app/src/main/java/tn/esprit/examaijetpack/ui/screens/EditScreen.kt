package tn.esprit.examaijetpack.ui.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import tn.esprit.examaijetpack.ui.viewModels.EditViewModel

@Composable
fun EditScreen(
    examId: String,
    initialText: String,
    viewModel: EditViewModel = viewModel(),
    onSaveSuccess: () -> Unit
) {
    // Initialize the ViewModel's text
    LaunchedEffect(Unit) {
        viewModel.updateText(initialText)
    }

    var showError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
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
                            onSuccess = {
                                showError = ""
                                onSaveSuccess() // Call success action
                            },
                            onError = { errorMessage ->
                                showError = errorMessage
                            }
                        )
                    }
            )
        }

        BasicTextField(
            value = viewModel.examText,
            onValueChange = { viewModel.updateText(it) },
            textStyle = TextStyle(
                textAlign = TextAlign.Right, // Right-align text for Arabic writing
            ),
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
        )

        if (showError != null) {
            Text(
                text = "Error: ${showError}",
                color = androidx.compose.ui.graphics.Color.Red,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
