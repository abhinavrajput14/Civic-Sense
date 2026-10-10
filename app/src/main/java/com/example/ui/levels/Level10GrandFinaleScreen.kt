package com.example.ui.levels

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Elderly
import androidx.compose.material.icons.filled.WaterDrop
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
import com.example.ui.components.SuccessDialog
import com.example.viewmodel.GameViewModel

@Composable
fun Level10GrandFinaleScreen(
  viewModel: GameViewModel,
  onNextLevel: () -> Unit,
  onLevelMap: () -> Unit
) {
  val gameState by viewModel.gameState.collectAsState()
  val levelInfo = LevelCatalog.getLevel(10)!!

  var trashBinned by remember { mutableStateOf(false) }
  var tapClosed by remember { mutableStateOf(false) }
  var zebraCrossed by remember { mutableStateOf(false) }
  var elderHelped by remember { mutableStateOf(false) }

  var hasCompleted by remember { mutableStateOf(false) }
  var hasFailed by remember { mutableStateOf(false) }
  var failureReason by remember { mutableStateOf("") }
  var isFirstTry by remember { mutableStateOf(true) }

  val completedTasksCount = listOf(trashBinned, tapClosed, zebraCrossed, elderHelped).count { it }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          listOf(Color(0xFFE0F7FA), Color(0xFFFFF9C4), Color(0xFFFFE0B2))
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
        title = "Level 10: Grand Finale",
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
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.WorkspacePremium,
                contentDescription = null,
                tint = Color(0xFFFF8F00),
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "The Central City Chowk Challenge!",
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF1E293B)
              )
            }
            Text(
              text = "$completedTasksCount/4 Done",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              color = Color(0xFF00897B)
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          LinearProgressIndicator(
            progress = { completedTasksCount / 4f },
            modifier = Modifier
              .fillMaxWidth()
              .height(6.dp)
              .clip(RoundedCornerShape(3.dp)),
            color = Color(0xFF00897B),
            trackColor = Color(0xFFE2E8F0)
          )
        }
      }

      // Hero Character & City Square Center
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
          CharacterAvatar(
            character = gameState.character,
            expression = if (completedTasksCount == 4) CharacterExpression.CELEBRATING else CharacterExpression.POINTING,
            size = 140.dp
          )

          Spacer(modifier = Modifier.height(10.dp))

          // 4 Civic Task Cards Grid in the City Chowk
          LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            // Task 1: Cleanliness
            item {
              FinaleTaskCard(
                title = "Juice Box in Bin",
                icon = Icons.Default.Delete,
                isDone = trashBinned,
                activeColor = Color(0xFF2E7D32),
                testTag = "finale_task_trash",
                onClick = {
                  if (!trashBinned) {
                    trashBinned = true
                    viewModel.soundManager.playCoin(gameState.soundEnabled)
                    checkCompletion(
                      trashBinned = true,
                      tapClosed = tapClosed,
                      zebraCrossed = zebraCrossed,
                      elderHelped = elderHelped,
                      onAllDone = {
                        viewModel.completeLevel(10, isFirstTry)
                        hasCompleted = true
                      }
                    )
                  }
                }
              )
            }

            // Task 2: Water Care
            item {
              FinaleTaskCard(
                title = "Shut Dripping Tap",
                icon = Icons.Default.WaterDrop,
                isDone = tapClosed,
                activeColor = Color(0xFF0288D1),
                testTag = "finale_task_water",
                onClick = {
                  if (!tapClosed) {
                    tapClosed = true
                    viewModel.soundManager.playCoin(gameState.soundEnabled)
                    checkCompletion(
                      trashBinned = trashBinned,
                      tapClosed = true,
                      zebraCrossed = zebraCrossed,
                      elderHelped = elderHelped,
                      onAllDone = {
                        viewModel.completeLevel(10, isFirstTry)
                        hasCompleted = true
                      }
                    )
                  }
                }
              )
            }

            // Task 3: Zebra Crossing Safety
            item {
              FinaleTaskCard(
                title = "Cross on Zebra",
                icon = Icons.Default.DirectionsWalk,
                isDone = zebraCrossed,
                activeColor = Color(0xFF00897B),
                testTag = "finale_task_zebra",
                onClick = {
                  if (!zebraCrossed) {
                    zebraCrossed = true
                    viewModel.soundManager.playCoin(gameState.soundEnabled)
                    checkCompletion(
                      trashBinned = trashBinned,
                      tapClosed = tapClosed,
                      zebraCrossed = true,
                      elderHelped = elderHelped,
                      onAllDone = {
                        viewModel.completeLevel(10, isFirstTry)
                        hasCompleted = true
                      }
                    )
                  }
                }
              )
            }

            // Task 4: Elder Help
            item {
              FinaleTaskCard(
                title = "Assist Elder Neighbor",
                icon = Icons.Default.Elderly,
                isDone = elderHelped,
                activeColor = Color(0xFFE65100),
                testTag = "finale_task_elder",
                onClick = {
                  if (!elderHelped) {
                    elderHelped = true
                    viewModel.soundManager.playCoin(gameState.soundEnabled)
                    checkCompletion(
                      trashBinned = trashBinned,
                      tapClosed = tapClosed,
                      zebraCrossed = zebraCrossed,
                      elderHelped = true,
                      onAllDone = {
                        viewModel.completeLevel(10, isFirstTry)
                        hasCompleted = true
                      }
                    )
                  }
                }
              )
            }
          }
        }
      }

      // Bottom Call to Action Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
      ) {
        Column(
          modifier = Modifier.padding(14.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          if (completedTasksCount == 4) {
            Button(
              onClick = {
                viewModel.soundManager.playSuccess(gameState.soundEnabled)
                hasCompleted = true
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("finale_claim_button"),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00897B)),
              shape = RoundedCornerShape(16.dp)
            ) {
              Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "CLAIM MASTER CITIZEN BADGE! 🏆",
                fontWeight = FontWeight.Black,
                fontSize = 14.sp
              )
            }
          } else {
            Text(
              text = "Tap all 4 stations above to complete your final civic quest!",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF64748B),
              textAlign = TextAlign.Center
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
          trashBinned = false
          tapClosed = false
          zebraCrossed = false
          elderHelped = false
          viewModel.setExpression(CharacterExpression.IDLE)
        },
        onLevelMap = onLevelMap
      )
    }
  }
}

