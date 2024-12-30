package tn.esprit.examaijetpack.ui.navigation

import androidx.compose.material.MaterialTheme
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import tn.esprit.examaijetpack.R

sealed class BottomNavItemStudent(val route: String, val icon: Int, val label: String) {
    object Home : BottomNavItemStudent("homeStudent", R.drawable.ic_home, "HomeStudent")
   // object Calendar : BottomNavItemStudent("calendar", R.drawable.ic_favorite, "Calendar")
    object Attempts : BottomNavItemStudent("attempts", R.drawable.ic_editor, "Attempts")
}

@Composable
fun BottomNavigationBarStudent(navController: NavController) {
    val items = listOf(
        BottomNavItemStudent.Home,
        //BottomNavItemStudent.Calendar,
        BottomNavItemStudent.Attempts
    )

    BottomNavigation(
        backgroundColor = MaterialTheme.colors.background,
        contentColor = MaterialTheme.colors.primary
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(painter = painterResource(id = item.icon), contentDescription = item.label) },
                label = { Text(text = item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                alwaysShowLabel = true,
                selectedContentColor = MaterialTheme.colors.primary,
                unselectedContentColor = Color.Gray
            )
        }
    }
}
