package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.Accessory
import com.example.model.CharacterExpression
import com.example.model.CharacterGender
import com.example.model.CharacterProfile
import com.example.model.OutfitStyle

/**
 * Ultra-Premium stylized 3D-cartoon character renderer for Civic Quest protagonists.
 * Scaled to fit comfortably on screen with zero clipping, featuring true 3D spherical
 * and cylindrical volumetric shading, sculpted 3D hair clumps, expressive 3D eyes,
 * and likeness to the boy and girl reference avatars.
 */
@Composable
fun CharacterAvatar(
  character: CharacterProfile,
  modifier: Modifier = Modifier,
  expression: CharacterExpression = CharacterExpression.IDLE,
  size: Dp = 135.dp
) {
  val infiniteTransition = rememberInfiniteTransition(label = "avatarAnimation")

  // Idle breath & body bounce
  val breathingOffset by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = if (expression == CharacterExpression.CELEBRATING) -12f else -3f,
    animationSpec = infiniteRepeatable(
      animation = tween(
        durationMillis = if (expression == CharacterExpression.CELEBRATING) 350 else 1250,
        easing = FastOutSlowInEasing
      ),
      repeatMode = RepeatMode.Reverse
    ),
    label = "breathing"
  )

  // Arm subtle waving / bounce cycle
  val armAngle by infiniteTransition.animateFloat(
    initialValue = -12f,
    targetValue = 22f,
    animationSpec = infiniteRepeatable(
      animation = tween(380, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "arm"
  )

  // Gentle blink phase
  val blinkProgress by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(3200, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "blink"
  )
  val isBlinking = blinkProgress in 0.94f..1.0f

  Box(
    modifier = modifier.size(size),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.size(size)) {
      val w = this.size.width
      val h = this.size.height
      if (w <= 10f || h <= 10f) return@Canvas

      // Master Color Palette derivation with soft specular highlights and ambient occlusion
      val baseSkin = Color(character.skinTone.hex)
      val shadowSkin = Color(
        red = (baseSkin.red * 0.82f).coerceIn(0f, 1f),
        green = (baseSkin.green * 0.78f).coerceIn(0f, 1f),
        blue = (baseSkin.blue * 0.74f).coerceIn(0f, 1f)
      )
      val highlightSkin = Color(
        red = (baseSkin.red * 1.08f).coerceIn(0f, 1f),
        green = (baseSkin.green * 1.08f).coerceIn(0f, 1f),
        blue = (baseSkin.blue * 1.08f).coerceIn(0f, 1f)
      )
      val hairColor = Color(character.hairColor.hex)
      val hairHighlight = Color(
        red = (hairColor.red * 1.35f + 0.08f).coerceIn(0f, 1f),
        green = (hairColor.green * 1.35f + 0.08f).coerceIn(0f, 1f),
        blue = (hairColor.blue * 1.35f + 0.08f).coerceIn(0f, 1f)
      )
      val outfitPrimary = Color(character.outfitStyle.primaryHex)
      val outfitSecondary = Color(character.outfitStyle.secondaryHex)
      val pantsColor = Color(character.pantsStyle.hex)
      val shoesColor = Color(character.shoesStyle.hex)
      val backpackColor = Color(character.backpackHex)

      // Center reference & calibrated vertical proportions (ensures 100% onscreen fit)
      val centerX = w * 0.5f
      val headCenterY = h * 0.280f + (breathingOffset * (h / 260f))
      val headRadius = w * 0.145f

      // 1. Soft 3D Ground Contact Shadow (anchors character in 3D space)
      drawOval(
        brush = Brush.radialGradient(
          colors = listOf(Color(0x44000000), Color(0x18000000), Color.Transparent),
          center = Offset(centerX, h * 0.825f),
          radius = w * 0.30f
        ),
        topLeft = Offset(centerX - w * 0.29f, h * 0.795f),
        size = Size(w * 0.58f, h * 0.065f)
      )

      // 2. Backpack silhouette behind shoulders
      val backpackWidth = w * 0.38f
      val backpackHeight = h * 0.25f
      val backpackTop = headCenterY + headRadius * 0.65f
      drawRoundRect(
        brush = Brush.verticalGradient(
          colors = listOf(backpackColor, Color(
            red = (backpackColor.red * 0.8f).coerceIn(0f, 1f),
            green = (backpackColor.green * 0.8f).coerceIn(0f, 1f),
            blue = (backpackColor.blue * 0.8f).coerceIn(0f, 1f)
          )),
          startY = backpackTop,
          endY = backpackTop + backpackHeight
        ),
        topLeft = Offset(centerX - backpackWidth * 0.5f, backpackTop),
        size = Size(backpackWidth, backpackHeight),
        cornerRadius = CornerRadius(w * 0.07f, w * 0.07f)
      )
      // Backpack front loop zipper accent
      drawArc(
        color = Color(0x55FFFFFF),
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(centerX - w * 0.05f, backpackTop - h * 0.015f),
        size = Size(w * 0.10f, h * 0.030f),
        style = Stroke(width = w * 0.010f, cap = StrokeCap.Round)
      )

      // 3. Legs & Pants (3D Jeans/Chinos with seams, folds, and contrast stitching)
      val hipY = headCenterY + headRadius + h * 0.195f
      val legWidth = w * 0.076f
      val legHeight = h * 0.215f

      // Waistband & Belt
      val waistBandWidth = legWidth * 3.4f
      drawRoundRect(
        color = Color(
          red = (pantsColor.red * 0.90f).coerceIn(0f, 1f),
          green = (pantsColor.green * 0.90f).coerceIn(0f, 1f),
          blue = (pantsColor.blue * 0.90f).coerceIn(0f, 1f)
        ),
        topLeft = Offset(centerX - waistBandWidth * 0.5f, hipY - h * 0.014f),
        size = Size(waistBandWidth, h * 0.020f),
        cornerRadius = CornerRadius(w * 0.010f, w * 0.010f)
      )
      // Contrast stitching on waist
      drawLine(
        color = Color(0x66F59E0B),
        start = Offset(centerX - waistBandWidth * 0.46f, hipY - h * 0.004f),
        end = Offset(centerX + waistBandWidth * 0.46f, hipY - h * 0.004f),
        strokeWidth = w * 0.005f
      )

      // Left Leg (3D Cylindrical gradient)
      val leftLegX = centerX - legWidth * 1.55f
      drawRoundRect(
        brush = Brush.horizontalGradient(
          colors = listOf(
            pantsColor,
            Color(
              red = (pantsColor.red * 0.80f).coerceIn(0f, 1f),
              green = (pantsColor.green * 0.80f).coerceIn(0f, 1f),
              blue = (pantsColor.blue * 0.80f).coerceIn(0f, 1f)
            )
          ),
          startX = leftLegX,
          endX = leftLegX + legWidth
        ),
        topLeft = Offset(leftLegX, hipY),
        size = Size(legWidth, legHeight),
        cornerRadius = CornerRadius(legWidth * 0.35f, legWidth * 0.35f)
      )
      // Knee crease folds
      drawLine(
        color = Color(0x44000000),
        start = Offset(leftLegX + legWidth * 0.2f, hipY + legHeight * 0.48f),
        end = Offset(leftLegX + legWidth * 0.8f, hipY + legHeight * 0.46f),
        strokeWidth = w * 0.005f,
        cap = StrokeCap.Round
      )

      // Right Leg (3D Cylindrical gradient)
      val rightLegX = centerX + legWidth * 0.55f
      drawRoundRect(
        brush = Brush.horizontalGradient(
          colors = listOf(
            Color(
              red = (pantsColor.red * 1.05f).coerceIn(0f, 1f),
              green = (pantsColor.green * 1.05f).coerceIn(0f, 1f),
              blue = (pantsColor.blue * 1.05f).coerceIn(0f, 1f)
            ),
            pantsColor
          ),
          startX = rightLegX,
          endX = rightLegX + legWidth
        ),
        topLeft = Offset(rightLegX, hipY),
        size = Size(legWidth, legHeight),
        cornerRadius = CornerRadius(legWidth * 0.35f, legWidth * 0.35f)
      )
      // Knee crease folds
      drawLine(
        color = Color(0x44000000),
        start = Offset(rightLegX + legWidth * 0.2f, hipY + legHeight * 0.46f),
        end = Offset(rightLegX + legWidth * 0.8f, hipY + legHeight * 0.48f),
        strokeWidth = w * 0.005f,
        cap = StrokeCap.Round
      )

      // 4. Stylized 3D Kicks / Sneakers (Vulcanized Rubber Sole, Rubber Toe Bumper, Crisp Laces)
      val shoeY = hipY + legHeight - h * 0.018f
      val shoeWidth = legWidth * 1.55f
      val shoeHeight = h * 0.056f

      // Left Shoe Upper
      val leftShoeX = centerX - legWidth * 1.90f
      drawRoundRect(
        brush = Brush.verticalGradient(
          colors = listOf(
            shoesColor,
            Color(
              red = (shoesColor.red * 0.82f).coerceIn(0f, 1f),
              green = (shoesColor.green * 0.82f).coerceIn(0f, 1f),
              blue = (shoesColor.blue * 0.82f).coerceIn(0f, 1f)
            )
          )
        ),
        topLeft = Offset(leftShoeX, shoeY),
        size = Size(shoeWidth, shoeHeight),
        cornerRadius = CornerRadius(shoeHeight * 0.48f, shoeHeight * 0.48f)
      )
      // Left Sole (Vulcanized white rubber with pinstripe)
      val leftSoleY = shoeY + shoeHeight * 0.62f
      val soleH = shoeHeight * 0.38f
      drawRoundRect(
        color = Color(0xFFF8FAFC),
        topLeft = Offset(leftShoeX, leftSoleY),
        size = Size(shoeWidth, soleH),
        cornerRadius = CornerRadius(shoeHeight * 0.18f, shoeHeight * 0.18f)
      )
      drawLine(
        color = Color(0xFF475569),
        start = Offset(leftShoeX + w * 0.005f, leftSoleY + soleH * 0.45f),
        end = Offset(leftShoeX + shoeWidth - w * 0.005f, leftSoleY + soleH * 0.45f),
        strokeWidth = w * 0.003f
      )
      // Left Toe Rubber Cap
      drawOval(
        brush = Brush.radialGradient(
          colors = listOf(Color(0xFFFFFFFF), Color(0xFFE2E8F0)),
          center = Offset(leftShoeX + shoeWidth * 0.12f, shoeY + shoeHeight * 0.55f),
          radius = shoeWidth * 0.25f
        ),
        topLeft = Offset(leftShoeX - shoeWidth * 0.05f, shoeY + shoeHeight * 0.32f),
        size = Size(shoeWidth * 0.38f, shoeHeight * 0.52f)
      )
      // Left Criss-cross laces
      for (i in 0..2) {
        val laceY = shoeY + shoeHeight * (0.22f + i * 0.14f)
        drawLine(
          color = Color.White,
          start = Offset(leftShoeX + shoeWidth * 0.38f, laceY),
          end = Offset(leftShoeX + shoeWidth * 0.68f, laceY + shoeHeight * 0.08f),
          strokeWidth = w * 0.005f,
          cap = StrokeCap.Round
        )
      }

      // Right Shoe Upper
      val rightShoeX = centerX + legWidth * 0.42f
      drawRoundRect(
        brush = Brush.verticalGradient(
          colors = listOf(
            shoesColor,
            Color(
              red = (shoesColor.red * 0.82f).coerceIn(0f, 1f),
              green = (shoesColor.green * 0.82f).coerceIn(0f, 1f),
              blue = (shoesColor.blue * 0.82f).coerceIn(0f, 1f)
            )
          )
        ),
        topLeft = Offset(rightShoeX, shoeY),
        size = Size(shoeWidth, shoeHeight),
        cornerRadius = CornerRadius(shoeHeight * 0.48f, shoeHeight * 0.48f)
      )
      // Right Sole
      val rightSoleY = shoeY + shoeHeight * 0.62f
      drawRoundRect(
        color = Color(0xFFF8FAFC),
        topLeft = Offset(rightShoeX, rightSoleY),
        size = Size(shoeWidth, soleH),
        cornerRadius = CornerRadius(shoeHeight * 0.18f, shoeHeight * 0.18f)
      )
      drawLine(
        color = Color(0xFF475569),
        start = Offset(rightShoeX + w * 0.005f, rightSoleY + soleH * 0.45f),
        end = Offset(rightShoeX + shoeWidth - w * 0.005f, rightSoleY + soleH * 0.45f),
        strokeWidth = w * 0.003f
      )
      // Right Toe Rubber Cap
      drawOval(
        brush = Brush.radialGradient(
          colors = listOf(Color(0xFFFFFFFF), Color(0xFFE2E8F0)),
          center = Offset(rightShoeX + shoeWidth * 0.88f, shoeY + shoeHeight * 0.55f),
          radius = shoeWidth * 0.25f
        ),
        topLeft = Offset(rightShoeX + shoeWidth * 0.67f, shoeY + shoeHeight * 0.32f),
        size = Size(shoeWidth * 0.38f, shoeHeight * 0.52f)
      )
      // Right Criss-cross laces
      for (i in 0..2) {
        val laceY = shoeY + shoeHeight * (0.22f + i * 0.14f)
        drawLine(
          color = Color.White,
          start = Offset(rightShoeX + shoeWidth * 0.32f, laceY + shoeHeight * 0.08f),
          end = Offset(rightShoeX + shoeWidth * 0.62f, laceY),
          strokeWidth = w * 0.005f,
          cap = StrokeCap.Round
        )
      }

      // 5. Upper Body Clothing: School Uniform vs T-Shirts vs Thematic Outfits
      val isWearingTShirt = character.selectedTShirt != null
      val isSchoolOutfit = !isWearingTShirt && character.outfitStyle == OutfitStyle.SCHOOL_HERO

      val torsoTopY = headCenterY + headRadius * 0.70f
      val torsoWidth = w * 0.285f
      val torsoHeight = h * 0.215f

      val upperBodySleeveColor = when {
        isWearingTShirt -> Color(character.selectedTShirt!!.hex)
        isSchoolOutfit -> Color(0xFFFFFFFF)
        else -> outfitPrimary
      }

      if (isSchoolOutfit) {
        // --- POLISHED SCHOOL UNIFORM (Crisp white shirt + necktie + breast pocket with pen) ---
        drawRoundRect(
          brush = Brush.verticalGradient(
            colors = listOf(Color(0xFFFFFFFF), Color(0xFFF1F5F9), Color(0xFFE2E8F0)),
            startY = torsoTopY,
            endY = torsoTopY + torsoHeight
          ),
          topLeft = Offset(centerX - torsoWidth * 0.5f, torsoTopY),
          size = Size(torsoWidth, torsoHeight),
          cornerRadius = CornerRadius(w * 0.04f, w * 0.04f)
        )
        // Central placket & buttons
        drawRect(
          color = Color(0xFFE2E8F0),
          topLeft = Offset(centerX - w * 0.012f, torsoTopY),
          size = Size(w * 0.024f, torsoHeight)
        )
        for (b in 1..3) {
          drawCircle(
            color = Color(0xFF94A3B8),
            radius = w * 0.006f,
            center = Offset(centerX, torsoTopY + torsoHeight * (0.35f + b * 0.16f))
          )
        }
        // Lapels
        val leftLapel = Path().apply {
          moveTo(centerX - w * 0.075f, torsoTopY)
          lineTo(centerX - w * 0.014f, torsoTopY + h * 0.024f)
          lineTo(centerX - w * 0.055f, torsoTopY + h * 0.048f)
          close()
        }
        val rightLapel = Path().apply {
          moveTo(centerX + w * 0.075f, torsoTopY)
          lineTo(centerX + w * 0.014f, torsoTopY + h * 0.024f)
          lineTo(centerX + w * 0.055f, torsoTopY + h * 0.048f)
          close()
        }
        drawPath(leftLapel, Color(0xFFFFFFFF))
        drawPath(leftLapel, Color(0xFFCBD5E1), style = Stroke(width = w * 0.004f))
        drawPath(rightLapel, Color(0xFFFFFFFF))
        drawPath(rightLapel, Color(0xFFCBD5E1), style = Stroke(width = w * 0.004f))

        // Necktie
        val knotY = torsoTopY + h * 0.010f
        val knotPath = Path().apply {
          moveTo(centerX - w * 0.020f, knotY)
          lineTo(centerX + w * 0.020f, knotY)
          lineTo(centerX + w * 0.015f, knotY + h * 0.022f)
          lineTo(centerX - w * 0.015f, knotY + h * 0.022f)
          close()
        }
        drawPath(knotPath, Color(0xFF1E293B))

        val tieBladePath = Path().apply {
          moveTo(centerX - w * 0.016f, knotY + h * 0.020f)
          lineTo(centerX + w * 0.016f, knotY + h * 0.020f)
          lineTo(centerX + w * 0.024f, knotY + h * 0.140f)
          lineTo(centerX, knotY + h * 0.165f)
          lineTo(centerX - w * 0.024f, knotY + h * 0.140f)
          close()
        }
        drawPath(tieBladePath, Color(0xFF0F172A))
      } else if (isWearingTShirt) {
        // --- CASUAL T-SHIRT (Clean crewneck, rich folds, accent collar) ---
        val ts = character.selectedTShirt!!
        val mainColor = Color(ts.hex)
        val shadeColor = Color(ts.shadeHex)
        val collarColor = Color(ts.collarHex)

        // Body with vertical 3D shading
        drawRoundRect(
          brush = Brush.verticalGradient(
            colors = listOf(mainColor, shadeColor),
            startY = torsoTopY,
            endY = torsoTopY + torsoHeight
          ),
          topLeft = Offset(centerX - torsoWidth * 0.5f, torsoTopY),
          size = Size(torsoWidth, torsoHeight),
          cornerRadius = CornerRadius(w * 0.04f, w * 0.04f)
        )
        // Collar
        drawArc(
          color = collarColor,
          startAngle = 0f,
          sweepAngle = 180f,
          useCenter = false,
          topLeft = Offset(centerX - w * 0.065f, torsoTopY - h * 0.010f),
          size = Size(w * 0.13f, h * 0.032f),
          style = Stroke(width = w * 0.018f, cap = StrokeCap.Round)
        )
        // Chest civic hero insignia
        drawCircle(
          color = Color.White.copy(alpha = 0.35f),
          radius = w * 0.028f,
          center = Offset(centerX, torsoTopY + torsoHeight * 0.32f)
        )
        drawCircle(
          color = mainColor,
          radius = w * 0.018f,
          center = Offset(centerX, torsoTopY + torsoHeight * 0.32f)
        )
      } else {
        // --- THEMATIC OUTFIT ---
        drawRoundRect(
          brush = Brush.verticalGradient(
            colors = listOf(outfitPrimary, Color(
              red = (outfitPrimary.red * 0.8f).coerceIn(0f, 1f),
              green = (outfitPrimary.green * 0.8f).coerceIn(0f, 1f),
              blue = (outfitPrimary.blue * 0.8f).coerceIn(0f, 1f)
            ))
          ),
          topLeft = Offset(centerX - torsoWidth * 0.5f, torsoTopY),
          size = Size(torsoWidth, torsoHeight),
          cornerRadius = CornerRadius(w * 0.04f, w * 0.04f)
        )
        // Accent stripe
        val stripePath = Path().apply {
          moveTo(centerX - torsoWidth * 0.5f, torsoTopY + torsoHeight * 0.3f)
          lineTo(centerX, torsoTopY + torsoHeight * 0.45f)
          lineTo(centerX + torsoWidth * 0.5f, torsoTopY + torsoHeight * 0.3f)
          lineTo(centerX + torsoWidth * 0.5f, torsoTopY + torsoHeight * 0.40f)
          lineTo(centerX, torsoTopY + torsoHeight * 0.55f)
          lineTo(centerX - torsoWidth * 0.5f, torsoTopY + torsoHeight * 0.40f)
          close()
        }
        drawPath(stripePath, outfitSecondary)
      }

      // 6. Badge Pin Accessory
      if (character.accessory == Accessory.BADGE_PIN) {
        val pinX = if (isSchoolOutfit) centerX - torsoWidth * 0.28f else centerX + torsoWidth * 0.28f
        val pinY = torsoTopY + torsoHeight * 0.26f
        drawCircle(Color(0xFFFFD54F), radius = w * 0.028f, center = Offset(pinX, pinY))
        drawCircle(Color(0xFF00897B), radius = w * 0.018f, center = Offset(pinX, pinY))
      }

      // 7. Expressive 3D Arms & Hands
      drawArms3D(
        expression = expression,
        armAngle = armAngle,
        centerX = centerX,
        torsoTopY = torsoTopY,
        torsoWidth = torsoWidth,
        torsoHeight = torsoHeight,
        w = w,
        h = h,
        skinColor = baseSkin,
        skinShadow = shadowSkin,
        sleeveColor = upperBodySleeveColor,
        character = character
      )

      // 8. Neck (With ambient occlusion shadow under chin)
      val neckWidth = w * 0.082f
      val neckHeight = headRadius * 0.42f
      val neckTopY = headCenterY + headRadius * 0.62f
      drawRoundRect(
        color = shadowSkin,
        topLeft = Offset(centerX - neckWidth * 0.5f, neckTopY),
        size = Size(neckWidth, neckHeight),
        cornerRadius = CornerRadius(w * 0.016f, w * 0.016f)
      )

      // 9. Back Hair (Ponytails, twin braids, wavy bob framing behind head)
      drawBackHair3D(
        character = character,
        centerX = centerX,
        headCenterY = headCenterY,
        headRadius = headRadius,
        w = w,
        h = h,
        hairColor = hairColor,
        hairHighlight = hairHighlight
      )

      // 10. Head Geometry & 3D Spherical Volume Shading
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(highlightSkin, baseSkin, shadowSkin),
          center = Offset(centerX - headRadius * 0.25f, headCenterY - headRadius * 0.25f),
          radius = headRadius * 1.25f
        ),
        radius = headRadius,
        center = Offset(centerX, headCenterY)
      )

      // Ears (with 3D inner shadow)
      val earRadius = headRadius * 0.26f
      val earY = headCenterY + headRadius * 0.05f
      drawCircle(baseSkin, radius = earRadius, center = Offset(centerX - headRadius * 0.98f, earY))
      drawCircle(shadowSkin, radius = earRadius * 0.55f, center = Offset(centerX - headRadius * 0.96f, earY))
      drawCircle(baseSkin, radius = earRadius, center = Offset(centerX + headRadius * 0.98f, earY))
      drawCircle(shadowSkin, radius = earRadius * 0.55f, center = Offset(centerX + headRadius * 0.96f, earY))

      // Cheeks (warm 3D radial blush)
      val blushColor = Color(0x3DF43F5E)
      drawCircle(blushColor, radius = headRadius * 0.25f, center = Offset(centerX - headRadius * 0.52f, headCenterY + headRadius * 0.24f))
      drawCircle(blushColor, radius = headRadius * 0.25f, center = Offset(centerX + headRadius * 0.52f, headCenterY + headRadius * 0.24f))

      // 11. Expressive Stylized 3D Eyes (Matching Reference Character Likeness)
      val eyeY = headCenterY + headRadius * 0.06f
      val eyeSpacing = headRadius * 0.42f
      val eyeRadius = headRadius * 0.17f

      if (isBlinking || expression == CharacterExpression.HAPPY || expression == CharacterExpression.CELEBRATING) {
        // Joyful smiling eye arcs
        val arcStroke = Stroke(width = w * 0.018f, cap = StrokeCap.Round)
        drawArc(
          color = Color(0xFF0F172A),
          startAngle = 190f,
          sweepAngle = 160f,
          useCenter = false,
          topLeft = Offset(centerX - eyeSpacing - eyeRadius, eyeY - eyeRadius * 0.5f),
          size = Size(eyeRadius * 2f, eyeRadius * 1.3f),
          style = arcStroke
        )
        drawArc(
          color = Color(0xFF0F172A),
          startAngle = 190f,
          sweepAngle = 160f,
          useCenter = false,
          topLeft = Offset(centerX + eyeSpacing - eyeRadius, eyeY - eyeRadius * 0.5f),
          size = Size(eyeRadius * 2f, eyeRadius * 1.3f),
          style = arcStroke
        )
      } else {
        // Sculpted 3D Eyes with Depth, Rich Hazel/Amber Gradient, and Dual Specular Catchlights
        for (isRight in listOf(false, true)) {
          val eyeCenterX = if (isRight) centerX + eyeSpacing else centerX - eyeSpacing

          // 1. Sclera with soft top eyelid shadow
          drawCircle(
            brush = Brush.verticalGradient(
              colors = listOf(Color(0xFFE2E8F0), Color.White, Color.White),
              startY = eyeY - eyeRadius,
              endY = eyeY + eyeRadius
            ),
            radius = eyeRadius,
            center = Offset(eyeCenterX, eyeY)
          )

          // 2. Iris Ring & Gradient
          val irisRadius = eyeRadius * 0.76f
          val irisCenter = Offset(eyeCenterX, eyeY + eyeRadius * 0.04f)
          drawCircle(
            brush = Brush.radialGradient(
              colors = listOf(
                Color(0xFF8D5B4C),
                Color(0xFF633A18),
                Color(0xFF2E1708)
              ),
              center = Offset(irisCenter.x - irisRadius * 0.2f, irisCenter.y - irisRadius * 0.2f),
              radius = irisRadius
            ),
            radius = irisRadius,
            center = irisCenter
          )
          // Golden reflective crescent on lower iris
          drawArc(
            color = Color(0xAAD97706),
            startAngle = 30f,
            sweepAngle = 120f,
            useCenter = false,
            topLeft = Offset(irisCenter.x - irisRadius * 0.85f, irisCenter.y - irisRadius * 0.85f),
            size = Size(irisRadius * 1.70f, irisRadius * 1.70f),
            style = Stroke(width = irisRadius * 0.22f)
          )

          // 3. Deep Obsidian Pupil
          val pupilRadius = irisRadius * 0.46f
          drawCircle(
            color = Color(0xFF090D16),
            radius = pupilRadius,
            center = irisCenter
          )

          // 4. Primary & Secondary Specular Catchlights
          drawCircle(
            color = Color.White,
            radius = pupilRadius * 0.44f,
            center = Offset(irisCenter.x - pupilRadius * 0.52f, irisCenter.y - pupilRadius * 0.54f)
          )
          drawCircle(
            color = Color(0xCCFFFFFF),
            radius = pupilRadius * 0.22f,
            center = Offset(irisCenter.x + pupilRadius * 0.48f, irisCenter.y + pupilRadius * 0.48f)
          )

          // 5. Stylized Lash Line & Lid Contour
          val lashColor = Color(0xFF0F172A)
          val lashPath = Path().apply {
            moveTo(eyeCenterX - eyeRadius * 1.15f, eyeY - eyeRadius * 0.15f)
            quadraticTo(eyeCenterX, eyeY - eyeRadius * 1.15f, eyeCenterX + eyeRadius * 1.15f, eyeY - eyeRadius * 0.15f)
          }
          drawPath(lashPath, lashColor, style = Stroke(width = w * 0.013f, cap = StrokeCap.Round))

          // Winged lash flick for Girl character
          if (character.gender == CharacterGender.GIRL) {
            val wingEnd = if (isRight) {
              Offset(eyeCenterX + eyeRadius * 1.35f, eyeY - eyeRadius * 0.55f)
            } else {
              Offset(eyeCenterX - eyeRadius * 1.35f, eyeY - eyeRadius * 0.55f)
            }
            val wingStart = if (isRight) {
              Offset(eyeCenterX + eyeRadius * 1.05f, eyeY - eyeRadius * 0.15f)
            } else {
              Offset(eyeCenterX - eyeRadius * 1.05f, eyeY - eyeRadius * 0.15f)
            }
            drawLine(
              color = lashColor,
              start = wingStart,
              end = wingEnd,
              strokeWidth = w * 0.010f,
              cap = StrokeCap.Round
            )
          }
        }
      }

      // 12. Sculpted 3D Eyebrows
      val browY = headCenterY - headRadius * 0.22f
      val browStroke = Stroke(width = w * 0.014f, cap = StrokeCap.Round)
      val browColor = Color(
        red = (hairColor.red * 0.75f).coerceIn(0f, 1f),
        green = (hairColor.green * 0.75f).coerceIn(0f, 1f),
        blue = (hairColor.blue * 0.75f).coerceIn(0f, 1f)
      )

      val leftBrow = Path().apply {
        moveTo(centerX - eyeSpacing - eyeRadius * 0.95f, browY + headRadius * 0.02f)
        quadraticTo(centerX - eyeSpacing, browY - headRadius * 0.06f, centerX - eyeSpacing + eyeRadius * 0.95f, browY)
      }
      val rightBrow = Path().apply {
        moveTo(centerX + eyeSpacing - eyeRadius * 0.95f, browY)
        quadraticTo(centerX + eyeSpacing, browY - headRadius * 0.06f, centerX + eyeSpacing + eyeRadius * 0.95f, browY + headRadius * 0.02f)
      }
      drawPath(leftBrow, browColor, style = browStroke)
      drawPath(rightBrow, browColor, style = browStroke)

      // 13. Sculpted 3D Nose with Bridge & Specular Ball Tip
      val noseTipY = headCenterY + headRadius * 0.28f
      // Soft vertical bridge shadow
      drawLine(
        color = shadowSkin.copy(alpha = 0.55f),
        start = Offset(centerX - w * 0.003f, headCenterY + headRadius * 0.08f),
        end = Offset(centerX - w * 0.003f, noseTipY - h * 0.006f),
        strokeWidth = w * 0.008f,
        cap = StrokeCap.Round
      )
      // Specular bridge highlight line
      drawLine(
        color = highlightSkin.copy(alpha = 0.70f),
        start = Offset(centerX, headCenterY + headRadius * 0.08f),
        end = Offset(centerX, noseTipY - h * 0.006f),
        strokeWidth = w * 0.005f,
        cap = StrokeCap.Round
      )
      // Volumetric rounded nose tip sphere
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(highlightSkin, shadowSkin),
          center = Offset(centerX - w * 0.004f, noseTipY - h * 0.003f),
          radius = headRadius * 0.11f
        ),
        radius = headRadius * 0.10f,
        center = Offset(centerX, noseTipY)
      )
      // Bright specular glint on nose tip
      drawCircle(
        color = Color.White.copy(alpha = 0.75f),
        radius = headRadius * 0.030f,
        center = Offset(centerX - w * 0.003f, noseTipY - h * 0.004f)
      )

      // 14. Friendly 3D Mouth & Sculpted Lips
      val mouthY = headCenterY + headRadius * 0.54f
      val mouthWidth = headRadius * 0.44f

      when (expression) {
        CharacterExpression.HAPPY, CharacterExpression.CELEBRATING -> {
          // Open cheerful smile with teeth and tongue
          val mouthPath = Path().apply {
            moveTo(centerX - mouthWidth * 0.65f, mouthY - h * 0.006f)
            quadraticTo(centerX, mouthY - h * 0.002f, centerX + mouthWidth * 0.65f, mouthY - h * 0.006f)
            quadraticTo(centerX, mouthY + h * 0.042f, centerX - mouthWidth * 0.65f, mouthY - h * 0.006f)
            close()
          }
          drawPath(mouthPath, Color(0xFF7F1D1D))
          // Teeth
          drawRoundRect(
            color = Color.White,
            topLeft = Offset(centerX - mouthWidth * 0.45f, mouthY - h * 0.004f),
            size = Size(mouthWidth * 0.90f, h * 0.012f),
            cornerRadius = CornerRadius(w * 0.006f, w * 0.006f)
          )
          // Pink tongue
          drawCircle(
            color = Color(0xFFF43F5E),
            radius = mouthWidth * 0.28f,
            center = Offset(centerX, mouthY + h * 0.024f)
          )
        }
        CharacterExpression.CONFUSED -> {
          // Puzzled wavy mouth
          val mouthPath = Path().apply {
            moveTo(centerX - mouthWidth * 0.45f, mouthY)
            quadraticTo(centerX - mouthWidth * 0.15f, mouthY - h * 0.012f, centerX, mouthY)
            quadraticTo(centerX + mouthWidth * 0.35f, mouthY + h * 0.012f, centerX + mouthWidth * 0.45f, mouthY)
          }
          drawPath(mouthPath, Color(0xFF991B1B), style = Stroke(width = w * 0.011f, cap = StrokeCap.Round))
        }
        else -> {
          // Friendly gentle smile with 3D lower lip contour
          val mouthPath = Path().apply {
            moveTo(centerX - mouthWidth * 0.50f, mouthY - h * 0.003f)
            quadraticTo(centerX, mouthY + h * 0.022f, centerX + mouthWidth * 0.50f, mouthY - h * 0.003f)
          }
          drawPath(mouthPath, Color(0xFF991B1B), style = Stroke(width = w * 0.012f, cap = StrokeCap.Round))
          // Lower lip 3D volume & specular sheen
          drawArc(
            color = Color(0x44F43F5E),
            startAngle = 10f,
            sweepAngle = 160f,
            useCenter = false,
            topLeft = Offset(centerX - mouthWidth * 0.30f, mouthY + h * 0.008f),
            size = Size(mouthWidth * 0.60f, h * 0.012f),
            style = Stroke(width = w * 0.008f, cap = StrokeCap.Round)
          )
          // Corner smile dimples
          drawCircle(shadowSkin, radius = w * 0.006f, center = Offset(centerX - mouthWidth * 0.52f, mouthY - h * 0.002f))
          drawCircle(shadowSkin, radius = w * 0.006f, center = Offset(centerX + mouthWidth * 0.52f, mouthY - h * 0.002f))
        }
      }

      // 15. Front Hair, Bangs, and Headgear with Volumetric 3D Sculpting
      drawHairAndHeadgear3D(
        character = character,
        centerX = centerX,
        headCenterY = headCenterY,
        headRadius = headRadius,
        w = w,
        h = h,
        hairColor = hairColor,
        hairHighlight = hairHighlight
      )
    }
  }
}
