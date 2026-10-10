package com.example.ui.levels

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Signpost
import androidx.compose.material.icons.filled.SportsHandball
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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

private data class PublicAssetInspection(
  val id: String,
  val name: String,
  val icon: ImageVector,
  val situation: String,
  val correctActionText: String,
  val wrongActionText: String,
  val wrongExplanation: String
)

@Composable
fun Level13DontDamageItScreen(
  viewModel: GameViewModel,
  onNextLevel: () -> Unit,
  onLevelMap: () -> Unit
) {
  val gameState by viewModel.gameState.collectAsState()
  val levelInfo = LevelCatalog.getLevel(13)!!

  val publicAssets = remember {
    listOf(
      PublicAssetInspection(
        id = "bench",
        name = "Park Bench",
        icon = Icons.Default.Park,
        situation = "A freshly painted park bench is here. A marker pen is lying nearby on the grass.",
        correctActionText = "Sit gently or pick up marker to keep the bench clean",
        wrongActionText = "Carve initials with a coin or scribble on the wood",
        wrongExplanation = "Scratching or scribbling on public benches spoils community property that others use to rest."
      ),
      PublicAssetInspection(
        id = "bus_stop",
        name = "Bus Stop Shelter",
        icon = Icons.Default.DirectionsBus,
        situation = "The bus shelter display screen shows arrival times for city routes.",
        correctActionText = "Read the route schedule peacefully while waiting",
        wrongActionText = "Kick the metal shelter pole and paste unauthorized stickers",
        wrongExplanation = "Kicking bus stop posts and sticking ads damages municipal infrastructure that guides daily commuters."
      ),
      PublicAssetInspection(
        id = "street_sign",
        name = "Heritage Street Sign",
        icon = Icons.Default.Signpost,
        situation = "The street signboard indicates directions to the local hospital and school.",
        correctActionText = "Keep the sign clear and unobstructed for drivers",
        wrongActionText = "Bend the signboard post and spray paint over the letters",
        wrongExplanation = "Defacing directional signs causes drivers to miss turns and can delay emergency ambulances."
      ),
      PublicAssetInspection(
        id = "public_light",
        name = "Solar Public Lamp",
        icon = Icons.Default.Lightbulb,
        situation = "A solar-powered LED pole illuminates the sidewalk safely at night.",
        correctActionText = "Protect the solar light so the street stays well-lit",
        wrongActionText = "Throw stones at the solar panel to hear it crack",
        wrongExplanation = "Breaking public solar lights leaves neighborhood streets dark and unsafe for walkers."
      )
    )
  }

  var inspectedAssets by remember { mutableStateOf(setOf<String>()) }
  var selectedAsset by remember { mutableStateOf<PublicAssetInspection?>(publicAssets[0]) }

  var hasCompleted by remember { mutableStateOf(false) }
  var hasFailed by remember { mutableStateOf(false) }
  var failureReason by remember { mutableStateOf("") }
  var isFirstTry by remember { mutableStateOf(true) }

  val allDone = inspectedAssets.size >= publicAssets.size

  Box(modifier = Modifier.fillMaxSize()) {
    LivingCityBackground(showRoad = true, isParkScene = true)

    Column(
      modifier = Modifier
        .fillMaxSize()
        .navigationBarsPadding(),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      GameTopBar(
        gameState = gameState,
        onBackClick = onLevelMap,
        title = "Level 13: Don't Damage It",
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
              imageVector = Icons.Default.Shield,
              contentDescription = null,
              tint = Color(0xFF00897B),
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "Inspect all 4 public property spots and choose to protect them.",
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF1E293B)
            )
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(Color(0xFFE0F2F1))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = "${inspectedAssets.size}/4 Protected",
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp,
              color = Color(0xFF00695C)
            )
          }
        }
      }

      // Public Property Environment Grid
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
          // 4 Asset Stations in a clean 2x2 Grid
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            publicAssets.take(2).forEach { asset ->
              AssetStationTile(
                asset = asset,
                isSelected = selectedAsset?.id == asset.id,
                isProtected = inspectedAssets.contains(asset.id),
                modifier = Modifier.weight(1f),
                onClick = { selectedAsset = asset }
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            publicAssets.drop(2).forEach { asset ->
              AssetStationTile(
                asset = asset,
                isSelected = selectedAsset?.id == asset.id,
                isProtected = inspectedAssets.contains(asset.id),
                modifier = Modifier.weight(1f),
                onClick = { selectedAsset = asset }
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Mini Character status
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(14.dp))
              .background(Color.White.copy(alpha = 0.9f))
              .padding(horizontal = 14.dp, vertical = 6.dp)
          ) {
            CharacterAvatar(
              character = gameState.character,
              expression = if (allDone) CharacterExpression.CELEBRATING else CharacterExpression.POINTING,
              size = 40.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (allDone) "All community property safe!" else "Selecting: ${selectedAsset?.name}",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF1E293B)
            )
          }
        }
      }

      // Interaction Panel for Selected Asset
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
          selectedAsset?.let { asset ->
            Text(
              text = "INSPECTING: ${asset.name.uppercase()}",
              fontWeight = FontWeight.Black,
              fontSize = 12.sp,
              color = Color(0xFF00897B),
              letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = asset.situation,
              fontSize = 12.sp,
              color = Color(0xFF64748B),
              textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))

            if (inspectedAssets.contains(asset.id)) {
              Surface(
                color = Color(0xFFE8F5E9),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(10.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32))
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "You protected this asset responsibly! Select another station.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                  )
                }
              }
            } else {
              // Action 1: Responsible
              Surface(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(14.dp))
                  .clickable {
                    viewModel.soundManager.playCoin(gameState.soundEnabled)
                    val nextSet = inspectedAssets + asset.id
                    inspectedAssets = nextSet
                    if (nextSet.size >= publicAssets.size) {
                      viewModel.completeLevel(13, isFirstTry)
                      hasCompleted = true
                    } else {
                      // Switch to next uninspected
                      selectedAsset = publicAssets.firstOrNull { !nextSet.contains(it.id) }
                    }
                  }
                  .testTag("asset_protect_${asset.id}"),
                color = Color(0xFFF1F8E9),
                shape = RoundedCornerShape(14.dp),
                shadowElevation = 2.dp
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32))
                  Spacer(modifier = Modifier.width(10.dp))
                  Text(
                    text = asset.correctActionText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1E293B)
                  )
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              // Action 2: Vandalism / Harmful
              Surface(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(14.dp))
                  .clickable {
                    isFirstTry = false
                    failureReason = asset.wrongExplanation
                    hasFailed = true
                    viewModel.failLevel(13)
                  }
                  .testTag("asset_damage_${asset.id}"),
                color = Color(0xFFFFF3E0),
                shape = RoundedCornerShape(14.dp),
                shadowElevation = 2.dp
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = Color(0xFFE65100))
                  Spacer(modifier = Modifier.width(10.dp))
                  Text(
                    text = asset.wrongActionText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1E293B)
                  )
                }
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
          viewModel.setExpression(CharacterExpression.IDLE)
        },
        onLevelMap = onLevelMap
      )
    }
  }
}

