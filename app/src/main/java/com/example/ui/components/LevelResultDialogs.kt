package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import com.example.model.LevelCatalog
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.CharacterExpression
import com.example.model.CharacterProfile
import com.example.model.LevelInfo
import kotlin.random.Random

@Composable
fun SuccessDialog(
  level: LevelInfo,
  character: CharacterProfile,
  isFirstTry: Boolean,
  onNextLevel: () -> Unit,
  onLevelMap: () -> Unit
) {
  val scaleAnim = remember { Animatable(0.7f) }
  LaunchedEffect(Unit) {
    scaleAnim.animateTo(1f, animationSpec = tween(400, easing = FastOutSlowInEasing))
  }

  val totalCoins = level.rewardCoins + if (isFirstTry) level.bonusCoinsFirstTry else 0

  Dialog(
    onDismissRequest = {},
    properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .scale(scaleAnim.value),
      contentAlignment = Alignment.Center
    ) {
      // Confetti burst
      ConfettiEffect(modifier = Modifier.fillMaxSize())

      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(8.dp)
          .testTag("level_success_dialog"),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Banner
          Surface(
            color = Color(0xFFE8F5E9),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.padding(bottom = 8.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = Color(0xFF2E7D32),
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "LEVEL COMPLETE!",
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                color = Color(0xFF2E7D32),
                letterSpacing = 0.5.sp
              )
            }
          }

          // Stars Row
          Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 4.dp)
          ) {
            repeat(3) {
              Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color(0xFFFFB300),
                modifier = Modifier.size(34.dp)
              )
            }
          }

          // Character Celebrating Avatar
          CharacterAvatar(
            character = character,
            expression = CharacterExpression.CELEBRATING,
            size = 130.dp,
            modifier = Modifier.padding(vertical = 4.dp)
          )

          Text(
            text = level.title,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFF1E293B)
          )
          Text(
            text = "Great civic effort, ${character.name}!",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = Color(0xFF00897B),
            modifier = Modifier.padding(top = 2.dp)
          )

          // Rewards Container with 3D stylized cards
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Coins Card
            Surface(
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(14.dp),
              color = Color(0xFFFFFBEB),
              border = BorderStroke(1.dp, Color(0xFFFDE68A)),
              shadowElevation = 2.dp
            ) {
              Column(
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.MonetizationOn,
                    contentDescription = null,
                    tint = Color(0xFFF59E0B),
                    modifier = Modifier.size(20.dp)
                  )
                  Spacer(modifier = Modifier.width(3.dp))
                  Text(
                    text = "+$totalCoins",
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp,
                    color = Color(0xFF78350F)
                  )
                }
                Text(
                  text = if (isFirstTry) "Coins Bonus!" else "Coins",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = Color(0xFFB45309)
                )
              }
            }

            // XP Card
            Surface(
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(14.dp),
              color = Color(0xFFF0FDFA),
              border = BorderStroke(1.dp, Color(0xFF99F6E4)),
              shadowElevation = 2.dp
            ) {
              Column(
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFF0D9488),
                    modifier = Modifier.size(20.dp)
                  )
                  Spacer(modifier = Modifier.width(3.dp))
                  Text(
                    text = "+${level.rewardXp}",
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp,
                    color = Color(0xFF134E4A)
                  )
                }
                Text(
                  text = "XP Earned",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = Color(0xFF0F766E)
                )
              }
            }

            // Civic Points Card
            Surface(
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(14.dp),
              color = Color(0xFFF0FDF4),
              border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
              shadowElevation = 2.dp
            ) {
              Column(
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = Color(0xFF16A34A),
                    modifier = Modifier.size(20.dp)
                  )
                  Spacer(modifier = Modifier.width(3.dp))
                  Text(
                    text = "+${level.rewardCivicPoints}",
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp,
                    color = Color(0xFF14532D)
                  )
                }
                Text(
                  text = "Civic Points",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = Color(0xFF15803D)
                )
              }
            }
          }

          // Moral Message Box
          Surface(
            color = Color(0xFFEFF6FF),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Text(
                text = "CIVIC LESSON",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF2563EB),
                letterSpacing = 0.5.sp
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "\"${level.moralMessage}\"",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1E3A8A),
                textAlign = TextAlign.Start
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Action Buttons
          Button(
            onClick = onNextLevel,
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("next_level_button"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00897B)),
            shape = RoundedCornerShape(16.dp)
          ) {
            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (level.id >= LevelCatalog.LEVELS.size) "CLAIM VICTORY!" else "NEXT LEVEL",
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedButton(
            onClick = onLevelMap,
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("level_map_button"),
            shape = RoundedCornerShape(16.dp)
          ) {
            Icon(imageVector = Icons.Default.Map, contentDescription = null, tint = Color(0xFF546E7A))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "LEVEL MAP",
              fontWeight = FontWeight.Bold,
              color = Color(0xFF546E7A)
            )
          }
        }
      }
    }
  }
}

