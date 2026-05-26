package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.KimiViewModel
import com.example.ui.theme.*

enum class AppSubTab {
    SWIPE,
    CHATS,
    FRIENDS,
    PROFILE,
    RULES,
    ADMIN
}

@Composable
fun MainAppScreen(viewModel: KimiViewModel) {
    var tekuciTab by remember { mutableStateOf(AppSubTab.SWIPE) }
    val prijavljeniKorisnik by viewModel.prijavljeniKorisnik.collectAsState()
    val aktivniCaskalacId by viewModel.aktivniCaskalacId.collectAsState()

    val ulogovaniUser = prijavljeniKorisnik ?: return

    // Show Chat Detail if session is open
    if (aktivniCaskalacId != null) {
        ChatDetailScreen(
            viewModel = viewModel,
            partnerId = aktivniCaskalacId!!,
            onBack = { viewModel.postaviAktivnogCaskalca(null) }
        )
    } else {
        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = DarkNavySurface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    // 1. Swipe
                    NavigationBarItem(
                        selected = tekuciTab == AppSubTab.SWIPE,
                        onClick = { tekuciTab = AppSubTab.SWIPE },
                        icon = { Icon(Icons.Default.Favorite, contentDescription = "Upoznavanje") },
                        label = { Text("Upoznavanje", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            unselectedIconColor = KimiGray,
                            selectedTextColor = ElectricBlue,
                            unselectedTextColor = KimiGray,
                            indicatorColor = ElectricBlue
                        ),
                        modifier = Modifier.testTag("nav_swipe")
                    )

                    // 2. Chats
                    NavigationBarItem(
                        selected = tekuciTab == AppSubTab.CHATS,
                        onClick = { tekuciTab = AppSubTab.CHATS },
                        icon = { Icon(Icons.Default.Message, contentDescription = "Časkanja") },
                        label = { Text("Časkanja", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            unselectedIconColor = KimiGray,
                            selectedTextColor = ElectricBlue,
                            unselectedTextColor = KimiGray,
                            indicatorColor = ElectricBlue
                        ),
                        modifier = Modifier.testTag("nav_chats")
                    )

                    // 3. Friends
                    NavigationBarItem(
                        selected = tekuciTab == AppSubTab.FRIENDS,
                        onClick = { tekuciTab = AppSubTab.FRIENDS },
                        icon = { Icon(Icons.Default.People, contentDescription = "Prijatelji") },
                        label = { Text("Prijatelji", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            unselectedIconColor = KimiGray,
                            selectedTextColor = ElectricBlue,
                            unselectedTextColor = KimiGray,
                            indicatorColor = ElectricBlue
                        ),
                        modifier = Modifier.testTag("nav_friends")
                    )

                    // 4. Profile
                    NavigationBarItem(
                        selected = tekuciTab == AppSubTab.PROFILE,
                        onClick = { tekuciTab = AppSubTab.PROFILE },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Moj Profil") },
                        label = { Text("Profil", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            unselectedIconColor = KimiGray,
                            selectedTextColor = ElectricBlue,
                            unselectedTextColor = KimiGray,
                            indicatorColor = ElectricBlue
                        ),
                        modifier = Modifier.testTag("nav_profile")
                    )

                    // 5. Rules
                    NavigationBarItem(
                        selected = tekuciTab == AppSubTab.RULES,
                        onClick = { tekuciTab = AppSubTab.RULES },
                        icon = { Icon(Icons.Default.Rule, contentDescription = "Pravila") },
                        label = { Text("Pravila", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            unselectedIconColor = KimiGray,
                            selectedTextColor = ElectricBlue,
                            unselectedTextColor = KimiGray,
                            indicatorColor = ElectricBlue
                        ),
                        modifier = Modifier.testTag("nav_rules")
                    )

                    // 6. Admin Panel (Conditional display for Owner, Admin, Mod, Helper)
                    val imaPristupAdministraciji = remember(ulogovaniUser.uloga) {
                        ulogovaniUser.uloga in listOf("Vlasnik", "Admin", "Moderator", "Helper")
                    }
                    if (imaPristupAdministraciji) {
                        NavigationBarItem(
                            selected = tekuciTab == AppSubTab.ADMIN,
                            onClick = { tekuciTab = AppSubTab.ADMIN },
                            icon = { Icon(Icons.Default.Shield, contentDescription = "Admin") },
                            label = { Text("Admin", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                unselectedIconColor = KimiGray,
                                selectedTextColor = ElectricBlue,
                                unselectedTextColor = KimiGray,
                                indicatorColor = ElectricBlue
                            ),
                            modifier = Modifier.testTag("nav_admin")
                        )
                    }
                }
            },
            containerColor = DarkNavyBackground
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (tekuciTab) {
                    AppSubTab.SWIPE -> SwipeScreen(viewModel = viewModel)
                    AppSubTab.CHATS -> ChatListScreen(
                        viewModel = viewModel,
                        onChatSelected = { id -> viewModel.postaviAktivnogCaskalca(id) }
                    )
                    AppSubTab.FRIENDS -> FriendsScreen(
                        viewModel = viewModel,
                        onStartChat = { id -> viewModel.postaviAktivnogCaskalca(id) }
                    )
                    AppSubTab.PROFILE -> ProfileScreen(viewModel = viewModel)
                    AppSubTab.RULES -> RulesScreen()
                    AppSubTab.ADMIN -> AdminPanelScreen(viewModel = viewModel)
                }
            }
        }
    }
}
