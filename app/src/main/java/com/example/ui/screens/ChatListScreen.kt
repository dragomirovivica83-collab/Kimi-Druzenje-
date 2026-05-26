package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.User
import com.example.ui.KimiViewModel
import com.example.ui.components.KimiAvatar
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(viewModel: KimiViewModel, onChatSelected: (Int) -> Unit) {
    val sviKorisnici by viewModel.sviKorisnici.collectAsState()
    val prijavljeniKorisnik by viewModel.prijavljeniKorisnik.collectAsState()
    
    var pretragaText by remember { mutableStateOf("") }

    // Chat list candidates: Everyone except ourselves and including Kimi AI.
    // Filter by search.
    val chatKorisnici = remember(sviKorisnici, prijavljeniKorisnik, pretragaText) {
        sviKorisnici.filter { user ->
            user.id != (prijavljeniKorisnik?.id ?: 0) &&
            user.status != "Banovan" &&
            (user.username.contains(pretragaText, ignoreCase = true) ||
             user.grad.contains(pretragaText, ignoreCase = true))
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .background(DarkNavyBackground)
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Messenger Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Časkanja 💬",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextLight
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Bar
                OutlinedTextField(
                    value = pretragaText,
                    onValueChange = { pretragaText = it },
                    placeholder = { Text("Pretraži ćaskanja...", color = KimiGray) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = KimiGray) },
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
                        .height(52.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Active Stories Row (Mini bubbles like Messenger)
                Text(
                    text = "Aktivni sada",
                    color = KimiGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(end = 16.dp)
                ) {
                    items(sviKorisnici.filter { it.id != (prijavljeniKorisnik?.id ?: 0) && it.status != "Banovan" }) { user ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { onChatSelected(user.id) }
                                .padding(2.dp)
                        ) {
                            KimiAvatar(
                                avatarUrl = user.avatarUrl,
                                size = 54.dp,
                                showActiveBadge = true // Messenger green badge
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (user.id == 999) "Asistent" else user.username,
                                color = TextLight,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.width(60.dp),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        },
        containerColor = DarkNavyBackground
    ) { innerPadding ->
        if (chatKorisnici.isEmpty()) {
            // Empty view
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .wrapContentSize(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Nema pronađenih ćaskanja 🤷‍♂️",
                    color = TextLight,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Probajte da izmenite reč za pretragu.",
                    color = KimiGray,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .testTag("chat_list")
            ) {
                item {
                    Divider(color = SecondaryNavySurface, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                }

                items(chatKorisnici) { user ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onChatSelected(user.id) }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                            .testTag("chat_item_${user.id}"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        KimiAvatar(
                            avatarUrl = user.avatarUrl,
                            size = 58.dp,
                            showActiveBadge = true
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = user.username + if(user.id == 999) " 🤖" else "",
                                    color = TextLight,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (user.id == 999) "AI asistent" else user.grad,
                                    color = KimiGray,
                                    fontSize = 11.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = user.biografija.ifBlank { "Klikni ovde za slanje poruke..." },
                                color = KimiGray,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}
