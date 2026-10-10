package com.example.ui.levels

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Elderly
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.VolunteerActivism
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.model.CharacterExpression
import com.example.model.LevelCatalog
import com.example.ui.components.CharacterAvatar
import com.example.ui.components.FailureDialog
import com.example.ui.components.GameTopBar
import com.example.ui.components.LivingCityBackground
import com.example.ui.components.SuccessDialog
import com.example.viewmodel.GameViewModel

private enum class HelpScenarioState {
  DROPPED_BAG,
  ELDER_DIRECTIONS,
  LOST_CHILD_SAFE_AREA
}

@Composable
fun Level11HelpingHandScreen(
  viewModel: GameViewModel,
  onNextLevel: () -> Unit,
  onLevelMap: () -> Unit
) {
  val gameState by viewModel.gameState.collectAsState()
  val levelInfo = LevelCatalog.getLevel(11)!!

  var currentScenario by remember { mutableStateOf(HelpScenarioState.DROPPED_BAG) }
  var completedCount by remember { mutableIntStateOf(0) }
  var playerApproached by remember { mutableStateOf(false) }
  var feedbackMessage by remember { mutableStateOf<String?>(null) }

  var hasCompleted by remember { mutableStateOf(false) }
  var hasFailed by remember { mutableStateOf(false) }
  var failureReason by remember { mutableStateOf("") }
  var isFirstTry by remember { mutableStateOf(true) }

  Box(modifier = Modifier.fillMaxSize()) {
    LivingCityBackground(showRoad = true)

    Column(
      modifier = Modifier
        .fillMaxSize()
        .navigationBarsPadding(),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      GameTopBar(
        gameState = gameState,
        onBackClick = onLevelMap,
        title = "Level 11: The Helping Hand",
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
              imageVector = Icons.Default.VolunteerActivism,
              contentDescription = null,
              tint = Color(0xFFE91E63),
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = when (currentScenario) {
                HelpScenarioState.DROPPED_BAG -> "Situation 1/3: A vendor's fruit packet fell and spilled on the walkway."
                HelpScenarioState.ELDER_DIRECTIONS -> "Situation 2/3: An elderly grandmother is looking around, unsure which road leads to the Clinic."
                HelpScenarioState.LOST_CHILD_SAFE_AREA -> "Situation 3/3: A little child is separated from family and crying by the busy gate."
              },
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF1E293B)
            )
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(Color(0xFFFCE4EC))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = "$completedCount/3 Helped",
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp,
              color = Color(0xFFC2185B)
            )
          }
        }
      }

      // Interactive Street Scene
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
          // NPC & Character Interactive Row
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(22.dp))
              .background(Color.White.copy(alpha = 0.92f))
              .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Player
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              CharacterAvatar(
                character = gameState.character,
                expression = if (playerApproached) CharacterExpression.WAVING else CharacterExpression.IDLE,
                size = 110.dp
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = gameState.character.name,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color(0xFF1E293B)
              )
              Text(
                text = if (playerApproached) "Approached Safely" else "Observing from walkway",
                fontSize = 10.sp,
                color = if (playerApproached) Color(0xFF2E7D32) else Color(0xFF78909C)
              )
            }

            // NPC Visual based on current situation
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Box(
                modifier = Modifier
                  .size(90.dp)
                  .clip(CircleShape)
                  .background(
                    when (currentScenario) {
                      HelpScenarioState.DROPPED_BAG -> Color(0xFFFFF3E0)
                      HelpScenarioState.ELDER_DIRECTIONS -> Color(0xFFE8EAF6)
                      HelpScenarioState.LOST_CHILD_SAFE_AREA -> Color(0xFFE0F2F1)
                    }
                  ),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = when (currentScenario) {
                    HelpScenarioState.DROPPED_BAG -> "🍎"
                    HelpScenarioState.ELDER_DIRECTIONS -> "👵"
                    HelpScenarioState.LOST_CHILD_SAFE_AREA -> "👦"
                  },
                  fontSize = 42.sp
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = when (currentScenario) {
                  HelpScenarioState.DROPPED_BAG -> "Vendor Ramesh"
                  HelpScenarioState.ELDER_DIRECTIONS -> "Dadi Shanti"
                  HelpScenarioState.LOST_CHILD_SAFE_AREA -> "Young Kabir"
                },
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color(0xFF1E293B)
              )
              Text(
                text = when (currentScenario) {
                  HelpScenarioState.DROPPED_BAG -> "Dropped belongings"
                  HelpScenarioState.ELDER_DIRECTIONS -> "Needs clinic route"
                  HelpScenarioState.LOST_CHILD_SAFE_AREA -> "Looking for parents"
                },
                fontSize = 10.sp,
                color = Color(0xFF78909C)
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Approach button if not approached yet
          if (!playerApproached) {
            Button(
              onClick = {
                playerApproached = true
                viewModel.soundManager.playClick(gameState.soundEnabled)
              },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00897B)),
              shape = RoundedCornerShape(16.dp),
              modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(46.dp)
                .testTag("approach_npc_button")
            ) {
              Icon(imageVector = Icons.Default.DirectionsWalk, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Walk Towards Them Gently",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
            }
          }

          // Feedback message
          feedbackMessage?.let { msg ->
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
              color = Color(0xFFE8F5E9),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.padding(horizontal = 8.dp)
            ) {
              Text(
                text = msg,
                color = Color(0xFF2E7D32),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                textAlign = TextAlign.Center
              )
            }
          }
        }
      }

      // Action Choices Panel
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
            text = if (playerApproached) "CHOOSE SAFE & HELPFUL ACTION:" else "FIRST: APPROACH THE SITUATION",
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            color = Color(0xFF00897B),
            letterSpacing = 0.5.sp
          )
          Spacer(modifier = Modifier.height(10.dp))

          if (!playerApproached) {
            Text(
              text = "Observe what is happening, then tap 'Walk Towards Them Gently' to get close enough to help safely.",
              fontSize = 12.sp,
              color = Color(0xFF64748B),
              textAlign = TextAlign.Center,
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )
          } else {
            when (currentScenario) {
              HelpScenarioState.DROPPED_BAG -> {
                HelpActionOption(
                  title = "Help pick up the dropped fruits and hand them over safely",
                  isSafeAndHelpful = true,
                  testTag = "help_action_pick_fruits",
                  onClick = {
                    viewModel.soundManager.playCoin(gameState.soundEnabled)
                    feedbackMessage = "Ramesh: 'Thank you so much! You saved my fruits!'"
                    completedCount = 1
                    playerApproached = false
                    currentScenario = HelpScenarioState.ELDER_DIRECTIONS
                  }
                )
                Spacer(modifier = Modifier.height(8.dp))
                HelpActionOption(
                  title = "Kick the fruits aside and laugh at the mishap",
                  isSafeAndHelpful = false,
                  testTag = "help_action_kick_fruits",
                  onClick = {
                    isFirstTry = false
                    failureReason = "Kicking or mocking someone who dropped their belongings is unkind. Always offer a helping hand!"
                    hasFailed = true
                    viewModel.failLevel(11)
                  }
                )
              }

              HelpScenarioState.ELDER_DIRECTIONS -> {
                HelpActionOption(
                  title = "Politely point towards the Community Clinic signboard 50m ahead",
                  isSafeAndHelpful = true,
                  testTag = "help_action_point_clinic",
                  onClick = {
                    viewModel.soundManager.playCoin(gameState.soundEnabled)
                    feedbackMessage = "Dadi: 'Bless you child! I was so confused by the crossroads.'"
                    completedCount = 2
                    playerApproached = false
                    currentScenario = HelpScenarioState.LOST_CHILD_SAFE_AREA
                  }
                )
                Spacer(modifier = Modifier.height(8.dp))
                HelpActionOption(
                  title = "Ignore her completely and walk away staring at your phone",
                  isSafeAndHelpful = false,
                  testTag = "help_action_ignore_elder",
                  onClick = {
                    isFirstTry = false
                    failureReason = "Elderly citizens may need simple guidance. Being helpful and patient creates a respectful community."
                    hasFailed = true
                    viewModel.failLevel(11)
                  }
                )
              }

              HelpScenarioState.LOST_CHILD_SAFE_AREA -> {
                HelpActionOption(
                  title = "Guide the child to the nearby Park Security Booth / Help Desk",
                  isSafeAndHelpful = true,
                  testTag = "help_action_security_booth",
                  onClick = {
                    completedCount = 3
                    viewModel.completeLevel(11, isFirstTry)
                    hasCompleted = true
                  }
                )
                Spacer(modifier = Modifier.height(8.dp))
                HelpActionOption(
                  title = "Tell the child to wander alone down the dark alley",
                  isSafeAndHelpful = false,
                  testTag = "help_action_unsafe_alley",
                  onClick = {
                    isFirstTry = false
                    failureReason = "Safety is paramount! Always escort a lost child to a recognized, safe authority like the Park Security Booth or Teacher."
                    hasFailed = true
                    viewModel.failLevel(11)
                  }
                )
              }
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
          playerApproached = false
          feedbackMessage = null
          viewModel.setExpression(CharacterExpression.IDLE)
        },
        onLevelMap = onLevelMap
      )
    }
  }
}

@Composable
private fun HelpActionOption(
  title: String,
  isSafeAndHelpful: Boolean,
  testTag: String,
  onClick: () -> Unit
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .clickable(onClick = onClick)
      .testTag(testTag),
    color = if (isSafeAndHelpful) Color(0xFFF1F8E9) else Color(0xFFFFF3E0),
    shape = RoundedCornerShape(14.dp),
    shadowElevation = 2.dp
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = if (isSafeAndHelpful) Icons.Default.CheckCircle else Icons.Default.Info,
        contentDescription = null,
        tint = if (isSafeAndHelpful) Color(0xFF2E7D32) else Color(0xFFE65100),
        modifier = Modifier.size(20.dp)
      )
      Spacer(modifier = Modifier.width(10.dp))
      Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = Color(0xFF1E293B),
        modifier = Modifier.weight(1f)
      )
    }
  }
}
