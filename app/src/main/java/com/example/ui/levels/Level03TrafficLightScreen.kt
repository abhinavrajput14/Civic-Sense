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
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Traffic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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

enum class TrafficLightState {
  VEHICLE_GREEN,   // Pedestrian RED
  VEHICLE_YELLOW,  // Pedestrian RED
  VEHICLE_RED      // Pedestrian GREEN
}

@Composable
fun Level03TrafficLightScreen(
  viewModel: GameViewModel,
  onNextLevel: () -> Unit,
  onLevelMap: () -> Unit
) {
  val gameState by viewModel.gameState.collectAsState()
  val levelInfo = LevelCatalog.getLevel(3)!!

  var lightState by remember { mutableStateOf(TrafficLightState.VEHICLE_GREEN) }
  var hasCompleted by remember { mutableStateOf(false) }
  var hasFailed by remember { mutableStateOf(false) }
  var failureReason by remember { mutableStateOf("") }
  var isFirstTry by remember { mutableStateOf(true) }
  var characterCrossing by remember { mutableStateOf(false) }

  // Automatic realistic traffic light cycle
  LaunchedEffect(hasCompleted, hasFailed) {
    while (!hasCompleted && !hasFailed) {
      lightState = TrafficLightState.VEHICLE_GREEN
      delay(3200)
      lightState = TrafficLightState.VEHICLE_YELLOW
      delay(1800)
      lightState = TrafficLightState.VEHICLE_RED
      delay(4000)
    }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          listOf(Color(0xFFE0F7FA), Color(0xFFECEFF1), Color(0xFFCFD8DC))
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
        title = "Level 3: Red Means Stop",
        onSoundToggle = { viewModel.toggleSound() },
        onMusicToggle = { viewModel.toggleMusic() }
      )

      // Instructions Banner
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
            imageVector = Icons.Default.Traffic,
            contentDescription = null,
            tint = Color(0xFF00897B),
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "Observe the signal. Cross ONLY when the pedestrian walk signal turns GREEN!",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1E293B)
          )
        }
      }

      // Traffic Signal Scene
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
          // Character
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CharacterAvatar(
              character = gameState.character,
              expression = when {
                characterCrossing -> CharacterExpression.HAPPY
                lightState == TrafficLightState.VEHICLE_RED -> CharacterExpression.WAVING
                else -> CharacterExpression.IDLE
              },
              size = 170.dp
            )

            Surface(
              color = if (lightState == TrafficLightState.VEHICLE_RED) Color(0xFF2E7D32) else Color(0xFFC62828),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.padding(top = 6.dp)
            ) {
              Text(
                text = if (lightState == TrafficLightState.VEHICLE_RED) "WALK SIGNAL ON" else "WAIT SIGNAL (RED)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
              )
            }
          }

          // Animated Interactive Traffic Light Box
          TrafficLightBox(lightState = lightState)
        }
      }

      // Decision Controls
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
            text = "DECIDE YOUR ACTION:",
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            color = Color(0xFF00897B),
            letterSpacing = 0.5.sp
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Option 1: Cross Road Now
          Button(
            onClick = {
              if (lightState == TrafficLightState.VEHICLE_RED) {
                // Correct: Vehicles have red, pedestrian has green!
                viewModel.soundManager.playSuccess(gameState.soundEnabled)
                characterCrossing = true
                viewModel.completeLevel(3, isFirstTry)
                hasCompleted = true
              } else {
                // Dangerous: Vehicles have green/yellow!
                viewModel.failLevel(3)
                isFirstTry = false
                failureReason = if (lightState == TrafficLightState.VEHICLE_GREEN) {
                  "Vehicles have the green light and are driving at full speed! Stepping onto the road now causes a serious collision risk."
                } else {
                  "Yellow light means vehicles are preparing to change or stop. Pedestrians must NEVER rush across during yellow!"
                }
                hasFailed = true
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("action_cross_road"),
            colors = ButtonDefaults.buttonColors(
              containerColor = if (lightState == TrafficLightState.VEHICLE_RED) Color(0xFF2E7D32) else Color(0xFF00897B)
            ),
            shape = RoundedCornerShape(14.dp)
          ) {
            Icon(imageVector = Icons.Default.DirectionsWalk, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (lightState == TrafficLightState.VEHICLE_RED) "Safe to Cross Now! (Walk)" else "Cross Road Now",
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Option 2: Wait Patiently
          Button(
            onClick = {
              viewModel.soundManager.playClick(gameState.soundEnabled)
              // If waiting on green/yellow, good choice!
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("action_wait_light"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9)),
            shape = RoundedCornerShape(14.dp)
          ) {
            Text(
              text = "Wait for Signal to Turn Green ⏱️",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = Color(0xFF334155)
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
private fun TrafficLightBox(lightState: TrafficLightState) {
  Canvas(modifier = Modifier.size(90.dp, 200.dp)) {
    val w = size.width
    val h = size.height

    // Pole
    drawRect(
      color = Color(0xFF455A64),
      topLeft = Offset(w * 0.42f, h * 0.75f),
      size = Size(w * 0.16f, h * 0.25f)
    )

    // Housing Box
    drawRoundRect(
      color = Color(0xFF212121),
      topLeft = Offset(0f, 0f),
      size = Size(w, h * 0.75f),
      cornerRadius = CornerRadius(16f, 16f)
    )

    val bulbRadius = w * 0.28f
    val centerX = w * 0.5f

    // Red Bulb (Vehicles Red = Pedestrian Green!)
    val isRedActive = lightState == TrafficLightState.VEHICLE_RED
    drawCircle(
      color = if (isRedActive) Color(0xFFFF1744) else Color(0xFF421010),
      radius = bulbRadius,
      center = Offset(centerX, h * 0.16f)
    )
    if (isRedActive) {
      // Glow
      drawCircle(
        color = Color(0x66FF1744),
        radius = bulbRadius * 1.3f,
        center = Offset(centerX, h * 0.16f)
      )
    }

    // Yellow Bulb
    val isYellowActive = lightState == TrafficLightState.VEHICLE_YELLOW
    drawCircle(
      color = if (isYellowActive) Color(0xFFFFD600) else Color(0xFF423800),
      radius = bulbRadius,
      center = Offset(centerX, h * 0.38f)
    )
    if (isYellowActive) {
      drawCircle(
        color = Color(0x66FFD600),
        radius = bulbRadius * 1.3f,
        center = Offset(centerX, h * 0.38f)
      )
    }

    // Green Bulb
    val isGreenActive = lightState == TrafficLightState.VEHICLE_GREEN
    drawCircle(
      color = if (isGreenActive) Color(0xFF00E676) else Color(0xFF053818),
      radius = bulbRadius,
      center = Offset(centerX, h * 0.60f)
    )
    if (isGreenActive) {
      drawCircle(
        color = Color(0x6600E676),
        radius = bulbRadius * 1.3f,
        center = Offset(centerX, h * 0.60f)
      )
    }
  }
}
