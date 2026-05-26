package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.User
import com.example.ui.KimiViewModel
import com.example.ui.components.KimiAvatar
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    viewModel: KimiViewModel,
    partnerId: Int,
    onBack: () -> Unit
) {
    val sviKorisnici by viewModel.sviKorisnici.collectAsState()
    val prijavljeniKorisnik by viewModel.prijavljeniKorisnik.collectAsState()
    val poruke by viewModel.aktivnePoruke.collectAsState()
    
    val partner = remember(sviKorisnici, partnerId) {
        sviKorisnici.find { it.id == partnerId }
    }

    var tekstPoruke by remember { mutableStateOf("") }
    
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Select partner in session ViewModel
    LaunchedEffect(partnerId) {
        viewModel.postaviAktivnogCaskalca(partnerId)
    }

    // Scroll automatically when new messages arrive
    LaunchedEffect(poruke.size) {
        if (poruke.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(poruke.size - 1)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (partner != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            KimiAvatar(avatarUrl = partner.avatarUrl, size = 42.dp, showActiveBadge = true)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    partner.username,
                                    color = TextLight,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    if (partner.id == 999) "Aktivan sada (AI)" else "Aktivan sada",
                                    color = Color(0xFF22C55E),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    } else {
                        Text("Ćaskanje", color = TextLight)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { 
                        viewModel.postaviAktivnogCaskalca(null)
                        onBack() 
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Nazad",
                            tint = TextLight
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkNavySurface),
                modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing)
            )
        },
        bottomBar = {
            // Text Input Box (Messenger style)
            Surface(
                color = DarkNavySurface,
                tonalElevation = 8.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = tekstPoruke,
                        onValueChange = { tekstPoruke = it },
                        placeholder = { Text("Napišite poruku...", color = KimiGray) },
                        maxLines = 4,
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextLight,
                            unfocusedTextColor = TextLight,
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = SecondaryNavySurface,
                            containerColor = DarkNavyBackground
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_text_field"),
                        singleLine = false
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Floating Round Send button (High accessibility, min touch area: 48dp)
                    IconButton(
                        onClick = {
                            if (tekstPoruke.isNotBlank()) {
                                viewModel.posaljiPoruku(tekstPoruke.trim())
                                tekstPoruke = ""
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(ElectricBlue, RoundedCornerShape(24.dp))
                            .testTag("chat_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Pošalji poruku",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        },
        containerColor = DarkNavyBackground
    ) { innerPadding ->
        if (partner == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Korisnik nije pronađen.", color = TextLight)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (poruke.isEmpty()) {
                    // Chat Empty State
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        KimiAvatar(avatarUrl = partner.avatarUrl, size = 90.dp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Započnite razgovor sa @${partner.username}! 👋",
                            color = TextLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Pošaljite poruku da probijete led. Budite kulturni i poštujte pravila Kimi zajednice.",
                            color = KimiGray,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .testTag("chat_messages_list"),
                        contentPadding = PaddingValues(vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(poruke) { poruka ->
                            val isMe = poruka.posiljalacId == (prijavljeniKorisnik?.id ?: 0)
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                            ) {
                                if (!isMe) {
                                    KimiAvatar(avatarUrl = partner.avatarUrl, size = 32.dp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                }

                                Column(
                                    horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                                ) {
                                    // Message Card bubble
                                    Surface(
                                        color = if (isMe) ElectricBlue else SecondaryNavySurface,
                                        shape = RoundedCornerShape(
                                            topStart = 16.dp,
                                            topEnd = 16.dp,
                                            bottomStart = if (isMe) 16.dp else 4.dp,
                                            bottomEnd = if (isMe) 4.dp else 16.dp
                                        ),
                                        modifier = Modifier.widthIn(max = 260.dp)
                                    ) {
                                        Text(
                                            text = poruka.tekst,
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
                                        )
                                    }
                                    
                                    // Timestamp
                                    val vremeString = remember(poruka.vreme) {
                                        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                                        sdf.format(Date(poruka.vreme))
                                    }
                                    Text(
                                        text = vremeString,
                                        color = KimiGray,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
