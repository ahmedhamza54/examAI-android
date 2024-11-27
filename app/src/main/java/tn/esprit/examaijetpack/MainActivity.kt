package tn.esprit.examaijetpack

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import tn.esprit.examaijetpack.ui.navigation.BottomNavigationBar
import tn.esprit.examaijetpack.ui.navigation.NavGraph
import tn.esprit.examaijetpack.ui.theme.ExamAIjetpackTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyApp()
        }
    }
}

@Composable
fun MyApp() {
    ExamAIjetpackTheme {
        val navController = rememberNavController()
        MainScreen(navController)
    }
}

@Composable
fun MainScreen(navController: androidx.navigation.NavHostController) {
    // Define bottom navigation items
    val bottomNavItems = listOf(
        tn.esprit.examaijetpack.ui.navigation.BottomNavItem.Home,
        tn.esprit.examaijetpack.ui.navigation.BottomNavItem.Editor,
        tn.esprit.examaijetpack.ui.navigation.BottomNavItem.Create,
        tn.esprit.examaijetpack.ui.navigation.BottomNavItem.Favorites,
        tn.esprit.examaijetpack.ui.navigation.BottomNavItem.Class
    )

    // Get the current route
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            // Show bottom navigation only for specific routes
            if (currentRoute in bottomNavItems.map { it.route }) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

fun checkAndRequestPermissions(context: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                1
            )
        }
    } else {
        Log.d("Permissions", "No explicit storage permission needed for Android 11+.")
    }
}

@Composable
fun RequestPermissions() {
    val context = LocalContext.current
    val activity = context as? Activity

    LaunchedEffect(Unit) {
        activity?.let {
            checkAndRequestPermissions(it)
        }
    }
}
