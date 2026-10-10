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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LevelCatalog
import com.example.model.LevelInfo
import com.example.ui.components.GameTopBar
import com.example.viewmodel.GameViewModel

@Composable
fun LevelMapScreen(
  viewModel: GameViewModel,
  onLevelSelected: (Int) -> Unit,
  onBackClick: () -> Unit
) {
  val gameState by viewModel.gameState.collectAsState()
  val levels = LevelCatalog.LEVELS

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          listOf(Color(0xFFE0F2F1), Color(0xFFE8F5E9), Color(0xFFF1F8E9))
        )
      )
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .navigationBarsPadding()
    ) {
      // Top Bar
      GameTopBar(
        gameState = gameState,
        onBackClick = onBackClick,
        title = "City Route Map",
        onSoundToggle = { viewModel.toggleSound() },
        onMusicToggle = { viewModel.toggleMusic() }
      )

      // Map Scroll View
      Column(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
          .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Map Header Banner
        Surface(
          color = Color.White.copy(alpha = 0.9f),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text(
                text = "CIVIC ADVENTURE ROUTE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF00897B),
                letterSpacing = 0.5.sp
              )
              Text(
                text = "Progress: ${gameState.completedLevels.size}/${levels.size} Levels",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
              )
            }
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF00897B))
                .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
              val progressPct = if (levels.isNotEmpty()) (gameState.completedLevels.size * 100) / levels.size else 0
              Text(
                text = "$progressPct%",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
            }
          }
        }

        // Connected Level Cards Grouped with World Headers
        levels.forEachIndexed { index, level ->
          val isUnlocked = gameState.unlockedLevels.contains(level.id)
          val completion = gameState.completedLevels[level.id]
          val isCompleted = completion != null

          // World Divider Banner
          if (index == 0 || level.world != levels[index - 1].world) {
            val worldBg = when (level.world) {
              1 -> Color(0xFF00897B)
              2 -> Color(0xFF1976D2)
              else -> Color(0xFF7B1FA2)
            }
            Surface(
              color = worldBg,
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Column {
                  Text(
                    text = "WORLD ${level.world}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White.copy(alpha = 0.85f),
                    letterSpacing = 1.sp
                  )
                  Text(
                    text = level.worldTitle.uppercase(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                  )
                }
                Text(
                  text = when (level.world) {
                    1 -> "Levels 1–5"
                    2 -> "Levels 6–10"
                    else -> "Levels 11–15"
                  },
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White.copy(alpha = 0.9f)
                )
              }
            }
          }

          LevelMapNodeCard(
            level = level,
            isUnlocked = isUnlocked,
            isCompleted = isCompleted,
            stars = completion?.stars ?: 0,
            isNextInLine = isUnlocked && !isCompleted,
            onClick = {
              if (isUnlocked) {
                viewModel.startLevel(level.id)
                onLevelSelected(level.id)
              } else {
                viewModel.soundManager.playFail(gameState.soundEnabled)
              }
            }
          )

          // Road connector path between nodes
          if (index < levels.size - 1) {
            RoadConnector(isCompleted = isCompleted)
          }
        }

        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}

@Composable
private fun LevelMapNodeCard(
  level: LevelInfo,
  isUnlocked: Boolean,
  isCompleted: Boolean,
  stars: Int,
  isNextInLine: Boolean,
  onClick: () -> Unit
) {
  val containerColor = when {
    isNextInLine -> Color(0xFF00897B)
    isCompleted -> Color.White
    else -> Color(0xFFECEFF1)
  }

  val contentColor = when {
    isNextInLine -> Color.White
    isCompleted -> Color(0xFF1E293B)
    else -> Color(0xFF90A4AE)
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(22.dp))
      .clickable(enabled = isUnlocked, onClick = onClick)
      .testTag("level_node_${level.id}"),
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = containerColor),
    elevation = CardDefaults.cardElevation(defaultElevation = if (isNextInLine) 8.dp else 3.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // Left: Level Number Badge
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(
              when {
                isNextInLine -> Color.White.copy(alpha = 0.25f)
                isCompleted -> Color(0xFFE8F5E9)
                else -> Color(0xFFCFD8DC)
              }
            ),
          contentAlignment = Alignment.Center
        ) {
          if (!isUnlocked) {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = "Locked",
              tint = Color(0xFF78909C),
              modifier = Modifier.size(22.dp)
            )
          } else if (isCompleted) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = "Completed",
              tint = Color(0xFF2E7D32),
              modifier = Modifier.size(26.dp)
            )
          } else {
            Text(
              text = if (level.id < 10) "0${level.id}" else "${level.id}",
              fontWeight = FontWeight.Black,
              fontSize = 18.sp,
              color = contentColor
            )
          }
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Center: Title & Topic
        Column {
          Text(
            text = "Level ${level.id} • ${level.topic}",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isNextInLine) Color.White.copy(alpha = 0.85f) else Color(0xFF78909C)
          )
          Text(
            text = level.title,
            fontWeight = FontWeight.Black,
            fontSize = 16.sp,
            color = contentColor
          )
          Text(
            text = level.subtitle,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (isNextInLine) Color.White.copy(alpha = 0.9f) else Color(0xFF64748B)
          )
        }
      }

      // Right: Stars or Play Prompt
      Column(horizontalAlignment = Alignment.End) {
        if (isCompleted) {
          Row {
            repeat(stars) {
              Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color(0xFFFFB300),
                modifier = Modifier.size(18.dp)
              )
            }
          }
        } else if (isNextInLine) {
          Surface(
            color = Color.White,
            shape = RoundedCornerShape(12.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color(0xFF00897B),
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(2.dp))
              Text(
                text = "PLAY",
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                color = Color(0xFF00897B)
              )
            }
          }
        } else {
          Text(
            text = "LOCKED",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF90A4AE)
          )
        }
      }
    }
  }
}

@Composable
private fun RoadConnector(isCompleted: Boolean) {
  Box(
    modifier = Modifier
      .padding(vertical = 4.dp)
      .width(6.dp)
      .height(22.dp)
      .clip(RoundedCornerShape(3.dp))
      .background(if (isCompleted) Color(0xFF00897B) else Color(0xFFCFD8DC))
  )
}
