package tn.esprit.examaijetpack.ui.screens.student

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import tn.esprit.examaijetpack.R

@Composable
fun ProfileStudentPage(navController: NavController) {
    var isDarkMode by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("English") }

    Scaffold(
        topBar = { TopAppBarWithLogoProfile(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .background(if (isDarkMode) Color(0xFF121212) else Color(0xFFF7F7F7))
        ) {
            // Profile Section
            ProfileImageSection()

            Spacer(modifier = Modifier.height(24.dp))

            // Profile Settings
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = 4.dp,
                backgroundColor = if (isDarkMode) Color(0xFF1E1E1E) else Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    DarkModeSwitch(isDarkMode = isDarkMode, onSwitchChange = { isDarkMode = it })
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    LanguageOption(selectedLanguage = selectedLanguage, onLanguageChange = { selectedLanguage = it })
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Privacy Button
            PrivacyAndCodeOfConductButton()

            Spacer(modifier = Modifier.weight(1f))

            // Log Out Button
            LogOutButton()
        }
    }
}

@Composable
fun TopAppBarWithLogoProfile(navController: NavController) {
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
        navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) { // Navigate back to the previous screen
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }
        },
        actions = {
            IconButton(onClick = { /* TODO: Implement search */ }) {
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
fun ProfileImageSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.4f), // Make this section take up only part of the screen (e.g., 40% of the screen height)
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.blankprofile),
                    contentDescription = "Profile Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "John Doe",
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = Color(0xFF424242)
            )

            Text(
                text = "Student ID: #123456",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun DarkModeSwitch(isDarkMode: Boolean, onSwitchChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Dark Mode",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Gray,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = isDarkMode,
            onCheckedChange = { onSwitchChange(it) },
            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF2196F3))
        )
    }
}

@Composable
fun LanguageOption(selectedLanguage: String, onLanguageChange: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Language",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Gray,
            modifier = Modifier.weight(1f)
        )
        TextButton(onClick = { onLanguageChange("French") }) {
            Text(
                text = selectedLanguage,
                color = Color(0xFF2196F3),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PrivacyAndCodeOfConductButton() {
    Button(
        onClick = { /* Navigate to Privacy */ },
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF2196F3))
    ) {
        Text(
            "Privacy and Code of Conduct",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun LogOutButton() {
    Button(
        onClick = { /* Log Out Action */ },
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFE53935))
    ) {
        Text(
            "Log Out",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewProfileStudentPage() {
    val navController = androidx.navigation.compose.rememberNavController()
    ProfileStudentPage(navController = navController)
}
