package com.example.model

data class LevelCompletion(
  val stars: Int = 3,
  val firstTry: Boolean = true,
  val completedAt: Long = System.currentTimeMillis()
)

data class GameState(
  val character: CharacterProfile = CharacterProfile(),
  val isCharacterCreated: Boolean = false,
  val coins: Int = 120,
  val xp: Int = 0,
  val civicPoints: Int = 0,
  val unlockedLevels: Set<Int> = setOf(1),
  val completedLevels: Map<Int, LevelCompletion> = emptyMap(),
  val unlockedBadges: Set<String> = emptySet(),
  val soundEnabled: Boolean = true,
  val musicEnabled: Boolean = true,
  val activeLevelId: Int? = null,
  val characterExpression: CharacterExpression = CharacterExpression.IDLE
) {
  val playerLevel: Int
    get() = (xp / 120) + 1

  val progressToNextLevel: Float
    get() {
      val currentLevelXp = xp % 120
      return (currentLevelXp / 120f).coerceIn(0f, 1f)
    }

  val rankTitle: String
    get() = when {
      completedLevels.size >= 15 -> "Legendary Civic Hero"
      completedLevels.size >= 10 -> "Master Civic Hero"
      completedLevels.size >= 7 -> "Civic Champion"
      completedLevels.size >= 4 -> "Community Guardian"
      completedLevels.size >= 1 -> "Helpful Citizen"
      else -> "Civic Explorer"
    }
}
