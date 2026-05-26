package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.KimiViewModel
import com.example.ui.components.KimiAvatar
import com.example.ui.components.RoleBadge
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: KimiViewModel) {
    val context = LocalContext.current
    val prijavljeniKorisnik by viewModel.prijavljeniKorisnik.collectAsState()

    val ulogovani = prijavljeniKorisnik ?: return

    // Form states
    var korisnickoIme by remember { mutableStateOf(ulogovani.username) }
    var godineStr by remember { mutableStateOf(ulogovani.godine.toString()) }
    var grad by remember { mutableStateOf(ulogovani.grad) }
    var pol by remember { mutableStateOf(ulogovani.pol) }
    var biografija by remember { mutableStateOf(ulogovani.biografija) }
    var odabraniAvatar by remember { mutableStateOf(ulogovani.avatarUrl) }

    var prikazanaGalerijaAvatara by remember { mutableStateOf(false) }

    val presetAvatari = listOf(
        "preset_1", "preset_2", "preset_3", "preset_4", "preset_5", "preset_6"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Moj Profil 👤", color = TextLight, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkNavySurface),
                actions = {
                    IconButton(
                        onClick = { viewModel.odjaviSe() },
                        modifier = Modifier.testTag("logout_button")
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Odjavi se", tint = KimiRed)
                    }
                },
                modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing)
            )
        },
        containerColor = DarkNavyBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User Avatar Click to change
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .clickable { prikazanaGalerijaAvatara = true }
                    .testTag("avatar_profile_click")
            ) {
                KimiAvatar(avatarUrl = odabraniAvatar, size = 110.dp)
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(ElectricBlue)
                        .border(1.5.dp, DarkNavyBackground, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("⚙️", fontSize = 12.sp)
                }
            }

            // User Badges
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RoleBadge(uloga = ulogovani.uloga)
                StatusBadge(status = ulogovani.status)
            }

            // User Joined Meta
            val datumPridruzivanja = remember(ulogovani.registracijaVreme) {
                val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                sdf.format(Date(ulogovani.registracijaVreme))
            }
            Text(
                text = "Član od: $datumPridruzivanja",
                color = KimiGray,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Text Inputs
            OutlinedTextField(
                value = korisnickoIme,
                onValueChange = { korisnickoIme = it },
                label = { Text("Korisničko ime") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextLight,
                    unfocusedTextColor = TextLight,
                    focusedBorderColor = ElectricBlue,
                    unfocusedBorderColor = SecondaryNavySurface,
                    containerColor = DarkNavySurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_username_field")
            )

            OutlinedTextField(
                value = godineStr,
                onValueChange = { val clean = it.filter { c -> c.isDigit() }; if(clean.length <= 2) godineStr = clean },
                label = { Text("Godine") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextLight,
                    unfocusedTextColor = TextLight,
                    focusedBorderColor = ElectricBlue,
                    unfocusedBorderColor = SecondaryNavySurface,
                    containerColor = DarkNavySurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_age_field")
            )

            OutlinedTextField(
                value = grad,
                onValueChange = { grad = it },
                label = { Text("Grad / Mesto boravka") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextLight,
                    unfocusedTextColor = TextLight,
                    focusedBorderColor = ElectricBlue,
                    unfocusedBorderColor = SecondaryNavySurface,
                    containerColor = DarkNavySurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_location_field")
            )

            // Gender selector
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Pol:", color = KimiGray, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf("Muško", "Žensko").forEach { opcija ->
                        val odabran = pol == opcija
                        Button(
                            onClick = { pol = opcija },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (odabran) ElectricBlue else DarkNavySurface,
                                contentColor = if (odabran) Color.White else TextLight
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                        ) {
                            Text(opcija, fontSize = 13.sp)
                        }
                    }
                }
            }

            OutlinedTextField(
                value = biografija,
                onValueChange = { biografija = it },
                label = { Text("O meni (biografija)") },
                maxLines = 5,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextLight,
                    unfocusedTextColor = TextLight,
                    focusedBorderColor = ElectricBlue,
                    unfocusedBorderColor = SecondaryNavySurface,
                    containerColor = DarkNavySurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_bio_field")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Save Profiles Button (Touch target size high, min 48dp)
            Button(
                onClick = {
                    val g = godineStr.toIntOrNull() ?: ulogovani.godine
                    if (korisnickoIme.isNotBlank()) {
                        viewModel.azurirajProfil(
                            novoIme = korisnickoIme.trim(),
                            godine = g,
                            grad = grad.trim(),
                            pol = pol,
                            biografija = biografija.trim(),
                            avatarPreset = odabraniAvatar
                        )
                        Toast.makeText(context, "Profil je uspešno sačuvan!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Korisničko ime ne može biti prazno!", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_profile_button")
            ) {
                Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("SAČUVAJ IZMENE", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        // Avatar presets bottom gallery sheet dialog
        if (prikazanaGalerijaAvatara) {
            AlertDialog(
                onDismissRequest = { prikazanaGalerijaAvatara = false },
                containerColor = DarkNavySurface,
                title = { Text("Izaberi avatar iz galerije 🖼️", color = TextLight, fontWeight = FontWeight.Bold) },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Kliknite na sličicu avatara da zamenite trenutni profil:",
                            color = KimiGray,
                            fontSize = 13.sp
                        )
                        
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(presetAvatari) { preset ->
                                val selektovan = odabraniAvatar == preset
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .clickable {
                                            odabraniAvatar = preset
                                            prikazanaGalerijaAvatara = false
                                        }
                                        .border(
                                            width = if (selektovan) 3.dp else 1.dp,
                                            color = if (selektovan) ElectricBlue else Color.Transparent,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    KimiAvatar(avatarUrl = preset, size = 60.dp)
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { prikazanaGalerijaAvatara = false }) {
                        Text("Zatvori", color = ElectricBlue)
                    }
                }
            )
        }
    }
}
