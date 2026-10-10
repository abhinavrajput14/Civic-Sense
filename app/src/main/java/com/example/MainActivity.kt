package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.levels.DynamicLevelScreen
import com.example.ui.levels.Level01WrapperScreen
import com.example.ui.levels.Level02SortWasteScreen
import com.example.ui.levels.Level03TrafficLightScreen
import com.example.ui.levels.Level04ZebraCrossingScreen
import com.example.ui.levels.Level05ProtectGreenScreen
import com.example.ui.levels.Level06SaveWaterScreen
import com.example.ui.levels.Level07QueueDisciplineScreen
import com.example.ui.levels.Level08BusEtiquetteScreen
import com.example.ui.levels.Level09PublicParkResponsibilityScreen
import com.example.ui.levels.Level10GrandFinaleScreen
import com.example.ui.levels.Level11HelpingHandScreen
import com.example.ui.levels.Level12SpeakWithRespectScreen
import com.example.ui.levels.Level13DontDamageItScreen
import com.example.ui.levels.Level14KeepItQuietScreen
import com.example.ui.levels.Level15ShareTheSpaceScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.BadgesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LevelMapScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.WelcomeCharacterScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.GameViewModel
import com.google.firebase.auth.FirebaseAuth

sealed interface GameScreen {
  object Welcome : GameScreen
  object Home : GameScreen
  object LevelMap : GameScreen
  object Badges : GameScreen
  object Settings : GameScreen
  data class Level(val id: Int) : GameScreen
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        AppContent()
      }
    }
  }
}

@Composable
fun AppContent() {
  val auth = FirebaseAuth.getInstance()
  var currentUser by remember { mutableStateOf(auth.currentUser) }

  DisposableEffect(Unit) {
    val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
      currentUser = firebaseAuth.currentUser
    }
    auth.addAuthStateListener(listener)
    onDispose {
      auth.removeAuthStateListener(listener)
    }
  }

  if (currentUser == null) {
    AuthScreen(
      onAuthSuccess = {
        currentUser = FirebaseAuth.getInstance().currentUser
      }
    )
  } else {
    CivicQuestApp()
  }
}

