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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
  val cloudLevels by viewModel.cloudLevels.collectAsState()
  val levels = remember(cloudLevels, gameState) { viewModel.getAllCombinedLevels() }
  var showCreateDialog by remember { mutableStateOf(false) }

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
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "CIVIC ADVENTURE ROUTE",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Black,
                  color = Color(0xFF00897B),
                  letterSpacing = 0.5.sp
                )
                if (cloudLevels.isNotEmpty()) {
                  Spacer(modifier = Modifier.width(6.dp))
                  Icon(
                    Icons.Default.CloudDone,
                    contentDescription = "Cloud Connected",
                    tint = Color(0xFF1976D2),
                    modifier = Modifier.size(14.dp)
                  )
                }
              }
              Text(
                text = "Progress: ${gameState.completedLevels.size}/${levels.size} Levels (${cloudLevels.size} Cloud)",
                fontSize = 13.sp,
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
          val isCloudMission = level.id > 15 || cloudLevels.any { it.id == level.id }

          // World Divider Banner
          if (index == 0 || level.world != levels[index - 1].world) {
            val worldBg = when (level.world) {
              1 -> Color(0xFF00897B)
              2 -> Color(0xFF1976D2)
              3 -> Color(0xFF7B1FA2)
              else -> Color(0xFFE65100)
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
                    3 -> "Levels 11–15"
                    else -> "Cloud Missions"
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
            isCloudMission = isCloudMission,
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

        Spacer(modifier = Modifier.height(20.dp))

        // Creator Button to Add New Cloud Mission Live
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { showCreateDialog = true }
            .testTag("add_cloud_mission_button")
        ) {
          Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color(0xFFE8F5E9)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF2E7D32))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Add New Cloud Mission",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Publish new civic challenges to cloud live without updating APK!",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(30.dp))
      }
    }

    if (showCreateDialog) {
      CreateMissionDialog(
        viewModel = viewModel,
        onDismiss = { showCreateDialog = false }
      )
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
  isCloudMission: Boolean,
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
    elevation = CardDefaults.cardElevation(defaultElevation = if (isNextInLine) 8.dp else 2.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Level Badge / State Icon
      Box(
        modifier = Modifier
          .size(48.dp)
          .clip(CircleShape)
          .background(
            when {
              isCompleted -> Color(0xFFE0F2F1)
              isNextInLine -> Color.White.copy(alpha = 0.25f)
              else -> Color(0xFFCFD8DC)
            }
          ),
        contentAlignment = Alignment.Center
      ) {
        when {
          isCompleted -> Icon(Icons.Default.CheckCircle, contentDescription = "Completed", tint = Color(0xFF00897B), modifier = Modifier.size(28.dp))
          isNextInLine -> Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.White, modifier = Modifier.size(32.dp))
          else -> Icon(Icons.Default.Lock, contentDescription = "Locked", tint = Color(0xFF78909C), modifier = Modifier.size(24.dp))
        }
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "LEVEL ${level.id}",
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = if (isNextInLine) Color.White.copy(alpha = 0.85f) else Color(0xFF00897B),
            letterSpacing = 0.5.sp
          )
          if (isCloudMission) {
            Spacer(modifier = Modifier.width(6.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(if (isNextInLine) Color.White.copy(alpha = 0.3f) else Color(0xFFE3F2FD))
                .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
              Text(
                text = "CLOUD",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = if (isNextInLine) Color.White else Color(0xFF1976D2)
              )
            }
          }
        }
        Text(
          text = level.title,
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = contentColor
        )
        Text(
          text = level.subtitle,
          fontSize = 12.sp,
          color = if (isNextInLine) Color.White.copy(alpha = 0.85f) else Color(0xFF64748B),
          maxLines = 1
        )
      }

      // Stars or lock state
      if (isCompleted) {
        Row {
          repeat(3) { starIndex ->
            Icon(
              Icons.Default.Star,
              contentDescription = null,
              tint = if (starIndex < stars) Color(0xFFFFB300) else Color(0xFFCFD8DC),
              modifier = Modifier.size(18.dp)
            )
          }
        }
      } else if (isNextInLine) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
          Text(
            text = "PLAY",
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            color = Color(0xFF00897B)
          )
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

