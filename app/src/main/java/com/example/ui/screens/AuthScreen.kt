package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.KimiViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(viewModel: KimiViewModel, onAuthSuccess: () -> Unit) {
    var isLoginMode by remember { mutableStateOf(true) }
    
    // Form Inputs
    var korisnickoIme by remember { mutableStateOf("") }
    var lozinka by remember { mutableStateOf("") }
    var godineStr by remember { mutableStateOf("") }
    var grad by remember { mutableStateOf("") }
    var pol by remember { mutableStateOf("Muško") }
    var biografija by remember { mutableStateOf("") }

    var lozinkaVidljiva by remember { mutableStateOf(false) }
    
    val authError by viewModel.authError.collectAsState()
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBackground)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo Icon
            Image(
                painter = painterResource(id = R.drawable.kimi_logo),
                contentDescription = "Kimi Druženje Logo",
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .clickable { focusManager.clearFocus() }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Kimi Druženje",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextLight,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Upoznajte i četujte sa novim ljudima",
                fontSize = 14.sp,
                color = KimiGray,
                modifier = Modifier.padding(top = 4.dp),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            // Form Title
            Text(
                text = if (isLoginMode) "Prijavi se" else "Napravi novi nalog",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextLight,
                modifier = Modifier.align(Alignment.Start)
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // Error Display
            if (authError != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = KimiRed.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Greška",
                            tint = KimiRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = authError ?: "",
                            color = KimiRed,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Korisničko ime (Username Input)
            OutlinedTextField(
                value = korisnickoIme,
                onValueChange = { korisnickoIme = it },
                label = { Text("Korisničko ime") },
                placeholder = { Text("Npr. milan_99") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = KimiGray) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextLight,
                    unfocusedTextColor = TextLight,
                    focusedBorderColor = ElectricBlue,
                    unfocusedBorderColor = SecondaryNavySurface,
                    focusedLabelColor = ElectricBlue,
                    unfocusedLabelColor = KimiGray,
                    containerColor = DarkNavySurface
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("username_input")
            )
            
            Spacer(modifier = Modifier.height(12.dp))

            // Lozinka (Password Input)
            OutlinedTextField(
                value = lozinka,
                onValueChange = { lozinka = it },
                label = { Text("Lozinka") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = KimiGray) },
                trailingIcon = {
                    val icon = if (lozinkaVidljiva) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { lozinkaVidljiva = !lozinkaVidljiva }) {
                        Icon(icon, contentDescription = "Togluj vidljivost lozinke", tint = KimiGray)
                    }
                },
                visualTransformation = if (lozinkaVidljiva) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextLight,
                    unfocusedTextColor = TextLight,
                    focusedBorderColor = ElectricBlue,
                    unfocusedBorderColor = SecondaryNavySurface,
                    focusedLabelColor = ElectricBlue,
                    unfocusedLabelColor = KimiGray,
                    containerColor = DarkNavySurface
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = if (isLoginMode) ImeAction.Done else ImeAction.Next
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("password_input")
            )

            // Additional Fields for Register Mode
            AnimatedVisibility(
                visible = !isLoginMode,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Godine (Age Input)
                    OutlinedTextField(
                        value = godineStr,
                        onValueChange = { val clean = it.filter { c -> c.isDigit() }; if(clean.length <= 2) godineStr = clean },
                        label = { Text("Godine starosti") },
                        leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = KimiGray) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextLight,
                            unfocusedTextColor = TextLight,
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = SecondaryNavySurface,
                            focusedLabelColor = ElectricBlue,
                            unfocusedLabelColor = KimiGray,
                            containerColor = DarkNavySurface
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("age_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Grad (Location / City Input)
                    OutlinedTextField(
                        value = grad,
                        onValueChange = { grad = it },
                        label = { Text("Grad / Mesto boravka") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = KimiGray) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextLight,
                            unfocusedTextColor = TextLight,
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = SecondaryNavySurface,
                            focusedLabelColor = ElectricBlue,
                            unfocusedLabelColor = KimiGray,
                            containerColor = DarkNavySurface
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("location_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Pol Selection (Male vs Female Toggle)
                    Text("Izaberite pol:", color = TextLight, fontSize = 14.sp, modifier = Modifier.padding(bottom = 6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        listOf("Muško", "Žensko").forEach { opcija ->
                            val odabran = pol == opcija
                            Button(
                                onClick = { pol = opcija },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (odabran) ElectricBlue else DarkNavySurface,
                                    contentColor = if (odabran) Color.White else TextLight
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                            ) {
                                Text(opcija)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Biografija / Opis (Bio Input)
                    OutlinedTextField(
                        value = biografija,
                        onValueChange = { biografija = it },
                        label = { Text("Biografija (nešto o vama)") },
                        placeholder = { Text("Npr. Volim sport, kafu i duga časkanja...") },
                        maxLines = 4,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextLight,
                            unfocusedTextColor = TextLight,
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = SecondaryNavySurface,
                            focusedLabelColor = ElectricBlue,
                            unfocusedLabelColor = KimiGray,
                            containerColor = DarkNavySurface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("bio_input")
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Button
            Button(
                onClick = {
                    focusManager.clearFocus()
                    if (isLoginMode) {
                        viewModel.prijaviSe(korisnickoIme, lozinka, onAuthSuccess)
                    } else {
                        val godine = godineStr.toIntOrNull() ?: 0
                        viewModel.registrujSe(korisnickoIme, lozinka, godine, grad, pol, biografija, onAuthSuccess)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_button")
            ) {
                Text(
                    text = if (isLoginMode) "PRIJAVI SE" else "NAPRAVI NALOG",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Switch Mode Text Link
            Text(
                text = if (isLoginMode) "Nemate nalog? Registrujte se" else "Već imate nalog? Prijavite se",
                color = IceBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clickable { isLoginMode = !isLoginMode; viewModel.prijaviSe("", "") } // clears state/errors
                    .padding(8.dp)
                    .testTag("switch_auth_mode")
            )
        }
    }
}
