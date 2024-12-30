package tn.esprit.examaijetpack.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tn.esprit.examaijetpack.R
import tn.esprit.examaijetpack.ui.viewModels.LoginViewModel
import android.content.Context
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")


@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    onSignUpClick: () -> Unit
) {
    val email = viewModel.email.observeAsState("")
    val password = viewModel.password.observeAsState("")
    val isLoading = viewModel.isLoading.observeAsState(false)
    val errorMessage = viewModel.errorMessage.observeAsState(null)
    val loginSuccess = viewModel.loginSuccess.observeAsState(false)
    val teacherId = viewModel.teacherId.observeAsState(false).value
    val specialization = viewModel.specialization.observeAsState(false).value
    val studentId = viewModel.studentId.observeAsState(false).value
    val grade = viewModel.grade.observeAsState(false).value
    val context = LocalContext.current // Get the current context

    if (loginSuccess.value == true ) {
        LaunchedEffect(Unit) {
            viewModel.saveTeacherId(context,teacherId.toString())
            viewModel.saveSpecialization(context,specialization.toString())
            viewModel.saveStudentId(context,studentId.toString())
            viewModel.saveGrade(context,grade.toString())
        }
        onLoginSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.exam_ai_logo), // Replace with your logo's resource ID
            contentDescription = "App Logo",
            modifier = Modifier
                .size(150.dp)
                .padding(bottom = 32.dp)
        )
        Text("Login to ExamAI", fontSize = 24.sp, modifier = Modifier.padding(8.dp))

        OutlinedTextField(
            value = email.value,
            onValueChange = { viewModel.email.value = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password.value,
            onValueChange = { viewModel.password.value = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        errorMessage.value?.let {
            Text(it, color = MaterialTheme.colors.error, modifier = Modifier.padding(8.dp))
        }

        Button(
            onClick = { viewModel.login() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading.value,
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFE53935))
        ) {
            Text(if (isLoading.value) "Logging in..." else "Login")
        }

        TextButton(onClick = onSignUpClick
            ) {
            Text(
                text = "Don't have an account? Sign Up",
                color = Color(0xFF000000) // Replace with your desired color
            )

        }
    }
}
