package com.example.ui.levels

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CharacterExpression
import com.example.model.LevelCatalog
import com.example.ui.components.CharacterAvatar
import com.example.ui.components.FailureDialog
import com.example.ui.components.GameTopBar
import com.example.ui.components.SuccessDialog
import com.example.viewmodel.GameViewModel

@Composable
fun Level05ProtectGreenScreen(
  viewModel: GameViewModel,
  onNextLevel: () -> Unit,
  onLevelMap: () -> Unit
) {
  val gameState by viewModel.gameState.collectAsState()
  val levelInfo = LevelCatalog.getLevel(5)!!

  var litterCleared by remember { mutableStateOf(false) }
  var saplingWatered by remember { mutableStateOf(false) }
  var hasCompleted by remember { mutableStateOf(false) }
  var hasFailed by remember { mutableStateOf(false) }
  var failureReason by remember { mutableStateOf("") }
  var isFirstTry by remember { mutableStateOf(true) }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          listOf(Color(0xFFE8F5E9), Color(0xFFC8E6C9), Color(0xFFA5D6A7))
        )
      )
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .navigationBarsPadding(),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      GameTopBar(
        gameState = gameState,
        onBackClick = onLevelMap,
        title = "Level 5: Protect the Green",
        onSoundToggle = { viewModel.toggleSound() },
        onMusicToggle = { viewModel.toggleMusic() }
      )

      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.94f))
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Park,
            contentDescription = null,
            tint = Color(0xFF2E7D32),
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "A newly planted sapling in the public park needs your care to grow!",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1E293B)
          )
        }
      }

      // Garden Canvas
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceAround,
          verticalAlignment = Alignment.Bottom
        ) {
          CharacterAvatar(
            character = gameState.character,
            expression = if (hasCompleted) CharacterExpression.CELEBRATING else CharacterExpression.HAPPY,
            size = 160.dp
          )

          // Sapling Canvas
          PlantArt(isBloomed = saplingWatered && litterCleared)
        }
      }

      // Actions Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "HOW WILL YOU PROTECT THE PARK?",
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            color = Color(0xFF2E7D32),
            letterSpacing = 0.5.sp
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Action 1: Clear Litter & Water
          Button(
            onClick = {
              viewModel.soundManager.playCoin(gameState.soundEnabled)
              litterCleared = true
              saplingWatered = true
              viewModel.completeLevel(5, isFirstTry)
              hasCompleted = true
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("action_care_plant"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
            shape = RoundedCornerShape(14.dp)
          ) {
            Icon(imageVector = Icons.Default.WaterDrop, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Clear Trash & Water the Sapling 🌸",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Action 2: Trample through flowerbed
          Button(
            onClick = {
              viewModel.failLevel(5)
              isFirstTry = false
              failureReason = "Trampling through flowerbeds breaks young roots and ruins weeks of community gardening. Always stay on designated walking paths!"
              hasFailed = true
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("action_trample_flowerbed"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350)),
            shape = RoundedCornerShape(14.dp)
          ) {
            Text(
              text = "Trample Across Flowerbeds to Take Shortcut",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }
        }
      }
    }

    if (hasCompleted) {
      SuccessDialog(
        level = levelInfo,
        character = gameState.character,
        isFirstTry = isFirstTry,
        onNextLevel = onNextLevel,
        onLevelMap = onLevelMap
      )
    }

    if (hasFailed) {
      FailureDialog(
        reasonExplanation = failureReason,
        character = gameState.character,
        onRetry = {
          hasFailed = false
          viewModel.setExpression(CharacterExpression.IDLE)
        },
        onLevelMap = onLevelMap
      )
    }
  }
}

@Composable
private fun PlantArt(isBloomed: Boolean) {
  Canvas(modifier = Modifier.size(110.dp, 160.dp)) {
    val w = size.width
    val h = size.height

    // Flower pot
    drawRoundRect(
      color = Color(0xFFD87D4A),
      topLeft = Offset(w * 0.2f, h * 0.65f),
      size = Size(w * 0.6f, h * 0.32f),
      cornerRadius = CornerRadius(8f, 8f)
    )
    // Pot rim
    drawRoundRect(
      color = Color(0xFFB55D28),
      topLeft = Offset(w * 0.15f, h * 0.60f),
      size = Size(w * 0.7f, h * 0.08f),
      cornerRadius = CornerRadius(4f, 4f)
    )

    // Stem
    drawLine(
      color = Color(0xFF4CAF50),
      start = Offset(w * 0.5f, h * 0.62f),
      end = Offset(w * 0.5f, h * 0.22f),
      strokeWidth = 8f
    )

    // Leaves
    drawOval(
      color = Color(0xFF66BB6A),
      topLeft = Offset(w * 0.28f, h * 0.36f),
      size = Size(w * 0.22f, h * 0.10f)
    )
    drawOval(
      color = Color(0xFF66BB6A),
      topLeft = Offset(w * 0.50f, h * 0.32f),
      size = Size(Size(w * 0.22f, h * 0.10f).width, Size(w * 0.22f, h * 0.10f).height)
    )

    // Flower blossom on top if bloomed
    if (isBloomed) {
      val blossomCenter = Offset(w * 0.5f, h * 0.18f)
      val petalRadius = w * 0.10f
      // 4 Petals
      drawCircle(Color(0xFFFF4081), radius = petalRadius, center = Offset(blossomCenter.x - 12f, blossomCenter.y))
      drawCircle(Color(0xFFFF4081), radius = petalRadius, center = Offset(blossomCenter.x + 12f, blossomCenter.y))
      drawCircle(Color(0xFFFF4081), radius = petalRadius, center = Offset(blossomCenter.x, blossomCenter.y - 12f))
      drawCircle(Color(0xFFFF4081), radius = petalRadius, center = Offset(blossomCenter.x, blossomCenter.y + 12f))
      // Center
      drawCircle(Color(0xFFFFEB3B), radius = petalRadius * 0.8f, center = blossomCenter)
    }
  }
}
