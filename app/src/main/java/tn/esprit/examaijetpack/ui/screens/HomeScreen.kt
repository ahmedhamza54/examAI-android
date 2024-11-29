package tn.esprit.examaijetpack.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import tn.esprit.examaijetpack.R
import tn.esprit.examaijetpack.ui.navigation.BottomNavigationBar

//@Preview
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import tn.esprit.examaijetpack.ui.viewmodels.HomeViewModel
import tn.esprit.examaijetpack.ui.viewmodels.Exam

@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel()
) {
    val exams by homeViewModel.exams.collectAsState()

    LaunchedEffect(Unit) {
        homeViewModel.fetchExams()
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

            ExamsSection(exams = exams)
        }
    }
}
@Composable
fun HomeTopBar() {
    TopAppBar(
        title = {
            Text("ExamAI", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        },
        actions = {
            IconButton(onClick = { /* TODO: Implement search */ }) {
                Icon(painter = painterResource(id = R.drawable.ic_search), contentDescription = "Search")
            }
        },
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = Color.White
    )
}

@Composable
fun WelcomeSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        contentAlignment = Alignment.Center
    ) {
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
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(text = "+ Add to my Exams")
                }
                Button(
                    onClick = { /* TODO: Navigate to View Class */ },
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary)
                ) {
                    Text(text = "View My class")
                }
            }
        }
    }
}

@Composable
fun ExamsSection(exams: List<Exam>) {
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
                    ExamItem(exam = exam)
                }
            }
        }

        Button(
            onClick = { /* TODO: Navigate to view all exams */ },
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary),
            shape = RoundedCornerShape(50),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "View All Exams")
        }
    }
}

@Composable
fun ExamItem(exam: Exam) {
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

        Column(modifier = Modifier.weight(1f)) {
            Text(text = exam.subject, fontWeight = FontWeight.Bold)
            Text(text = "Grade: ${exam.grade}", color = Color.Gray, fontSize = 12.sp)
            Text(text = "Difficulty: ${exam.difficultyLevel}", color = Color.Gray, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.width(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { /* TODO: Edit */ }) {
                Icon(painter = painterResource(id = R.drawable.ic_edit), contentDescription = "Edit")
            }
            IconButton(onClick = { /* TODO: Share */ }) {
                Icon(painter = painterResource(id = R.drawable.ic_share), contentDescription = "Share")
            }
            IconButton(onClick = { /* TODO: Delete */ }) {
                Icon(painter = painterResource(id = R.drawable.ic_delete), contentDescription = "Delete")
            }
        }
    }
}