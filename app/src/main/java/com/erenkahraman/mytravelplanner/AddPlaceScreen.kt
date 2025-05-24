package com.erenkahraman.mytravelplanner

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment


@Composable
fun AddPlaceScreen() {
    // 📌 Kullanıcının yazdığı metni saklamak için state
    var placeName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Add a New Place",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 📝 Metin girişi (kullanıcıdan yer adı al)
        OutlinedTextField(
            value = placeName,
            onValueChange = { placeName = it },
            label = { Text("Place name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 💾 Kayıt butonu
        Button(
            onClick = {
                // Tıklanınca yapılacak işlem
                println("Place saved: $placeName")
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Save")
        }
    }
}