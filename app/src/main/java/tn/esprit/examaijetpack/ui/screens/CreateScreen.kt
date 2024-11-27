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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import tn.esprit.examaijetpack.ui.Models.Exam
import tn.esprit.examaijetpack.ui.viewModels.CreateViewModel

@Composable
fun CreateScreen(
    onNavigateToRegenerate: (String, String) -> Unit,
    viewModel: CreateViewModel = viewModel()
) {
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
        viewModel.clearExamResponse() // Clear the response to avoid duplicate navigation
        onNavigateToRegenerate(id, text)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Create your exam preferences",
            style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Grade Selection
        Text("Grade", style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            val grades = listOf("7th", "8th", "9th")
            grades.forEach { grade ->
                Button(
                    onClick = { selectedGrade = grade
                        selectedSemester = null // Reset semester on grade change
                        selectedChapters = emptySet() // Reset chapters on grade change
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
                    text = "Semester",
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
                            onClick = { selectedSemester = semester
                                selectedChapters = emptySet() // Reset chapters on semester change
                                selectedGrade?.let { grade ->
                                    // Fetch chapters dynamically based on grade and semester
                                    viewModel.getChapters("Math", grade, semester)
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
                    text = "Chapters",
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    //val chapters = viewModel.chapters.collectAsState().value ?: emptyList()
                    // val chapters = listOf("Chapter 1", "Chapter 2", "Chapter 3", "Chapter 4", "Chapter 5")
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
                    text = "Key Words",
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                TextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    placeholder = { Text("Enter your prompt") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Gray.copy(alpha = 0.1f))
                )

                // Generate Exam Button
                Button(
                    onClick = {
                        // Trigger the ViewModel to send the request
                        viewModel.createExam(
                            Exam(
                                teacherId = "63f6c27f5d0f6e001fc7f2e1", // Replace with actual teacher ID
                                subject = "Math",    // Replace with actual subject
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
                dotsCount = (dotsCount % 5) + 1 // Cycle from 1 to 5
                kotlinx.coroutines.delay(500) // Adjust delay for speed of animation
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
