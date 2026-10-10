package com.example.data

import com.example.base.FirestoreEmulatorTestBase
import com.example.model.CloudLevel
import com.example.model.CloudLevelProgress
import com.example.model.CloudUserProfile
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class CivicFirestoreRepositoryRuleTest : FirestoreEmulatorTestBase() {

  @Test
  fun saveAndObserveUserProfile_authenticatedOwner_succeeds() = runBlocking {
    val uid = signInTestUser("player1@example.com")
    val repo = CivicFirestoreRepository(firestore)

    val profile = CloudUserProfile(
      userId = uid,
      username = "TestCivicHero",
      coins = 200,
      xp = 150,
      civicPoints = 40,
      unlockedBadges = listOf("green_guardian")
    )

    val saveResult = repo.saveUserProfile(profile)
    assertTrue("Profile save should succeed", saveResult.isSuccess)

    val observed = withTimeout(5000L) {
      repo.observeUserProfile(uid).first { it != null }
    }
    assertNotNull(observed)
    assertEquals("TestCivicHero", observed?.username)
    assertEquals(200, observed?.coins)
  }

  @Test
  fun saveLevelProgress_authenticatedOwner_succeeds() = runBlocking {
    val uid = signInTestUser("player2@example.com")
    val repo = CivicFirestoreRepository(firestore)

    val progress = CloudLevelProgress(
      levelId = 1,
      stars = 3,
      firstTry = true,
      userId = uid
    )

    val saveResult = repo.saveLevelProgress(progress)
    assertTrue("Progress save should succeed", saveResult.isSuccess)

    val observedList = withTimeout(5000L) {
      repo.observeUserProgress(uid).first { list -> list.any { it.levelId == 1 } }
    }
    assertTrue("Progress should contain level 1", observedList.any { it.levelId == 1 })
  }

  @Test
  fun saveLevel_authenticatedUser_succeedsAndObserves() = runBlocking {
    val uid = signInTestUser("creator@example.com")
    val repo = CivicFirestoreRepository(firestore)

    val level = CloudLevel(
      id = 16,
      title = "Cyber Safety Patrol",
      subtitle = "Online Ethics & Scams",
      topic = "Digital Citizenship",
      scenarioDescription = "You receive an unsolicited message claiming you won a lottery.",
      objective = "Identify the phishing message and block the sender.",
      moralMessage = "Think before you click and protect your private data.",
      rewardCoins = 100,
      rewardXp = 200,
      rewardCivicPoints = 50,
      world = 4,
      worldTitle = "Digital Citizenship",
      isPublished = true,
      authorId = uid
    )

    val saveResult = repo.saveLevel(level)
    assertTrue("Saving level should succeed", saveResult.isSuccess)

    val levelsList = withTimeout(5000L) {
      repo.observeLevels().first { list -> list.any { it.id == 16 } }
    }
    assertTrue("Levels should contain newly published level 16", levelsList.any { it.id == 16 })
  }

  @Test
  fun unauthenticatedUser_cannotWriteProfile_fails() = runBlocking {
    auth.signOut()
    val repo = CivicFirestoreRepository(firestore)
    val profile = CloudUserProfile(
      userId = "unauth_user",
      username = "Hacker"
    )

    try {
      repo.saveUserProfile(profile)
      fail("Should throw IllegalStateException for unauthenticated user")
    } catch (e: IllegalStateException) {
      assertTrue(e.message?.contains("User must be signed in with Google") == true)
    }
  }
}
