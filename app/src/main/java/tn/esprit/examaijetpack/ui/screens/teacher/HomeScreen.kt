package tn.esprit.examaijetpack.ui.screens.teacher


import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import tn.esprit.examaijetpack.R

//@Preview
import androidx.compose.runtime.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import tn.esprit.examaijetpack.ui.navigation.encode
import tn.esprit.examaijetpack.ui.viewmodels.HomeViewModel
import tn.esprit.examaijetpack.ui.viewmodels.Exam


@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel()
) {
    val exams by homeViewModel.exams.collectAsState()
    val context = LocalContext.current
    val teacherIdFlow = getTeacherId(context)
    val teacherId by teacherIdFlow.collectAsState(initial = "Unknown")

    var selectedYear by remember { mutableStateOf("2024-2025") }

    LaunchedEffect(Unit) {
        homeViewModel.fetchExams(teacherId.toString())

        Log.d("teacherId", "teacherId: " + teacherId)
    }

    LaunchedEffect(homeViewModel.refreshTrigger.collectAsState()) {
        homeViewModel.fetchExams(teacherId.toString())
    }

    Scaffold(
        topBar = { HomeTopBar() },
        // bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            WelcomeSection()

            Spacer(modifier = Modifier.height(16.dp))

            YearSelector(
                selectedYear = selectedYear,
                onYearChange = { newYear -> selectedYear = newYear }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedYear == "2024-2025") {
                ExamsSection(exams = exams, navController = navController)
            } else {
                Text(
                    text = "No exams created.",
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun YearSelector(selectedYear: String, onYearChange: (String) -> Unit) {
    val minYear = "2022-2023"
    val maxYear = "2024-2025"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                val year = selectedYear.split("-")[0].toInt() - 1
                onYearChange("$year-${year + 1}")
            },
            enabled = selectedYear != minYear
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_left),
                contentDescription = "Previous Year",
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = selectedYear,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = {
                val year = selectedYear.split("-")[0].toInt() + 1
                onYearChange("$year-${year + 1}")
            },
            enabled = selectedYear != maxYear
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = "Next Year",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun HomeTopBar() {
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
            //IconButton(onClick = { /* TODO: Implement search */ }) {
               // Icon(painter = painterResource(id = R.drawable.ic_search), contentDescription = "Search")
            //}
          //  IconButton(onClick = { /* TODO: Implement menu */ }) {
                //  Icon(painter = painterResource(id = R.drawable.menu), contentDescription = "Menu")
            //}
        },
        backgroundColor = Color.White, // Background color for the top bar
        contentColor = Color.Black
    )
}


@Composable
fun WelcomeSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp) // Adjust height to fit your design
                .border(width = 2.dp, color = Color.Black) // Black frame
        ) {
            Image(
                painter = painterResource(id = R.drawable.home_bg),
                contentDescription = "Home Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Home Page",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Welcome Back!",
                fontSize = 16.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Button(
                    onClick = { /* TODO: Navigate to Add Exam */ },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFE53935)), // Red for primary (from LightColorScheme)
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(text = "+ Add to my Exams")
                }
                Button(
                    onClick = { /* TODO: Navigate to View Class */ },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.White), // White for secondary
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(text = "View My class", color = Color.Black) // Black text for white button
                }
            }
        }
    }
}

@Composable
fun ExamsSection(exams: List<Exam>, navController: NavController) {
    Column {
        Text(
            text = "Your Exams",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Within the Last 7 days",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (exams.isEmpty()) {
            Text("No exams available.", color = Color.Gray)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(), // Allows the column to fill available space
                contentPadding = PaddingValues(16.dp), // Padding around the entire column
                verticalArrangement = Arrangement.spacedBy(8.dp) // Space between items
            ) {
                items(exams) { exam ->
                    ExamItem(exam = exam, navController = navController)
                }
            }
        }

        Button(
            onClick = { /* TODO: Navigate to view all exams */ },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White), // White for secondary
            shape = RoundedCornerShape(50),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "View All Exams", color = Color.Black) // Black text for white button
        }
    }
}

@Composable
fun ExamItem(exam: Exam,navController: NavController,homeViewModel: HomeViewModel = viewModel()) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(Color.Gray, shape = RoundedCornerShape(8.dp))
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f).fillMaxWidth().clickable {
            // Navigate to the RegenerateScreen on item click
            navController.navigate("regenerate/${exam._id}/${exam.text.encode()}")
        }) {
            Text(text = exam.subject, fontWeight = FontWeight.Bold)
            Text(text = "Grade: ${exam.grade}", color = Color.Gray, fontSize = 12.sp)
            Text(text = "Difficulty: ${exam.difficultyLevel}", color = Color.Gray, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.width(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { /* TODO: Share */ }) {
                Icon(painter = painterResource(id = R.drawable.ic_share), contentDescription = "Share")
            }
            IconButton(onClick = {  showDeleteConfirmation = true }) {
                Icon(painter = painterResource(id = R.drawable.ic_delete), contentDescription = "Delete")
            }
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text(text = "Confirm Deletion") },
            text = { Text(text = "Are you sure you want to delete this exam?") },
            confirmButton = {
                TextButton(onClick = {
                    // Perform delete operation
                    homeViewModel.deleteExam(exam._id)
                    // Fetch the updated list of exams
                    homeViewModel.fetchExams(exam.teacherId)
                    // Close the dialog
                    showDeleteConfirmation = false
                }) {
                    Text(text = "Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    // Close the dialog without doing anything
                    showDeleteConfirmation = false
                }) {
                    Text(text = "No")
                }
            }
        )
    }
}
