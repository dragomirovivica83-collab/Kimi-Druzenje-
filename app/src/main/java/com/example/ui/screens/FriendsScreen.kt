package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.User
import com.example.ui.KimiViewModel
import com.example.ui.components.KimiAvatar
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(viewModel: KimiViewModel, onStartChat: (Int) -> Unit) {
    val context = LocalContext.current
    val prijavljeniKorisnik by viewModel.prijavljeniKorisnik.collectAsState()
    val sviKorisnici by viewModel.sviKorisnici.collectAsState()
    val prijateljstva by viewModel.prijateljstva.collectAsState()

    var korisnickoImeZaDodavanje by remember { mutableStateOf("") }
    var prikazanDijalogZaDodavanje by remember { mutableStateOf(false) }

    // Resolve separate lists
    val loggedInId = prijavljeniKorisnik?.id ?: 0

    // 1. Friends list (Accepted)
    val spisakPrijatelja = remember(prijateljstva, sviKorisnici, loggedInId) {
        prijateljstva.filter { it.status == "Prijatelji" }.mapNotNull { f ->
            val partnerId = if (f.posiljalacId == loggedInId) f.primalacId else f.posiljalacId
            val partner = sviKorisnici.find { it.id == partnerId }
            if (partner != null) partner to f else null
        }
    }

    // 2. Incoming Requests list (Sent to us and Status == Zahtev)
    val dolazniZahtevi = remember(prijateljstva, sviKorisnici, loggedInId) {
        prijateljstva.filter { it.primalacId == loggedInId && it.status == "Zahtev" }.mapNotNull { f ->
            val posiljalac = sviKorisnici.find { it.id == f.posiljalacId }
            if (posiljalac != null) posiljalac to f else null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Prijatelji 👥", color = TextLight, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkNavySurface),
                actions = {
                    // Floating button layout inside topbar options
                    IconButton(
                        onClick = { prikazanDijalogZaDodavanje = true },
                        modifier = Modifier.testTag("add_friend_icon_button")
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "Dodaj prijatelja", tint = IceBlue)
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
        ) {
            // Incoming Requests Section (If any exist)
            if (dolazniZahtevi.isNotEmpty()) {
                Text(
                    text = "Zahtevi za prijateljstvo (${dolazniZahtevi.size})",
                    color = IceBlue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(bottom = 16.dp)
                ) {
                    items(dolazniZahtevi) { (posiljalac, f) ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                KimiAvatar(avatarUrl = posiljalac.avatarUrl, size = 44.dp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = posiljalac.username,
                                        color = TextLight,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Iz: ${posiljalac.grad}",
                                        color = KimiGray,
                                        fontSize = 11.sp
                                    )
                                }
                                
                                // Accept
                                IconButton(
                                    onClick = { viewModel.prihvatiPrijatelja(f.id) },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(Color(0xFF22C55E), RoundedCornerShape(18.dp))
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = "Prihvati", tint = Color.White, modifier = Modifier.size(16.dp))
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Decline
                                IconButton(
                                    onClick = { viewModel.odbijIliObrisiPrijatelja(f) },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(KimiRed, RoundedCornerShape(18.dp))
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Odbij", tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
                
                Divider(color = SecondaryNavySurface, modifier = Modifier.padding(bottom = 16.dp))
            }

            // My Friends Section
            Text(
                text = "Moji prijatelji (${spisakPrijatelja.size})",
                color = KimiGray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            if (spisakPrijatelja.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Lista prijatelja je prazna 👥",
                        color = TextLight,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Idite na tab 'Upoznavanje' da pronađete partnere ili dodajte prijatelje po korisničkom imenu preko gornje ikonice!",
                        color = KimiGray,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp, horizontal = 16.dp),
                        lineHeight = 18.sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .testTag("friends_list")
                ) {
                    items(spisakPrijatelja) { (prijatelj, f) ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                KimiAvatar(avatarUrl = prijatelj.avatarUrl, size = 46.dp, showActiveBadge = true)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = prijatelj.username,
                                        color = TextLight,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${prijatelj.godine} god, ${prijatelj.grad}",
                                        color = KimiGray,
                                        fontSize = 12.sp
                                    )
                                }

                                // Message Button
                                IconButton(
                                    onClick = { onStartChat(prijatelj.id) },
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(ElectricBlue, RoundedCornerShape(19.dp))
                                ) {
                                    Icon(Icons.Default.Message, contentDescription = "Četuj", tint = Color.White, modifier = Modifier.size(16.dp))
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Remove Button
                                IconButton(
                                    onClick = { viewModel.odbijIliObrisiPrijatelja(f) },
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(SecondaryNavySurface, RoundedCornerShape(19.dp))
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Obriši prijatelja", tint = KimiRed, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Add Friend Dialog
        if (prikazanDijalogZaDodavanje) {
            AlertDialog(
                onDismissRequest = { prikazanDijalogZaDodavanje = false },
                containerColor = DarkNavySurface,
                title = { Text("Dodaj prijatelja ➕", color = TextLight, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "Unesite tačno korisničko ime člana sa kojim želite da se povežete:",
                            color = KimiGray,
                            fontSize = 13.sp
                        )
                        OutlinedTextField(
                            value = korisnickoImeZaDodavanje,
                            onValueChange = { korisnickoImeZaDodavanje = it },
                            placeholder = { Text("Korisničko ime", color = KimiGray) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextLight,
                                unfocusedTextColor = TextLight,
                                focusedBorderColor = ElectricBlue,
                                unfocusedBorderColor = SecondaryNavySurface,
                                containerColor = DarkNavyBackground
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("add_friend_username_field")
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (korisnickoImeZaDodavanje.isNotBlank()) {
                                viewModel.dodajPrijateljaPoImenu(korisnickoImeZaDodavanje.trim()) { poruka ->
                                    Toast.makeText(context, poruka, Toast.LENGTH_LONG).show()
                                }
                                korisnickoImeZaDodavanje = ""
                                prikazanDijalogZaDodavanje = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                    ) {
                        Text("Pošalji zahtev", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { prikazanDijalogZaDodavanje = false }) {
                        Text("Zatvori", color = KimiGray)
                    }
                }
            )
        }
    }
}
