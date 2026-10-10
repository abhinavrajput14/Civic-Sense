package com.example.ui.levels

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Diversity3
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.SportsHandball
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WorkspacePremium
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.ui.components.LivingCityBackground
import com.example.ui.components.SuccessDialog
import com.example.viewmodel.GameViewModel
import kotlinx.coroutines.delay

private enum class PlayStation {
  SWING,
  SLIDE,
  SEESAW
}

@Composable
fun Level15ShareTheSpaceScreen(
  viewModel: GameViewModel,
  onNextLevel: () -> Unit,
  onLevelMap: () -> Unit
) {
  val gameState by viewModel.gameState.collectAsState()
  val levelInfo = LevelCatalog.getLevel(15)!!

  var currentStation by remember { mutableStateOf(PlayStation.SWING) }
  var completedStations by remember { mutableIntStateOf(0) }
  var waitingTurnDone by remember { mutableStateOf(false) }
  var turnCountdown by remember { mutableIntStateOf(3) }
  var isCountingDown by remember { mutableStateOf(false) }

  var hasCompleted by remember { mutableStateOf(false) }
  var hasFailed by remember { mutableStateOf(false) }
  var failureReason by remember { mutableStateOf("") }
  var isFirstTry by remember { mutableStateOf(true) }

  // Turn timer effect
  LaunchedEffect(isCountingDown, turnCountdown) {
    if (isCountingDown && turnCountdown > 0) {
      delay(800)
      turnCountdown -= 1
    } else if (isCountingDown && turnCountdown == 0) {
      isCountingDown = false
      waitingTurnDone = true
      viewModel.soundManager.playCoin(gameState.soundEnabled)
    }
  }

  Box(modifier = Modifier.fillMaxSize()) {
    LivingCityBackground(showRoad = false, isParkScene = true)

    Column(
      modifier = Modifier
        .fillMaxSize()
        .navigationBarsPadding(),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      GameTopBar(
        gameState = gameState,
        onBackClick = onLevelMap,
        title = "Level 15: Share the Space",
        onSoundToggle = { viewModel.toggleSound() },
        onMusicToggle = { viewModel.toggleMusic() }
      )

      // Header Objective Banner
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.94f))
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(
              imageVector = Icons.Default.Groups,
              contentDescription = null,
              tint = Color(0xFFFB8C00),
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = when (currentStation) {
                PlayStation.SWING -> "Playground 1/3: Park Swings (Rohan is swinging happily)"
                PlayStation.SLIDE -> "Playground 2/3: Rainbow Slide (Kids lining up orderly)"
                PlayStation.SEESAW -> "Playground 3/3: Seesaw (Needs a cooperative partner)"
              },
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF1E293B)
            )
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(Color(0xFFFFF3E0))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = "$completedStations/3 Shared",
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp,
              color = Color(0xFFE65100)
            )
          }
        }
      }

      // Interactive Playground Scene
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.fillMaxWidth()
        ) {
          // Play Area Presentation Card
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.94f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
          ) {
            Column(
              modifier = Modifier.padding(18.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              // Station Icon & Graphic
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  CharacterAvatar(
                    character = gameState.character,
                    expression = if (waitingTurnDone) CharacterExpression.HAPPY else CharacterExpression.IDLE,
                    size = 90.dp
                  )
                  Text(
                    text = gameState.character.name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                  )
                }

                Box(
                  modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFF8E1)),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = when (currentStation) {
                      PlayStation.SWING -> "🎪"
                      PlayStation.SLIDE -> "🛝"
                      PlayStation.SEESAW -> "⚖️"
                    },
                    fontSize = 38.sp
                  )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Box(
                    modifier = Modifier
                      .size(80.dp)
                      .clip(CircleShape)
                      .background(Color(0xFFE8F5E9)),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = when (currentStation) {
                        PlayStation.SWING -> "👦"
                        PlayStation.SLIDE -> "👧"
                        PlayStation.SEESAW -> "🧑"
                      },
                      fontSize = 36.sp
                    )
                  }
                  Text(
                    text = when (currentStation) {
                      PlayStation.SWING -> "Rohan"
                      PlayStation.SLIDE -> "Meera"
                      PlayStation.SEESAW -> "Dev"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                  )
                }
              }

              Spacer(modifier = Modifier.height(14.dp))

              // Timer / Status Bar
              if (isCountingDown) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(imageVector = Icons.Default.Timer, contentDescription = null, tint = Color(0xFFFB8C00))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "Waiting patiently for Rohan's turn to finish: ${turnCountdown}s",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE65100)
                  )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                  progress = { (3 - turnCountdown) / 3f },
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                  color = Color(0xFFFB8C00),
                  trackColor = Color(0xFFFFE0B2)
                )
              } else if (waitingTurnDone) {
                Surface(
                  color = Color(0xFFE8F5E9),
                  shape = RoundedCornerShape(12.dp)
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "Turn taken respectfully! Now ready for next friend.",
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF1B5E20)
                    )
                  }
                }
              } else {
                Text(
                  text = when (currentStation) {
                    PlayStation.SWING -> "Rohan is enjoying the swing. How will you take your turn?"
                    PlayStation.SLIDE -> "Meera is preparing to slide down safely. How do you approach?"
                    PlayStation.SEESAW -> "Dev wants someone balanced and friendly to play seesaw together."
                  },
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Medium,
                  color = Color(0xFF475569),
                  textAlign = TextAlign.Center
                )
              }
            }
          }
        }
      }

      // Actions Panel
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
            text = "PLAYGROUND CONDUCT:",
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            color = Color(0xFFFB8C00),
            letterSpacing = 0.5.sp
          )
          Spacer(modifier = Modifier.height(10.dp))

          when (currentStation) {
            PlayStation.SWING -> {
              if (!waitingTurnDone && !isCountingDown) {
                PlayActionTile(
                  text = "Ask politely: 'May I take a turn on the swing after you, Rohan?' and wait",
                  isFair = true,
                  testTag = "swing_wait_turn",
                  onClick = {
                    isCountingDown = true
                    turnCountdown = 3
                  }
                )
                Spacer(modifier = Modifier.height(8.dp))
                PlayActionTile(
                  text = "Push Rohan off the swing and grab it forcefully",
                  isFair = false,
                  testTag = "swing_push",
                  onClick = {
                    isFirstTry = false
                    failureReason = "Pushing others off playground swings is dangerous and unfair. Always ask politely and wait for your turn!"
                    hasFailed = true
                    viewModel.failLevel(15)
                  }
                )
              } else if (waitingTurnDone) {
                Button(
                  onClick = {
                    completedStations = 1
                    waitingTurnDone = false
                    currentStation = PlayStation.SLIDE
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00897B)),
                  shape = RoundedCornerShape(16.dp),
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("next_station_slide")
                ) {
                  Text("Next: Rainbow Slide", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
              }
            }

            PlayStation.SLIDE -> {
              PlayActionTile(
                text = "Wait at the bottom of the ladder until Meera has fully exited the slide",
                isFair = true,
                testTag = "slide_wait_orderly",
                onClick = {
                  viewModel.soundManager.playCoin(gameState.soundEnabled)
                  completedStations = 2
                  currentStation = PlayStation.SEESAW
                }
              )
              Spacer(modifier = Modifier.height(8.dp))
              PlayActionTile(
                text = "Climb up backwards while Meera is sliding down to crash into her",
                isFair = false,
                testTag = "slide_climb_backwards",
                onClick = {
                  isFirstTry = false
                  failureReason = "Climbing up slides backwards can cause head injuries and collisions. Always use stairs and slide in one direction."
                  hasFailed = true
                  viewModel.failLevel(15)
                }
              )
            }

            PlayStation.SEESAW -> {
              PlayActionTile(
                text = "Sit gently on the opposite side of Dev and balance turns rhythmically",
                isFair = true,
                testTag = "seesaw_share_balance",
                onClick = {
                  completedStations = 3
                  viewModel.completeLevel(15, isFirstTry)
                  hasCompleted = true
                }
              )
              Spacer(modifier = Modifier.height(8.dp))
              PlayActionTile(
                text = "Suddenly jump off the seesaw while Dev is high in the air",
                isFair = false,
                testTag = "seesaw_jump_off",
                onClick = {
                  isFirstTry = false
                  failureReason = "Jumping off suddenly sends the partner crashing down onto the ground hard. Playground equipment requires mutual care."
                  hasFailed = true
                  viewModel.failLevel(15)
                }
              )
            }
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
          isCountingDown = false
          waitingTurnDone = false
          turnCountdown = 3
          viewModel.setExpression(CharacterExpression.IDLE)
        },
        onLevelMap = onLevelMap
      )
    }
  }
}

@Composable
private fun PlayActionTile(
  text: String,
  isFair: Boolean,
  testTag: String,
  onClick: () -> Unit
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .clickable(onClick = onClick)
      .testTag(testTag),
    color = if (isFair) Color(0xFFF1F8E9) else Color(0xFFFFF3E0),
    shape = RoundedCornerShape(14.dp),
    shadowElevation = 2.dp
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = if (isFair) Icons.Default.CheckCircle else Icons.Default.Park,
        contentDescription = null,
        tint = if (isFair) Color(0xFF2E7D32) else Color(0xFFE65100),
        modifier = Modifier.size(20.dp)
      )
      Spacer(modifier = Modifier.width(10.dp))
      Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = Color(0xFF1E293B),
        modifier = Modifier.weight(1f)
      )
    }
  }
}
