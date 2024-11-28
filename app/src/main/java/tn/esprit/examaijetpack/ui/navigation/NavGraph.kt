package tn.esprit.examaijetpack.ui.navigation

import CreateScreen
import RegenerateScreen
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import tn.esprit.examaijetpack.ui.screens.GetStartedScreen
import tn.esprit.examaijetpack.ui.screens.HomeScreen
import tn.esprit.examaijetpack.ui.screens.LoginScreen
import tn.esprit.examaijetpack.ui.screens.SignUpScreen
import tn.esprit.examaijetpack.ui.viewModels.LoginViewModel
import tn.esprit.examaijetpack.ui.viewModels.SignUpViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import androidx.compose.ui.Modifier

fun String.encode(): String = URLEncoder.encode(this, StandardCharsets.UTF_8.toString())

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = "getStarted", modifier = modifier) {
        composable("getStarted") {
            GetStartedScreen(navController = navController)
        }
        composable("login") {
            val viewModel: LoginViewModel = viewModel()
            val email by viewModel.email.observeAsState("")
            val password by viewModel.password.observeAsState("")
            val isLoading by viewModel.isLoading.observeAsState(false)
            val errorMessage by viewModel.errorMessage.observeAsState(null)
            val loginSuccess by viewModel.loginSuccess.observeAsState(false)

            // Navigate to home screen when login succeeds
            if (loginSuccess && navController.currentDestination?.route != "home") {
                navController.navigate("home") {
                    popUpTo(0) { inclusive = true } // Clears the entire backstack
                }
            }

            LoginScreen(
                email = email,
                password = password,
                isLoading = isLoading,
                errorMessage = errorMessage,
                onEmailChange = { viewModel.email.value = it },
                onPasswordChange = { viewModel.password.value = it },
                onLoginClick = { viewModel.login() },
                onSignUpClick = { navController.navigate("signup") }
            )
        }
        composable("home") {
            // Handle back button to exit the app from the home screen
            BackHandler {
                navController.popBackStack() // Exit the app when back is pressed on the home screen
            }

            HomeScreen(navController)
        }
        composable("signup") {
            val viewModel: SignUpViewModel = viewModel()
            val fullName by viewModel.name.observeAsState("")
            val email by viewModel.email.observeAsState("")
            val password by viewModel.password.observeAsState("")
            val confirmPassword by viewModel.confirmPassword.observeAsState("")
            val termsAccepted by viewModel.termsAccepted.observeAsState(false)
            val isLoading by viewModel.isLoading.observeAsState(false)
            val errorMessage by viewModel.errorMessage.observeAsState(null)
            val signUpSuccess by viewModel.signUpSuccess.observeAsState(false)

            if (signUpSuccess) {
                navController.navigate("home") {
                    popUpTo(0) { inclusive = true } // Clears the entire backstack
                }
            }

            SignUpScreen(
                fullName = fullName,
                email = email,
                password = password,
                confirmPassword = confirmPassword,
                isLoading = isLoading,
                errorMessage = errorMessage,
                termsAccepted = termsAccepted,
                onFullNameChange = { viewModel.name.value = it },
                onEmailChange = { viewModel.email.value = it },
                onPasswordChange = { viewModel.password.value = it },
                onConfirmPasswordChange = { viewModel.confirmPassword.value = it },
                onTermsAcceptedChange = { viewModel.setTermsAccepted(it) },
                onSignUpClick = { viewModel.signUp() }
            )
        }
        composable(
            route = "regenerate/{id}/{text}",
            arguments = listOf(
                navArgument("id") { type = NavType.StringType },
                navArgument("text") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            val text = backStackEntry.arguments?.getString("text") ?: ""
            RegenerateScreen(context = navController.context, examId = id, examText = text)
        }
        composable("editor") { /* TODO: Add EditorScreen */ }
        composable("create") {
            CreateScreen(
                onNavigateToRegenerate = { id, text ->
                    navController.navigate("regenerate/$id/${text.encode()}")
                }
            )
        }
        composable("favorites") { /* TODO: Add FavoritesScreen */ }
        composable("class") { /* TODO: Add ClassScreen */ }
    }
}
