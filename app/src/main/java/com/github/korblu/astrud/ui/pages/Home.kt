package com.github.korblu.astrud.ui.pages

import android.app.Activity
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.github.korblu.astrud.data.media.AudioFiles
import com.github.korblu.astrud.ui.theme.AstrudTheme

// I can't tell if Jetpack Compose is really like this or I'm just unorganized.
// Feels like a mess. Maybe UI Toolkits are just like this?
// It is an intuitive to build mess, though. This is pretty fun. 05/23/2025

@Composable
fun AstrudHeader() {
    Row(
        modifier = Modifier.padding(top = 10.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Start
    ) {
        IconButton(
            onClick = {},
            modifier = Modifier
                .padding(start = 15.dp, top = 5.dp)
                .size(25.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Spacer(modifier = Modifier.weight(1f))

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

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            onClick = {},
            modifier = Modifier
                .padding(end = 15.dp, top = 5.dp)
                .size(25.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun AstrudDial() {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 20.dp)
                .heightIn(max = 320.dp)
                .widthIn(max = 310.dp)
        ) {
            val gridItems = List(9) { "Item $it" }

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .height(286.dp)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp),
                userScrollEnabled = false
            ) {
                items(gridItems) { item ->
                    @Composable
                    fun ImageBox(roundUnit1: Dp, roundUnit2: Dp, roundUnit3: Dp, roundUnit4: Dp) {
                        Box(
                            modifier = Modifier
                                .height(90.dp)
                                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(topStart = roundUnit1, topEnd = roundUnit2, bottomStart = roundUnit3, bottomEnd = roundUnit4)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }

                    when (item) {
                        "Item 0" -> {
                            ImageBox(12.dp, 0.dp, 0.dp, 0.dp)
                        }
                        "Item 2" -> {
                            ImageBox(0.dp, 12.dp, 0.dp, 0.dp)
                        }
                        "Item 6" -> {
                            ImageBox(0.dp, 0.dp, 12.dp, 0.dp)
                        }
                        "Item 8" -> {
                            ImageBox(0.dp, 0.dp, 0.dp, 12.dp)
                        }
                        else -> {
                            ImageBox(0.dp, 0.dp, 0.dp, 0.dp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AstrudHome(navController: NavController) {
    val context = LocalContext.current
    val audioFiles = remember { AudioFiles(context as Activity) }
    var dataText by remember { mutableStateOf("Metadata Test Button") }

    val launchTree =
        rememberLauncherForActivityResult(audioFiles.contractDocumentTree, audioFiles.createDir)
    val launchSingle =
        rememberLauncherForActivityResult(audioFiles.contractOpenDocument) { uri: Uri? ->
            if (uri != null) {
                dataText = audioFiles.getMetadata(context, uri).toString()
            }
        }

    AstrudTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
            ) {
                item {
                    AstrudHeader()
                }

                item {
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = "Speed Dial:",
                            textAlign = TextAlign.Left,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            modifier = Modifier.padding(top = 25.dp)
                        )

                        Spacer(modifier = Modifier.weight(1f))
                    }
                }

                item {
                    AstrudDial()
                }

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(
                            onClick = { launchTree.launch(null) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ) {
                            Text(text = "Testie Button")
                        }
                    }
                }

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(
                            onClick = { launchSingle.launch(arrayOf("audio/*")) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ) {
                            Text(text = dataText)
                        }
                    }
                }
            }
        }
    }
}

