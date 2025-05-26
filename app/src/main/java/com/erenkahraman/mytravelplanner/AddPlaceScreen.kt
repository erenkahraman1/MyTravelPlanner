package com.erenkahraman.mytravelplanner

// AI assistance used for debugging UI issues and API integration guidance

import com.erenkahraman.mytravelplanner.GeoapifyService
import com.erenkahraman.mytravelplanner.GEOAPIFY_API_KEY
import com.erenkahraman.mytravelplanner.Place
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
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

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
    ),
    "Latvia" to mapOf(
        "Riga" to Pair(56.9496, 24.1052),
        "Daugavpils" to Pair(55.8747, 26.5345),
        "Liepaja" to Pair(56.5051, 21.0107)
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlaceScreen(navController: NavHostController) {
    var selectedLatLng by remember { mutableStateOf(Pair(0.0, 0.0)) }
    var placeName by remember { mutableStateOf("") }
    val countryList = listOf("Latvia", "France", "Italy", "Turkey")
    var selectedCountry by remember { mutableStateOf(countryList[0]) }
    var cityList by remember { mutableStateOf(listOf<String>()) }
    var selectedCity by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var poiList by remember { mutableStateOf(listOf<String>()) }
    var selectedPoi by remember { mutableStateOf("") }

    val countries = listOf("Turkey", "France", "Italy")
    val citiesMap = mapOf(
        "Turkey" to listOf("Istanbul", "Ankara", "Izmir"),
        "France" to listOf("Paris", "Lyon", "Nice"),
        "Italy" to listOf("Rome", "Milan", "Florence"),
        "Latvia" to listOf("Riga", "Daugavpils", "Liepaja")
    )
    var searchResults by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedAddress by remember { mutableStateOf<String?>(null) }
    var useApiSearch by remember { mutableStateOf(false) }
    var countrySearchQuery by remember { mutableStateOf("") }
    var citySearchQuery by remember { mutableStateOf("") }
    var poiSearchQuery by remember { mutableStateOf("") }
    var countryResults by remember { mutableStateOf<List<String>>(emptyList()) }
    var cityResults by remember { mutableStateOf<List<String>>(emptyList()) }
    var poiResults by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedCountryFromSearch by remember { mutableStateOf("") }
    var selectedCityFromSearch by remember { mutableStateOf("") }
    var selectedPOI by remember { mutableStateOf("") }

    LaunchedEffect(selectedCountry) {
        cityList = citiesMap[selectedCountry] ?: emptyList()
        if (cityList.isNotEmpty()) {
            selectedCity = cityList[0]
        }
    }

    LaunchedEffect(selectedCity) {
        if (selectedCity.isNotBlank()) {
            coroutineScope.launch {
                poiList = GeoapifyService.getPlacesByCity(selectedCity, GEOAPIFY_API_KEY)
                if (poiList.isNotEmpty()) {
                    selectedPoi = poiList[0]
                }
            }
        }
    }

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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    onClick = { useApiSearch = false },
                    label = { Text("Predefined Cities") },
                    selected = !useApiSearch
                )
                FilterChip(
                    onClick = { useApiSearch = true },
                    label = { Text("Search Places") },
                    selected = useApiSearch
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (useApiSearch) {
                Text("Search Country")
                OutlinedTextField(
                    value = countrySearchQuery,
                    onValueChange = { query ->
                        countrySearchQuery = query
                        if (query.length >= 2) {
                            coroutineScope.launch {
                                countryResults = GeoapifyService.searchCountries(query, GEOAPIFY_API_KEY)
                                println("Country search: $query, results: ${countryResults.size}")
                            }
                        }else {
                            countryResults = emptyList()
                        }
                    },
                    label = { Text("Type country name") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (countryResults.isNotEmpty()) {
                    println("Showing dropdown with ${countryResults.size} countries: $countryResults")
                    DropdownMenuBox(
                        items = countryResults,
                        selectedItem = selectedCountryFromSearch.ifEmpty { "Select a country" },
                        onItemSelected = { selected ->
                            selectedCountryFromSearch = selected
                            countrySearchQuery = selected
                            countryResults = emptyList()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (selectedCountryFromSearch.isNotEmpty()) {
                    Text("Search City in $selectedCountryFromSearch")
                    OutlinedTextField(
                        value = citySearchQuery,
                        onValueChange = { query ->
                            citySearchQuery = query
                            if (query.length >= 2) {
                                coroutineScope.launch {
                                    cityResults = GeoapifyService.searchCitiesByCountry(selectedCountryFromSearch, query, GEOAPIFY_API_KEY)
                                    println("City search: $query in $selectedCountryFromSearch, results: ${cityResults.size}")
                                }
                            }else {
                                cityResults = emptyList()
                            }
                        },
                        label = { Text("Type city name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (cityResults.isNotEmpty()) {
                        DropdownMenuBox(
                            items = cityResults,
                            selectedItem = selectedCityFromSearch.ifEmpty { "Select a city" },
                            onItemSelected = { selected ->
                                selectedCityFromSearch = selected
                                citySearchQuery = selected
                                cityResults = emptyList()
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (selectedCityFromSearch.isNotEmpty()) {
                    Text("Search Tourist Place in $selectedCityFromSearch")
                    OutlinedTextField(
                        value = poiSearchQuery,
                        onValueChange = { query ->
                            poiSearchQuery = query
                            if (query.length >= 2) {
                                coroutineScope.launch {
                                    poiResults = GeoapifyService.searchPOIByCity(selectedCityFromSearch, query, GEOAPIFY_API_KEY)
                                }
                            } else {
                                poiResults = emptyList()
                            }
                        },
                        label = { Text("Type place name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (poiResults.isNotEmpty()) {
                        DropdownMenuBox(
                            items = poiResults,
                            selectedItem = selectedPOI.ifEmpty { "Select a place" },
                            onItemSelected = { selected ->
                                selectedPOI = selected
                                poiSearchQuery = selected
                                placeName = selected
                                poiResults = emptyList()

                                coroutineScope.launch {
                                    val coords = GeoapifyService.getCoordinatesForPlace(selected, GEOAPIFY_API_KEY)
                                    selectedLatLng = coords
                                    println("POI coordinates: $coords")
                                }
                            }
                        )
                    }
                }
            }
            else {

                Text("Select a Country")

                DropdownMenuBox(
                    items = countryList,
                    selectedItem = selectedCountry,
                    onItemSelected = { selected ->
                        selectedCountry = selected
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (cityList.isNotEmpty()) {
                    Text("Select a City")

                    DropdownMenuBox(
                        items = cityList,
                        selectedItem = selectedCity,
                        onItemSelected = { selected ->
                            selectedCity = selected
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                OutlinedTextField(
                    value = placeName,
                    onValueChange = { placeName = it },
                    label = { Text("Place name") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            LaunchedEffect(selectedCountry, selectedCity) {
                if (!useApiSearch) {
                    selectedLatLng = cityCoordinates[selectedCountry]?.get(selectedCity) ?: Pair(0.0, 0.0)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Selected Location Info:")
                    if (!useApiSearch) {
                        Text("Country: $selectedCountry")
                        Text("City: $selectedCity")
                    }
                    Text("Place Name: $placeName")
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
                                Place(
                                    name = placeName,
                                    latitude = selectedLatLng.first,
                                    longitude = selectedLatLng.second,
                                    note = null,
                                    photoUri = null,
                                    createdDate = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
                                )
                            )
                        }
                        navController.navigate("map/${selectedLatLng.first}/${selectedLatLng.second}")
                    },
                    enabled = placeName.isNotBlank() && (selectedLatLng != Pair(0.0, 0.0) || !useApiSearch)
                ) {
                    Text("Save & Show on Map")
                }
            }
        }
    }
}

@Composable
fun DropdownMenuBox(
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = selectedItem.ifEmpty { "Select an option" },
                modifier = Modifier.weight(1f)
            )
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