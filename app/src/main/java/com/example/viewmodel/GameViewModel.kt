package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundManager
import com.example.data.CivicQuestDatabase
import com.example.data.CivicQuestRepository
import com.example.model.Accessory
import com.example.model.BadgesData
import com.example.model.CharacterExpression
import com.example.model.CharacterGender
import com.example.model.CharacterProfile
import com.example.model.GameState
import com.example.model.HairColor
import com.example.model.HairStyle
import com.example.model.LevelCatalog
import com.example.model.LevelCompletion
import com.example.model.OutfitStyle
import com.example.model.PantsStyle
import com.example.model.ShoesStyle
import com.example.model.SkinTone
import com.example.model.TShirtColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {
  val soundManager = SoundManager()

  private val repository = CivicQuestRepository(
    CivicQuestDatabase.getDatabase(application).civicQuestDao()
  )

  private val _gameState = MutableStateFlow(GameState())
  val gameState: StateFlow<GameState> = _gameState.asStateFlow()

  init {
    viewModelScope.launch(Dispatchers.IO) {
      val savedState = repository.loadGameState()
      if (savedState != null) {
        _gameState.value = savedState
      }
    }
  }

  private fun persistState() {
    val currentState = _gameState.value
    viewModelScope.launch(Dispatchers.IO) {
      try {
        repository.saveGameState(currentState)
      } catch (e: Exception) {
        // Safe fallback in case of background persistence interruption
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
    val level = LevelCatalog.getLevel(levelId) ?: return
    val bonus = if (firstTry) level.bonusCoinsFirstTry else 0
    val earnedCoins = level.rewardCoins + bonus
    val earnedXp = level.rewardXp
    val earnedCivic = level.rewardCivicPoints

    soundManager.playVictoryJingle(_gameState.value.soundEnabled)

    _gameState.update { state ->
      val newUnlocked = state.unlockedLevels.toMutableSet()
      if (levelId < LevelCatalog.LEVELS.size) {
        newUnlocked.add(levelId + 1)
      }

      val newCompleted = state.completedLevels.toMutableMap()
      newCompleted[levelId] = LevelCompletion(
        stars = if (firstTry) 3 else 2,
        firstTry = firstTry
      )

      val newBadges = state.unlockedBadges.toMutableSet()
      level.badgeId?.let { newBadges.add(it) }

      // Check if all levels completed for ultimate badge
      if (newCompleted.size >= LevelCatalog.LEVELS.size) {
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
        repository.resetAll()
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
