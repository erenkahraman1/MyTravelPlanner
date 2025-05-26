package com.erenkahraman.mytravelplanner

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import androidx.compose.material.icons.filled.MoreVert
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MainScreen(navController: NavHostController) {
    val context = LocalContext.current
    val cameraImageUri = remember { mutableStateOf<Uri?>(null) }
    var showPhotoChoiceDialog by remember { mutableStateOf(false) }

    var showNoteDialog by remember { mutableStateOf(false) }
    var noteDialogText by remember { mutableStateOf("") }
    var selectedPlaceForNote by remember { mutableStateOf<Place?>(null) }
    var selectedPlaceForPhoto by remember { mutableStateOf<Place?>(null) }
    var showDateDialog by remember { mutableStateOf(false) }
    var selectedPlaceForDate by remember { mutableStateOf<Place?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val index = MainActivity.savedPlaces.indexOf(selectedPlaceForPhoto)
            if (index != -1) {
                MainActivity.savedPlaces[index] =
                    MainActivity.savedPlaces[index].copy(photoUri = it.toString())
            }
        }
    }
    if (showPhotoChoiceDialog && selectedPlaceForPhoto != null) {
        AlertDialog(
            onDismissRequest = { showPhotoChoiceDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    photoPickerLauncher.launch("image/*")
                    showPhotoChoiceDialog = false
                }) {
                    Text("Choose from Gallery")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPhotoChoiceDialog = false
                }) {
                    Text("Take a Photo")
                }
            },
            title = { Text("Select Photo") },
            text = { Text("Choose photo source:") }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("My Travel Planner", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            navController.navigate("add_place")
        }) {
            Text("Add New Place")
        }

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn {
            items(MainActivity.savedPlaces) { place ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            navController.navigate("map/${place.latitude}/${place.longitude}")
                        }

                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = place.name)

                            if (place.createdDate.isNotEmpty()) {
                                Text(
                                    text = "Added: ${place.createdDate}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }

                            if (!place.note.isNullOrBlank()) {
                                Text(
                                    text = "Note: ${place.note}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }

                        Box {
                            val expanded = remember { mutableStateOf(false) }
                            IconButton(onClick = { expanded.value = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                            }

                            DropdownMenu(
                                expanded = expanded.value,
                                onDismissRequest = { expanded.value = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Add Note") },
                                    onClick = {
                                        noteDialogText = place.note ?: ""
                                        selectedPlaceForNote = place
                                        showNoteDialog = true
                                        expanded.value = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Edit Date") },
                                    onClick = {
                                        selectedPlaceForDate = place
                                        showDateDialog = true
                                        expanded.value = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Add Photo") },
                                    onClick = {
                                        selectedPlaceForPhoto = place
                                        showPhotoChoiceDialog = true
                                        expanded.value = false
                                    }
                                )
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
                            }
                        }
                    }
                }
            }
        }
        if (showNoteDialog && selectedPlaceForNote != null) {
            AlertDialog(
                onDismissRequest = { showNoteDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        val index = MainActivity.savedPlaces.indexOf(selectedPlaceForNote)
                        if (index != -1) {
                            MainActivity.savedPlaces[index] =
                                MainActivity.savedPlaces[index].copy(note = noteDialogText)
                        }
                        showNoteDialog = false
                    }) {
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
        if (showDateDialog && selectedPlaceForDate != null) {
            var dateText by remember { mutableStateOf(selectedPlaceForDate?.createdDate ?: "") }

            AlertDialog(
                onDismissRequest = { showDateDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        val index = MainActivity.savedPlaces.indexOf(selectedPlaceForDate)
                        if (index != -1) {
                            MainActivity.savedPlaces[index] =
                                MainActivity.savedPlaces[index].copy(createdDate = dateText)
                        }
                        showDateDialog = false
                    }) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDateDialog = false }) {
                        Text("Cancel")
                    }
                },
                title = { Text("Edit Travel Date") },
                text = {
                    OutlinedTextField(
                        value = dateText,
                        onValueChange = { dateText = it },
                        label = { Text("Travel Date (dd/MM/yyyy HH:mm)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            )
        }
    }
}

