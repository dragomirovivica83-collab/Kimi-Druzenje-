package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RulesScreen() {
    val pravilaListe = listOf(
        PraviloPodatak(
            broj = "1. ",
            naslov = "Kulturno ponašanje 🤝",
            opis = "Strogo je zabranjeno vređanje, klevetanje, pretnje ili bilo kakav vid uznemiravanja i govora mržnje. Budite kulturni i poštujte druge.",
            icon = Icons.Default.Favorite,
            bojaIkone = Color(0xFFEF4444)
        ),
        PraviloPodatak(
            broj = "2. ",
            naslov = "Istiniti nalozi 👤",
            opis = "Zabranjeno je lažno predstavljanje, korišćenje tuđih fotografija i kreiranje višestrukih lažnih profila. Cenimo iskrenost i autentičnost.",
            icon = Icons.Default.Portrait,
            bojaIkone = Color(0xFF10B981)
        ),
        PraviloPodatak(
            broj = "3. ",
            naslov = "Održavanje privatnosti 🔒",
            opis = "Poštujte privatnost drugih korisnika. Zabranjeno je deljenje ili javno objavljivanje privatnih čatova, ličnih telefona ili slika bez pristanka.",
            icon = Icons.Default.Lock,
            bojaIkone = Color(0xFF0084FF)
        ),
        PraviloPodatak(
            broj = "4. ",
            naslov = "Zabrana spama i reklama 🚫",
            opis = "Slanje masovnih copy-paste poruka, linkova, reklamnih ponuda ili promocija drugih sajtova i aplikacija će rezultirati trajnim banom.",
            icon = Icons.Default.Cancel,
            bojaIkone = Color(0xFFF59E0B)
        ),
        PraviloPodatak(
            broj = "5. ",
            naslov = "Poštovanje administracije 🛡️",
            opis = "Naš tim (Vlasnik, Admini, Moderatori, Helperi) aktivno patrolira aplikacijom. Odluke tima o kaznama, utišavanju i banovima su konačne.",
            icon = Icons.Default.Shield,
            bojaIkone = Color(0xFF845EF7)
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pravila Zajednice 📜", color = TextLight, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkNavySurface),
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
            // Header Info Card
            Card(
                colors = CardDefaults.cardColors(containerColor = SecondaryNavySurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().testTag("rules_header")
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Kućni red Kimi Druženja",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Dobrodošli u našu zajednicu! Da bismo osigurali sigurno i prijatno okruženje za sve nas, molimo vas da se striktno pridržavate sledećih pravila:",
                        color = TextLight,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            }

            // Rules Cards List
            pravilaListe.forEach { pravilo ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("rule_${pravilo.naslov}")
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(pravilo.bojaIkone.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = pravilo.icon,
                                contentDescription = null,
                                tint = pravilo.bojaIkone,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = pravilo.naslov,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextLight
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = pravilo.opis,
                                fontSize = 13.sp,
                                color = KimiGray,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Kimi Druženje © 2026. Sva prava zadržana.",
                color = KimiGray,
                fontSize = 11.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
    }
}

data class PraviloPodatak(
    val broj: String,
    val naslov: String,
    val opis: String,
    val icon: ImageVector,
    val bojaIkone: Color
)
