package com.erenkahraman.mytravelplanner

import android.content.Intent
import android.util.Log
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
import androidx.navigation.NavController

@Composable
fun MainScreen(navController: NavController) {
    val context = LocalContext.current
    val cameraImageUri = remember { mutableStateOf<Uri?>(null) }
    var showPhotoChoiceDialog by remember { mutableStateOf(false) }

    var showNoteDialog by remember { mutableStateOf(false) }
    var noteDialogText by remember { mutableStateOf("") }
    var selectedPlaceForNote by remember { mutableStateOf<Place?>(null) }
    var selectedPlaceForPhoto by remember { mutableStateOf<Place?>(null) } // ✅ BURADA

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
                    val uri = createImageUri(context)
                    cameraImageUri.value = uri
                    cameraLauncher.launch(uri)
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
                            Text(text = "Lat: ${place.latitude}, Lon: ${place.longitude}")
                        }

                        IconButton(onClick = {
                            MainActivity.savedPlaces.remove(place)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Place"
                            )
                        }
                    }
                }
            }
        }

    }
}
LaunchedEffect(Unit) {
    val apiKey = "42931a168b4d428f900b6c8f5e6479ff" // 🔑 kendi key’in
    val city = "Istanbul" // 💡 istersen dinamik yaparız

    val places = GeoapifyService.getPlacesByCity(city, apiKey)
    places.forEach {
        println("📍 Geoapify result: $it")
    }
}
