package tn.esprit.examaijetpack.ui.navigation

//noinspection UsingMaterialAndMaterial3Libraries
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

sealed class BottomNavItem(val route: String, val icon: Int, val label: String) {
    object Home : BottomNavItem("home", R.drawable.ic_home, "Home")
    object Editor : BottomNavItem("Edit/{id}/here you can edit your exam", R.drawable.ic_editor, "Editor")
    object Create : BottomNavItem("create", R.drawable.ic_create, "Create")
    object Favorites : BottomNavItem("favorites", R.drawable.ic_favorite, "Favorites")
    object Class : BottomNavItem("class", R.drawable.ic_class, "Class")
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Editor,
        BottomNavItem.Create,
        BottomNavItem.Favorites,
        BottomNavItem.Class
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