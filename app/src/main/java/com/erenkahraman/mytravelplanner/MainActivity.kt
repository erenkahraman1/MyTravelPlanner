package com.erenkahraman.mytravelplanner

// AI assistance used for debugging and navigation setup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.erenkahraman.mytravelplanner.ui.theme.MyTravelPlannerTheme
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.erenkahraman.mytravelplanner.MapScreen



class MainActivity : ComponentActivity() {
    companion object {
        val savedPlaces = mutableStateListOf<Place>().apply {
            add(Place("Test Place", 40.0, 29.0, null, null, "27/05/2025 10:00"))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTravelPlannerTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "main") {
                    composable("main") {
                        MainScreen(navController)
                    }
                    composable("add_place") {
                        AddPlaceScreen(navController)
                    }
                    composable("map/{latitude}/{longitude}") { backStackEntry ->
                        val latitude = backStackEntry.arguments?.getString("latitude")?.toDoubleOrNull() ?: 41.0082
                        val longitude = backStackEntry.arguments?.getString("longitude")?.toDoubleOrNull() ?: 28.9784
                        val context = androidx.compose.ui.platform.LocalContext.current

                        MapScreen(
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