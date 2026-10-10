package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameState

@Composable
fun GameTopBar(
  gameState: GameState,
  onBackClick: (() -> Unit)? = null,
  onSoundToggle: () -> Unit,
  onMusicToggle: () -> Unit,
  title: String? = null
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .statusBarsPadding()
      .padding(horizontal = 12.dp, vertical = 6.dp),
    shape = RoundedCornerShape(24.dp),
    color = Color.White.copy(alpha = 0.94f),
    shadowElevation = 6.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        // Left: Back button or Civic Quest logo
        if (onBackClick != null) {
          IconButton(
            onClick = onBackClick,
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(Color(0xFFECEFF1))
              .testTag("top_bar_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = Color(0xFF37474F)
            )
          }
        } else {
          // Player badge & Name
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(16.dp))
              .background(Color(0xFFE0F2F1))
              .padding(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Box(
              modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Color(0xFF00897B)),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "${gameState.playerLevel}",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = gameState.character.name.ifBlank { "Citizen" },
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = Color(0xFF004D40)
            )
          }
        }

        if (title != null) {
          Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B),
            modifier = Modifier.padding(horizontal = 4.dp)
          )
        }

        // Center / Stats: Coins & Civic Points
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Coins Pill
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(16.dp))
              .background(
                Brush.horizontalGradient(
                  listOf(Color(0xFFFFF8E1), Color(0xFFFFECB3))
                )
              )
              .padding(horizontal = 8.dp, vertical = 4.dp)
              .testTag("stat_coins")
          ) {
            Icon(
              imageVector = Icons.Default.MonetizationOn,
              contentDescription = "Coins",
              tint = Color(0xFFFFA000),
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            AnimatedContent(
              targetState = gameState.coins,
              transitionSpec = { slideInVertically { -it } + fadeIn() togetherWith slideOutVertically { it } + fadeOut() },
              label = "coinsAnim"
            ) { coins ->
              Text(
                text = "$coins",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp,
                color = Color(0xFF5D4037)
              )
            }
          }

          // Civic Points Pill
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(16.dp))
              .background(
                Brush.horizontalGradient(
                  listOf(Color(0xFFE8F5E9), Color(0xFFC8E6C9))
                )
              )
              .padding(horizontal = 8.dp, vertical = 4.dp)
              .testTag("stat_civic_points")
          ) {
            Icon(
              imageVector = Icons.Default.Shield,
              contentDescription = "Civic Points",
              tint = Color(0xFF2E7D32),
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            AnimatedContent(
              targetState = gameState.civicPoints,
              transitionSpec = { slideInVertically { -it } + fadeIn() togetherWith slideOutVertically { it } + fadeOut() },
              label = "civicPointsAnim"
            ) { points ->
              Text(
                text = "$points CP",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp,
                color = Color(0xFF1B5E20)
              )
            }
          }

          // Audio Toggles
          IconButton(
            onClick = onSoundToggle,
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(if (gameState.soundEnabled) Color(0xFFE0F7FA) else Color(0xFFFFEBEE))
              .testTag("toggle_sound_button")
          ) {
            Icon(
              imageVector = if (gameState.soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
              contentDescription = "Sound Toggle",
              tint = if (gameState.soundEnabled) Color(0xFF00838F) else Color(0xFFC62828),
              modifier = Modifier.size(18.dp)
            )
          }

          IconButton(
            onClick = onMusicToggle,
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(if (gameState.musicEnabled) Color(0xFFEDE7F6) else Color(0xFFF5F5F5))
              .testTag("toggle_music_button")
          ) {
            Icon(
              imageVector = if (gameState.musicEnabled) Icons.Default.MusicNote else Icons.Default.MusicOff,
              contentDescription = "Music Toggle",
              tint = if (gameState.musicEnabled) Color(0xFF5E35B1) else Color(0xFF9E9E9E),
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      // Small XP level progression bar
      Spacer(modifier = Modifier.height(4.dp))
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = "XP: ${gameState.xp % 120}/120",
          fontSize = 10.sp,
          fontWeight = FontWeight.SemiBold,
          color = Color(0xFF78909C)
        )
        Spacer(modifier = Modifier.width(6.dp))
        LinearProgressIndicator(
          progress = { gameState.progressToNextLevel },
          modifier = Modifier
            .weight(1f)
            .height(5.dp)
            .clip(RoundedCornerShape(3.dp)),
          color = Color(0xFF00897B),
          trackColor = Color(0xFFE0E0E0)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = gameState.rankTitle,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF00897B)
        )
      }
    }
  }
}
