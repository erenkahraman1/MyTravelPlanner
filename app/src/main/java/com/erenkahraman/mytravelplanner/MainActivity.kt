package com.erenkahraman.mytravelplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.erenkahraman.mytravelplanner.ui.theme.MyTravelPlannerTheme
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


class MainActivity : ComponentActivity() {
    companion object {
        val savedPlaces = mutableStateListOf<Place>().apply {
            add(Place("Test Place", 40.0, 29.0))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTravelPlannerTheme {
                val navController = rememberNavController()
                NavHost(navController, startDestination = "main") {
                    composable("main") { MainScreen(navController) }
                    composable("add_place") { AddPlaceScreen(navController) }
                    composable("map/{latitude}/{longitude}") { backStackEntry ->
                        val latitude = backStackEntry.arguments?.getString("latitude")?.toDoubleOrNull() ?: 41.0082
                        val longitude = backStackEntry.arguments?.getString("longitude")?.toDoubleOrNull() ?: 28.9784
                        val context = LocalContext.current
                        MapScreenWithTopBar(
                            context = context,
                            latitude = latitude,
                            longitude = longitude,
                            onBack = { navController.popBackStack() }
                        )
                    }

                }
            }
        }
    }
}

@Composable
fun MainScreen(navController: NavHostController) {
    var showNoteDialog by remember { mutableStateOf(false) }
    var noteDialogText by remember { mutableStateOf("") }
    var selectedPlaceForNote by remember { mutableStateOf<Place?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "My Travel Planner",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            navController.navigate("add_place")
        }) {
            Text("Add New Place")
        }

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn {
            items(MainActivity.savedPlaces) { place ->
                val expanded = remember { mutableStateOf<Boolean>(false) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = place.name)
                        Text(text = "Lat: ${place.latitude}, Lon: ${place.longitude}")

                        if (!place.note.isNullOrBlank()) {
                            Text(text = "Note: ${place.note}")
                        }
                    }
                    Box {
                        IconButton(onClick = { expanded.value = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                        }

                        DropdownMenu(
                            expanded = expanded.value,
                            onDismissRequest = { expanded.value = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Show on Map") },
                                onClick = {
                                    navController.navigate("map/${place.latitude}/${place.longitude}")
                                    expanded.value = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete") },
                                onClick = {
                                    MainActivity.savedPlaces.remove(place)
                                    expanded.value = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Add Photo") },
                                onClick = {
                                    // Henüz işlev eklenmedi, sonra yapılacak
                                    expanded.value = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Add Note") },
                                onClick = {
                                    noteDialogText = place.note
                                    selectedPlaceForNote = place
                                    showNoteDialog = true
                                    expanded.value = false
                                }
                            )

                        }
                    }
                }

                Divider(thickness = 1.dp)
            }
        }
        if (showNoteDialog && selectedPlaceForNote != null) {
            AlertDialog(
                onDismissRequest = {
                    showNoteDialog = false
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val index = MainActivity.savedPlaces.indexOf(selectedPlaceForNote)
                            if (index != -1) {
                                MainActivity.savedPlaces[index] =
                                    MainActivity.savedPlaces[index].copy(note = noteDialogText)
                            }
                            showNoteDialog = false
                        }
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showNoteDialog = false }) {
                        Text("Cancel")
                    }
                },
                title = { Text("Add Note") },
                text = {
                    OutlinedTextField(
                        value = noteDialogText,
                        onValueChange = { noteDialogText = it },
                        label = { Text("Note") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            )
        }
    }
}