private fun checkCompletion(
  trashBinned: Boolean,
  tapClosed: Boolean,
  zebraCrossed: Boolean,
  elderHelped: Boolean,
  onAllDone: () -> Unit
) {
  if (trashBinned && tapClosed && zebraCrossed && elderHelped) {
    onAllDone()
  }
}

@Composable
private fun FinaleTaskCard(
  title: String,
  icon: ImageVector,
  isDone: Boolean,
  activeColor: Color,
  testTag: String,
  onClick: () -> Unit
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .height(84.dp)
      .clip(RoundedCornerShape(16.dp))
      .clickable(onClick = onClick)
      .testTag(testTag),
    color = if (isDone) activeColor.copy(alpha = 0.15f) else Color.White,
    shape = RoundedCornerShape(16.dp),
    border = androidx.compose.foundation.BorderStroke(
      width = if (isDone) 2.dp else 1.dp,
      color = if (isDone) activeColor else Color(0xFFE2E8F0)
    ),
    shadowElevation = if (isDone) 1.dp else 3.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxSize()
        .padding(10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(38.dp)
          .clip(CircleShape)
          .background(if (isDone) activeColor else Color(0xFFF1F5F9)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = if (isDone) Icons.Default.CheckCircle else icon,
          contentDescription = title,
          tint = if (isDone) Color.White else activeColor,
          modifier = Modifier.size(22.dp)
        )
      }

      Spacer(modifier = Modifier.width(8.dp))

      Column {
        Text(
          text = title,
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp,
          color = if (isDone) activeColor else Color(0xFF1E293B),
          maxLines = 2,
          lineHeight = 14.sp
        )
        Text(
          text = if (isDone) "COMPLETED" else "TAP TO ACT",
          fontSize = 9.sp,
          fontWeight = FontWeight.Black,
          color = if (isDone) activeColor else Color(0xFF94A3B8)
        )
      }
    }
  }
}
