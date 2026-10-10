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
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Info
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
fun Level04ZebraCrossingScreen(
  viewModel: GameViewModel,
  onNextLevel: () -> Unit,
  onLevelMap: () -> Unit
) {
  val gameState by viewModel.gameState.collectAsState()
  val levelInfo = LevelCatalog.getLevel(4)!!

  var hasCompleted by remember { mutableStateOf(false) }
  var hasFailed by remember { mutableStateOf(false) }
  var failureReason by remember { mutableStateOf("") }
  var isFirstTry by remember { mutableStateOf(true) }
  var crossedOnZebra by remember { mutableStateOf(false) }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          listOf(Color(0xFFE0F7FA), Color(0xFFF1F8E9), Color(0xFFCFD8DC))
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
        title = "Level 4: Safe Crossing",
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
            imageVector = Icons.Default.DirectionsWalk,
            contentDescription = null,
            tint = Color(0xFF00897B),
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "You need to reach the library across the busy road. Where and how should you cross?",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1E293B)
          )
        }
      }

      // Interactive Street Canvas with Zebra Stripes
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Road canvas with Zebra markings
          Canvas(
            modifier = Modifier
              .fillMaxWidth()
              .height(130.dp)
              .clip(RoundedCornerShape(16.dp))
          ) {
            val w = size.width
            val h = size.height

            // Asphalt road
            drawRect(Color(0xFF37474F))

            // White border curbs
            drawRect(Color(0xFFCFD8DC), topLeft = Offset(0f, 0f), size = Size(w, 8f))
            drawRect(Color(0xFFCFD8DC), topLeft = Offset(0f, h - 8f), size = Size(w, 8f))

            // Zebra Crossing stripes
            val stripeWidth = w * 0.07f
            val gap = w * 0.04f
            val startX = w * 0.25f
            for (i in 0 until 5) {
              drawRoundRect(
                color = Color.White,
                topLeft = Offset(startX + i * (stripeWidth + gap), h * 0.18f),
                size = Size(stripeWidth, h * 0.64f),
                cornerRadius = CornerRadius(4f, 4f)
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          CharacterAvatar(
            character = gameState.character,
            expression = if (crossedOnZebra) CharacterExpression.CELEBRATING else CharacterExpression.POINTING,
            size = 150.dp
          )
        }
      }

      // Choices Card
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
            text = "HOW WILL YOU CROSS?",
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            color = Color(0xFF00897B),
            letterSpacing = 0.5.sp
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Option 1 (Correct): Walk to Zebra Crossing & look both ways
          Button(
            onClick = {
              viewModel.soundManager.playSuccess(gameState.soundEnabled)
              crossedOnZebra = true
              viewModel.completeLevel(4, isFirstTry)
              hasCompleted = true
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("action_zebra_crossing"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00897B)),
            shape = RoundedCornerShape(14.dp)
          ) {
            Icon(imageVector = Icons.Default.DirectionsWalk, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Use the Zebra Crossing & Look Both Ways",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Option 2 (Incorrect): Jaywalk
          Button(
            onClick = {
              viewModel.failLevel(4)
              isFirstTry = false
              failureReason = "Jaywalking across the road between moving vehicles is unpredictable and gives drivers zero reaction time. Always walk to the zebra crossing!"
              hasFailed = true
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("action_jaywalk"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350)),
            shape = RoundedCornerShape(14.dp)
          ) {
            Text(
              text = "Sprint Across Middle of Road",
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
