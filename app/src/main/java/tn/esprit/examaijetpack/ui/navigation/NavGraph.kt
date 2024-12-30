package tn.esprit.examaijetpack.ui.navigation

import tn.esprit.examaijetpack.ui.screens.teacher.CreateScreen
import tn.esprit.examaijetpack.ui.screens.teacher.RegenerateScreen
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
import tn.esprit.examaijetpack.ui.screens.login.GetStartedScreen
import tn.esprit.examaijetpack.ui.screens.teacher.HomeScreen
import tn.esprit.examaijetpack.ui.screens.login.LoginScreen
import tn.esprit.examaijetpack.ui.screens.login.SignUpScreen
import tn.esprit.examaijetpack.ui.viewModels.LoginViewModel
import tn.esprit.examaijetpack.ui.viewModels.SignUpViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import tn.esprit.examaijetpack.ui.screens.student.AnswerExamScreen
import tn.esprit.examaijetpack.ui.screens.student.ExamTextAreaScreen
import tn.esprit.examaijetpack.ui.screens.student.HomeScreenStudent
import tn.esprit.examaijetpack.ui.screens.student.ListExamAttemptsScreen
import tn.esprit.examaijetpack.ui.screens.student.PassExamPreview
import tn.esprit.examaijetpack.ui.screens.student.ProfileStudentPage
import tn.esprit.examaijetpack.ui.screens.student.SimplePdfViewerScreen
import tn.esprit.examaijetpack.ui.screens.teacher.EditScreen

fun String.encode(): String = URLEncoder.encode(this, StandardCharsets.UTF_8.toString())

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = "getStarted", modifier = modifier) {
        composable("getStarted") {
            GetStartedScreen(navController = navController)
           /* SimplePdfViewerScreen(examText = "Solve for \n" +
                    "\n" +
                    "3x+5=14\n" +
                    "\n" +
                    "Solve for \n" +
                    "\n" +
                    "2y−7=9", examId = "673dfb8610b9829c8e2fbc97", context = navController.context, navController = navController)
       */ }
        composable("login") {
            val loginViewModel: LoginViewModel = viewModel()
            val currentRoute = navController.currentDestination?.route
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    if (loginViewModel.studentId.value == "") {
                        if (currentRoute != "home") {
                            navController.navigate("home") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    } else
                    {
                        if (currentRoute != "homeStudent") {
                            navController.navigate("homeStudent") {
                            popUpTo(0) { inclusive = true }
                            }
                        }
                    }

                },
                onSignUpClick = { navController.navigate("signup") }
            )
        }
        composable("home") {
            // Handle back button to exit the app from the home screen
            BackHandler {
                navController.popBackStack() // Exit the app when back is pressed on the home screen
            }

            HomeScreen(navController,  homeViewModel = viewModel())
        }
        composable("homeStudent") {
            // Handle back button to exit the app from the home screen
            BackHandler {
                navController.popBackStack() // Exit the app when back is pressed on the home screen
            }

            HomeScreenStudent(navController)
        }
        composable("attempts") {
            // Handle back button to exit the app from the home screen
            BackHandler {
                navController.popBackStack() // Exit the app when back is pressed on the home screen
            }

            ListExamAttemptsScreen(navController, studentId = "67588f48560d1cdec95fc73f" )
        }

        composable("profileStudent") {
            // Handle back button to exit the app from the home screen
            BackHandler {
                navController.popBackStack() // Exit the app when back is pressed on the home screen
            }

            ProfileStudentPage(navController)
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
            RegenerateScreen(context = navController.context, examId = id, examText = text,navController = navController)
        }
        composable("Edit/{id}/{text}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: "ID123"
            val text = backStackEntry.arguments?.getString("text") ?: "here you can edit your exam"

            EditScreen(
                examId = id,
                initialText = text,
                onSaveSuccess = { updatedText ->
                    navController.navigate("regenerate/$id/${updatedText.encode()}") {
                        popUpTo("home"){ inclusive = false }

                    }
                }
            )
        }
        composable("create") {
            CreateScreen(
                onNavigateToRegenerate = { id, text ->
                    navController.navigate("regenerate/$id/${text.encode()}")
                }
            )
        }
        composable("favorites") { /* TODO: Add FavoritesScreen */ }
        composable("class") { /* TODO: Add ClassScreen */ }
        // New navigation for PassExamPreview
        composable(
            route = "simplePdfViewer/{examId}/{examText}",
            arguments = listOf(
                navArgument("examId") { type = NavType.StringType },
                navArgument("examText") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val examId = backStackEntry.arguments?.getString("examId") ?: ""
            val examText = backStackEntry.arguments?.getString("examText") ?: ""
            SimplePdfViewerScreen(
                examText = examText,
                examId = examId,
                context = LocalContext.current,
                navController = navController
            )
        }
        composable("answerExam/{id}",
            arguments = listOf(
                navArgument("id") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            AnswerExamScreen(navController = navController, examId = id)
        }

        composable(
            route = "examTextArea/{examId}/{examTextContent}",
            arguments = listOf(
                navArgument("examId") { type = NavType.StringType },
                navArgument("examTextContent") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val examId = backStackEntry.arguments?.getString("examId") ?: ""
            val examTextContent = backStackEntry.arguments?.getString("examTextContent") ?: ""
            ExamTextAreaScreen(
                examId = examId,
                examTextContent = examTextContent,
                navController = navController
            )
        }


    }

}




