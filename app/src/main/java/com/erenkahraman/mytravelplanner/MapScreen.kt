package com.erenkahraman.mytravelplanner

// Online resources used for Google Maps integration

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MapScreen(
    context: Context,
    latitude: Double,
    longitude: Double,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Selected Location:",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("Latitude: $latitude")
        Text("Longitude: $longitude")

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            val uri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.google.android.apps.maps")
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                val webUri = Uri.parse("https://www.google.com/maps?q=$latitude,$longitude")
                val webIntent = Intent(Intent.ACTION_VIEW, webUri)
                context.startActivity(webIntent)
            }
        }) {
            Text("Open in Google Maps")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { onBack() }) {
            Text("Go Back")
        }
    }
}