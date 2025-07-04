package com.github.korblu.astrud

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.korblu.astrud.ui.pages.AstrudHome
import com.github.korblu.astrud.ui.pages.AstrudWelcome
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
// You should listen to it. 05/25/2025

@Composable
fun AstrudAppBar() {
    BottomAppBar(
        modifier = Modifier.height(105.dp)
            .fillMaxWidth(),
        actions = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(55.dp)
                ) {
                    IconButton(onClick = { /* todo Make it do something */ }) {
                        Icon(
                            Icons.Filled.Home,
                            contentDescription = "Home"
                        )
                    }
                    IconButton(onClick = { /* todo Make it do something */ }) {
                        Icon(
                            Icons.Filled.Album,
                            contentDescription = "Albums",
                        )
                    }
                    IconButton(onClick = { /* todo Make it do something */ }) {
                        Icon(
                            Icons.Filled.LibraryMusic,
                            contentDescription = "Songs",
                        )
                    }
                    IconButton(onClick = { /* todo Make it do something */ }) {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = "User",
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun AstrudApp() {
    AstrudTheme {
        Scaffold(
            bottomBar = {
                AstrudAppBar()
            }
        ) { innerPadding ->
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "Welcome",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(
                    route = "Welcome",
                    enterTransition = { EnterTransition.None },
                    exitTransition = { ExitTransition.None })
                { AstrudWelcome(navController) }
                composable(
                    route = "Home",
                    enterTransition = { fadeIn(animationSpec = tween(400)) },
                    exitTransition = { fadeOut(animationSpec = tween(400)) }
                ) { AstrudHome(navController) }
            }
        }
    }
}