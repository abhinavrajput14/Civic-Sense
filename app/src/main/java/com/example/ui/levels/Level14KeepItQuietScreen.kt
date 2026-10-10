package com.example.ui.levels

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

private data class QuietZone(
  val name: String,
  val icon: ImageVector,
  val description: String,
  val targetDecibels: Int,
  val initialDecibels: Int,
  val contextDetail: String
)

@Composable
fun Level14KeepItQuietScreen(
  viewModel: GameViewModel,
  onNextLevel: () -> Unit,
  onLevelMap: () -> Unit
) {
  val gameState by viewModel.gameState.collectAsState()
  val levelInfo = LevelCatalog.getLevel(14)!!

  val zones = remember {
    listOf(
      QuietZone(
        name = "Public Library Reading Hall",
        icon = Icons.Default.MenuBook,
        description = "Readers are concentrating. A mobile phone rings at full volume with loud video clips playing without earphones.",
        targetDecibels = 30,
        initialDecibels = 85,
        contextDetail = "Readers cover their ears in distress!"
      ),
      QuietZone(
        name = "Hospital Silent Zone",
        icon = Icons.Default.LocalHospital,
        description = "Patients are resting in the recovery ward. Someone is shouting loudly into a phone on speaker.",
        targetDecibels = 35,
        initialDecibels = 90,
        contextDetail = "Rest is vital for patient recovery."
      ),
      QuietZone(
        name = "School Exam Hall Corridor",
        icon = Icons.Default.School,
        description = "Students are taking an examination. Loud laughter and running footsteps echo in the hallway.",
        targetDecibels = 25,
        initialDecibels = 80,
        contextDetail = "Noise disrupts students' focus."
      )
    )
  }

  var currentZoneIndex by remember { mutableIntStateOf(0) }
  var currentVolumeSlider by remember { mutableFloatStateOf(85f) }
  var headphonesEquipped by remember { mutableStateOf(false) }

  var hasCompleted by remember { mutableStateOf(false) }
  var hasFailed by remember { mutableStateOf(false) }
  var failureReason by remember { mutableStateOf("") }
  var isFirstTry by remember { mutableStateOf(true) }

  val currentZone = zones[currentZoneIndex]
  val isQuietEnough = currentVolumeSlider <= currentZone.targetDecibels || (headphonesEquipped && currentVolumeSlider <= 50)

  val soundColor = when {
    currentVolumeSlider <= 35 -> Color(0xFF2E7D32)
    currentVolumeSlider <= 60 -> Color(0xFFFFA000)
    else -> Color(0xFFD32F2F)
  }

  Box(modifier = Modifier.fillMaxSize()) {
    LivingCityBackground(showRoad = false)

    Column(
      modifier = Modifier
        .fillMaxSize()
        .navigationBarsPadding(),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      GameTopBar(
        gameState = gameState,
        onBackClick = onLevelMap,
        title = "Level 14: Keep It Quiet",
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
              imageVector = currentZone.icon,
              contentDescription = null,
              tint = Color(0xFF0288D1),
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "Zone ${currentZoneIndex + 1}/3: ${currentZone.name}",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF1E293B)
            )
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(Color(0xFFE1F5FE))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = "${currentZoneIndex}/3 Controlled",
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp,
              color = Color(0xFF0277BD)
            )
          }
        }
      }

      // Decibel Meter and Environment Visualization
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
          // Interactive Sound Level Monitor Card
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
          ) {
            Column(
              modifier = Modifier.padding(18.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = "COMMUNITY SOUND LEVEL MONITOR",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF0288D1),
                letterSpacing = 0.5.sp
              )
              Spacer(modifier = Modifier.height(10.dp))

              // Decibel Gauge Display
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
              ) {
                Icon(
                  imageVector = if (isQuietEnough) Icons.Default.VolumeDown else Icons.Default.VolumeUp,
                  contentDescription = null,
                  tint = soundColor,
                  modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "${currentVolumeSlider.toInt()} dB",
                  fontWeight = FontWeight.Black,
                  fontSize = 32.sp,
                  color = soundColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "(Limit: ${currentZone.targetDecibels} dB)",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF64748B)
                )
              }

              Spacer(modifier = Modifier.height(8.dp))

              // Progress Bar Visualizer
              LinearProgressIndicator(
                progress = { (currentVolumeSlider / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(12.dp)
                  .clip(RoundedCornerShape(6.dp)),
                color = soundColor,
                trackColor = Color(0xFFE2E8F0)
              )

              Spacer(modifier = Modifier.height(10.dp))

              Text(
                text = if (isQuietEnough) "Quiet & Peaceful ✓ (Considerate)" else "TOO LOUD! Disturbs people around you ⚠️",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isQuietEnough) Color(0xFF2E7D32) else Color(0xFFD32F2F)
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Zone Situation Details Card
          Surface(
            color = Color(0xFFF0FDF4),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              CharacterAvatar(
                character = gameState.character,
                expression = if (isQuietEnough) CharacterExpression.HAPPY else CharacterExpression.CONFUSED,
                size = 65.dp
              )
              Spacer(modifier = Modifier.width(12.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = currentZone.description,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Medium,
                  color = Color(0xFF1E293B)
                )
                if (headphonesEquipped) {
                  Text(
                    text = "🎧 Headphones enabled: audio kept to yourself!",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0288D1)
                  )
                }
              }
            }
          }
        }
      }

      // Interactive Sound Control Panel
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
            text = "ADJUST AUDIO & BEHAVIOUR:",
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            color = Color(0xFF0288D1),
            letterSpacing = 0.5.sp
          )
          Spacer(modifier = Modifier.height(6.dp))

          // Slider to lower volume
          Slider(
            value = currentVolumeSlider,
            onValueChange = { currentVolumeSlider = it },
            valueRange = 10f..100f,
            steps = 9,
            colors = SliderDefaults.colors(
              thumbColor = soundColor,
              activeTrackColor = soundColor
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("volume_slider")
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = "Whisper (10 dB)", fontSize = 10.sp, color = Color(0xFF64748B))
            Text(text = "Shouting/Blaring (100 dB)", fontSize = 10.sp, color = Color(0xFF64748B))
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Quick Headphones / Silence Toggle
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = {
                headphonesEquipped = true
                currentVolumeSlider = 20f
                viewModel.soundManager.playClick(gameState.soundEnabled)
              },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE1F5FE)),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .weight(1f)
                .testTag("plug_headphones_button")
            ) {
              Icon(imageVector = Icons.Default.Headphones, contentDescription = null, tint = Color(0xFF0288D1))
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Plug Headphones",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0277BD)
              )
            }

            Button(
              onClick = {
                currentVolumeSlider = 100f
                isFirstTry = false
                failureReason = "Shouting and blasting speakers in quiet zones like hospitals or libraries causes distress to patients and learners."
                hasFailed = true
                viewModel.failLevel(14)
              },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE)),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .weight(1f)
                .testTag("blast_speaker_button")
            ) {
              Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null, tint = Color(0xFFD32F2F))
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Blast Speaker",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD32F2F)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Confirm Quiet Behaviour Button
          Button(
            onClick = {
              if (isQuietEnough) {
                viewModel.soundManager.playCoin(gameState.soundEnabled)
                if (currentZoneIndex < zones.size - 1) {
                  currentZoneIndex += 1
                  val nextZone = zones[currentZoneIndex]
                  currentVolumeSlider = nextZone.initialDecibels.toFloat()
                  headphonesEquipped = false
                } else {
                  viewModel.completeLevel(14, isFirstTry)
                  hasCompleted = true
                }
              } else {
                isFirstTry = false
                failureReason = "Volume (${currentVolumeSlider.toInt()} dB) is above the zone limit (${currentZone.targetDecibels} dB). Lower the slider or put on headphones!"
                hasFailed = true
                viewModel.failLevel(14)
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = if (isQuietEnough) Color(0xFF00897B) else Color(0xFF94A3B8)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("confirm_quiet_button")
          ) {
            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (currentZoneIndex < zones.size - 1) "Confirm Respectful Quiet & Next Zone" else "Complete Quiet Zone Challenge",
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
          currentVolumeSlider = currentZone.initialDecibels.toFloat()
          headphonesEquipped = false
          viewModel.setExpression(CharacterExpression.IDLE)
        },
        onLevelMap = onLevelMap
      )
    }
  }
}
