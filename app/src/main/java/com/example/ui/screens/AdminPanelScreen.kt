package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.data.model.Punishment
import com.example.ui.KimiViewModel
import com.example.ui.components.KimiAvatar
import com.example.ui.components.RoleBadge
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(viewModel: KimiViewModel) {
    val context = LocalContext.current
    val prijavljeniKorisnik by viewModel.prijavljeniKorisnik.collectAsState()
    val sviKorisnici by viewModel.sviKorisnici.collectAsState()
    val sveKazne by viewModel.sveKazne.collectAsState()

    val ulogovani = prijavljeniKorisnik ?: return

    // Screen tabs: 1. Korisnici (Users), 2. Dnevnik Kazni (Punishments Log)
    var selectedTab by remember { mutableStateOf(0) }
    var pretragaText by remember { mutableStateOf("") }

    // Dialog controllers
    var selektovanKorisnikZaKaznu by remember { mutableStateOf<User?>(null) }
    var razlogKazne by remember { mutableStateOf("") }
    var tipOdabraneKazne by remember { mutableStateOf("Upozori") }

    var selektovanKorisnikZaUlogu by remember { mutableStateOf<User?>(null) }
    var novaUlogaId by remember { mutableStateOf("Korisnik") }

    // Filtered user search list (exclude currently logged-in user and Kimi AI)
    val pretrazeniKorisnici = remember(sviKorisnici, pretragaText, ulogovani) {
        sviKorisnici.filter { u ->
            u.id != ulogovani.id &&
            u.id != 999 &&
            u.username.contains(pretragaText, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .background(DarkNavySurface)
                    .windowInsetsPadding(WindowInsets.safeDrawing)
            ) {
                TopAppBar(
                    title = { Text("Administracija 🛡️", color = TextLight, fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkNavySurface)
                )

                // Sub tabs: Korisnici vs Dnevnik
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = DarkNavySurface,
                    contentColor = ElectricBlue
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Korisnici", color = if (selectedTab == 0) ElectricBlue else KimiGray) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Dnevnik Kazni", color = if (selectedTab == 1) ElectricBlue else KimiGray) }
                    )
                }
            }
        },
        containerColor = DarkNavyBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (selectedTab == 0) {
                // TABS 0: USERS LIST & MOD TOOLS
                OutlinedTextField(
                    value = pretragaText,
                    onValueChange = { pretragaText = it },
                    placeholder = { Text("Pretraži korisnike...", color = KimiGray) },
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
                        .padding(bottom = 12.dp)
                )

                if (pretrazeniKorisnici.isEmpty()) {
                    Box(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Nema pronađenih korisnika.", color = KimiGray)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .testTag("admin_users_list")
                    ) {
                        items(pretrazeniKorisnici) { user ->
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
                                    KimiAvatar(avatarUrl = user.avatarUrl, size = 44.dp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = user.username,
                                            color = TextLight,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(top = 2.dp)
                                        ) {
                                            RoleBadge(uloga = user.uloga)
                                            StatusBadge(status = user.status)
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    // Action buttons depending on privileges
                                    // Vlasnik & Admin can change roles and punish
                                    if (privilegijaPromeneUloge(ulogovani.uloga, user.uloga)) {
                                        IconButton(
                                            onClick = { 
                                                novaUlogaId = user.uloga
                                                selektovanKorisnikZaUlogu = user 
                                            },
                                            modifier = Modifier.size(36.dp).background(SecondaryNavySurface, RoundedCornerShape(18.dp))
                                        ) {
                                            Icon(Icons.Default.ManageAccounts, contentDescription = "Uloga", tint = IceBlue, modifier = Modifier.size(16.dp))
                                        }

                                        Spacer(modifier = Modifier.width(6.dp))
                                    }

                                    if (privilegijaKaznjavanja(ulogovani.uloga, user.uloga)) {
                                        IconButton(
                                            onClick = { 
                                                razlogKazne = ""
                                                tipOdabraneKazne = "Upozori"
                                                selektovanKorisnikZaKaznu = user 
                                            },
                                            modifier = Modifier.size(36.dp).background(SecondaryNavySurface, RoundedCornerShape(18.dp))
                                        ) {
                                            Icon(Icons.Default.Gavel, contentDescription = "Kazni", tint = KimiRed, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // TABS 1: LOGS OF PUNISHMENTS
                if (sveKazne.isEmpty()) {
                    Box(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Dnevnik kazni je prazan. Svi korisnici poštuju kućni red! 🥰", color = KimiGray, textAlign = TextAlign.Center)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .testTag("punishments_log_list")
                    ) {
                        items(sveKazne) { k ->
                            val vremeString = remember(k.vreme) {
                                val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                                sdf.format(Date(k.vreme))
                            }
                            Card(
                                colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "@${k.targetUsername}",
                                            color = TextLight,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(
                                                    when (k.tipKazne) {
                                                        "Banuj" -> Color.Red.copy(alpha = 0.2f)
                                                        "Utišaj" -> Color.Magenta.copy(alpha = 0.2f)
                                                        "Upozori" -> Color.Yellow.copy(alpha = 0.2f)
                                                        else -> Color.Green.copy(alpha = 0.15f)
                                                    }
                                                )
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = k.tipKazne.uppercase(),
                                                color = when (k.tipKazne) {
                                                    "Banuj" -> Color.Red
                                                    "Utišaj" -> Color.Magenta
                                                    "Upozori" -> Color.Yellow
                                                    else -> Color.Green
                                                },
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = "Razlog: ${k.razlog}",
                                        color = TextLight,
                                        fontSize = 13.sp
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Divider(color = SecondaryNavySurface, thickness = 0.5.dp)

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Kaznio/la: @${k.moderatorUsername}",
                                            color = KimiGray,
                                            fontSize = 11.sp
                                        )
                                        Text(
                                            text = vremeString,
                                            color = KimiGray,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Apply Punishment Dialog
        if (selektovanKorisnikZaKaznu != null) {
            val user = selektovanKorisnikZaKaznu!!
            AlertDialog(
                onDismissRequest = { selektovanKorisnikZaKaznu = null },
                containerColor = DarkNavySurface,
                title = { Text("Izreci kaznu @${user.username} ⚖️", color = TextLight, fontWeight = FontWeight.Bold) },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
                        Text("Izaberite mjeru kazne:", color = KimiGray, fontSize = 13.sp)
                        
                        // Dropdown-like Selection Toggles
                        val dostupneKazne = when (ulogovani.uloga) {
                            "Vlasnik", "Admin" -> listOf("Upozori", "Utišaj", "Banuj", "Odblokiraj")
                            "Moderator" -> listOf("Upozori", "Utišaj")
                            else -> listOf("Upozori")
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            dostupneKazne.forEach { kazna ->
                                val selektovana = tipOdabraneKazne == kazna
                                Button(
                                    onClick = { tipOdabraneKazne = kazna },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (selektovana) KimiRed else DarkNavyBackground,
                                        contentColor = TextLight
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).height(38.dp),
                                    contentPadding = PaddingValues(2.dp)
                                ) {
                                    Text(kazna, fontSize = 11.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        OutlinedTextField(
                            value = razlogKazne,
                            onValueChange = { razlogKazne = it },
                            placeholder = { Text("Unesite zvanični razlog kazne...", color = KimiGray) },
                            maxLines = 4,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextLight,
                                unfocusedTextColor = TextLight,
                                focusedBorderColor = ElectricBlue,
                                unfocusedBorderColor = SecondaryNavySurface,
                                containerColor = DarkNavyBackground
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (razlogKazne.isBlank()) {
                                Toast.makeText(context, "Unesite razlog kazne!", Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.kazniKorisnika(user, tipOdabraneKazne, razlogKazne.trim(), ulogovani)
                                Toast.makeText(context, "Kazna uspešno izrečena!", Toast.LENGTH_SHORT).show()
                                selektovanKorisnikZaKaznu = null
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = KimiRed)
                    ) {
                        Text("Izvrši", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selektovanKorisnikZaKaznu = null }) {
                        Text("Zatvori", color = KimiGray)
                    }
                }
            )
        }

        // Change Users Role Dialog
        if (selektovanKorisnikZaUlogu != null) {
            val user = selektovanKorisnikZaUlogu!!
            AlertDialog(
                onDismissRequest = { selektovanKorisnikZaUlogu = null },
                containerColor = DarkNavySurface,
                title = { Text("Promeni ulogu za @${user.username}", color = TextLight, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Izaberite novu službenu ulogu:", color = KimiGray, fontSize = 13.sp)

                        val moguceUloge = when (ulogovani.uloga) {
                            "Vlasnik" -> listOf("Korisnik", "Helper", "Moderator", "Admin")
                            "Admin" -> listOf("Korisnik", "Helper", "Moderator")
                            else -> emptyList()
                        }

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth().height(160.dp)
                        ) {
                            items(moguceUloge) { uloga ->
                                val selektovana = novaUlogaId == uloga
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selektovana) ElectricBlue.copy(alpha = 0.2f) else DarkNavyBackground)
                                        .clickable { novaUlogaId = uloga }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = selektovana,
                                        onClick = { novaUlogaId = uloga },
                                        colors = RadioButtonDefaults.colors(selectedColor = ElectricBlue)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(uloga, color = TextLight, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.promeniUloguKorisnika(user.id, novaUlogaId)
                            Toast.makeText(context, "Uloga promenjena u $novaUlogaId!", Toast.LENGTH_SHORT).show()
                            selektovanKorisnikZaUlogu = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                    ) {
                        Text("Sačuvaj", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selektovanKorisnikZaUlogu = null }) {
                        Text("Zatvori", color = KimiGray)
                    }
                }
            )
        }
    }
}

// Privilege validation checkers
private fun privilegijaPromeneUloge(mojaUloga: String, njegovaUloga: String): Boolean {
    if (mojaUloga == "Vlasnik") return njegovaUloga != "Vlasnik"
    if (mojaUloga == "Admin") return njegovaUloga == "Korisnik" || njegovaUloga == "Helper" || njegovaUloga == "Moderator"
    return false
}

private fun privilegijaKaznjavanja(mojaUloga: String, njegovaUloga: String): Boolean {
    if (mojaUloga == "Vlasnik") return njegovaUloga != "Vlasnik"
    if (mojaUloga == "Admin") return njegovaUloga != "Vlasnik" && njegovaUloga != "Admin"
    if (mojaUloga == "Moderator") return njegovaUloga == "Korisnik" || njegovaUloga == "Helper"
    if (mojaUloga == "Helper") return njegovaUloga == "Korisnik"
    return false
}
