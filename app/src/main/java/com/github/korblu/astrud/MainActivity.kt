package com.github.korblu.astrud

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.github.korblu.astrud.ui.pages.AstrudHome
import com.github.korblu.astrud.ui.pages.AstrudSongList
import com.github.korblu.astrud.ui.theme.AstrudTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AstrudApp()
        }
    }
}

// Unrelated, but did you know Astrud sung Girl from Ipanema?
// You should listen to it. -K 05/25/2025

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun astrudHeader(scrollBehavior: TopAppBarScrollBehavior): TopAppBarScrollBehavior {
    CenterAlignedTopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .size(25.dp)
                        .align(Alignment.CenterVertically)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.Top,
                ) {
                    Text(
                        text = "As",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "trud",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .padding(top = 2.dp, end = 5.dp)
                        .size(25.dp)
                        .align(Alignment.CenterVertically)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.inverseOnSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface
        ),
    )
    return scrollBehavior
}

@Composable
fun BottomBarIconButton(
    navController: NavController,
    targetRoute: String = "I'm gonna crash your ass!!",
    icon: ImageVector = Icons.Filled.Home,
    description: String = "Funny Little Place"
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val selectedPrimary = if (currentRoute == targetRoute) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    IconButton(
        onClick = {
            if (currentRoute != targetRoute) {
                navController.navigate(targetRoute) {
                    val startDestinationRoute = navController.graph.findStartDestination().route

                    popUpTo(startDestinationRoute ?: "Home") {
                        saveState = false
                    }

                    launchSingleTop = true
                    restoreState = true
                }
            }
        },
        content = {
            Icon(
                icon,
                description,
                tint = selectedPrimary
            )
        }
    )
}

@Composable
fun AstrudAppBar(navController: NavController) {
    BottomAppBar(
        modifier = Modifier
            .height(90.dp)
            .fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surface,
        actions = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(55.dp)
                ) {
                    BottomBarIconButton(navController, "Home", Icons.Filled.Home, "Home")
                    BottomBarIconButton(navController, "List", Icons.Filled.LibraryMusic, "Songs")
                    BottomBarIconButton(navController, icon = Icons.Filled.Album, description =  "Albums")
                    BottomBarIconButton(navController, icon = Icons.Filled.Person, description = "Artists")
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AstrudApp() {
    AstrudTheme {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        val navController = rememberNavController()

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        LaunchedEffect(navBackStackEntry?.destination) {
            scrollBehavior.state.heightOffset = 0f
            scrollBehavior.state.contentOffset = 0f
        }

        Scaffold(
            bottomBar = { AstrudAppBar(navController) },
            topBar = { astrudHeader(scrollBehavior) }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "Home",
            ) {
                composable(
                    "Home",
                    enterTransition = { fadeIn(animationSpec = tween(300)) + slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300)) },
                    exitTransition = { fadeOut(animationSpec = tween(300)) + slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300)) },
                    popEnterTransition = { fadeIn(animationSpec = tween(300)) + slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300)) },
                    popExitTransition = { fadeOut(animationSpec = tween(300)) + slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300)) }
                ) { AstrudHome(navController, innerPadding, scrollBehavior) }
                composable(
                    "List",
                    enterTransition = { fadeIn(animationSpec = tween(300)) + slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) },
                    exitTransition = { fadeOut(animationSpec = tween(300)) + slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) },
                    popEnterTransition = { fadeIn(animationSpec = tween(300)) + slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) },
                    popExitTransition = { fadeOut(animationSpec = tween(300)) + slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) }
                ) { AstrudSongList(innerPadding, scrollBehavior) }
            }
        }
    }
}




