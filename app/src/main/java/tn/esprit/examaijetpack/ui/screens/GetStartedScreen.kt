package tn.esprit.examaijetpack.ui.screens

import android.os.Environment
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import tn.esprit.examaijetpack.R
import java.io.File

@Composable
fun GetStartedScreen(navController: NavController) {
    val pagerState = rememberPagerState()
    val filePath = "/Downloads/dhia.pdf"
    val file = File(filePath)
    if (file.exists()) {
        Log.d("FileCheck", "File exists at $filePath")
    } else {
        Log.e("FileCheck", "File does not exist at $filePath")
    }
    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val pdfFile = File(downloadsDir, "dhia.pdf")
    if (pdfFile.exists()) {
        Log.d("FileCheck", "File found at: ${pdfFile.absolutePath}")
    } else {
        Log.e("FileCheck", "File does not exist at: ${pdfFile.absolutePath}")
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Swipeable Image Section with Description
            HorizontalPager(
                count = 3,
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val imageRes = when (page) {
                        0 -> R.drawable.exam_ai_logo
                        1 -> R.drawable.teacher_
                        else -> R.drawable.student
                    }
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = null,
                        modifier = Modifier
                            .size(200.dp)
                            .padding(8.dp)
                    )

                    val description = when (page) {
                        0 -> "Explore AI-powered exam solutions."
                        1 -> "Tools tailored for teachers."
                        else -> "Seamless experience for students."
                    }
                    Text(
                        text = description,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            HorizontalPagerIndicator(
                pagerState = pagerState,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally),
                activeColor = MaterialTheme.colors.primary,
                inactiveColor = MaterialTheme.colors.onSurface.copy(alpha = 0.4f)
            )

            Button(
                onClick = { navController.navigate("login") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
            ) {
                Text(
                    text = "GET STARTED",
                    color = MaterialTheme.colors.onPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            TextButton(
                onClick = { navController.navigate("login") }
            ) {
                Text(
                    text = "Already have an account? Log in",
                    color = MaterialTheme.colors.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GetStartedScreenPreview() {
    GetStartedScreen(navController = NavController(LocalContext.current))
}