@Composable
fun FailureDialog(
  reasonExplanation: String,
  character: CharacterProfile,
  onRetry: () -> Unit,
  onLevelMap: () -> Unit
) {
  Dialog(
    onDismissRequest = {},
    properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .testTag("level_failure_dialog"),
      shape = RoundedCornerShape(28.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Red badge
        Surface(
          color = Color(0xFFFFEBEE),
          shape = RoundedCornerShape(20.dp),
          modifier = Modifier.padding(bottom = 8.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = null,
              tint = Color(0xFFD32F2F),
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "MISSION FAILED",
              fontWeight = FontWeight.Black,
              fontSize = 18.sp,
              color = Color(0xFFD32F2F),
              letterSpacing = 0.5.sp
            )
          }
        }

        // Puzzled/Confused Character
        CharacterAvatar(
          character = character,
          expression = CharacterExpression.CONFUSED,
          size = 120.dp,
          modifier = Modifier.padding(vertical = 4.dp)
        )

        Text(
          text = "Keep going, ${character.name}!",
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp,
          color = Color(0xFF263238),
          modifier = Modifier.padding(top = 4.dp)
        )

        // Clear, gentle explanation box
        Surface(
          color = Color(0xFFFFF8E1),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
        ) {
          Text(
            text = reasonExplanation,
            fontSize = 14.sp,
            color = Color(0xFF5D4037),
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(14.dp)
          )
        }

        Text(
          text = "Every mistake is a chance to learn! Try again.",
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = Color(0xFF78909C),
          modifier = Modifier.padding(bottom = 16.dp)
        )

        // Buttons
        Button(
          onClick = onRetry,
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("retry_button"),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00897B)),
          shape = RoundedCornerShape(16.dp)
        ) {
          Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "TRY AGAIN", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
          onClick = onLevelMap,
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("failure_level_map_button"),
          shape = RoundedCornerShape(16.dp)
        ) {
          Icon(imageVector = Icons.Default.Map, contentDescription = null, tint = Color(0xFF546E7A))
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "LEVEL MAP", fontWeight = FontWeight.Bold, color = Color(0xFF546E7A))
        }
      }
    }
  }
}

@Composable
fun ConfettiEffect(modifier: Modifier = Modifier) {
  val confettiColors = listOf(
    Color(0xFFFFB300),
    Color(0xFF00E676),
    Color(0xFF00B0FF),
    Color(0xFFFF4081),
    Color(0xFF7C4DFF)
  )

  val pieces = remember {
    List(36) {
      ConfettiPiece(
        x = Random.nextFloat(),
        y = Random.nextFloat() * -0.5f,
        color = confettiColors[Random.nextInt(confettiColors.size)],
        size = Random.nextFloat() * 12f + 8f,
        speed = Random.nextFloat() * 0.4f + 0.3f
      )
    }
  }

  val transition = rememberInfiniteTransition(label = "confetti")
  val animProgress by transition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(2400, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "confettiAnim"
  )

  Canvas(modifier = modifier) {
    pieces.forEach { piece ->
      val currentY = (piece.y + (animProgress * piece.speed * 2f)) % 1.2f
      val xOffset = piece.x * size.width
      val yOffset = currentY * size.height
      drawCircle(
        color = piece.color,
        radius = piece.size * 0.5f,
        center = Offset(xOffset, yOffset)
      )
    }
  }
}

private data class ConfettiPiece(
  val x: Float,
  val y: Float,
  val color: Color,
  val size: Float,
  val speed: Float
)
