package com.example.ui.levels

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Recycling
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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

enum class BinCategory {
  WET_ORGANIC,
  DRY_RECYCLABLE,
  PAPER
}

data class WasteItem(
  val id: Int,
  val name: String,
  val emoji: String,
  val correctCategory: BinCategory,
  val explanation: String
)

@Composable
fun Level02SortWasteScreen(
  viewModel: GameViewModel,
  onNextLevel: () -> Unit,
  onLevelMap: () -> Unit
) {
  val gameState by viewModel.gameState.collectAsState()
  val levelInfo = LevelCatalog.getLevel(2)!!

  val wasteItems = remember {
    listOf(
      WasteItem(1, "Banana Peel", "🍌", BinCategory.WET_ORGANIC, "Fruit peels are organic wet waste that can decompose into natural compost."),
      WasteItem(2, "Plastic Soda Bottle", "🧴", BinCategory.DRY_RECYCLABLE, "Plastic bottles are non-biodegradable recyclable dry waste."),
      WasteItem(3, "Old School Notebook", "📄", BinCategory.PAPER, "Clean dry paper can be easily recycled into new books and boxes."),
      WasteItem(4, "Leftover Apple Core", "🍎", BinCategory.WET_ORGANIC, "Food leftovers belong in the Wet bin to produce biogas or manure.")
    )
  }

  var currentItemIndex by remember { mutableIntStateOf(0) }
  var hasCompleted by remember { mutableStateOf(false) }
  var hasFailed by remember { mutableStateOf(false) }
  var failureReason by remember { mutableStateOf("") }
  var isFirstTry by remember { mutableStateOf(true) }

  val currentItem = if (currentItemIndex < wasteItems.size) wasteItems[currentItemIndex] else null

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          listOf(Color(0xFFE8F5E9), Color(0xFFE0F7FA), Color(0xFFFFF9C4))
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
        title = "Level 2: Sort It Right",
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
            imageVector = Icons.Default.Recycling,
            contentDescription = null,
            tint = Color(0xFF00897B),
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "Sort all 4 items into their correct color-coded bins (${currentItemIndex}/4 completed)",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1E293B)
          )
        }
      }

      // Center Stage: Current Waste Item & Character
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        if (currentItem != null) {
          // Waste Item Showcase Card
          Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier.padding(bottom = 12.dp)
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp)
            ) {
              Text(
                text = "ITEM TO SORT",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF00897B),
                letterSpacing = 0.5.sp
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = currentItem.emoji,
                fontSize = 52.sp
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = currentItem.name,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                color = Color(0xFF1E293B)
              )
            }
          }
        }

        CharacterAvatar(
          character = gameState.character,
          expression = if (currentItemIndex > 0) CharacterExpression.HAPPY else CharacterExpression.POINTING,
          size = 140.dp
        )
      }

      // Three Color-Coded Bins
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
          Text(
            text = "TAP THE CORRECT BIN:",
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            color = Color(0xFF00897B),
            letterSpacing = 0.5.sp
          )

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // Bin 1: Wet / Organic (Green)
            WasteBinButton(
              title = "WET",
              subtitle = "Organic / Food",
              color = Color(0xFF2E7D32),
              modifier = Modifier.weight(1f),
              testTag = "bin_wet",
              onClick = {
                handleBinChoice(
                  chosen = BinCategory.WET_ORGANIC,
                  currentItem = currentItem,
                  onCorrect = {
                    viewModel.soundManager.playCoin(gameState.soundEnabled)
                    currentItemIndex++
                    if (currentItemIndex >= wasteItems.size) {
                      viewModel.completeLevel(2, isFirstTry)
                      hasCompleted = true
                    }
                  },
                  onWrong = { reason ->
                    isFirstTry = false
                    failureReason = reason
                    viewModel.failLevel(2)
                    hasFailed = true
                  }
                )
              }
            )

            // Bin 2: Dry / Recyclable (Blue)
            WasteBinButton(
              title = "DRY",
              subtitle = "Plastic / Metal",
              color = Color(0xFF1565C0),
              modifier = Modifier.weight(1f),
              testTag = "bin_dry",
              onClick = {
                handleBinChoice(
                  chosen = BinCategory.DRY_RECYCLABLE,
                  currentItem = currentItem,
                  onCorrect = {
                    viewModel.soundManager.playCoin(gameState.soundEnabled)
                    currentItemIndex++
                    if (currentItemIndex >= wasteItems.size) {
                      viewModel.completeLevel(2, isFirstTry)
                      hasCompleted = true
                    }
                  },
                  onWrong = { reason ->
                    isFirstTry = false
                    failureReason = reason
                    viewModel.failLevel(2)
                    hasFailed = true
                  }
                )
              }
            )

            // Bin 3: Paper (Yellow)
            WasteBinButton(
              title = "PAPER",
              subtitle = "Cardboard / Books",
              color = Color(0xFFF57F17),
              modifier = Modifier.weight(1f),
              testTag = "bin_paper",
              onClick = {
                handleBinChoice(
                  chosen = BinCategory.PAPER,
                  currentItem = currentItem,
                  onCorrect = {
                    viewModel.soundManager.playCoin(gameState.soundEnabled)
                    currentItemIndex++
                    if (currentItemIndex >= wasteItems.size) {
                      viewModel.completeLevel(2, isFirstTry)
                      hasCompleted = true
                    }
                  },
                  onWrong = { reason ->
                    isFirstTry = false
                    failureReason = reason
                    viewModel.failLevel(2)
                    hasFailed = true
                  }
                )
              }
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
          currentItemIndex = 0
          viewModel.setExpression(CharacterExpression.IDLE)
        },
        onLevelMap = onLevelMap
      )
    }
  }
}

private fun handleBinChoice(
  chosen: BinCategory,
  currentItem: WasteItem?,
  onCorrect: () -> Unit,
  onWrong: (String) -> Unit
) {
  if (currentItem == null) return
  if (chosen == currentItem.correctCategory) {
    onCorrect()
  } else {
    val binName = when (chosen) {
      BinCategory.WET_ORGANIC -> "Wet Organic"
      BinCategory.DRY_RECYCLABLE -> "Dry Recyclable"
      BinCategory.PAPER -> "Paper"
    }
    onWrong("Mixing ${currentItem.name} into the $binName bin contaminates the waste. ${currentItem.explanation}")
  }
}

@Composable
private fun WasteBinButton(
  title: String,
  subtitle: String,
  color: Color,
  modifier: Modifier = Modifier,
  testTag: String,
  onClick: () -> Unit
) {
  Surface(
    modifier = modifier
      .height(84.dp)
      .clip(RoundedCornerShape(16.dp))
      .clickable(onClick = onClick)
      .testTag(testTag),
    color = color,
    shape = RoundedCornerShape(16.dp),
    shadowElevation = 4.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Text(
        text = title,
        fontWeight = FontWeight.Black,
        fontSize = 15.sp,
        color = Color.White
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = subtitle,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        color = Color.White.copy(alpha = 0.9f),
        textAlign = TextAlign.Center,
        lineHeight = 12.sp
      )
    }
  }
}
