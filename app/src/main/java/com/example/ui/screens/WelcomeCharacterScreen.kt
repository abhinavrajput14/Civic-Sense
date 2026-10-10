package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Accessory
import com.example.model.CharacterExpression
import com.example.model.CharacterGender
import com.example.model.HairColor
import com.example.model.HairStyle
import com.example.model.OutfitStyle
import com.example.model.PantsStyle
import com.example.model.ShoesStyle
import com.example.model.SkinTone
import com.example.model.TShirtColor
import com.example.ui.components.CharacterAvatar
import com.example.viewmodel.GameViewModel

@Composable
fun WelcomeCharacterScreen(
  viewModel: GameViewModel,
  onFinished: () -> Unit,
  onBackClick: (() -> Unit)? = null
) {
  val focusManager = LocalFocusManager.current
  val gameState by viewModel.gameState.collectAsState()
  val character = gameState.character

  var selectedTab by remember { mutableIntStateOf(0) }
  val tabs = listOf("Hair & Skin", "T-Shirts", "Outfits", "Accessories")

  val scrollState = rememberScrollState()

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          listOf(Color(0xFFE0F7FA), Color(0xFFF1F8E9), Color(0xFFFFFDE7))
        )
      )
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
        .imePadding()
        .verticalScroll(scrollState)
        .padding(horizontal = 20.dp, vertical = 12.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Top header with Back button if character already created
      if (gameState.isCharacterCreated || onBackClick != null) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = { onBackClick?.invoke() ?: onFinished() },
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(Color.White.copy(alpha = 0.85f))
              .testTag("character_customization_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back to Home",
              tint = Color(0xFF00695C)
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Text(
            text = "Character Studio",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color(0xFF00695C)
          )
        }
      } else {
        // Game Branding
        Text(
          text = "CIVIC QUEST",
          fontWeight = FontWeight.Black,
          fontSize = 28.sp,
          color = Color(0xFF00695C),
          letterSpacing = 1.sp
        )
        Text(
          text = "Play • Choose • Learn • Change",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF00897B),
          modifier = Modifier.padding(bottom = 12.dp)
        )
      }

      // Character Preview Card with interactive tap to cheer!
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(24.dp))
          .clickable {
            viewModel.setExpression(CharacterExpression.HAPPY)
            viewModel.soundManager.playClick()
          }
          .testTag("character_preview_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "Tap your hero to say hello!",
            fontSize = 11.sp,
            color = Color(0xFF90A4AE),
            fontWeight = FontWeight.SemiBold
          )

          CharacterAvatar(
            character = character,
            expression = gameState.characterExpression,
            size = 120.dp,
            modifier = Modifier.padding(vertical = 2.dp)
          )

          // Gender Selector (BOY / GIRL)
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(16.dp))
              .background(Color(0xFFF1F5F9))
              .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            val isBoy = character.gender == CharacterGender.BOY
            Surface(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable { viewModel.selectGender(CharacterGender.BOY) }
                .testTag("select_boy_button"),
              color = if (isBoy) Color(0xFF0288D1) else Color.Transparent,
              shape = RoundedCornerShape(12.dp)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Male,
                  contentDescription = "Boy",
                  tint = if (isBoy) Color.White else Color(0xFF64748B),
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "BOY",
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp,
                  color = if (isBoy) Color.White else Color(0xFF64748B)
                )
              }
            }

            val isGirl = character.gender == CharacterGender.GIRL
            Surface(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable { viewModel.selectGender(CharacterGender.GIRL) }
                .testTag("select_girl_button"),
              color = if (isGirl) Color(0xFFD81B60) else Color.Transparent,
              shape = RoundedCornerShape(12.dp)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Female,
                  contentDescription = "Girl",
                  tint = if (isGirl) Color.White else Color(0xFF64748B),
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "GIRL",
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp,
                  color = if (isGirl) Color.White else Color(0xFF64748B)
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Character Name input (controlled reactive state)
      var nameText by remember(character.name) { mutableStateOf(character.name) }

      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
      ) {
        Text(
          text = "Player Name",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF00695C),
          modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )

        OutlinedTextField(
          value = nameText,
          onValueChange = { newText ->
            nameText = newText
            viewModel.updateName(newText)
          },
          placeholder = {
            Text(
              text = "Enter your name",
              color = Color(0xFF94A3B8),
              fontSize = 16.sp
            )
          },
          textStyle = androidx.compose.ui.text.TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
          ),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("character_name_input"),
          shape = RoundedCornerShape(16.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color(0xFF1E293B),
            unfocusedTextColor = Color(0xFF1E293B),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = Color(0xFF00897B),
            unfocusedBorderColor = Color(0xFF94A3B8),
            cursorColor = Color(0xFF00897B)
          ),
          keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
          keyboardActions = KeyboardActions(onDone = {
            viewModel.saveName(nameText)
            focusManager.clearFocus()
          })
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Customization Category Tabs - Segmented Pill Bar with distinct badges
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState())
          .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        val tabIcons = listOf("✨ Hair & Skin", "👕 T-Shirts", "👔 Outfits", "🎒 Extras")
        tabs.forEachIndexed { index, title ->
          val isSelected = selectedTab == index
          Surface(
            modifier = Modifier
              .clip(RoundedCornerShape(16.dp))
              .clickable { selectedTab = index }
              .testTag("tab_${title.lowercase().replace(" ", "_").replace("&", "and")}"),
            color = if (isSelected) Color(0xFF00695C) else Color.White,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(
              width = if (isSelected) 2.dp else 1.dp,
              color = if (isSelected) Color(0xFF004D40) else Color(0xFFB0BEC5)
            ),
            shadowElevation = if (isSelected) 4.dp else 1.dp
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = tabIcons.getOrElse(index) { title },
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                fontSize = 13.sp,
                color = if (isSelected) Color.White else Color(0xFF37474F)
              )
              if (index == 1 && character.selectedTShirt != null) {
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color(character.selectedTShirt!!.hex))
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Tab Content
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          when (selectedTab) {
            0 -> {
              // Skin Tone Selector
              Text(
                text = "Skin Tone",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color(0xFF37474F)
              )
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState())
                  .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                SkinTone.values().forEach { tone ->
                  val isSelected = character.skinTone == tone
                  Box(
                    modifier = Modifier
                      .size(42.dp)
                      .clip(CircleShape)
                      .background(Color(tone.hex))
                      .border(
                        width = if (isSelected) 3.dp else 1.dp,
                        color = if (isSelected) Color(0xFF00897B) else Color(0xFFCFD8DC),
                        shape = CircleShape
                      )
                      .clickable { viewModel.updateSkinTone(tone) },
                    contentAlignment = Alignment.Center
                  ) {
                    if (isSelected) {
                      Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color(0xFF212121),
                        modifier = Modifier.size(20.dp)
                      )
                    }
                  }
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              // Hair Style Selector
              Text(
                text = "Hairstyle",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color(0xFF37474F)
              )
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState())
                  .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                HairStyle.values().forEach { style ->
                  val isSelected = character.hairStyle == style
                  Surface(
                    color = if (isSelected) Color(0xFFE0F2F1) else Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                      width = if (isSelected) 2.dp else 1.dp,
                      color = if (isSelected) Color(0xFF00897B) else Color(0xFFE2E8F0)
                    ),
                    modifier = Modifier.clickable { viewModel.updateHairStyle(style) }
                  ) {
                    Text(
                      text = style.label,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                      fontSize = 12.sp,
                      color = if (isSelected) Color(0xFF004D40) else Color(0xFF475569),
                      modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              // Hair Color Selector
              Text(
                text = "Hair Color",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color(0xFF37474F)
              )
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState())
                  .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                HairColor.values().forEach { color ->
                  val isSelected = character.hairColor == color
                  Box(
                    modifier = Modifier
                      .size(38.dp)
                      .clip(CircleShape)
                      .background(Color(color.hex))
                      .border(
                        width = if (isSelected) 3.dp else 1.dp,
                        color = if (isSelected) Color(0xFF00897B) else Color(0xFFCFD8DC),
                        shape = CircleShape
                      )
                      .clickable { viewModel.updateHairColor(color) },
                    contentAlignment = Alignment.Center
                  ) {
                    if (isSelected) {
                      Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                      )
                    }
                  }
                }
              }
            }

            1 -> {
              // Tab 1: Everyday T-Shirts
              val isWearingSchoolOutfit = character.selectedTShirt == null && character.outfitStyle == OutfitStyle.SCHOOL_HERO

              // Quick action: Switch to School Outfit
              Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (isWearingSchoolOutfit) Color(0xFFE0F2F1) else Color(0xFFF1F5F9),
                border = BorderStroke(
                  width = if (isWearingSchoolOutfit) 2.dp else 1.dp,
                  color = if (isWearingSchoolOutfit) Color(0xFF00897B) else Color(0xFFE2E8F0)
                ),
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(14.dp))
                  .clickable { viewModel.selectSchoolOutfit() }
                  .testTag("switch_to_school_outfit")
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Box(
                    modifier = Modifier
                      .size(34.dp)
                      .clip(CircleShape)
                      .background(Color.White)
                      .border(1.5.dp, Color(0xFFCFD8DC), CircleShape),
                    contentAlignment = Alignment.Center
                  ) {
                    Text("👔", fontSize = 17.sp)
                  }
                  Spacer(modifier = Modifier.width(12.dp))
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = "School Outfit",
                      fontWeight = FontWeight.Bold,
                      fontSize = 13.sp,
                      color = Color(0xFF1E293B)
                    )
                    Text(
                      text = "Clean white shirt + solid black necktie",
                      fontSize = 11.sp,
                      color = Color(0xFF64748B)
                    )
                  }
                  if (isWearingSchoolOutfit) {
                    Surface(
                      shape = CircleShape,
                      color = Color(0xFF00897B),
                      modifier = Modifier.size(24.dp)
                    ) {
                      Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Active",
                        tint = Color.White,
                        modifier = Modifier.padding(4.dp)
                      )
                    }
                  }
                }
              }

              Spacer(modifier = Modifier.height(14.dp))

              Text(
                text = "Everyday T-Shirts (12 Colors)",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color(0xFF37474F)
              )
              Text(
                text = "Simple, clean, solid-colour everyday T-shirts",
                fontSize = 11.sp,
                color = Color(0xFF78909C),
                modifier = Modifier.padding(bottom = 8.dp)
              )

              val tshirts = TShirtColor.values()
              for (i in tshirts.indices step 2) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  val t1 = tshirts[i]
                  TShirtCard(
                    tshirt = t1,
                    isSelected = character.selectedTShirt == t1,
                    onClick = { viewModel.selectTShirt(t1) },
                    modifier = Modifier.weight(1f)
                  )
                  if (i + 1 < tshirts.size) {
                    val t2 = tshirts[i + 1]
                    TShirtCard(
                      tshirt = t2,
                      isSelected = character.selectedTShirt == t2,
                      onClick = { viewModel.selectTShirt(t2) },
                      modifier = Modifier.weight(1f)
                    )
                  } else {
                    Spacer(modifier = Modifier.weight(1f))
                  }
                }
              }
            }

            2 -> {
              // Tab 2: Outfits (School + Thematic)
              Text(
                text = "School & Thematic Outfits",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color(0xFF37474F)
              )
              if (character.selectedTShirt != null) {
                Text(
                  text = "Currently wearing: ${character.selectedTShirt?.label}. Tap below to switch to an outfit.",
                  fontSize = 11.sp,
                  color = Color(0xFF00897B),
                  modifier = Modifier.padding(top = 2.dp, bottom = 4.dp)
                )
              } else {
                Spacer(modifier = Modifier.height(4.dp))
              }

              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState())
                  .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                OutfitStyle.values().forEach { outfit ->
                  val isSelected = character.selectedTShirt == null && character.outfitStyle == outfit
                  Surface(
                    color = if (isSelected) Color(0xFFE0F2F1) else Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                      width = if (isSelected) 2.dp else 1.dp,
                      color = if (isSelected) Color(0xFF00897B) else Color(0xFFE2E8F0)
                    ),
                    modifier = Modifier
                      .clip(RoundedCornerShape(12.dp))
                      .clickable {
                        if (outfit == OutfitStyle.SCHOOL_HERO) {
                          viewModel.selectSchoolOutfit()
                        } else {
                          viewModel.updateOutfit(outfit)
                        }
                      }
                      .testTag("outfit_${outfit.name.lowercase()}")
                  ) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                      if (outfit == OutfitStyle.SCHOOL_HERO) {
                        Box(
                          modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, Color(0xFFB0BEC5), CircleShape),
                          contentAlignment = Alignment.Center
                        ) {
                          // Mini black necktie indicator
                          Box(
                            modifier = Modifier
                              .width(4.dp)
                              .height(10.dp)
                              .background(Color(0xFF212121), RoundedCornerShape(1.dp))
                          )
                        }
                      } else {
                        Box(
                          modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(Color(outfit.primaryHex))
                        )
                      }
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(
                        text = outfit.label,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 12.sp,
                        color = if (isSelected) Color(0xFF004D40) else Color(0xFF475569)
                      )
                      if (isSelected) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                          imageVector = Icons.Default.Check,
                          contentDescription = null,
                          tint = Color(0xFF00897B),
                          modifier = Modifier.size(14.dp)
                        )
                      }
                    }
                  }
                }
              }

              Spacer(modifier = Modifier.height(12.dp))

              // Pants
              Text(
                text = "Pants / Shorts",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color(0xFF37474F)
              )
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState())
                  .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                PantsStyle.values().forEach { pants ->
                  val isSelected = character.pantsStyle == pants
                  Surface(
                    color = if (isSelected) Color(0xFFE0F2F1) else Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                      width = if (isSelected) 2.dp else 1.dp,
                      color = if (isSelected) Color(0xFF00897B) else Color(0xFFE2E8F0)
                    ),
                    modifier = Modifier.clickable { viewModel.updatePants(pants) }
                  ) {
                    Text(
                      text = pants.label,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                      fontSize = 12.sp,
                      color = if (isSelected) Color(0xFF004D40) else Color(0xFF475569),
                      modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(12.dp))

              // Shoes
              Text(
                text = "Footwear",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color(0xFF37474F)
              )
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState())
                  .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                ShoesStyle.values().forEach { shoes ->
                  val isSelected = character.shoesStyle == shoes
                  Surface(
                    color = if (isSelected) Color(0xFFE0F2F1) else Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                      width = if (isSelected) 2.dp else 1.dp,
                      color = if (isSelected) Color(0xFF00897B) else Color(0xFFE2E8F0)
                    ),
                    modifier = Modifier.clickable { viewModel.updateShoes(shoes) }
                  ) {
                    Text(
                      text = shoes.label,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                      fontSize = 12.sp,
                      color = if (isSelected) Color(0xFF004D40) else Color(0xFF475569),
                      modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                  }
                }
              }
            }

            3 -> {
              // Tab 3: Accessories & Backpack
              Text(
                text = "Headgear & Accessories",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color(0xFF37474F)
              )
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState())
                  .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Accessory.values().forEach { acc ->
                  val isSelected = character.accessory == acc
                  Surface(
                    color = if (isSelected) Color(0xFFE0F2F1) else Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                      width = if (isSelected) 2.dp else 1.dp,
                      color = if (isSelected) Color(0xFF00897B) else Color(0xFFE2E8F0)
                    ),
                    modifier = Modifier.clickable { viewModel.updateAccessory(acc) }
                  ) {
                    Text(
                      text = acc.label,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                      fontSize = 12.sp,
                      color = if (isSelected) Color(0xFF004D40) else Color(0xFF475569),
                      modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(14.dp))

              // Backpack Color Palette
              Text(
                text = "School Backpack Color",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color(0xFF37474F)
              )
              val backpackOptions = listOf(
                0xFFFF7043 to "Sunset Orange",
                0xFF1E88E5 to "Royal Blue",
                0xFF43A047 to "Forest Green",
                0xFF8E24AA to "Deep Purple",
                0xFFE53935 to "Fire Red",
                0xFF37474F to "Slate Navy"
              )
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState())
                  .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                backpackOptions.forEach { (colorHex, label) ->
                  val isSelected = character.backpackHex == colorHex
                  Box(
                    modifier = Modifier
                      .size(42.dp)
                      .clip(CircleShape)
                      .background(Color(colorHex))
                      .border(
                        width = if (isSelected) 3.dp else 1.dp,
                        color = if (isSelected) Color(0xFF00897B) else Color(0xFFCFD8DC),
                        shape = CircleShape
                      )
                      .clickable { viewModel.updateBackpack(colorHex) },
                    contentAlignment = Alignment.Center
                  ) {
                    if (isSelected) {
                      Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = label,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Continue / Save Button
      Button(
        onClick = {
          viewModel.saveName(nameText)
          viewModel.completeCharacterCreation()
          onFinished()
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(56.dp)
          .testTag("continue_to_game_button"),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00897B)),
        shape = RoundedCornerShape(18.dp)
      ) {
        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = if (gameState.isCharacterCreated) "SAVE & CONTINUE" else "CONTINUE",
          fontWeight = FontWeight.Black,
          fontSize = 18.sp,
          letterSpacing = 1.sp
        )
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

@Composable
private fun TShirtCard(
  tshirt: TShirtColor,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    color = if (isSelected) Color(0xFFE0F2F1) else Color(0xFFF8FAFC),
    shape = RoundedCornerShape(12.dp),
    border = BorderStroke(
      width = if (isSelected) 2.dp else 1.dp,
      color = if (isSelected) Color(0xFF00897B) else Color(0xFFE2E8F0)
    ),
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .clickable { onClick() }
      .testTag("tshirt_${tshirt.name.lowercase()}")
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
      Box(
        modifier = Modifier
          .size(24.dp)
          .clip(CircleShape)
          .background(Color(tshirt.hex))
          .border(
            width = 1.dp,
            color = if (tshirt == TShirtColor.WHITE) Color(0xFFCFD8DC) else Color(tshirt.shadeHex),
            shape = CircleShape
          ),
        contentAlignment = Alignment.Center
      ) {
        if (isSelected) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = if (tshirt == TShirtColor.WHITE || tshirt == TShirtColor.YELLOW) Color(0xFF212121) else Color.White,
            modifier = Modifier.size(14.dp)
          )
        }
      }
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = tshirt.label,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
        fontSize = 11.5.sp,
        color = if (isSelected) Color(0xFF004D40) else Color(0xFF334155),
        maxLines = 1
      )
    }
  }
}

