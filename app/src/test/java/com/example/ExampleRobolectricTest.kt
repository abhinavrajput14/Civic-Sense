package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.BadgesData
import com.example.model.LevelCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Civic Quest", appName)
  }

  @Test
  fun `verify level catalog has 15 complete levels`() {
    assertEquals(15, LevelCatalog.LEVELS.size)
    for (i in 1..15) {
      val level = LevelCatalog.getLevel(i)
      assertNotNull("Level $i must exist", level)
      assertTrue("Level $i must have a non-empty title", level!!.title.isNotBlank())
      assertTrue("Level $i must have a moral message", level.moralMessage.isNotBlank())
    }
  }

  @Test
  fun `verify all badges configured`() {
    assertEquals(15, BadgesData.ALL_BADGES.size)
  }

  @Test
  fun `verify character name update and fallback persistence`() {
    val app = ApplicationProvider.getApplicationContext<android.app.Application>()
    val vm = com.example.viewmodel.GameViewModel(app)
    // Default boy name is Spidyy
    assertEquals("Spidyy", vm.gameState.value.character.name)

    // Update custom name
    vm.updateName("Priya Sharma")
    assertEquals("Priya Sharma", vm.gameState.value.character.name)

    // Save trimmed name
    vm.saveName("  Priya Sharma  ")
    assertEquals("Priya Sharma", vm.gameState.value.character.name)

    // Switching gender with custom name must PRESERVE custom name
    vm.selectGender(com.example.model.CharacterGender.GIRL)
    assertEquals("Priya Sharma", vm.gameState.value.character.name)
    assertEquals(com.example.model.CharacterGender.GIRL, vm.gameState.value.character.gender)
    assertEquals(com.example.model.HairStyle.WAVY_BOB, vm.gameState.value.character.hairStyle)

    // Saving blank name falls back gracefully without corrupting state
    vm.saveName("   ")
    assertEquals("Priya Sharma", vm.gameState.value.character.name)
  }

  @Test
  fun `verify instant customization updates`() {
    val app = ApplicationProvider.getApplicationContext<android.app.Application>()
    val vm = com.example.viewmodel.GameViewModel(app)

    vm.updateOutfit(com.example.model.OutfitStyle.SCHOOL_HERO)
    assertEquals(com.example.model.OutfitStyle.SCHOOL_HERO, vm.gameState.value.character.outfitStyle)

    vm.updatePants(com.example.model.PantsStyle.DARK_JOGGERS)
    assertEquals(com.example.model.PantsStyle.DARK_JOGGERS, vm.gameState.value.character.pantsStyle)

    vm.updateShoes(com.example.model.ShoesStyle.RED_RUNNERS)
    assertEquals(com.example.model.ShoesStyle.RED_RUNNERS, vm.gameState.value.character.shoesStyle)

    vm.updateHairStyle(com.example.model.HairStyle.WAVY_BOB)
    assertEquals(com.example.model.HairStyle.WAVY_BOB, vm.gameState.value.character.hairStyle)

    vm.updateAccessory(com.example.model.Accessory.COOL_CAP)
    assertEquals(com.example.model.Accessory.COOL_CAP, vm.gameState.value.character.accessory)
  }

  @Test
  fun `verify all 12 basic t-shirt colors exist and are valid`() {
    val tshirts = com.example.model.TShirtColor.values()
    assertEquals(12, tshirts.size)
    val expectedLabels = listOf(
      "White T-Shirt",
      "Black T-Shirt",
      "Red T-Shirt",
      "Blue T-Shirt",
      "Green T-Shirt",
      "Yellow T-Shirt",
      "Orange T-Shirt",
      "Purple T-Shirt",
      "Sky Blue T-Shirt",
      "Pink T-Shirt",
      "Grey T-Shirt",
      "Dark Blue T-Shirt"
    )
    for (expected in expectedLabels) {
      assertTrue("T-Shirt catalog should contain $expected", tshirts.any { it.label == expected })
    }
  }

  @Test
  fun `verify t-shirt selection and mutual exclusivity with school outfit`() {
    val app = ApplicationProvider.getApplicationContext<android.app.Application>()
    val vm = com.example.viewmodel.GameViewModel(app)

    // Default outfit is School Outfit
    assertEquals(com.example.model.OutfitStyle.SCHOOL_HERO, vm.gameState.value.character.outfitStyle)
    assertEquals(null, vm.gameState.value.character.selectedTShirt)

    // 1. Select Black T-Shirt
    vm.selectTShirt(com.example.model.TShirtColor.BLACK)
    assertEquals(com.example.model.TShirtColor.BLACK, vm.gameState.value.character.selectedTShirt)

    // 2. Select Sky Blue T-Shirt -> immediately changes, deselects Black
    vm.selectTShirt(com.example.model.TShirtColor.SKY_BLUE)
    assertEquals(com.example.model.TShirtColor.SKY_BLUE, vm.gameState.value.character.selectedTShirt)

    // 3. Switch back to School Outfit -> clears T-shirt and sets School Outfit
    vm.selectSchoolOutfit()
    assertEquals(null, vm.gameState.value.character.selectedTShirt)
    assertEquals(com.example.model.OutfitStyle.SCHOOL_HERO, vm.gameState.value.character.outfitStyle)

    // 4. Select Yellow T-Shirt
    vm.selectTShirt(com.example.model.TShirtColor.YELLOW)
    assertEquals(com.example.model.TShirtColor.YELLOW, vm.gameState.value.character.selectedTShirt)

    // 5. Select another outfit (e.g. Explorer) -> clears T-shirt
    vm.updateOutfit(com.example.model.OutfitStyle.EXPLORER)
    assertEquals(null, vm.gameState.value.character.selectedTShirt)
    assertEquals(com.example.model.OutfitStyle.EXPLORER, vm.gameState.value.character.outfitStyle)
  }
}

