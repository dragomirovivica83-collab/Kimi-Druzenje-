package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.User
import com.example.ui.KimiViewModel
import com.example.ui.components.KimiAvatar
import com.example.ui.theme.*

@Composable
fun SwipeScreen(viewModel: KimiViewModel) {
    val kandidati by viewModel.getSwipeKandidati().collectAsState(initial = emptyList())
    var trenutniIndeks by remember { mutableStateOf(0) }
    var prikazanInfoDialog by remember { mutableStateOf<User?>(null) }
    var uparenKorisnik by remember { mutableStateOf<User?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBackground)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (trenutniIndeks < kandidati.size) {
            val korisnik = kandidati[trenutniIndeks]
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header of Swipe
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Upoznavanje 💖",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextLight
                    )
                    Text(
                        text = "Prevucite desno za lajk, levo za sledeći profil",
                        fontSize = 12.sp,
                        color = KimiGray,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Profile Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .border(1.dp, SecondaryNavySurface, RoundedCornerShape(24.dp))
                        .testTag("swipe_candidate_card")
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Background gradient for depth
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            DarkNavyBackground.copy(alpha = 0.85f),
                                            DarkNavyBackground
                                        )
                                    )
                                )
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Top part of card - Avatar
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                            ) {
                                KimiAvatar(
                                    avatarUrl = korisnik.avatarUrl,
                                    size = 140.dp
                                )
                            }

                            // Bottom part of card - Info
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "${korisnik.username}, ${korisnik.godine}",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextLight
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                if (korisnik.pol == "Muško") Color(0xFF0EA5E9).copy(alpha = 0.2f)
                                                else Color(0xFFEC4899).copy(alpha = 0.2f)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = korisnik.pol,
                                            color = if (korisnik.pol == "Muško") Color(0xFF38BDF8) else Color(0xFFF472B6),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = IceBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = korisnik.grad,
                                        fontSize = 14.sp,
                                        color = KimiGray,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = korisnik.biografija.ifBlank { "Korisnik nema napisanu biografiju." },
                                    fontSize = 14.sp,
                                    color = TextLight,
                                    maxLines = 3,
                                    lineHeight = 20.sp
                                )
                            }
                        }

                        // Info Icon button on top right of the card
                        IconButton(
                            onClick = { prikazanInfoDialog = korisnik },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Prikaži još detalja",
                                tint = KimiGray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Swipe Control Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // No Like (Dislike / Skip) Extra padding, touch size is high (56dp)
                    FilledIconButton(
                        onClick = { trenutniIndeks++ },
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = SecondaryNavySurface),
                        modifier = Modifier
                            .size(56.dp)
                            .testTag("swipe_dislike_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Preskoči profil",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Like (Sviđa mi se)
                    FilledIconButton(
                        onClick = {
                            viewModel.prevuciDesnoSvidjaMiSe(korisnik) {
                                uparenKorisnik = korisnik
                            }
                            trenutniIndeks++
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = ElectricBlue),
                        modifier = Modifier
                            .size(70.dp)
                            .testTag("swipe_like_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Sviđa mi se",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
        } else {
            // Empty State
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Nema više profila! ✨",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextLight
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Svi profili iz vaše blizine su pregledani. Vratite se kasnije ili pozovite prijatelje da se registruju!",
                    fontSize = 14.sp,
                    color = KimiGray,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { trenutniIndeks = 0 },
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryNavySurface)
                ) {
                    Text("Pogledaj ponovo", color = TextLight)
                }
            }
        }

        // Profile Info Dialog
        if (prikazanInfoDialog != null) {
            val user = prikazanInfoDialog!!
            AlertDialog(
                onDismissRequest = { prikazanInfoDialog = null },
                containerColor = DarkNavySurface,
                title = {
                    Text(
                        text = "${user.username}, ${user.godine}",
                        color = TextLight,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        KimiAvatar(avatarUrl = user.avatarUrl, size = 80.dp)
                        Text("Pol: ${user.pol}", color = TextLight, fontSize = 14.sp)
                        Text("Grad: ${user.grad}", color = TextLight, fontSize = 14.sp)
                        Divider(color = SecondaryNavySurface)
                        Text("Biografija:", color = IceBlue, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text(user.biografija.ifBlank { "Nema napisane biografije." }, color = TextLight, fontSize = 14.sp)
                    }
                },
                confirmButton = {
                    TextButton(onClick = { prikazanInfoDialog = null }) {
                        Text("Zatvori", color = ElectricBlue)
                    }
                }
            )
        }

        // Match Animation Popup
        if (uparenKorisnik != null) {
            val matched = uparenKorisnik!!
            AlertDialog(
                onDismissRequest = { uparenKorisnik = null },
                containerColor = SecondaryNavySurface,
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFD700))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SPARENI STE! 🥳", color = Color(0xFFFFD700), fontWeight = FontWeight.ExtraBold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFD700))
                    }
                },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Čestitamo! Uparili ste se sa korisnikom @${matched.username}!",
                            color = TextLight,
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp
                        )
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 12.dp)
                        ) {
                            KimiAvatar(avatarUrl = matched.avatarUrl, size = 90.dp)
                        }
                        Text(
                            text = "Automatska poruka dobrodošlice je poslata, možete odmah nastaviti ćaskanje u Čet listi!",
                            color = KimiGray,
                            textAlign = TextAlign.Center,
                            fontSize = 13.sp
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { uparenKorisnik = null },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("ZAPOČNI ČET 💬", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}
