package com.example.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class CloudLevel(
  @DocumentId val idString: String = "",
  val id: Int = 1,
  val title: String = "",
  val subtitle: String = "",
  val topic: String = "",
  val scenarioDescription: String = "",
  val objective: String = "",
  val moralMessage: String = "",
  val rewardCoins: Int = 50,
  val bonusCoinsFirstTry: Int = 25,
  val rewardXp: Int = 100,
  val rewardCivicPoints: Int = 25,
  val badgeId: String = "",
  val world: Int = 1,
  val worldTitle: String = "Civic Missions",
  val isPublished: Boolean = true,
  val authorId: String? = null,
  val questions: List<CloudQuestion> = emptyList(),
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class CloudQuestion(
  val questionText: String = "",
  val options: List<String> = emptyList(),
  val correctIndex: Int = 0,
  val explanation: String = ""
)

@IgnoreExtraProperties
data class CloudUserProfile(
  val userId: String = "",
  val username: String = "",
  val coins: Int = 120,
  val xp: Int = 0,
  val civicPoints: Int = 0,
  val unlockedBadges: List<String> = emptyList(),
  val characterName: String = "Spidyy",
  val gender: String = "BOY",
  val skinTone: String = "MEDIUM",
  val hairStyle: String = "SHORT_SPIKY",
  val hairColor: String = "BLACK",
  val outfitColor: String = "SCHOOL_HERO",
  val accessory: String = "BADGE_PIN",
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class CloudLevelProgress(
  val levelId: Int = 1,
  val stars: Int = 3,
  val firstTry: Boolean = true,
  val userId: String = "",
  @ServerTimestamp val completedAt: Timestamp? = null
)
