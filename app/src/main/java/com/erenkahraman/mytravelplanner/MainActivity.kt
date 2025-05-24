package com.erenkahraman.mytravelplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

import com.erenkahraman.mytravelplanner.ui.theme.MyTravelPlannerTheme
import com.erenkahraman.mytravelplanner.AddPlaceScreen
import com.erenkahraman.mytravelplanner.MapScreen



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTravelPlannerTheme {
                // Geoapify test konumu: Ayasofya, İstanbul
                MapScreen(
                    context = this,
                    latitude = 41.0082,
                    longitude = 28.9784
                )
            }
        }

    }
}

@Composable
fun MainScreen(navController: NavHostController) {
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
    }
}
