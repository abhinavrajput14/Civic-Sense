package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Traffic
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BadgesData
import com.example.model.GameBadge
import com.example.ui.components.GameTopBar
import com.example.viewmodel.GameViewModel

@Composable
fun BadgesScreen(
  viewModel: GameViewModel,
  onBackClick: () -> Unit
) {
  val gameState by viewModel.gameState.collectAsState()
  val badges = BadgesData.ALL_BADGES

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          listOf(Color(0xFFEDE7F6), Color(0xFFF3E5F5), Color(0xFFFAFAFA))
        )
      )
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .navigationBarsPadding()
    ) {
      GameTopBar(
        gameState = gameState,
        onBackClick = onBackClick,
        title = "Civic Badges",
        onSoundToggle = { viewModel.toggleSound() },
        onMusicToggle = { viewModel.toggleMusic() }
      )

      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp, vertical = 8.dp)
      ) {
        // Badges progress card
        Surface(
          color = Color.White.copy(alpha = 0.9f),
          shape = RoundedCornerShape(18.dp),
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text(
                text = "BADGE COLLECTION",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF6A1B9A),
                letterSpacing = 0.5.sp
              )
              Text(
                text = "${gameState.unlockedBadges.size} of ${badges.size} Badges Earned",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
              )
            }
            Icon(
              imageVector = Icons.Default.MilitaryTech,
              contentDescription = null,
              tint = Color(0xFFFFB300),
              modifier = Modifier.size(32.dp)
            )
          }
        }

        // Badges Grid
        LazyVerticalGrid(
          columns = GridCells.Fixed(2),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier
            .fillMaxSize()
            .testTag("badges_grid")
        ) {
          items(badges) { badge ->
            val isUnlocked = gameState.unlockedBadges.contains(badge.id)
            BadgeCard(badge = badge, isUnlocked = isUnlocked)
          }
        }
      }
    }
  }
}

@Composable
private fun BadgeCard(badge: GameBadge, isUnlocked: Boolean) {
  val icon = when (badge.iconName) {
    "delete" -> Icons.Default.Delete
    "recycling" -> Icons.Default.Recycling
    "traffic" -> Icons.Default.Traffic
    "directions_walk" -> Icons.Default.DirectionsWalk
    "park" -> Icons.Default.Park
    "water_drop" -> Icons.Default.WaterDrop
    "groups" -> Icons.Default.Handshake
    "directions_bus" -> Icons.Default.DirectionsBus
    "handshake" -> Icons.Default.Handshake
    else -> Icons.Default.WorkspacePremium
  }

  val badgeColor = Color(badge.colorHex)

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .height(180.dp)
      .testTag("badge_card_${badge.id}"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isUnlocked) Color.White else Color(0xFFF1F5F9)
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = if (isUnlocked) 4.dp else 1.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(12.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Box(
        modifier = Modifier
          .size(54.dp)
          .clip(CircleShape)
          .background(if (isUnlocked) badgeColor.copy(alpha = 0.15f) else Color(0xFFE2E8F0)),
        contentAlignment = Alignment.Center
      ) {
        if (isUnlocked) {
          Icon(
            imageVector = icon,
            contentDescription = badge.title,
            tint = badgeColor,
            modifier = Modifier.size(28.dp)
          )
        } else {
          Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = "Locked",
            tint = Color(0xFF94A3B8),
            modifier = Modifier.size(22.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = badge.title,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        color = if (isUnlocked) Color(0xFF1E293B) else Color(0xFF94A3B8),
        textAlign = TextAlign.Center,
        maxLines = 1
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = if (isUnlocked) badge.description else "Complete Level ${badge.unlockedByLevel} to unlock",
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
        color = if (isUnlocked) Color(0xFF64748B) else Color(0xFF94A3B8),
        textAlign = TextAlign.Center,
        lineHeight = 13.sp,
        maxLines = 3
      )
    }
  }
}