@Composable
private fun AssetStationTile(
  asset: PublicAssetInspection,
  isSelected: Boolean,
  isProtected: Boolean,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Card(
    modifier = modifier
      .height(105.dp)
      .clip(RoundedCornerShape(18.dp))
      .clickable(onClick = onClick)
      .testTag("asset_tile_${asset.id}"),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(
      containerColor = when {
        isProtected -> Color(0xFFE8F5E9)
        isSelected -> Color(0xFFE0F2F1)
        else -> Color.White.copy(alpha = 0.94f)
      }
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 6.dp else 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(10.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .background(if (isProtected) Color(0xFF2E7D32) else Color(0xFF00897B).copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        if (isProtected) {
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Protected",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
          )
        } else {
          Icon(
            imageVector = asset.icon,
            contentDescription = asset.name,
            tint = Color(0xFF00897B),
            modifier = Modifier.size(24.dp)
          )
        }
      }
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = asset.name,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        color = Color(0xFF1E293B),
        textAlign = TextAlign.Center,
        maxLines = 1
      )
      Text(
        text = if (isProtected) "Protected ✓" else "Tap to check",
        fontSize = 10.sp,
        color = if (isProtected) Color(0xFF2E7D32) else Color(0xFF78909C)
      )
    }
  }
}
