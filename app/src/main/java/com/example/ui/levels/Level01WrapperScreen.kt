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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
fun Level01WrapperScreen(
  viewModel: GameViewModel,
  onNextLevel: () -> Unit,
  onLevelMap: () -> Unit
) {
  val gameState by viewModel.gameState.collectAsState()
  val levelInfo = LevelCatalog.getLevel(1)!!

  var hasCompleted by remember { mutableStateOf(false) }
  var hasFailed by remember { mutableStateOf(false) }
  var failureReason by remember { mutableStateOf("") }
  var isFirstTry by remember { mutableStateOf(true) }
  var wrapperPlacedInBin by remember { mutableStateOf(false) }

  val transition = rememberInfiniteTransition(label = "binBounce")
  val pulseAnim by transition.animateFloat(
    initialValue = 0.95f,
    targetValue = 1.05f,
    animationSpec = infiniteRepeatable(
      animation = tween(800, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse"
  )

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          listOf(Color(0xFFE0F7FA), Color(0xFFE8F5E9), Color(0xFFC8E6C9))
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
        title = "Level 1: The Wrapper",
        onSoundToggle = { viewModel.toggleSound() },
        onMusicToggle = { viewModel.toggleMusic() }
      )

      // Objective Prompt Banner
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
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = Color(0xFF00897B),
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "You just finished your snack. What should you do with the snack wrapper?",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1E293B)
          )
        }
      }

      // Interactive Scene Canvas
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
          // Player Character
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CharacterAvatar(
              character = gameState.character,
              expression = if (wrapperPlacedInBin) CharacterExpression.CELEBRATING else CharacterExpression.POINTING,
              size = 130.dp
            )
            // Wrapper in hand indicator if not placed
            if (!wrapperPlacedInBin) {
              Surface(
                color = Color(0xFFFF7043),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.padding(top = 4.dp)
              ) {
                Text(
                  text = "🍟 Snack Wrapper",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }
          }

          // Interactive Green Public Dustbin
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
              .clip(RoundedCornerShape(24.dp))
              .clickable {
                viewModel.soundManager.playCoin(gameState.soundEnabled)
                wrapperPlacedInBin = true
                viewModel.completeLevel(1, isFirstTry)
                hasCompleted = true
              }
              .padding(8.dp)
              .testTag("level1_public_bin")
          ) {
            // Dustbin Art
            DustbinArt(isHappy = wrapperPlacedInBin)

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
              color = Color(0xFF00897B),
              shape = RoundedCornerShape(12.dp)
            ) {
              Text(
                text = "PUBLIC DUSTBIN",
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
              )
            }
          }
        }
      }

      // Bottom Decision Controls
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
            color = Color(0xFF00897B),
            letterSpacing = 0.5.sp
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Option 1 (Correct): Put wrapper in public bin
          Button(
            onClick = {
              viewModel.soundManager.playCoin(gameState.soundEnabled)
              wrapperPlacedInBin = true
              viewModel.completeLevel(1, isFirstTry)
              hasCompleted = true
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("action_bin_wrapper"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00897B)),
            shape = RoundedCornerShape(14.dp)
          ) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Put in the Public Dustbin",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Option 2 (Incorrect): Drop on the sidewalk
          Button(
            onClick = {
              viewModel.failLevel(1)
              isFirstTry = false
              failureReason = "Throwing waste on the street makes public spaces dirty, clogs drains during rain, and can cause accidents. Always use a bin!"
              hasFailed = true
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("action_drop_street"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350)),
            shape = RoundedCornerShape(14.dp)
          ) {
            Text(
              text = "Drop on Footpath (Nobody is looking)",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
          }
        }
      }
    }

    // Success Dialog
    if (hasCompleted) {
      SuccessDialog(
        level = levelInfo,
        character = gameState.character,
        isFirstTry = isFirstTry,
        onNextLevel = onNextLevel,
        onLevelMap = onLevelMap
      )
    }

    // Failure Dialog
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
private fun DustbinArt(isHappy: Boolean) {
  Canvas(modifier = Modifier.size(90.dp, 120.dp)) {
    val w = size.width
    val h = size.height

    // Lid
    drawRoundRect(
      color = Color(0xFF2E7D32),
      topLeft = Offset(w * 0.1f, h * 0.08f),
      size = Size(w * 0.8f, h * 0.14f),
      cornerRadius = CornerRadius(8f, 8f)
    )
    // Handle on lid
    drawRoundRect(
      color = Color(0xFF1B5E20),
      topLeft = Offset(w * 0.38f, 0f),
      size = Size(w * 0.24f, h * 0.10f),
      cornerRadius = CornerRadius(4f, 4f)
    )

    // Bin Body
    drawRoundRect(
      color = Color(0xFF43A047),
      topLeft = Offset(w * 0.15f, h * 0.22f),
      size = Size(w * 0.7f, h * 0.75f),
      cornerRadius = CornerRadius(12f, 12f)
    )

    // Recycle symbol or smiley face
    if (isHappy) {
      // Cheerful Eyes
      drawCircle(Color.White, radius = w * 0.06f, center = Offset(w * 0.38f, h * 0.45f))
      drawCircle(Color(0xFF1B5E20), radius = w * 0.03f, center = Offset(w * 0.38f, h * 0.45f))
      drawCircle(Color.White, radius = w * 0.06f, center = Offset(w * 0.62f, h * 0.45f))
      drawCircle(Color(0xFF1B5E20), radius = w * 0.03f, center = Offset(w * 0.62f, h * 0.45f))
      // Smile
      drawArc(
        color = Color.White,
        startAngle = 10f,
        sweepAngle = 160f,
        useCenter = false,
        topLeft = Offset(w * 0.35f, h * 0.55f),
        size = Size(w * 0.3f, h * 0.20f),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
      )
    } else {
      // Swachh / Cleanliness lines
      drawLine(
        color = Color(0xFF81C784),
        start = Offset(w * 0.30f, h * 0.40f),
        end = Offset(w * 0.30f, h * 0.85f),
        strokeWidth = 3f
      )
      drawLine(
        color = Color(0xFF81C784),
        start = Offset(w * 0.50f, h * 0.40f),
        end = Offset(w * 0.50f, h * 0.85f),
        strokeWidth = 3f
      )
      drawLine(
        color = Color(0xFF81C784),
        start = Offset(w * 0.70f, h * 0.40f),
        end = Offset(w * 0.70f, h * 0.85f),
        strokeWidth = 3f
      )
    }
  }
}
