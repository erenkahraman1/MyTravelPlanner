package com.erenkahraman.mytravelplanner

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api



val cityCoordinates = mapOf(
    "Turkey" to mapOf(
        "Istanbul" to Pair(41.0082, 28.9784),
        "Ankara" to Pair(39.9208, 32.8541),
        "Izmir" to Pair(38.4192, 27.1287)
    ),
    "France" to mapOf(
        "Paris" to Pair(48.8566, 2.3522),
        "Lyon" to Pair(45.75, 4.85),
        "Nice" to Pair(43.7034, 7.2663)
    ),
    "Italy" to mapOf(
        "Rome" to Pair(41.9028, 12.4964),
        "Milan" to Pair(45.4642, 9.19),
        "Florence" to Pair(43.7696, 11.2558)
    )
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlaceScreen(navController: NavHostController) {

    var selectedLatLng by remember { mutableStateOf(Pair(0.0, 0.0)) }
    var placeName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Place") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            Text(
                text = "Select location and name below",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            val countries = listOf("Turkey", "France", "Italy")
            val citiesMap = mapOf(
                "Turkey" to listOf("Istanbul", "Ankara", "Izmir"),
                "France" to listOf("Paris", "Lyon", "Nice"),
                "Italy" to listOf("Rome", "Milan", "Florence")
            )

            var selectedCountry by remember { mutableStateOf(countries[0]) }
            var selectedCity by remember { mutableStateOf(citiesMap[countries[0]]!!.first()) }

            LaunchedEffect(Unit) {
                selectedLatLng = cityCoordinates[selectedCountry]?.get(selectedCity) ?: Pair(0.0, 0.0)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Select a Country")
            DropdownMenuBox(
                items = countries,
                selectedItem = selectedCountry,
                onItemSelected = {
                    selectedCountry = it
                    selectedCity = citiesMap[it]!!.first()
                    selectedLatLng = cityCoordinates[selectedCountry]?.get(selectedCity) ?: Pair(0.0, 0.0)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Select a City")
            DropdownMenuBox(
                items = citiesMap[selectedCountry] ?: emptyList(),
                selectedItem = selectedCity,
                onItemSelected = {
                    selectedCity = it
                    selectedLatLng = cityCoordinates[selectedCountry]?.get(it) ?: Pair(0.0, 0.0)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = placeName,
                onValueChange = { placeName = it },
                label = { Text("Place name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Selected Location Info:")
                    Text("Country: $selectedCountry")
                    Text("City: $selectedCity")
                    Text("Coordinates: ${selectedLatLng.first}, ${selectedLatLng.second}")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        navController.popBackStack()
                    }
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = {
                        if (placeName.isNotBlank()) {
                            MainActivity.savedPlaces.add(
                                Place(placeName, selectedLatLng.first, selectedLatLng.second)
                            )
                        }

                        navController.navigate("map/${selectedLatLng.first}/${selectedLatLng.second}")
                    },
                    enabled = placeName.isNotBlank()
                ) {
                    Text("Save & Show on Map")
                }
            }
        }
    } // ← AddPlaceScreen fonksiyonu burada kapanıyor
}

// 🔻 Fonksiyonun dışında olması gereken ek bileşen:
@Composable
fun DropdownMenuBox(
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(selectedItem)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

