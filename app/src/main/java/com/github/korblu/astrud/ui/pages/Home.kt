package com.github.korblu.astrud.ui.pages

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.createBitmap
import androidx.navigation.NavController
import com.github.korblu.astrud.AstrudAppBar
import com.github.korblu.astrud.data.media.AudioFiles
import com.github.korblu.astrud.ui.theme.AstrudTheme

/*
    I can't tell if Jetpack Compose is really like this or I'm just unorganized.
    Feels like a mess. Maybe UI Toolkits are just like this?
    It is an intuitive to build mess, though. This is pretty fun. 05/23/2025
*/

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
    val context = LocalContext.current
    val audioFiles = remember { AudioFiles(context as Activity) }
    val getFolder = rememberLauncherForActivityResult(audioFiles.contractDocumentTree, audioFiles.onResult)

    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .padding(top = 20.dp)
                .heightIn(max = 280.dp)
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
                        var bitmap by remember { mutableStateOf(createBitmap(1, 1).asImageBitmap()) }
                        var click = 0
                        Image (
                            bitmap = bitmap,
                            contentDescription = "Album Cover",
                            modifier = Modifier
                                .height(90.dp)
                                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(topStart = roundUnit1, topEnd = roundUnit2, bottomStart = roundUnit3, bottomEnd = roundUnit4))
                                .clickable(
                                    enabled = true,
                                    // todo Fix whatever the fuck this is -K 06/06/2025
                                    // So true big K what the fuck is this -B 06/06/2025
                                    onClick = {
                                        if (click == 0) {
                                            getFolder.launch(null)
                                            click = 1
                                        } else {
                                            bitmap = audioFiles.getEmbeddedPic(context)?.asImageBitmap() ?: bitmap
                                            Log.d("test", audioFiles.getAllDefaultDirSongsMD(context).toString())
                                        }
                                    }
                                ),
                        )
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
fun Suggestions(flavorText: String) {
    Row(
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = flavorText,
            textAlign = TextAlign.Left,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            modifier = Modifier.padding(top = 25.dp, start = 15.dp)
        )
    }
    LazyRow(
        modifier = Modifier.padding(start = 15.dp, top = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        val suggestionList = List(10) { "Suggestion $it" }

        items(suggestionList) { song ->
            @Composable
            fun SongBox() {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(18.dp))
                        .width(130.dp)
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = song,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
            SongBox()
        }
    }
}

@Composable
fun AstrudHome(navController: NavController) {
    Scaffold(
        bottomBar = {
            AstrudAppBar(navController)
        }
    ) { innerPadding ->
        AstrudTheme {
            Surface(
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
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
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Speed Dial:",
                                textAlign = TextAlign.Left,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                modifier = Modifier.padding(top = 25.dp)
                            )
                        }
                    }

                    item {
                        AstrudDial()
                    }
                    // todo Actually make these different and interesting. 06/04/2025 -K
                    item {
                        Suggestions("Suggestions:")
                    }

                    item {
                        Suggestions("Recently Listened:")
                    }

                    item {
                        Suggestions("Recently Added:")
                    }
                }
            }
        }
    }
}

