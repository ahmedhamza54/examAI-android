package tn.esprit.examaijetpack.ui.screens.student


import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import tn.esprit.examaijetpack.ui.viewmodels.HomeViewModel
import tn.esprit.examaijetpack.ui.viewmodels.Exam
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import tn.esprit.examaijetpack.ui.navigation.encode
import tn.esprit.examaijetpack.ui.screens.teacher.getTeacherId


@Composable
fun HomeScreenStudent(
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel()
) {
    val exams by homeViewModel.exams.collectAsState()
    val context = LocalContext.current
    val teacherIdFlow = getTeacherId(context)
    val teacherId by teacherIdFlow.collectAsState(initial = "Unknown")
    val grade = "7th"

    LaunchedEffect(Unit) {
        homeViewModel.fetchExamsByGrade(grade)
        //Log.d("teacherId", "teacherId: " + teacherId)
    }

    Scaffold(
        topBar = { HomeTopBarStudent(navController = navController) },
        //bottomBar = { BottomNavigationBarStudent(navController = navController) } // Optional navigation bar
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            WelcomeSectionStudent()

            Spacer(modifier = Modifier.height(16.dp))

            ExamsSection(exams = exams, navController = navController)
        }
    }
}

@Composable
fun HomeTopBarStudent(navController: NavController) {
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
            IconButton(onClick = { navController.navigate("profileStudent") }) { // Navigate to ProfileStudent
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Profile Icon",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Black // Optional: Set color
                )
            }
            IconButton(onClick = { /* TODO: Implement menu */ }) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Menu Icon",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Black // Optional: Set color
                )
            }
        },
        backgroundColor = Color.White, // Background color for the top bar
        contentColor = Color.Black
    )
}



@Composable
fun WelcomeSectionStudent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp) // Padding for the entire section
    ) {
        // Background Image with Title
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp) // Adjusted height for the image
                .border(width = 2.dp, color = Color.Black, shape = RoundedCornerShape(8.dp)) // Black frame with rounded corners
        ) {
            Image(
                painter = painterResource(id = R.drawable.home_bg),
                contentDescription = "Home Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(start = 16.dp, top = 16.dp), // Align text to the left with padding
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Home Page",
                    fontSize = 40.sp, // Larger font size
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Welcome Back!",
                    fontSize = 22.sp, // Slightly larger font size
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp)) // Reduced space between image and buttons

        // Buttons Below the Image
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp), // Space between buttons
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp), // Slight padding on the top
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Smaller Primary Button with Gradient Background
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp) // Smaller button height
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(Color(0xFFE53935), Color(0xFFFF7043)) // Gradient: Dark Red to Light Red
                        )
                        ,
                        shape = RoundedCornerShape(50) // Rounded corners
                    )
                    .clickable { /* TODO: Navigate to Add Exam */ },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+ Pass an exam",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            // Smaller Secondary Outlined Button
            OutlinedButton(
                onClick = { /* TODO: Navigate to View Class */ },
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp), // Matching smaller button height
                shape = RoundedCornerShape(50),
                border = BorderStroke(2.dp, Color(0xFFE53935)), // Red border
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFE53935) // Red text color
                )

            ) {
                Text(
                    text = "My exam attempts",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
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
            text = "Filtered by Grade",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (exams.isEmpty()) {
            Text("No exams available.", color = Color.Gray)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(exams) { exam ->
                    StudentExamItem(exam = exam, navController = navController)
                }
            }
        }
    }
}


@Composable
fun StudentExamItem(
    exam: Exam,
    navController: NavController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                // Navigate to the exam details screen
                navController.navigate("simplePdfViewer/${exam._id}/${exam.text.encode()}")
            }
    ) {
        // Placeholder for exam thumbnail or icon
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Display exam details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = exam.subject,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "Grade: ${exam.grade}",
                color = Color.Gray,
                fontSize = 12.sp
            )
            Text(
                text = "Chapters: ${exam.chapters.joinToString(", ")}",
                color = Color.Gray,
                fontSize = 12.sp
            )
            Text(
                text = "Difficulty: ${exam.difficultyLevel}",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Action buttons (if needed)
        IconButton(onClick = { /* TODO: Share exam */ }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_share),
                contentDescription = "Share"
            )
        }
    }
}

@Composable
fun BottomSheetViewButton(onViewExam: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = onViewExam,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF2196F3)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "View",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreenStudent() {
    HomeScreenStudent(navController = rememberNavController())
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeTopBarStudent() {
    HomeTopBarStudent(navController = rememberNavController())
}

@Preview(showBackground = true)
@Composable
fun PreviewWelcomeSectionStudent() {
    WelcomeSectionStudent()
}


@Preview(showBackground = true)
@Composable
fun PreviewExamsSectionStudent() {
    val sampleExams = listOf(
        tn.esprit.examaijetpack.ui.viewmodels.Exam(
            _id = "1", // Add this field for the unique ID
            teacherId = "1",
            subject = "Math Exam",
            grade = "A",
            chapters = listOf("Chapter 1", "Chapter 2"),
            difficultyLevel = 2,
            prompt = "Solve the problems.",
            text = "Math Exam Text" // Add this field for the exam text
        ),
        tn.esprit.examaijetpack.ui.viewmodels.Exam(
            _id = "2", // Add this field for the unique ID
            teacherId = "2",
            subject = "Physics Exam",
            grade = "B",
            chapters = listOf("Chapter 1", "Chapter 3"),
            difficultyLevel = 3,
            prompt = "Answer the questions.",
            text = "Physics Exam Text" // Add this field for the exam text
        )
    )
    ExamsSection(exams = sampleExams, navController = rememberNavController())
}

@Preview(showBackground = true)
@Composable
fun PreviewExamItemStudent() {
    val sampleExam = tn.esprit.examaijetpack.ui.viewmodels.Exam(
        _id = "1", // Add this field for the unique ID
        teacherId = "1",
        subject = "Math Exam",
        grade = "A",
        chapters = listOf("Chapter 1", "Chapter 2"),
        difficultyLevel = 2,
        prompt = "Solve the problems.",
        text = "Math Exam Text" // Add this field for the exam text
    )
    //ExamItemStudent(exam = sampleExam, navController = rememberNavController())
}



