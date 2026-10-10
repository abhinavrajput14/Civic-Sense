package com.example.ui.levels

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import kotlinx.coroutines.delay

@Composable
fun Level06SaveWaterScreen(
  viewModel: GameViewModel,
  onNextLevel: () -> Unit,
  onLevelMap: () -> Unit
) {
  val gameState by viewModel.gameState.collectAsState()
  val levelInfo = LevelCatalog.getLevel(6)!!

  var tapClosed by remember { mutableStateOf(false) }
  var waterWastedLiters by remember { mutableFloatStateOf(1.2f) }
  var hasCompleted by remember { mutableStateOf(false) }
  var hasFailed by remember { mutableStateOf(false) }
  var failureReason by remember { mutableStateOf("") }
  var isFirstTry by remember { mutableStateOf(true) }

  // Leak timer increases water wasted
  LaunchedEffect(tapClosed, hasCompleted, hasFailed) {
    while (!tapClosed && !hasCompleted && !hasFailed) {
      delay(500)
      waterWastedLiters += 0.4f
    }
  }

  val transition = rememberInfiniteTransition(label = "waterStream")
  val dropOffset by transition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(400, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "drop"
  )

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          listOf(Color(0xFFE0F7FA), Color(0xFFB2EBF2), Color(0xFF80DEEA))
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
        title = "Level 6: Save Every Drop",
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
            imageVector = Icons.Default.WaterDrop,
            contentDescription = null,
            tint = Color(0xFF0288D1),
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "Someone left the public water tap running! Fresh water is wasting fast. What will you do?",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1E293B)
          )
        }
      }

      // Water Wastage Live Gauge
      Surface(
        color = Color.White.copy(alpha = 0.9f),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "Water Wasted:",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0277BD)
          )
          Text(
            text = String.format("%.1f Liters", waterWastedLiters),
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            color = if (waterWastedLiters > 5f) Color(0xFFD32F2F) else Color(0xFF0288D1)
          )
        }
      }

      // Interactive Tap & Character
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
          verticalAlignment = Alignment.CenterVertically
        ) {
          CharacterAvatar(
            character = gameState.character,
            expression = if (tapClosed) CharacterExpression.CELEBRATING else CharacterExpression.POINTING,
            size = 160.dp
          )

          // Tap Canvas
          RunningWaterTapArt(
            isClosed = tapClosed,
            dropAnim = dropOffset,
            onTapClick = {
              if (!tapClosed) {
                tapClosed = true
                viewModel.soundManager.playCoin(gameState.soundEnabled)
                viewModel.completeLevel(6, isFirstTry)
                hasCompleted = true
              }
            }
          )
        }
      }

      // Action Card
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
            text = "CHOOSE YOUR ACTION:",
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            color = Color(0xFF0288D1),
            letterSpacing = 0.5.sp
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Option 1 (Correct): Turn off the tap
          Button(
            onClick = {
              tapClosed = true
              viewModel.soundManager.playCoin(gameState.soundEnabled)
              viewModel.completeLevel(6, isFirstTry)
              hasCompleted = true
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("action_close_tap"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
            shape = RoundedCornerShape(14.dp)
          ) {
            Icon(imageVector = Icons.Default.WaterDrop, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Turn the Valve Tightly Shut 🚰",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Option 2 (Incorrect): Walk away
          Button(
            onClick = {
              viewModel.failLevel(6)
              isFirstTry = false
              failureReason = "Walking away lets hundreds of liters of precious drinking water go to waste. Every citizen has the power and duty to stop water wastage!"
              hasFailed = true
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("action_ignore_tap"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350)),
            shape = RoundedCornerShape(14.dp)
          ) {
            Text(
              text = "Ignore It (Someone else will close it)",
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
          tapClosed = false
          waterWastedLiters = 1.2f
          viewModel.setExpression(CharacterExpression.IDLE)
        },
        onLevelMap = onLevelMap
      )
    }
  }
}

@Composable
private fun RunningWaterTapArt(
  isClosed: Boolean,
  dropAnim: Float,
  onTapClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .size(110.dp, 160.dp)
      .clip(RoundedCornerShape(16.dp))
      .clickable(onClick = onTapClick)
      .testTag("interactive_water_tap"),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = size.width
      val h = size.height

      // Pipe coming from wall
      drawRect(
        color = Color(0xFF78909C),
        topLeft = Offset(0f, h * 0.35f),
        size = Size(w * 0.55f, h * 0.12f)
      )

      // Tap Spout pointing down
      drawRoundRect(
        color = Color(0xFF90A4AE),
        topLeft = Offset(w * 0.45f, h * 0.35f),
        size = Size(w * 0.22f, h * 0.28f),
        cornerRadius = CornerRadius(6f, 6f)
      )

      // Turn Valve handle on top
      drawRoundRect(
        color = if (isClosed) Color(0xFF2E7D32) else Color(0xFFD32F2F),
        topLeft = Offset(w * 0.40f, h * 0.22f),
        size = Size(w * 0.32f, h * 0.10f),
        cornerRadius = CornerRadius(4f, 4f)
      )

      // Water stream or drip
      if (!isClosed) {
        // Continuous water stream
        drawRoundRect(
          color = Color(0xFF29B6F6),
          topLeft = Offset(w * 0.50f, h * 0.63f),
          size = Size(w * 0.12f, h * 0.32f),
          cornerRadius = CornerRadius(4f, 4f)
        )
      } else {
        // Clean closed tap icon
        drawCircle(
          color = Color(0xFF4FC3F7),
          radius = 5f,
          center = Offset(w * 0.56f, h * 0.66f)
        )
      }
    }
  }
}
