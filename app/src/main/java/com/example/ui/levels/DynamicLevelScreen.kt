package com.example.ui.levels

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.RadioBottle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.model.CloudLevel
import com.example.ui.components.LevelSuccessDialog
import com.example.ui.components.LivingCityBackground
import com.example.viewmodel.GameViewModel

@Composable
fun DynamicLevelScreen(
  level: CloudLevel,
  viewModel: GameViewModel,
  onNextLevel: () -> Unit,
  onLevelMap: () -> Unit
) {
  var selectedChoice by remember { mutableIntStateOf(-1) }
  var hasSubmitted by remember { mutableStateOf(false) }
  var isCorrect by remember { mutableStateOf(false) }
  var showSuccessDialog by remember { mutableStateOf(false) }

  // Default choices if level doesn't specify custom questions
  val choices = remember(level) {
    if (level.questions.isNotEmpty() && level.questions.first().options.isNotEmpty()) {
      level.questions.first().options
    } else {
      listOf(
        "Take responsible civic action and follow the rules.",
        "Ignore the situation and walk away without helping.",
        "Take a shortcut that breaks community safety rules."
      )
    }
  }

  val correctIndex = remember(level) {
    if (level.questions.isNotEmpty()) level.questions.first().correctIndex else 0
  }

  Box(modifier = Modifier.fillMaxSize()) {
    LivingCityBackground()

    Scaffold(
      containerColor = Color.Transparent,
      topBar = {
        Surface(
          color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
          shadowElevation = 4.dp
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            IconButton(
              onClick = onLevelMap,
              modifier = Modifier.testTag("dynamic_level_back_button")
            ) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to map")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  Icons.Default.Cloud,
                  contentDescription = "Cloud Mission",
                  tint = Color(0xFF1976D2),
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "Level ${level.id}: ${level.title}",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold
                )
              }
              Text(
                text = "${level.worldTitle} • ${level.topic}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }
    ) { innerPadding ->
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
          .padding(16.dp)
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // Mission Briefing Card
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)),
          elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Flag, contentDescription = null, tint = Color(0xFF2E7D32))
              }
              Spacer(modifier = Modifier.width(12.dp))
              Text(
                text = "Mission Objective",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = level.objective.ifBlank { "Solve this civic community challenge with good civic sense!" },
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurface
            )
          }
        }

        // Real-Life Scenario Card
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)),
          elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(Color(0xFFE3F2FD)),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF1976D2))
              }
              Spacer(modifier = Modifier.width(12.dp))
              Text(
                text = "The Situation",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = level.scenarioDescription.ifBlank { "You face an everyday civic situation in the city." },
              style = MaterialTheme.typography.bodyLarge,
              color = MaterialTheme.colorScheme.onSurface
            )
          }
        }

        // Decision Choices
        Text(
          text = "What is the responsible choice?",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )

        choices.forEachIndexed { index, choiceText ->
          val isSelected = selectedChoice == index
          val containerColor = when {
            hasSubmitted && index == correctIndex -> Color(0xFFC8E6C9)
            hasSubmitted && isSelected && !isCorrect -> Color(0xFFFFCDD2)
            isSelected -> MaterialTheme.colorScheme.primaryContainer
            else -> MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
          }

          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = containerColor),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 6.dp else 2.dp),
            modifier = Modifier
              .fillMaxWidth()
              .clickable(enabled = !hasSubmitted) {
                selectedChoice = index
              }
              .testTag("dynamic_choice_$index")
          ) {
            Row(
              modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(28.dp)
                  .clip(CircleShape)
                  .background(
                    if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.5f)
                  ),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = ('A' + index).toString(),
                  color = if (isSelected) MaterialTheme.colorScheme.onPrimary else Color.DarkGray,
                  fontWeight = FontWeight.Bold
                )
              }
              Spacer(modifier = Modifier.width(14.dp))
              Text(
                text = choiceText,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                modifier = Modifier.weight(1f)
              )
            }
          }
        }

        // Action Button
        if (!hasSubmitted) {
          Button(
            onClick = {
              if (selectedChoice != -1) {
                hasSubmitted = true
                val success = selectedChoice == correctIndex
                isCorrect = success
                if (success) {
                  viewModel.completeLevel(
                    levelId = level.id,
                    stars = 3,
                    firstTry = true,
                    coinsReward = level.rewardCoins,
                    xpReward = level.rewardXp,
                    civicReward = level.rewardCivicPoints,
                    badgeId = level.badgeId.ifBlank { null }
                  )
                  showSuccessDialog = true
                }
              }
            },
            enabled = selectedChoice != -1,
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("dynamic_level_submit_button"),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("Confirm Choice", fontWeight = FontWeight.Bold)
          }
        } else if (!isCorrect) {
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text(
                text = "Think again!",
                fontWeight = FontWeight.Bold,
                color = Color(0xFFC62828)
              )
              Text(
                text = "Civic responsibility is about looking out for safety, fairness, and cleanliness for everyone.",
                style = MaterialTheme.typography.bodySmall
              )
              Spacer(modifier = Modifier.height(10.dp))
              Button(
                onClick = {
                  hasSubmitted = false
                  selectedChoice = -1
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
              ) {
                Text("Try Again")
              }
            }
          }
        }

        // Moral lesson card
        if (level.moralMessage.isNotBlank()) {
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1).copy(alpha = 0.95f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFFF57F17))
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = level.moralMessage,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF4E342E)
              )
            }
          }
        }
      }
    }

    if (showSuccessDialog) {
      LevelSuccessDialog(
        levelId = level.id,
        levelTitle = level.title,
        moralMessage = level.moralMessage.ifBlank { "Great civic action!" },
        coinsEarned = level.rewardCoins,
        xpEarned = level.rewardXp,
        civicPointsEarned = level.rewardCivicPoints,
        stars = 3,
        unlockedBadge = level.badgeId.ifBlank { null },
        onNextLevel = onNextLevel,
        onLevelMap = onLevelMap,
        onDismiss = { showSuccessDialog = false }
      )
    }
  }
}
