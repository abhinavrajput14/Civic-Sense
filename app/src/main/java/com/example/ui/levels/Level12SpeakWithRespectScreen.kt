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
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentSatisfied
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

private data class RespectScenario(
  val npcName: String,
  val npcEmoji: String,
  val situation: String,
  val politeChoice: String,
  val rudeChoice: String,
  val politeReaction: String,
  val rudeReaction: String,
  val failureMoral: String
)

@Composable
fun Level12SpeakWithRespectScreen(
  viewModel: GameViewModel,
  onNextLevel: () -> Unit,
  onLevelMap: () -> Unit
) {
  val gameState by viewModel.gameState.collectAsState()
  val levelInfo = LevelCatalog.getLevel(12)!!

  val scenarios = remember {
    listOf(
      RespectScenario(
        npcName = "Classmate Aarav",
        npcEmoji = "👦",
        situation = "Aarav accidentally steps on your shoe while carrying his notebook stack.",
        politeChoice = "\"It's alright, Aarav! Accidents happen. Let me help you carry those.\"",
        rudeChoice = "\"Hey watch where you're walking, you clumsy idiot!\"",
        politeReaction = "Aarav smiles warmly: \"Thanks so much for understanding! I'm really sorry.\"",
        rudeReaction = "Aarav looks hurt and upset: \"I didn't do it on purpose... you didn't have to shout.\"",
        failureMoral = "Accidents happen to everyone. Responding with kindness and calm de-escalates conflict."
      ),
      RespectScenario(
        npcName = "Shopkeeper Uncle Gupta",
        npcEmoji = "🧔",
        situation = "Uncle Gupta handed you a blue pen instead of the red pencil you needed.",
        politeChoice = "\"Excuse me, Uncle Gupta, could I please exchange this for the red pencil?\"",
        rudeChoice = "\"Can't you even hear properly? Give me what I asked for!\"",
        politeReaction = "Uncle Gupta beams: \"Of course, child! Here is the red pencil right away. Have a blessed day!\"",
        rudeReaction = "Uncle Gupta frowns: \"There is no need to be disrespectful to elders. You should learn manners.\"",
        failureMoral = "Using 'please' and polite words shows respect to workers, shopkeepers, and elders."
      ),
      RespectScenario(
        npcName = "Project Partner Priya",
        npcEmoji = "👧",
        situation = "Priya suggests a different design color for your school science poster than you had in mind.",
        politeChoice = "\"That's an interesting idea Priya! Let's try putting both colors side by side and decide together.\"",
        rudeChoice = "\"Your idea is stupid! Only my plan is good, do what I say.\"",
        politeReaction = "Priya is thrilled: \"Great idea! Working together makes our poster look so much better!\"",
        rudeReaction = "Priya steps back, feeling silenced: \"Fine... do whatever you want then.\"",
        failureMoral = "Respect means listening to differing viewpoints without belittling others."
      )
    )
  }

  var currentIndex by remember { mutableIntStateOf(0) }
  var latestReaction by remember { mutableStateOf<String?>(null) }
  var isReactionPositive by remember { mutableStateOf(true) }

  var hasCompleted by remember { mutableStateOf(false) }
  var hasFailed by remember { mutableStateOf(false) }
  var failureReason by remember { mutableStateOf("") }
  var isFirstTry by remember { mutableStateOf(true) }

  val currentScenario = scenarios[currentIndex]

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
        title = "Level 12: Speak With Respect",
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
              imageVector = Icons.Default.RecordVoiceOver,
              contentDescription = null,
              tint = Color(0xFF7C4DFF),
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "Conversation ${currentIndex + 1}/3: ${currentScenario.npcName}",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF1E293B)
            )
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(Color(0xFFEDE7F6))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = "${currentIndex}/3 Completed",
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp,
              color = Color(0xFF512DA8)
            )
          }
        }
      }

      // Live Conversation Stage
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
          // Character & NPC Encounter Card
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(24.dp))
              .background(Color.White.copy(alpha = 0.94f))
              .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              CharacterAvatar(
                character = gameState.character,
                expression = if (latestReaction != null && isReactionPositive) CharacterExpression.HAPPY else CharacterExpression.IDLE,
                size = 110.dp
              )
              Text(
                text = gameState.character.name,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color(0xFF1E293B)
              )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Box(
                modifier = Modifier
                  .size(86.dp)
                  .clip(CircleShape)
                  .background(if (isReactionPositive) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)),
                contentAlignment = Alignment.Center
              ) {
                Text(text = currentScenario.npcEmoji, fontSize = 42.sp)
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = currentScenario.npcName,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color(0xFF1E293B)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Situation Speech Bubble
          Surface(
            color = Color(0xFFF3E5F5),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text(
                text = "SITUATION:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF7B1FA2)
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = currentScenario.situation,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF311B92)
              )
            }
          }

          // Reaction Bubble if available
          latestReaction?.let { reaction ->
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
              color = if (isReactionPositive) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
              shape = RoundedCornerShape(18.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = if (isReactionPositive) Icons.Default.SentimentSatisfied else Icons.Default.SentimentDissatisfied,
                  contentDescription = null,
                  tint = if (isReactionPositive) Color(0xFF2E7D32) else Color(0xFFC62828),
                  modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = reaction,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isReactionPositive) Color(0xFF1B5E20) else Color(0xFFB71C1C)
                )
              }
            }
          }
        }
      }

      // Dialog Response Options Panel
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
            text = "HOW DO YOU CHOOSE TO RESPOND?",
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            color = Color(0xFF7C4DFF),
            letterSpacing = 0.5.sp
          )
          Spacer(modifier = Modifier.height(10.dp))

          // Option A: Polite
          ResponseOptionCard(
            text = currentScenario.politeChoice,
            isPolite = true,
            testTag = "respect_choice_polite",
            onClick = {
              viewModel.soundManager.playCoin(gameState.soundEnabled)
              latestReaction = currentScenario.politeReaction
              isReactionPositive = true

              if (currentIndex < scenarios.size - 1) {
                currentIndex += 1
              } else {
                viewModel.completeLevel(12, isFirstTry)
                hasCompleted = true
              }
            }
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Option B: Rude
          ResponseOptionCard(
            text = currentScenario.rudeChoice,
            isPolite = false,
            testTag = "respect_choice_rude",
            onClick = {
              isFirstTry = false
              latestReaction = currentScenario.rudeReaction
              isReactionPositive = false
              failureReason = currentScenario.failureMoral
              hasFailed = true
              viewModel.failLevel(12)
            }
          )
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
          latestReaction = null
          isReactionPositive = true
          viewModel.setExpression(CharacterExpression.IDLE)
        },
        onLevelMap = onLevelMap
      )
    }
  }
}

@Composable
private fun ResponseOptionCard(
  text: String,
  isPolite: Boolean,
  testTag: String,
  onClick: () -> Unit
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .clickable(onClick = onClick)
      .testTag(testTag),
    color = if (isPolite) Color(0xFFF1F8E9) else Color(0xFFFFF3E0),
    shape = RoundedCornerShape(14.dp),
    shadowElevation = 2.dp
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = if (isPolite) Icons.Default.CheckCircle else Icons.Default.Forum,
        contentDescription = null,
        tint = if (isPolite) Color(0xFF2E7D32) else Color(0xFFE65100),
        modifier = Modifier.size(20.dp)
      )
      Spacer(modifier = Modifier.width(10.dp))
      Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = Color(0xFF1E293B),
        modifier = Modifier.weight(1f)
      )
    }
  }
}
