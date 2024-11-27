package tn.esprit.examaijetpack.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SignUpScreen(
    fullName: String,
    email: String,
    password: String,
    confirmPassword: String,
    isLoading: Boolean,
    errorMessage: String?,
    termsAccepted: Boolean,
    onFullNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onTermsAcceptedChange: (Boolean) -> Unit,
    onSignUpClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Title
        Text(text = "Create Your Account", fontSize = 24.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
        Text(text = "Please fill this form", fontSize = 14.sp)

        Spacer(modifier = Modifier.height(16.dp))

        // Full Name
        OutlinedTextField(
            value = fullName,
            onValueChange = onFullNameChange,
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions.Default
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Email
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Your Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions.Default
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Password
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions.Default
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Confirm Password
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions.Default
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Terms and Conditions
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = termsAccepted,
                onCheckedChange = onTermsAcceptedChange
            )
            Text(text = "I accept the terms", modifier = Modifier.padding(start = 4.dp))
        }
        Text(
            text = "Read our T&Cs",
            fontSize = 12.sp,
            color = MaterialTheme.colors.primary,
            textDecoration = TextDecoration.Underline
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Error Message
        if (!errorMessage.isNullOrEmpty()) {
            Text(text = errorMessage, color = MaterialTheme.colors.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Save Button
        Button(
            onClick = onSignUpClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && termsAccepted
        ) {
            Text(if (isLoading) "Signing Up..." else "Save information")
        }
    }
}
