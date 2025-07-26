package com.github.korblu.astrud.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.github.korblu.astrud.ui.viewmodels.AppBarViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Options() {
    var expanded by remember { mutableStateOf(false) }
    Row {
        IconButton(
            onClick = { expanded = !expanded }
        ) {
            Icon(
                imageVector = Icons.Filled.FilterAlt,
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = "Filter Songs"
            )
        }

        DropdownMenu(
            expanded = expanded,
            offset = DpOffset(20.dp, 0.dp),
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Sort by Title") },
                onClick = { /* todo Make it do something */ }
            )

            DropdownMenuItem(
                text = { Text("Sort by Artist") },
                onClick = { /* todo Make it do something */ }
            )

            DropdownMenuItem(
                text = { Text("Sort by Album") },
                onClick = { /* todo Make it do something */ }
            )
        }

        Spacer(Modifier.weight(2f))

        IconButton(
            onClick = { /* todo Make it do something */ }
        ) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = "More Options"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AstrudSongList(innerPadding: PaddingValues, scrollBehavior: TopAppBarScrollBehavior, barViewModel: AppBarViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .padding(innerPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        state = barViewModel.listBarState.collectAsState().value
    ) {
        item {
            Options()
        }

        val songs = List(40) { "Song $it" }

        items(songs) { song ->
            Box(
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp, top = 15.dp, bottom = 15.dp)
                    .heightIn(max = 70.dp)
                    .widthIn(max = 430.dp)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.onSecondary, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$song - Album",
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}