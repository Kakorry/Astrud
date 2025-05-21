package com.github.korblu.astrud

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.korblu.astrud.ui.pages.AstrudWelcome
import com.github.korblu.astrud.ui.pages.HomePage

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
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "WelcomePage") {
        composable("WelcomePage") { AstrudWelcome(navController) }
        composable("HomeScreen") { HomePage() }
    }
}