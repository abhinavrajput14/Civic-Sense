package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import com.example.ui.components.GameTopBar
import com.example.viewmodel.GameViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SettingsScreen(
  viewModel: GameViewModel,
  onBackClick: () -> Unit
) {
  val gameState by viewModel.gameState.collectAsState()
  var showResetDialog by remember { mutableStateOf(false) }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          listOf(Color(0xFFE8EAF6), Color(0xFFEDE7F6), Color(0xFFF3E5F5))
        )
      )
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .navigationBarsPadding()
    ) {
      GameTopBar(
        gameState = gameState,
        onBackClick = onBackClick,
        title = "Settings",
        onSoundToggle = { viewModel.toggleSound() },
        onMusicToggle = { viewModel.toggleMusic() }
      )

      Column(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // Audio Settings Card
        Card(
          shape = RoundedCornerShape(22.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Text(
              text = "AUDIO & EFFECTS",
              fontWeight = FontWeight.Black,
              fontSize = 12.sp,
              color = Color(0xFF3949AB),
              letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Sound Effects Row
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.VolumeUp,
                  contentDescription = null,
                  tint = Color(0xFF3949AB),
                  modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                  Text(
                    text = "Sound Effects",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF1E293B)
                  )
                  Text(
                    text = "Interactive game tones & coin sounds",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                  )
                }
              }
              Switch(
                checked = gameState.soundEnabled,
                onCheckedChange = { viewModel.toggleSound() },
                modifier = Modifier.testTag("settings_sound_switch"),
                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF3949AB))
              )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Music Row
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.MusicNote,
                  contentDescription = null,
                  tint = Color(0xFF3949AB),
                  modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                  Text(
                    text = "Background Music",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF1E293B)
                  )
                  Text(
                    text = "Cheerful ambient city melody",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                  )
                }
              }
              Switch(
                checked = gameState.musicEnabled,
                onCheckedChange = { viewModel.toggleMusic() },
                modifier = Modifier.testTag("settings_music_switch"),
                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF3949AB))
              )
            }
          }
        }

        // About Civic Quest Card
        Card(
          shape = RoundedCornerShape(22.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = Color(0xFF00897B),
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = "ABOUT CIVIC QUEST",
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                color = Color(0xFF00897B),
                letterSpacing = 0.5.sp
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = "Civic Quest: Play. Choose. Learn. Change.",
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = "Designed for children and teenagers aged 7–14+ to experience real civic situations firsthand through gameplay: cleanliness, road safety, water conservation, queue discipline, respecting public property, and empathy.",
              fontSize = 13.sp,
              color = Color(0xFF475569),
              lineHeight = 18.sp
            )
          }
        }

        // Cloud Account & Sync Card
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        val auth = remember { FirebaseAuth.getInstance() }
        val currentUser = auth.currentUser

        Card(
          shape = RoundedCornerShape(22.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.CloudDone,
                contentDescription = null,
                tint = Color(0xFF1976D2),
                modifier = Modifier.size(22.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = "GOOGLE ACCOUNT & CLOUD SYNC",
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                color = Color(0xFF1976D2),
                letterSpacing = 0.5.sp
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = if (currentUser?.email != null) "Connected: ${currentUser.email}" else "Signed in with Google",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = Color(0xFF1E293B)
            )

            Text(
              text = "UID: ${currentUser?.uid?.take(12) ?: "Anonymous"}...",
              fontSize = 12.sp,
              color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
              onClick = {
                val credManager = CredentialManager.create(context)
                signOutUser(
                  context = context,
                  credentialManager = credManager,
                  onSignOutComplete = {
                    onBackClick()
                  },
                  scope = coroutineScope
                )
              },
              modifier = Modifier.fillMaxWidth().testTag("settings_sign_out_button"),
              shape = RoundedCornerShape(14.dp)
            ) {
              Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              Text(text = "SIGN OUT OF GOOGLE", fontWeight = FontWeight.Bold)
            }
          }
        }

        // Reset Game Progress Card
        Card(
          shape = RoundedCornerShape(22.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Text(
              text = "RESET GAME DATA",
              fontWeight = FontWeight.Black,
              fontSize = 12.sp,
              color = Color(0xFFD32F2F),
              letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = "Restart your journey with a new character and reset all level stars and badges.",
              fontSize = 12.sp,
              color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedButton(
              onClick = { showResetDialog = true },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("settings_reset_button"),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
              shape = RoundedCornerShape(14.dp)
            ) {
              Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              Text(text = "RESET PROGRESS", fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    if (showResetDialog) {
      AlertDialog(
        onDismissRequest = { showResetDialog = false },
        title = { Text("Reset All Progress?", fontWeight = FontWeight.Bold) },
        text = {
          Text("Are you sure you want to reset your character, badges, and completed levels? This cannot be undone.")
        },
        confirmButton = {
          Button(
            onClick = {
              showResetDialog = false
              viewModel.resetGame()
              onBackClick()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
          ) {
            Text("Yes, Reset")
          }
        },
        dismissButton = {
          TextButton(onClick = { showResetDialog = false }) {
            Text("Cancel")
          }
        }
      )
    }
  }
}
