package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

@Composable
fun KimiAvatar(
    avatarUrl: String,
    modifier: Modifier = Modifier,
    size: Dp = 50.dp,
    showActiveBadge: Boolean = false
) {
    val gradient = when (avatarUrl) {
        "preset_1" -> Brush.linearGradient(listOf(Color(0xFFFF6B6B), Color(0xFFFF8E53)))
        "preset_2" -> Brush.linearGradient(listOf(Color(0xFF845EF7), Color(0xFF5C7CFA)))
        "preset_3" -> Brush.linearGradient(listOf(Color(0xFF339AF0), Color(0xFF22B8CF)))
        "preset_4" -> Brush.linearGradient(listOf(Color(0xFF51CF66), Color(0xFF94D82D)))
        "preset_5" -> Brush.linearGradient(listOf(Color(0xFFFCC419), Color(0xFFFF922B)))
        "preset_6" -> Brush.linearGradient(listOf(Color(0xFFF06595), Color(0xFFE64980)))
        "preset_owner" -> Brush.linearGradient(listOf(Color(0xFFFFD700), Color(0xFFFFA500)))
        "preset_admin" -> Brush.linearGradient(listOf(Color(0xFFE11D48), Color(0xFF9F1239)))
        "preset_mod" -> Brush.linearGradient(listOf(Color(0xFF0084FF), Color(0xFF3B82F6)))
        "preset_helper" -> Brush.linearGradient(listOf(Color(0xFF10B981), Color(0xFF047857)))
        else -> Brush.linearGradient(listOf(Color(0xFF475569), Color(0xFF1E293B)))
    }

    val emoji = when (avatarUrl) {
        "preset_1" -> "👩‍🦰" // Milica
        "preset_2" -> "👱‍♀️" // Jelena
        "preset_3" -> "👦" // Nikola
        "preset_4" -> "🎸" // Stefan
        "preset_5" -> "👧" // Marija
        "preset_6" -> "👩" // Sandra
        "preset_owner" -> "👑"
        "preset_admin" -> "🛡️"
        "preset_mod" -> "⚔️"
        "preset_helper" -> "🩺"
        else -> "👤"
    }

    Box(
        modifier = modifier
            .size(size)
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        if (avatarUrl == "preset_0" || avatarUrl.contains("kimi_logo")) {
            // Special logo for Kimi AI
            Image(
                painter = painterResource(id = R.drawable.kimi_logo),
                contentDescription = "Kimi AI Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .border(1.5.dp, ElectricBlue, CircleShape)
            )
        } else {
            // Beautiful stylized circular profile icon
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(gradient)
                    .border(1.dp, Color(0x33FFFFFF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = emoji,
                    fontSize = (size.value * 0.45f).sp,
                    lineHeight = (size.value * 0.5f).sp
                )
            }
        }

        if (showActiveBadge) {
            Box(
                modifier = Modifier
                    .size(size * 0.28f)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(Color(0xFF22C55E)) // Green dot
                    .border(1.5.dp, DarkNavyBackground, CircleShape)
            )
        }
    }
}

@Composable
fun RoleBadge(uloga: String) {
    val (backgroundColor, textColor) = when (uloga) {
        "Vlasnik" -> Color(0xFFFFD700) to Color(0xFF0F172A)
        "Admin" -> Color(0xFFEF4444) to Color(0xFFFFFFFF)
        "Moderator" -> Color(0xFF3B82F6) to Color(0xFFFFFFFF)
        "Helper" -> Color(0xFF10B981) to Color(0xFFFFFFFF)
        else -> Color(0xFF475569) to Color(0xFFF1F5F9)
    }

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = uloga,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun StatusBadge(status: String) {
    val (backgroundColor, label) = when (status) {
        "Utišan" -> Color(0xFFF59E0B) to "UTIŠAN"
        "Banovan" -> Color(0xFFEF4444) to "BANOVAN"
        else -> Color(0xFF22C55E) to "AKTIVAN"
    }

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(backgroundColor.copy(alpha = 0.2f))
            .border(1.dp, backgroundColor, CircleShape)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            color = backgroundColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
