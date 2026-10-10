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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Elderly
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
fun Level08BusEtiquetteScreen(
  viewModel: GameViewModel,
  onNextLevel: () -> Unit,
  onLevelMap: () -> Unit
) {
  val gameState by viewModel.gameState.collectAsState()
  val levelInfo = LevelCatalog.getLevel(8)!!

  var seatOffered by remember { mutableStateOf(false) }
  var hasCompleted by remember { mutableStateOf(false) }
  var hasFailed by remember { mutableStateOf(false) }
  var failureReason by remember { mutableStateOf("") }
  var isFirstTry by remember { mutableStateOf(true) }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          listOf(Color(0xFFFFF3E0), Color(0xFFFFE0B2), Color(0xFFFFCC80))
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
        title = "Level 8: Ride Right",
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
            imageVector = Icons.Default.DirectionsBus,
            contentDescription = null,
            tint = Color(0xFFE65100),
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "You are on a packed city bus. An elderly person with a cane boards and cannot find a seat.",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1E293B)
          )
        }
      }

      // Bus Interior Scene
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
          // Bus Seat & Elderly Passenger Visual
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(20.dp))
              .background(Color.White.copy(alpha = 0.9f))
              .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Box(
                modifier = Modifier
                  .size(54.dp)
                  .clip(CircleShape)
                  .background(Color(0xFFFFECB3)),
                contentAlignment = Alignment.Center
              ) {
                Text(text = "👴", fontSize = 28.sp)
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = if (seatOffered) "Elderly Passenger (Seated)" else "Elderly Passenger (Standing)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5D4037)
              )
            }

            // Seat status indicator
            Surface(
              color = if (seatOffered) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
              shape = RoundedCornerShape(12.dp)
            ) {
              Text(
                text = if (seatOffered) "Seat Offered! ❤️" else "You are seated",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (seatOffered) Color(0xFF2E7D32) else Color(0xFFC62828),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          CharacterAvatar(
            character = gameState.character,
            expression = if (seatOffered) CharacterExpression.WAVING else CharacterExpression.POINTING,
            size = 140.dp
          )
        }
      }

      // Decision Card
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
            text = "WHAT WILL YOU DO?",
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            color = Color(0xFFE65100),
            letterSpacing = 0.5.sp
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Option 1 (Correct): Offer Seat
          Button(
            onClick = {
              seatOffered = true
              viewModel.soundManager.playCoin(gameState.soundEnabled)
              viewModel.completeLevel(8, isFirstTry)
              hasCompleted = true
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("action_offer_seat"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100)),
            shape = RoundedCornerShape(14.dp)
          ) {
            Icon(imageVector = Icons.Default.Elderly, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Stand Up & Offer Your Seat to the Elder",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Option 2 (Incorrect): Pretend not to notice
          Button(
            onClick = {
              viewModel.failLevel(8)
              isFirstTry = false
              failureReason = "Ignoring an elderly passenger on a moving bus is dangerous for their balance. Offering your seat demonstrates kindness, respect, and civic maturity!"
              hasFailed = true
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("action_ignore_elder"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350)),
            shape = RoundedCornerShape(14.dp)
          ) {
            Text(
              text = "Pretend to Look Out Window & Keep Bag on Seat",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
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
          seatOffered = false
          viewModel.setExpression(CharacterExpression.IDLE)
        },
        onLevelMap = onLevelMap
      )
    }
  }
}
