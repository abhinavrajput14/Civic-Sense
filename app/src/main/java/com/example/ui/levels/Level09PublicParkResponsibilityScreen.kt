package com.example.ui.levels

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
fun Level09PublicParkResponsibilityScreen(
  viewModel: GameViewModel,
  onNextLevel: () -> Unit,
  onLevelMap: () -> Unit
) {
  val gameState by viewModel.gameState.collectAsState()
  val levelInfo = LevelCatalog.getLevel(9)!!

  var benchCleaned by remember { mutableStateOf(false) }
  var hasCompleted by remember { mutableStateOf(false) }
  var hasFailed by remember { mutableStateOf(false) }
  var failureReason by remember { mutableStateOf("") }
  var isFirstTry by remember { mutableStateOf(true) }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          listOf(Color(0xFFE0F2F1), Color(0xFFC8E6C9), Color(0xFFA5D6A7))
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
        title = "Level 9: Our Park, Our Duty",
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
            tint = Color(0xFF00796B),
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "You see a public park bench with marker scribbles and a friend offering to write your initials on it.",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1E293B)
          )
        }
      }

      // Park Bench Canvas
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
            expression = if (benchCleaned) CharacterExpression.CELEBRATING else CharacterExpression.POINTING,
            size = 150.dp
          )

          // Park Bench Art
          ParkBenchArt(isClean = benchCleaned)
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
            text = "WHAT WILL YOU CHOOSE?",
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            color = Color(0xFF00796B),
            letterSpacing = 0.5.sp
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Option 1 (Correct): Protect Public Property & Clean
          Button(
            onClick = {
              benchCleaned = true
              viewModel.soundManager.playCoin(gameState.soundEnabled)
              viewModel.completeLevel(9, isFirstTry)
              hasCompleted = true
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("action_protect_property"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B)),
            shape = RoundedCornerShape(14.dp)
          ) {
            Icon(imageVector = Icons.Default.CleaningServices, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Remind Others to Respect Public Property & Wipe Clean",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Option 2 (Incorrect): Scribble on bench
          Button(
            onClick = {
              viewModel.failLevel(9)
              isFirstTry = false
              failureReason = "Scribbling on park benches and public heritage ruins common property and creates visual clutter. Responsible citizens protect what belongs to everyone!"
              hasFailed = true
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("action_scribble_bench"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350)),
            shape = RoundedCornerShape(14.dp)
          ) {
            Text(
              text = "Write Your Initials on the Wooden Slat",
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
          benchCleaned = false
          viewModel.setExpression(CharacterExpression.IDLE)
        },
        onLevelMap = onLevelMap
      )
    }
  }
}

@Composable
private fun ParkBenchArt(isClean: Boolean) {
  Canvas(modifier = Modifier.size(130.dp, 140.dp)) {
    val w = size.width
    val h = size.height

    // Legs of bench
    drawRect(Color(0xFF37474F), topLeft = Offset(w * 0.15f, h * 0.55f), size = Size(w * 0.08f, h * 0.40f))
    drawRect(Color(0xFF37474F), topLeft = Offset(w * 0.77f, h * 0.55f), size = Size(w * 0.08f, h * 0.40f))

    // Wooden Slats Backrest
    val slatColor = if (isClean) Color(0xFF8D6E63) else Color(0xFF6D4C41)
    drawRoundRect(
      color = slatColor,
      topLeft = Offset(w * 0.08f, h * 0.20f),
      size = Size(w * 0.84f, h * 0.12f),
      cornerRadius = CornerRadius(6f, 6f)
    )
    drawRoundRect(
      color = slatColor,
      topLeft = Offset(w * 0.08f, h * 0.36f),
      size = Size(w * 0.84f, h * 0.12f),
      cornerRadius = CornerRadius(6f, 6f)
    )

    // Wooden Seat
    drawRoundRect(
      color = Color(0xFFA1887F),
      topLeft = Offset(w * 0.05f, h * 0.52f),
      size = Size(w * 0.90f, h * 0.14f),
      cornerRadius = CornerRadius(6f, 6f)
    )

    // Scribbles if not clean
    if (!isClean) {
      drawLine(
        color = Color(0xFF212121),
        start = Offset(w * 0.20f, h * 0.25f),
        end = Offset(w * 0.45f, h * 0.27f),
        strokeWidth = 3f
      )
      drawLine(
        color = Color(0xFF212121),
        start = Offset(w * 0.50f, h * 0.38f),
        end = Offset(w * 0.75f, h * 0.42f),
        strokeWidth = 3f
      )
    } else {
      // Little clean sparkle
      drawCircle(Color.White, radius = 6f, center = Offset(w * 0.70f, h * 0.24f))
    }
  }
}
