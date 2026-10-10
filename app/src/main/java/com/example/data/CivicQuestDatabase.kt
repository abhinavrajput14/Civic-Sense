package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.model.Accessory
import com.example.model.CharacterGender
import com.example.model.CharacterProfile
import com.example.model.GameState
import com.example.model.HairColor
import com.example.model.HairStyle
import com.example.model.LevelCompletion
import com.example.model.OutfitStyle
import com.example.model.PantsStyle
import com.example.model.ShoesStyle
import com.example.model.SkinTone
import com.example.model.TShirtColor

@Entity(tableName = "player_profile")
data class PlayerProfileEntity(
  @PrimaryKey val id: Int = 1,
  val name: String = "Spidyy",
  val gender: String = CharacterGender.BOY.name,
  val skinTone: String = SkinTone.MEDIUM.name,
  val hairStyle: String = HairStyle.SHORT_SPIKY.name,
  val hairColor: String = HairColor.BLACK.name,
  val outfitStyle: String = OutfitStyle.SCHOOL_HERO.name,
  val selectedTShirt: String? = null,
  val pantsStyle: String = PantsStyle.DENIM_JEANS.name,
  val shoesStyle: String = ShoesStyle.WHITE_SNEAKERS.name,
  val accessory: String = Accessory.BADGE_PIN.name,
  val backpackHex: Long = 0xFFFF7043,
  val isCharacterCreated: Boolean = false,
  val coins: Int = 120,
  val xp: Int = 0,
  val civicPoints: Int = 0,
  val unlockedLevelsCsv: String = "1",
  val unlockedBadgesCsv: String = "",
  val soundEnabled: Boolean = true,
  val musicEnabled: Boolean = true
)

@Entity(tableName = "level_records")
data class LevelRecordEntity(
  @PrimaryKey val levelId: Int,
  val stars: Int,
  val firstTry: Boolean,
  val completedAt: Long
)

@Dao
interface CivicQuestDao {
  @Query("SELECT * FROM player_profile WHERE id = 1 LIMIT 1")
  suspend fun getProfile(): PlayerProfileEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveProfile(profile: PlayerProfileEntity)

  @Query("SELECT * FROM level_records")
  suspend fun getAllLevelRecords(): List<LevelRecordEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveLevelRecord(record: LevelRecordEntity)

  @Query("DELETE FROM level_records")
  suspend fun clearLevelRecords()

  @Query("DELETE FROM player_profile")
  suspend fun clearProfile()
}

@Database(
  entities = [PlayerProfileEntity::class, LevelRecordEntity::class],
  version = 1,
  exportSchema = false
)
abstract class CivicQuestDatabase : RoomDatabase() {
  abstract fun civicQuestDao(): CivicQuestDao

  companion object {
    @Volatile
    private var INSTANCE: CivicQuestDatabase? = null

    fun getDatabase(context: Context): CivicQuestDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          CivicQuestDatabase::class.java,
          "civic_quest_db"
        ).fallbackToDestructiveMigration().build()
        INSTANCE = instance
        instance
      }
    }
  }
}

class CivicQuestRepository(private val dao: CivicQuestDao) {

  suspend fun loadGameState(): GameState? {
    val profile = dao.getProfile() ?: return null
    val levelRecords = dao.getAllLevelRecords()

    val character = CharacterProfile(
      name = profile.name.ifBlank { "Spidyy" },
      gender = runCatching { CharacterGender.valueOf(profile.gender) }.getOrDefault(CharacterGender.BOY),
      skinTone = runCatching { SkinTone.valueOf(profile.skinTone) }.getOrDefault(SkinTone.MEDIUM),
      hairStyle = runCatching { HairStyle.valueOf(profile.hairStyle) }.getOrDefault(HairStyle.SHORT_SPIKY),
      hairColor = runCatching { HairColor.valueOf(profile.hairColor) }.getOrDefault(HairColor.BLACK),
      outfitStyle = runCatching { OutfitStyle.valueOf(profile.outfitStyle) }.getOrDefault(OutfitStyle.SCHOOL_HERO),
      selectedTShirt = profile.selectedTShirt?.let { runCatching { TShirtColor.valueOf(it) }.getOrNull() },
      pantsStyle = runCatching { PantsStyle.valueOf(profile.pantsStyle) }.getOrDefault(PantsStyle.DENIM_JEANS),
      shoesStyle = runCatching { ShoesStyle.valueOf(profile.shoesStyle) }.getOrDefault(ShoesStyle.WHITE_SNEAKERS),
      accessory = runCatching { Accessory.valueOf(profile.accessory) }.getOrDefault(Accessory.BADGE_PIN),
      backpackHex = profile.backpackHex
    )

    val unlockedLevels = if (profile.unlockedLevelsCsv.isBlank()) {
      setOf(1)
    } else {
      profile.unlockedLevelsCsv.split(",")
        .mapNotNull { it.trim().toIntOrNull() }
        .toSet()
        .ifEmpty { setOf(1) }
    }

    val unlockedBadges = if (profile.unlockedBadgesCsv.isBlank()) {
      emptySet()
    } else {
      profile.unlockedBadgesCsv.split(",")
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .toSet()
    }

    val completedLevelsMap = levelRecords.associate { record ->
      record.levelId to LevelCompletion(
        stars = record.stars,
        firstTry = record.firstTry,
        completedAt = record.completedAt
      )
    }

    return GameState(
      character = character,
      isCharacterCreated = profile.isCharacterCreated,
      coins = profile.coins,
      xp = profile.xp,
      civicPoints = profile.civicPoints,
      unlockedLevels = unlockedLevels,
      completedLevels = completedLevelsMap,
      unlockedBadges = unlockedBadges,
      soundEnabled = profile.soundEnabled,
      musicEnabled = profile.musicEnabled
    )
  }

  suspend fun saveGameState(state: GameState) {
    val profileEntity = PlayerProfileEntity(
      id = 1,
      name = state.character.name,
      gender = state.character.gender.name,
      skinTone = state.character.skinTone.name,
      hairStyle = state.character.hairStyle.name,
      hairColor = state.character.hairColor.name,
      outfitStyle = state.character.outfitStyle.name,
      selectedTShirt = state.character.selectedTShirt?.name,
      pantsStyle = state.character.pantsStyle.name,
      shoesStyle = state.character.shoesStyle.name,
      accessory = state.character.accessory.name,
      backpackHex = state.character.backpackHex,
      isCharacterCreated = state.isCharacterCreated,
      coins = state.coins,
      xp = state.xp,
      civicPoints = state.civicPoints,
      unlockedLevelsCsv = state.unlockedLevels.joinToString(","),
      unlockedBadgesCsv = state.unlockedBadges.joinToString(","),
      soundEnabled = state.soundEnabled,
      musicEnabled = state.musicEnabled
    )
    dao.saveProfile(profileEntity)

    state.completedLevels.forEach { (levelId, completion) ->
      dao.saveLevelRecord(
        LevelRecordEntity(
          levelId = levelId,
          stars = completion.stars,
          firstTry = completion.firstTry,
          completedAt = completion.completedAt
        )
      )
    }
  }

  suspend fun resetAll() {
    dao.clearLevelRecords()
    dao.clearProfile()
  }
}