@Composable
fun CreateMissionDialog(
  viewModel: GameViewModel,
  onDismiss: () -> Unit
) {
  var title by remember { mutableStateOf("") }
  var subtitle by remember { mutableStateOf("") }
  var topic by remember { mutableStateOf("") }
  var scenario by remember { mutableStateOf("") }
  var objective by remember { mutableStateOf("") }
  var moral by remember { mutableStateOf("") }
  var choiceA by remember { mutableStateOf("") }
  var choiceB by remember { mutableStateOf("") }
  var isPublishing by remember { mutableStateOf(false) }
  var statusMessage by remember { mutableStateOf<String?>(null) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Cloud, contentDescription = null, tint = Color(0xFF1976D2))
        Spacer(modifier = Modifier.width(8.dp))
        Text("Create Cloud Mission", fontWeight = FontWeight.Bold)
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text(
          text = "This mission will be saved to Firebase Firestore and immediately show up live in the app without reinstalling!",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Mission Title") },
          placeholder = { Text("e.g. Cyber Safety Patrol") },
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = subtitle,
          onValueChange = { subtitle = it },
          label = { Text("Subtitle") },
          placeholder = { Text("e.g. Online Ethics & Scams") },
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = topic,
          onValueChange = { topic = it },
          label = { Text("Civic Topic") },
          placeholder = { Text("e.g. Digital Safety") },
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = scenario,
          onValueChange = { scenario = it },
          label = { Text("Situation / Scenario") },
          placeholder = { Text("You receive a suspicious lottery link...") },
          modifier = Modifier.fillMaxWidth(),
          minLines = 2
        )

        OutlinedTextField(
          value = objective,
          onValueChange = { objective = it },
          label = { Text("Mission Objective") },
          placeholder = { Text("Verify sender and do not click strange links.") },
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = moral,
          onValueChange = { moral = it },
          label = { Text("Moral Lesson") },
          placeholder = { Text("Protecting digital safety protects your community.") },
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = choiceA,
          onValueChange = { choiceA = it },
          label = { Text("Responsible Choice (Option A - Correct)") },
          placeholder = { Text("Report and block the unknown number.") },
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = choiceB,
          onValueChange = { choiceB = it },
          label = { Text("Irresponsible Choice (Option B)") },
          placeholder = { Text("Click the link and share private details.") },
          modifier = Modifier.fillMaxWidth()
        )

        if (statusMessage != null) {
          Text(
            text = statusMessage ?: "",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodySmall
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (title.isNotBlank() && scenario.isNotBlank()) {
            isPublishing = true
            val choices = listOf(
              choiceA.ifBlank { "Take the safe and lawful action." },
              choiceB.ifBlank { "Ignore safety precautions and act carelessly." }
            )
            viewModel.publishNewCloudLevel(
              title = title.trim(),
              subtitle = subtitle.ifBlank { "Community Challenge" }.trim(),
              topic = topic.ifBlank { "Civic Duty" }.trim(),
              scenario = scenario.trim(),
              objective = objective.ifBlank { "Resolve the civic challenge safely." }.trim(),
              moral = moral.ifBlank { "Good civic habits make better cities." }.trim(),
              choices = choices,
              correctChoiceIndex = 0,
              onResult = { success, msg ->
                isPublishing = false
                statusMessage = msg
                if (success) {
                  onDismiss()
                }
              }
            )
          }
        },
        enabled = !isPublishing && title.isNotBlank() && scenario.isNotBlank()
      ) {
        Text(if (isPublishing) "Publishing..." else "Publish Live")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}
