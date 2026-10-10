package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundManager
import com.example.data.CivicFirestoreRepository
import com.example.data.CivicQuestDatabase
import com.example.data.CivicQuestRepository
import com.example.model.Accessory
import com.example.model.BadgesData
import com.example.model.CharacterExpression
import com.example.model.CharacterGender
import com.example.model.CharacterProfile
import com.example.model.CloudLevel
import com.example.model.CloudLevelProgress
import com.example.model.CloudQuestion
import com.example.model.CloudUserProfile
import com.example.model.GameState
import com.example.model.HairColor
import com.example.model.HairStyle
import com.example.model.LevelCatalog
import com.example.model.LevelCompletion
import com.example.model.LevelInfo
import com.example.model.OutfitStyle
import com.example.model.PantsStyle
import com.example.model.ShoesStyle
import com.example.model.SkinTone
import com.example.model.TShirtColor
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {
  val soundManager = SoundManager()

  private val localRepository = CivicQuestRepository(
    CivicQuestDatabase.getDatabase(application).civicQuestDao()
  )

  val firestoreRepository = CivicFirestoreRepository(application)

  private val _gameState = MutableStateFlow(GameState())
  val gameState: StateFlow<GameState> = _gameState.asStateFlow()

  private val _cloudLevels = MutableStateFlow<List<CloudLevel>>(emptyList())
  val cloudLevels: StateFlow<List<CloudLevel>> = _cloudLevels.asStateFlow()

  init {
    // 1. Load local state from Room
    viewModelScope.launch(Dispatchers.IO) {
      val savedState = localRepository.loadGameState()
      if (savedState != null) {
        _gameState.value = savedState
      }
    }

    // 2. Observe dynamic cloud levels from Firestore
    viewModelScope.launch(Dispatchers.IO) {
      firestoreRepository.observeLevels()
        .catch {
          // Fallback gracefully on network interruptions
        }
        .collect { levels ->
          _cloudLevels.value = levels
        }
    }

    // 3. Sync user profile and completed levels with Firestore if authenticated
    val auth = FirebaseAuth.getInstance()
    val uid = auth.currentUser?.uid
    if (uid != null) {
      viewModelScope.launch(Dispatchers.IO) {
        firestoreRepository.observeUserProfile(uid)
          .catch { }
          .collect { cloudProfile ->
            if (cloudProfile != null) {
              _gameState.update { current ->
                current.copy(
                  coins = maxOf(current.coins, cloudProfile.coins),
                  xp = maxOf(current.xp, cloudProfile.xp),
                  civicPoints = maxOf(current.civicPoints, cloudProfile.civicPoints),
                  unlockedBadges = current.unlockedBadges + cloudProfile.unlockedBadges
                )
              }
            }
          }
      }

      viewModelScope.launch(Dispatchers.IO) {
        firestoreRepository.observeUserProgress(uid)
          .catch { }
          .collect { progressList ->
            if (progressList.isNotEmpty()) {
              _gameState.update { current ->
                val updatedCompleted = current.completedLevels.toMutableMap()
                val updatedUnlocked = current.unlockedLevels.toMutableSet()
                progressList.forEach { p ->
                  updatedCompleted[p.levelId] = LevelCompletion(stars = p.stars, firstTry = p.firstTry)
                  updatedUnlocked.add(p.levelId)
                  updatedUnlocked.add(p.levelId + 1)
                }
                current.copy(
                  completedLevels = updatedCompleted,
                  unlockedLevels = updatedUnlocked
                )
              }
            }
          }
      }
    }
  }

  private fun persistState() {
    val currentState = _gameState.value
    viewModelScope.launch(Dispatchers.IO) {
      try {
        localRepository.saveGameState(currentState)
      } catch (e: Exception) {
        // Safe fallback
      }

      // Sync to cloud Firestore if user is authenticated
      val auth = FirebaseAuth.getInstance()
      val uid = auth.currentUser?.uid
      if (uid != null) {
        try {
          val cloudProfile = CloudUserProfile(
            userId = uid,
            username = currentState.character.name,
            coins = currentState.coins,
            xp = currentState.xp,
            civicPoints = currentState.civicPoints,
            unlockedBadges = currentState.unlockedBadges.toList(),
            characterName = currentState.character.name,
            gender = currentState.character.gender.name,
            skinTone = currentState.character.skinTone.name,
            hairStyle = currentState.character.hairStyle.name,
            hairColor = currentState.character.hairColor.name,
            outfitColor = currentState.character.outfitStyle.name,
            accessory = currentState.character.accessory.name
          )
          firestoreRepository.saveUserProfile(cloudProfile)
        } catch (e: Exception) {
          // Cloud sync will retry on next write
        }
      }
    }
  }

  fun getDynamicLevel(levelId: Int): CloudLevel? {
    return _cloudLevels.value.find { it.id == levelId }
  }

  /**
   * Combines built-in catalog levels with any newly loaded cloud levels.
   */
  fun getAllCombinedLevels(): List<LevelInfo> {
    val builtIn = LevelCatalog.LEVELS
    val builtInIds = builtIn.map { it.id }.toSet()
    val dynamicAsLevelInfo = _cloudLevels.value
      .filter { it.id !in builtInIds }
      .map { cloud ->
        LevelInfo(
          id = cloud.id,
          title = cloud.title,
          subtitle = cloud.subtitle,
          topic = cloud.topic,
          scenarioDescription = cloud.scenarioDescription,
          objective = cloud.objective,
          moralMessage = cloud.moralMessage,
          rewardCoins = cloud.rewardCoins,
          bonusCoinsFirstTry = cloud.bonusCoinsFirstTry,
          rewardXp = cloud.rewardXp,
          rewardCivicPoints = cloud.rewardCivicPoints,
          badgeId = cloud.badgeId.ifBlank { null },
          world = cloud.world,
          worldTitle = cloud.worldTitle
        )
      }
    return (builtIn + dynamicAsLevelInfo).sortedBy { it.id }
  }

  fun publishNewCloudLevel(
    title: String,
    subtitle: String,
    topic: String,
    scenario: String,
    objective: String,
    moral: String,
    coins: Int = 100,
    xp: Int = 150,
    civicPoints: Int = 50,
    choices: List<String> = emptyList(),
    correctChoiceIndex: Int = 0,
    onResult: (Boolean, String) -> Unit = { _, _ -> }
  ) {
    viewModelScope.launch(Dispatchers.IO) {
      val existingIds = getAllCombinedLevels().map { it.id }
      val nextId = (existingIds.maxOrNull() ?: 15) + 1
      val worldId = if (nextId > 15) 4 else 3
      val worldTitle = if (nextId > 15) "Digital & Emerging Civic Duties" else "Community Respect"

      val questionsList = if (choices.isNotEmpty()) {
        listOf(
          CloudQuestion(
            questionText = scenario,
            options = choices,
            correctIndex = correctChoiceIndex,
            explanation = moral
          )
        )
      } else emptyList()

      val newLevel = CloudLevel(
        id = nextId,
        title = title,
        subtitle = subtitle,
        topic = topic,
        scenarioDescription = scenario,
        objective = objective,
        moralMessage = moral,
        rewardCoins = coins,
        bonusCoinsFirstTry = 30,
        rewardXp = xp,
        rewardCivicPoints = civicPoints,
        world = worldId,
        worldTitle = worldTitle,
        isPublished = true,
        questions = questionsList
      )

      val result = firestoreRepository.saveLevel(newLevel)
      if (result.isSuccess) {
        onResult(true, "Level $nextId published to cloud live!")
      } else {
        onResult(false, result.exceptionOrNull()?.localizedMessage ?: "Failed to publish level")
      }
    }
  }

  fun selectGender(gender: CharacterGender) {
    soundManager.playClick(_gameState.value.soundEnabled)
    _gameState.update { state ->
      val defaultHair = if (gender == CharacterGender.BOY) HairStyle.SHORT_SPIKY else HairStyle.WAVY_BOB
      state.copy(
        character = state.character.copy(
          gender = gender,
          hairStyle = defaultHair,
          name = if (state.character.name.isBlank()) "Spidyy" else state.character.name
        )
      )
    }
  }

  fun updateName(name: String) {
    _gameState.update { it.copy(character = it.character.copy(name = name)) }
  }

  fun saveName(name: String) {
    val trimmed = name.trim()
    _gameState.update { state ->
      val resolvedName = when {
        trimmed.isNotBlank() -> trimmed
        state.character.name.trim().isNotBlank() -> state.character.name.trim()
        else -> "Spidyy"
      }
      state.copy(character = state.character.copy(name = resolvedName))
    }
    persistState()
  }

  fun updateSkinTone(skin: SkinTone) {
    soundManager.playClick(_gameState.value.soundEnabled)
    _gameState.update { it.copy(character = it.character.copy(skinTone = skin)) }
  }

  fun updateHairStyle(style: HairStyle) {
    soundManager.playClick(_gameState.value.soundEnabled)
    _gameState.update { it.copy(character = it.character.copy(hairStyle = style)) }
  }

  fun updateHairColor(color: HairColor) {
    soundManager.playClick(_gameState.value.soundEnabled)
    _gameState.update { it.copy(character = it.character.copy(hairColor = color)) }
  }

  fun updateOutfit(outfit: OutfitStyle) {
    soundManager.playClick(_gameState.value.soundEnabled)
    _gameState.update {
      it.copy(
        character = it.character.copy(
          outfitStyle = outfit,
          selectedTShirt = null
        )
      )
    }
  }

  fun selectSchoolOutfit() {
    soundManager.playClick(_gameState.value.soundEnabled)
    _gameState.update {
      it.copy(
        character = it.character.copy(
          outfitStyle = OutfitStyle.SCHOOL_HERO,
          selectedTShirt = null
        )
      )
    }
  }

  fun selectTShirt(tshirt: TShirtColor) {
    soundManager.playClick(_gameState.value.soundEnabled)
    _gameState.update {
      it.copy(
        character = it.character.copy(
          selectedTShirt = tshirt
        )
      )
    }
  }

  fun updatePants(pants: PantsStyle) {
    soundManager.playClick(_gameState.value.soundEnabled)
    _gameState.update { it.copy(character = it.character.copy(pantsStyle = pants)) }
  }

  fun updateShoes(shoes: ShoesStyle) {
    soundManager.playClick(_gameState.value.soundEnabled)
    _gameState.update { it.copy(character = it.character.copy(shoesStyle = shoes)) }
  }

  fun updateAccessory(accessory: Accessory) {
    soundManager.playClick(_gameState.value.soundEnabled)
    _gameState.update { it.copy(character = it.character.copy(accessory = accessory)) }
  }

  fun updateBackpack(backpackHex: Long) {
    soundManager.playClick(_gameState.value.soundEnabled)
    _gameState.update { it.copy(character = it.character.copy(backpackHex = backpackHex)) }
  }

  fun completeCharacterCreation() {
    soundManager.playSuccess(_gameState.value.soundEnabled)
    _gameState.update {
      it.copy(
        isCharacterCreated = true,
        characterExpression = CharacterExpression.HAPPY
      )
    }
    persistState()
  }

  fun setExpression(expression: CharacterExpression) {
    _gameState.update { it.copy(characterExpression = expression) }
  }

  fun startLevel(levelId: Int) {
    soundManager.playClick(_gameState.value.soundEnabled)
    _gameState.update {
      it.copy(
        activeLevelId = levelId,
        characterExpression = CharacterExpression.IDLE
      )
    }
  }

  fun completeLevel(levelId: Int, firstTry: Boolean = true) {
    completeDynamicLevel(
      levelId = levelId,
      stars = if (firstTry) 3 else 2,
      firstTry = firstTry
    )
  }

  fun completeDynamicLevel(
    levelId: Int,
    stars: Int = 3,
    firstTry: Boolean = true,
    coinsReward: Int? = null,
    xpReward: Int? = null,
    civicReward: Int? = null,
    badgeId: String? = null
  ) {
    val level = LevelCatalog.getLevel(levelId)
    val dynamic = getDynamicLevel(levelId)

    val baseCoins = coinsReward ?: level?.rewardCoins ?: dynamic?.rewardCoins ?: 50
    val bonus = if (firstTry) (level?.bonusCoinsFirstTry ?: dynamic?.bonusCoinsFirstTry ?: 20) else 0
    val earnedCoins = baseCoins + bonus
    val earnedXp = xpReward ?: level?.rewardXp ?: dynamic?.rewardXp ?: 100
    val earnedCivic = civicReward ?: level?.rewardCivicPoints ?: dynamic?.rewardCivicPoints ?: 25
    val effectiveBadge = badgeId ?: level?.badgeId ?: dynamic?.badgeId?.ifBlank { null }

    soundManager.playVictoryJingle(_gameState.value.soundEnabled)

    val totalLevelsCount = getAllCombinedLevels().size

    _gameState.update { state ->
      val newUnlocked = state.unlockedLevels.toMutableSet()
      if (levelId < totalLevelsCount) {
        newUnlocked.add(levelId + 1)
      }

      val newCompleted = state.completedLevels.toMutableMap()
      newCompleted[levelId] = LevelCompletion(
        stars = stars,
        firstTry = firstTry
      )

      val newBadges = state.unlockedBadges.toMutableSet()
      effectiveBadge?.let { newBadges.add(it) }

      if (newCompleted.size >= 15) {
        newBadges.add("responsible_citizen")
      }

      state.copy(
        coins = state.coins + earnedCoins,
        xp = state.xp + earnedXp,
        civicPoints = state.civicPoints + earnedCivic,
        unlockedLevels = newUnlocked,
        completedLevels = newCompleted,
        unlockedBadges = newBadges,
        characterExpression = CharacterExpression.CELEBRATING
      )
    }
    persistState()

    // Also persist level progress in Firestore
    val auth = FirebaseAuth.getInstance()
    val uid = auth.currentUser?.uid
    if (uid != null) {
      viewModelScope.launch(Dispatchers.IO) {
        firestoreRepository.saveLevelProgress(
          CloudLevelProgress(
            levelId = levelId,
            stars = stars,
            firstTry = firstTry,
            userId = uid
          )
        )
      }
    }
  }

  fun failLevel(levelId: Int) {
    soundManager.playFail(_gameState.value.soundEnabled)
    _gameState.update {
      it.copy(characterExpression = CharacterExpression.CONFUSED)
    }
  }

  fun toggleSound() {
    _gameState.update {
      val next = !it.soundEnabled
      soundManager.playClick(next)
      it.copy(soundEnabled = next)
    }
    persistState()
  }

  fun toggleMusic() {
    soundManager.playClick(_gameState.value.soundEnabled)
    _gameState.update { it.copy(musicEnabled = !it.musicEnabled) }
    persistState()
  }

  fun resetGame() {
    soundManager.playClick(_gameState.value.soundEnabled)
    _gameState.value = GameState(isCharacterCreated = false)
    viewModelScope.launch(Dispatchers.IO) {
      try {
        localRepository.resetAll()
      } catch (e: Exception) {
        // Ignored
      }
    }
  }

  override fun onCleared() {
    super.onCleared()
    soundManager.release()
  }
}
