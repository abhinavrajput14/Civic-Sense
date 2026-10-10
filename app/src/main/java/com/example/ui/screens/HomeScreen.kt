package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CharacterExpression
import com.example.ui.components.CharacterAvatar
import com.example.ui.components.GameTopBar
import com.example.ui.components.LivingCityBackground
import com.example.viewmodel.GameViewModel

@Composable
fun HomeScreen(
  viewModel: GameViewModel,
  onPlayClick: () -> Unit,
  onCharacterClick: () -> Unit,
  onLevelMapClick: () -> Unit,
  onBadgesClick: () -> Unit,
  onSettingsClick: () -> Unit
) {
  val gameState by viewModel.gameState.collectAsState()

  Box(modifier = Modifier.fillMaxSize()) {
    // 1. Living City animated backdrop
    LivingCityBackground(showRoad = true)

    // 2. Main Content Layout
    Column(
      modifier = Modifier
        .fillMaxSize()
        .navigationBarsPadding(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // Top Bar with Stats & Audio Toggles
      GameTopBar(
        gameState = gameState,
        onSoundToggle = { viewModel.toggleSound() },
        onMusicToggle = { viewModel.toggleMusic() }
      )

      // Center Stage: Living City Square with Character
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        // Tagline Pill
        Surface(
          color = Color.White.copy(alpha = 0.90f),
          shape = RoundedCornerShape(20.dp),
          shadowElevation = 4.dp
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = Color(0xFF00897B),
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Play • Choose • Learn • Change",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF00695C)
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Hero Character Avatar (Interactive)
        Box(
          modifier = Modifier
            .clickable {
              viewModel.setExpression(CharacterExpression.WAVING)
              viewModel.soundManager.playClick()
            }
            .testTag("home_character_avatar"),
          contentAlignment = Alignment.Center
        ) {
          CharacterAvatar(
            character = gameState.character,
            expression = gameState.characterExpression,
            size = 145.dp
          )
        }

        // Hero Card: Name & Rank
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.94f)),
          elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
          modifier = Modifier.padding(top = 8.dp)
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
          ) {
            Text(
              text = gameState.character.name,
              fontWeight = FontWeight.Black,
              fontSize = 20.sp,
              color = Color(0xFF1E293B)
            )
            Text(
              text = "${gameState.rankTitle} • Level ${gameState.playerLevel}",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF00897B)
            )
          }
        }
      }

      // Bottom Control Panel: Buttons
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Big PLAY Button
          Button(
            onClick = {
              viewModel.soundManager.playClick()
              onPlayClick()
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(58.dp)
              .testTag("home_play_button"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00897B)),
            shape = RoundedCornerShape(20.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.PlayArrow,
              contentDescription = null,
              modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "PLAY",
              fontWeight = FontWeight.Black,
              fontSize = 20.sp,
              letterSpacing = 1.sp
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          // 4 Grid Action Buttons: CHARACTER, LEVEL MAP, BADGES, SETTINGS
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            HomeSmallButton(
              title = "CHARACTER",
              icon = Icons.Default.Face,
              modifier = Modifier.weight(1f),
              testTag = "home_character_button",
              onClick = onCharacterClick
            )
            HomeSmallButton(
              title = "MAP",
              icon = Icons.Default.Map,
              modifier = Modifier.weight(1f),
              testTag = "home_map_button",
              onClick = onLevelMapClick
            )
            HomeSmallButton(
              title = "BADGES",
              icon = Icons.Default.MilitaryTech,
              modifier = Modifier.weight(1f),
              testTag = "home_badges_button",
              onClick = onBadgesClick
            )
            HomeSmallButton(
              title = "SETTINGS",
              icon = Icons.Default.Settings,
              modifier = Modifier.weight(1f),
              testTag = "home_settings_button",
              onClick = onSettingsClick
            )
          }
        }
      }
    }
  }
}

@Composable
private fun HomeSmallButton(
  title: String,
  icon: ImageVector,
  modifier: Modifier = Modifier,
  testTag: String,
  onClick: () -> Unit
) {
  OutlinedButton(
    onClick = onClick,
    modifier = modifier
      .height(50.dp)
      .testTag(testTag),
    shape = RoundedCornerShape(16.dp),
    colors = ButtonDefaults.outlinedButtonColors(
      containerColor = Color(0xFFF8FAFC),
      contentColor = Color(0xFF334155)
    )
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp))
      Text(
        text = title,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        maxLines = 1
      )
    }
  }
}