@Composable
fun CivicQuestApp(gameViewModel: GameViewModel = viewModel()) {
  val gameState by gameViewModel.gameState.collectAsState()

  var currentScreen by remember {
    mutableStateOf<GameScreen>(
      if (gameState.isCharacterCreated) GameScreen.Home else GameScreen.Welcome
    )
  }

  // Update currentScreen if character was restored from saved database on launch
  androidx.compose.runtime.LaunchedEffect(gameState.isCharacterCreated) {
    if (gameState.isCharacterCreated && currentScreen == GameScreen.Welcome) {
      currentScreen = GameScreen.Home
    }
  }

  // Handle system back button gracefully
  BackHandler(enabled = currentScreen != GameScreen.Home) {
    currentScreen = when (currentScreen) {
      is GameScreen.Level -> GameScreen.LevelMap
      is GameScreen.LevelMap, is GameScreen.Badges, is GameScreen.Settings -> GameScreen.Home
      is GameScreen.Welcome -> if (gameState.isCharacterCreated) GameScreen.Home else GameScreen.Welcome
      GameScreen.Home -> GameScreen.Home
    }
  }

  Surface(modifier = Modifier.fillMaxSize()) {
    Crossfade(targetState = currentScreen, label = "screenTransition") { screen ->
      when (screen) {
        GameScreen.Welcome -> {
          WelcomeCharacterScreen(
            viewModel = gameViewModel,
            onFinished = {
              currentScreen = GameScreen.Home
            },
            onBackClick = if (gameState.isCharacterCreated) {
              { currentScreen = GameScreen.Home }
            } else null
          )
        }

        GameScreen.Home -> {
          HomeScreen(
            viewModel = gameViewModel,
            onPlayClick = { currentScreen = GameScreen.LevelMap },
            onCharacterClick = { currentScreen = GameScreen.Welcome },
            onLevelMapClick = { currentScreen = GameScreen.LevelMap },
            onBadgesClick = { currentScreen = GameScreen.Badges },
            onSettingsClick = { currentScreen = GameScreen.Settings }
          )
        }

        GameScreen.LevelMap -> {
          LevelMapScreen(
            viewModel = gameViewModel,
            onLevelSelected = { levelId ->
              currentScreen = GameScreen.Level(levelId)
            },
            onBackClick = { currentScreen = GameScreen.Home }
          )
        }

        GameScreen.Badges -> {
          BadgesScreen(
            viewModel = gameViewModel,
            onBackClick = { currentScreen = GameScreen.Home }
          )
        }

        GameScreen.Settings -> {
          SettingsScreen(
            viewModel = gameViewModel,
            onBackClick = { currentScreen = GameScreen.Home }
          )
        }

        is GameScreen.Level -> {
          val dynamicLevel = gameViewModel.getDynamicLevel(screen.id)
          if (dynamicLevel != null && (screen.id > 15 || screen.id !in 1..15)) {
            DynamicLevelScreen(
              level = dynamicLevel,
              viewModel = gameViewModel,
              onNextLevel = {
                val nextId = screen.id + 1
                currentScreen = GameScreen.Level(nextId)
              },
              onLevelMap = { currentScreen = GameScreen.LevelMap }
            )
          } else {
            when (screen.id) {
              1 -> Level01WrapperScreen(
                viewModel = gameViewModel,
                onNextLevel = { currentScreen = GameScreen.Level(2) },
                onLevelMap = { currentScreen = GameScreen.LevelMap }
              )

              2 -> Level02SortWasteScreen(
                viewModel = gameViewModel,
                onNextLevel = { currentScreen = GameScreen.Level(3) },
                onLevelMap = { currentScreen = GameScreen.LevelMap }
              )

              3 -> Level03TrafficLightScreen(
                viewModel = gameViewModel,
                onNextLevel = { currentScreen = GameScreen.Level(4) },
                onLevelMap = { currentScreen = GameScreen.LevelMap }
              )

              4 -> Level04ZebraCrossingScreen(
                viewModel = gameViewModel,
                onNextLevel = { currentScreen = GameScreen.Level(5) },
                onLevelMap = { currentScreen = GameScreen.LevelMap }
              )

              5 -> Level05ProtectGreenScreen(
                viewModel = gameViewModel,
                onNextLevel = { currentScreen = GameScreen.Level(6) },
                onLevelMap = { currentScreen = GameScreen.LevelMap }
              )

              6 -> Level06SaveWaterScreen(
                viewModel = gameViewModel,
                onNextLevel = { currentScreen = GameScreen.Level(7) },
                onLevelMap = { currentScreen = GameScreen.LevelMap }
              )

              7 -> Level07QueueDisciplineScreen(
                viewModel = gameViewModel,
                onNextLevel = { currentScreen = GameScreen.Level(8) },
                onLevelMap = { currentScreen = GameScreen.LevelMap }
              )

              8 -> Level08BusEtiquetteScreen(
                viewModel = gameViewModel,
                onNextLevel = { currentScreen = GameScreen.Level(9) },
                onLevelMap = { currentScreen = GameScreen.LevelMap }
              )

              9 -> Level09PublicParkResponsibilityScreen(
                viewModel = gameViewModel,
                onNextLevel = { currentScreen = GameScreen.Level(10) },
                onLevelMap = { currentScreen = GameScreen.LevelMap }
              )

              10 -> Level10GrandFinaleScreen(
                viewModel = gameViewModel,
                onNextLevel = { currentScreen = GameScreen.Level(11) },
                onLevelMap = { currentScreen = GameScreen.LevelMap }
              )

              11 -> Level11HelpingHandScreen(
                viewModel = gameViewModel,
                onNextLevel = { currentScreen = GameScreen.Level(12) },
                onLevelMap = { currentScreen = GameScreen.LevelMap }
              )

              12 -> Level12SpeakWithRespectScreen(
                viewModel = gameViewModel,
                onNextLevel = { currentScreen = GameScreen.Level(13) },
                onLevelMap = { currentScreen = GameScreen.LevelMap }
              )

              13 -> Level13DontDamageItScreen(
                viewModel = gameViewModel,
                onNextLevel = { currentScreen = GameScreen.Level(14) },
                onLevelMap = { currentScreen = GameScreen.LevelMap }
              )

              14 -> Level14KeepItQuietScreen(
                viewModel = gameViewModel,
                onNextLevel = { currentScreen = GameScreen.Level(15) },
                onLevelMap = { currentScreen = GameScreen.LevelMap }
              )

              15 -> Level15ShareTheSpaceScreen(
                viewModel = gameViewModel,
                onNextLevel = { currentScreen = GameScreen.LevelMap },
                onLevelMap = { currentScreen = GameScreen.LevelMap }
              )

              else -> {
                val fallbackDynamic = gameViewModel.getDynamicLevel(screen.id)
                if (fallbackDynamic != null) {
                  DynamicLevelScreen(
                    level = fallbackDynamic,
                    viewModel = gameViewModel,
                    onNextLevel = { currentScreen = GameScreen.LevelMap },
                    onLevelMap = { currentScreen = GameScreen.LevelMap }
                  )
                } else {
                  LevelMapScreen(
                    viewModel = gameViewModel,
                    onLevelSelected = { currentScreen = GameScreen.Level(it) },
                    onBackClick = { currentScreen = GameScreen.Home }
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
