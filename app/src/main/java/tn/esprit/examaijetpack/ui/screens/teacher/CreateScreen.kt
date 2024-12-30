package tn.esprit.examaijetpack.ui.screens.teacher

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import tn.esprit.examaijetpack.ui.Models.Exam
import tn.esprit.examaijetpack.ui.screens.login.dataStore
import tn.esprit.examaijetpack.ui.viewModels.CreateViewModel

@Composable
fun CreateScreen(
    onNavigateToRegenerate: (String, String) -> Unit,
    viewModel: CreateViewModel = viewModel()
) {
    val context = LocalContext.current

    // Shared Preferences
    val teacherIdFlow = getTeacherId(context)
    val teacherId by teacherIdFlow.collectAsState(initial = "Unknown")

    val specializationFlow = getSpecialization(context)
    val specialization by specializationFlow.collectAsState(initial = "Unknown")

    // UI States
    var selectedGrade by remember { mutableStateOf<String?>(null) }
    var selectedSemester by remember { mutableStateOf<String?>(null) }
    var selectedChapters by remember { mutableStateOf(setOf<String>()) }
    var difficulty by remember { mutableStateOf(5f) }
    var prompt by remember { mutableStateOf("") }

    // ViewModel States
    val isLoading by viewModel.isLoading.collectAsState()
    val examResponse by viewModel.examResponse.collectAsState()
    val chapters by viewModel.chapters.collectAsState()

    // Observe response from ViewModel
    examResponse?.let { (id, text) ->
        viewModel.clearExamResponse()
        onNavigateToRegenerate(id, text)
    }

    Scaffold(
        topBar = { HomeTopBar() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Create your exam preferences",
                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Grade Selection
            Text("niveau", style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                val grades = listOf("7th", "8th", "9th")
                grades.forEach { grade ->
                    Button(
                        onClick = {
                            selectedGrade = grade
                            selectedSemester = null
                            selectedChapters = emptySet()
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = if (selectedGrade == grade) Color.Red else Color.White,
                            contentColor = if (selectedGrade == grade) Color.White else Color.Black
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = grade)
                    }
                }
            }

            // Semester Selection
            AnimatedVisibility(
                visible = selectedGrade != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column {
                    Text(
                        text = "Trimestre",
                        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        val semesters = listOf("1st", "2nd", "3rd")
                        semesters.forEach { semester ->
                            Button(
                                onClick = {
                                    selectedSemester = semester
                                    selectedChapters = emptySet()
                                    selectedGrade?.let { grade ->
                                        viewModel.getChapters(specialization.toString(), grade, semester)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = if (selectedSemester == semester) Color.Red else Color.White,
                                    contentColor = if (selectedSemester == semester) Color.White else Color.Black
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = semester)
                            }
                        }
                    }
                }
            }

            // Rest of the Form
            AnimatedVisibility(
                visible = selectedSemester != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Chapters Selection
                    Text(
                        text = "Chapitres",
                        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(chapters) { chapter ->
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (selectedChapters.contains(chapter)) Color.Red else Color.White,
                                        shape = MaterialTheme.shapes.medium
                                    )
                                    .clickable {
                                        selectedChapters = if (selectedChapters.contains(chapter)) {
                                            selectedChapters - chapter
                                        } else {
                                            selectedChapters + chapter
                                        }
                                    }
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = chapter,
                                    color = if (selectedChapters.contains(chapter)) Color.White else Color.Black,
                                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                )
                            }
                        }
                    }

                    // Difficulty Slider
                    Text(
                        text = "Difficulty of your exam: ${difficulty.toInt()}",
                        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Slider(
                        value = difficulty,
                        onValueChange = { difficulty = it },
                        valueRange = 1f..10f,
                        steps = 9,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // Key Words Input
                    Text(
                        text = "suggestions",
                        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    TextField(
                        value = prompt,
                        onValueChange = { prompt = it },
                        placeholder = { Text("Écrivez ici toutes vos suggestions") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Gray.copy(alpha = 0.1f))
                    )

                    // Generate Exam Button
                    Button(
                        onClick = {
                            viewModel.createExam(
                                Exam(
                                    teacherId = teacherId.toString(), // Replace with actual teacher ID
                                    subject = specialization.toString(),    // Replace with actual subject
                                    grade = selectedGrade.orEmpty(),
                                    chapters = selectedChapters.toList(),
                                    difficultyLevel = difficulty.toInt(),
                                    prompt = prompt
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red, contentColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        Text(text = "Generate Exam")
                    }
                }
            }
        }

        // Loading Overlay
        if (isLoading) {
            var dotsCount by remember { mutableStateOf(1) }

            LaunchedEffect(isLoading) {
                while (isLoading) {
                    dotsCount = (dotsCount % 5) + 1
                    kotlinx.coroutines.delay(500)
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
}

fun getTeacherId(context: Context): Flow<String?> {
    val teacherIdKey = stringPreferencesKey("teacher_id")
    return context.dataStore.data.map { preferences ->
        preferences[teacherIdKey]
    }
}

fun getSpecialization(context: Context): Flow<String?> {
    val specializationKey = stringPreferencesKey("specialization")
    return context.dataStore.data.map { preferences ->
        preferences[specializationKey]
    }
}
