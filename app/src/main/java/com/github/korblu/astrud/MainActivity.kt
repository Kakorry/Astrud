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
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ShoppingCart
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

@Composable
fun AstrudApp() {
    AstrudTheme {
        Scaffold(
            bottomBar = {
                BottomAppBar(
                    actions = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(35.dp)
                            ) {
                                IconButton(onClick = { /* do something */ }) {
                                    Icon(
                                        Icons.Filled.Check,
                                        contentDescription = "Localized description"
                                    )
                                }
                                IconButton(onClick = { /* do something */ }) {
                                    Icon(
                                        Icons.Filled.Edit,
                                        contentDescription = "Localized description",
                                    )
                                }
                                IconButton(onClick = { /* do something */ }) {
                                    Icon(
                                        Icons.Filled.ShoppingCart,
                                        contentDescription = "Localized description",
                                    )
                                }
                                IconButton(onClick = { /* do something */ }) {
                                    Icon(
                                        Icons.Filled.Phone,
                                        contentDescription = "Localized description",
                                    )
                                }
                            }
                        }
                    }
                )
            }
        ) { innerPadding ->
            var rememberNav = rememberNavController()

            NavHost(
                navController = rememberNav,
                startDestination = "Welcome",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(
                    route = "Welcome",
                    enterTransition = { EnterTransition.None },
                    exitTransition = { ExitTransition.None })
                { AstrudWelcome(rememberNav) }
                composable(
                    route = "Home",
                    enterTransition = { fadeIn(animationSpec = tween(400)) },
                    exitTransition = { fadeOut(animationSpec = tween(400)) }
                ) { AstrudHome() }
            }
        }
    }
}