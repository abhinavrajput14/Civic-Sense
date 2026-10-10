package com.example.data

import android.content.Context
import com.example.R
import com.example.model.CloudLevel
import com.example.model.CloudLevelProgress
import com.example.model.CloudUserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class CivicFirestoreRepository(private val db: FirebaseFirestore) {

  constructor(context: Context) : this(
    FirebaseFirestore.getInstance(
      context.applicationContext.getString(R.string.firestore_database_id)
    )
  )

  private val auth: FirebaseAuth get() = FirebaseAuth.getInstance()

  private fun requireUserId(): String {
    return auth.currentUser?.uid
      ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
  }

  fun observeLevels(): Flow<List<CloudLevel>> = flow {
    val path = "levels"
    emitAll(
      db.collection(path)
        .whereEqualTo("isPublished", true)
        .snapshots()
        .map { snapshot ->
          snapshot.documents.mapNotNull { it.toObject(CloudLevel::class.java) }
        }
        .catch { error ->
          if (error is Exception) {
            handleFirestoreError(error, OperationType.LIST, path)
          }
          throw error
        }
    )
  }

  suspend fun saveLevel(level: CloudLevel): Result<Unit> {
    val uid = requireUserId()
    val docPath = "levels/level_${level.id}"
    val docRef = db.collection("levels").document("level_${level.id}")
    return try {
      val data = hashMapOf(
        "id" to level.id,
        "title" to level.title,
        "subtitle" to level.subtitle,
        "topic" to level.topic,
        "scenarioDescription" to level.scenarioDescription,
        "objective" to level.objective,
        "moralMessage" to level.moralMessage,
        "rewardCoins" to level.rewardCoins,
        "bonusCoinsFirstTry" to level.bonusCoinsFirstTry,
        "rewardXp" to level.rewardXp,
        "rewardCivicPoints" to level.rewardCivicPoints,
        "badgeId" to level.badgeId,
        "world" to level.world,
        "worldTitle" to level.worldTitle,
        "isPublished" to level.isPublished,
        "authorId" to uid,
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
      )
      docRef.set(data, SetOptions.merge()).await()
      Result.success(Unit)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.WRITE, docPath)
      Result.failure(e)
    }
  }

  fun observeUserProfile(userId: String): Flow<CloudUserProfile?> = flow {
    val path = "users/$userId"
    emitAll(
      db.collection("users").document(userId)
        .snapshots()
        .map { snapshot ->
          if (snapshot.exists()) snapshot.toObject(CloudUserProfile::class.java) else null
        }
        .catch { error ->
          if (error is Exception) {
            handleFirestoreError(error, OperationType.GET, path)
          }
          throw error
        }
    )
  }

  suspend fun saveUserProfile(profile: CloudUserProfile): Result<Unit> {
    val uid = requireUserId()
    require(profile.userId == uid) { "Cannot modify another user profile" }
    val docPath = "users/$uid"
    val docRef = db.collection("users").document(uid)
    return try {
      val data = hashMapOf(
        "userId" to uid,
        "username" to profile.username,
        "coins" to profile.coins,
        "xp" to profile.xp,
        "civicPoints" to profile.civicPoints,
        "unlockedBadges" to profile.unlockedBadges,
        "characterName" to profile.characterName,
        "gender" to profile.gender,
        "skinTone" to profile.skinTone,
        "hairStyle" to profile.hairStyle,
        "hairColor" to profile.hairColor,
        "outfitColor" to profile.outfitColor,
        "accessory" to profile.accessory,
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
      )
      docRef.set(data, SetOptions.merge()).await()
      Result.success(Unit)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.WRITE, docPath)
      Result.failure(e)
    }
  }

  suspend fun saveLevelProgress(progress: CloudLevelProgress): Result<Unit> {
    val uid = requireUserId()
    require(progress.userId == uid) { "Cannot modify another user's progress" }
    val docPath = "users/$uid/progress/level_${progress.levelId}"
    val docRef = db.collection("users").document(uid)
      .collection("progress").document("level_${progress.levelId}")
    return try {
      val data = hashMapOf(
        "levelId" to progress.levelId,
        "stars" to progress.stars,
        "firstTry" to progress.firstTry,
        "userId" to uid,
        "completedAt" to FieldValue.serverTimestamp()
      )
      docRef.set(data, SetOptions.merge()).await()
      Result.success(Unit)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.WRITE, docPath)
      Result.failure(e)
    }
  }

  fun observeUserProgress(userId: String): Flow<List<CloudLevelProgress>> = flow {
    val path = "users/$userId/progress"
    emitAll(
      db.collection("users").document(userId).collection("progress")
        .snapshots()
        .map { snapshot ->
          snapshot.documents.mapNotNull { it.toObject(CloudLevelProgress::class.java) }
        }
        .catch { error ->
          if (error is Exception) {
            handleFirestoreError(error, OperationType.LIST, path)
          }
          throw error
        }
    )
  }
}
