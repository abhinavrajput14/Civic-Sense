package com.example.ui.components

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.model.Accessory
import com.example.model.CharacterExpression
import com.example.model.CharacterGender
import com.example.model.CharacterProfile

/**
 * 3D Volumetric arm, hand, and accessory renderer for Civic Quest protagonists.
 * Uses cylindrical lighting gradients, sculpted hands, realistic sleeve cuffs,
 * and high-fidelity wrist accessories (smartwatch for boy, beaded bracelet for girl).
 */
internal fun DrawScope.drawArms3D(
  expression: CharacterExpression,
  armAngle: Float,
  centerX: Float,
  torsoTopY: Float,
  torsoWidth: Float,
  torsoHeight: Float,
  w: Float,
  h: Float,
  skinColor: Color,
  skinShadow: Color,
  sleeveColor: Color,
  character: CharacterProfile
) {
  val armWidth = w * 0.076f
  val armLength = h * 0.178f

  val sleeveShadow = Color(
    red = (sleeveColor.red * 0.78f).coerceIn(0f, 1f),
    green = (sleeveColor.green * 0.78f).coerceIn(0f, 1f),
    blue = (sleeveColor.blue * 0.78f).coerceIn(0f, 1f)
  )

  when (expression) {
    CharacterExpression.WAVING -> {
      // Left arm resting naturally
      drawLeftArmResting(
        centerX = centerX,
        torsoWidth = torsoWidth,
        torsoTopY = torsoTopY,
        armWidth = armWidth,
        armLength = armLength,
        sleeveColor = sleeveColor,
        sleeveShadow = sleeveShadow,
        skinColor = skinColor,
        skinShadow = skinShadow,
        w = w,
        h = h,
        character = character
      )

      // Right arm waving up joyfully
      val shoulderX = centerX + torsoWidth * 0.48f
      val shoulderY = torsoTopY + h * 0.020f
      val handX = shoulderX + (w * 0.12f)
      val handY = shoulderY - (h * 0.14f) + (armAngle * 0.45f)

      // 3D cylindrical wave arm
      drawLine(
        brush = Brush.linearGradient(
          colors = listOf(skinColor, skinShadow),
          start = Offset(shoulderX, shoulderY),
          end = Offset(handX, handY)
        ),
        start = Offset(shoulderX, shoulderY),
        end = Offset(handX, handY),
        strokeWidth = armWidth,
        cap = StrokeCap.Round
      )
      // 3D palm
      drawCircle(skinColor, radius = armWidth * 0.62f, center = Offset(handX, handY))
      drawCircle(skinShadow.copy(alpha = 0.4f), radius = armWidth * 0.35f, center = Offset(handX + armWidth * 0.15f, handY + armWidth * 0.15f))
      // Thumb
      drawCircle(skinColor, radius = armWidth * 0.30f, center = Offset(handX - armWidth * 0.38f, handY - armWidth * 0.18f))
    }

    CharacterExpression.CELEBRATING -> {
      // Both arms raised high in triumphant victory
      val lShoulderX = centerX - torsoWidth * 0.48f
      val rShoulderX = centerX + torsoWidth * 0.48f
      val shoulderY = torsoTopY + h * 0.020f

      val leftHand = Offset(lShoulderX - w * 0.085f, shoulderY - h * 0.135f)
      val rightHand = Offset(rShoulderX + w * 0.085f, shoulderY - h * 0.135f)

      // Left arm celebrating
      drawLine(
        brush = Brush.linearGradient(
          colors = listOf(skinColor, skinShadow),
          start = Offset(lShoulderX, shoulderY),
          end = leftHand
        ),
        start = Offset(lShoulderX, shoulderY),
        end = leftHand,
        strokeWidth = armWidth,
        cap = StrokeCap.Round
      )
      drawCircle(skinColor, radius = armWidth * 0.60f, center = leftHand)

      // Right arm celebrating
      drawLine(
        brush = Brush.linearGradient(
          colors = listOf(skinColor, skinShadow),
          start = Offset(rShoulderX, shoulderY),
          end = rightHand
        ),
        start = Offset(rShoulderX, shoulderY),
        end = rightHand,
        strokeWidth = armWidth,
        cap = StrokeCap.Round
      )
      drawCircle(skinColor, radius = armWidth * 0.60f, center = rightHand)
    }

    CharacterExpression.POINTING -> {
      // Left arm resting
      drawLeftArmResting(
        centerX = centerX,
        torsoWidth = torsoWidth,
        torsoTopY = torsoTopY,
        armWidth = armWidth,
        armLength = armLength,
        sleeveColor = sleeveColor,
        sleeveShadow = sleeveShadow,
        skinColor = skinColor,
        skinShadow = skinShadow,
        w = w,
        h = h,
        character = character
      )

      // Right arm pointing straight forward to guide player
      val rShoulderX = centerX + torsoWidth * 0.48f
      val shoulderY = torsoTopY + h * 0.045f
      val pointEnd = Offset(rShoulderX + w * 0.155f, shoulderY - h * 0.012f)

      drawLine(
        brush = Brush.linearGradient(
          colors = listOf(skinColor, skinShadow),
          start = Offset(rShoulderX, shoulderY),
          end = pointEnd
        ),
        start = Offset(rShoulderX, shoulderY),
        end = pointEnd,
        strokeWidth = armWidth,
        cap = StrokeCap.Round
      )
      drawCircle(skinColor, radius = armWidth * 0.52f, center = pointEnd)
      // Extended index finger
      drawCircle(skinColor, radius = armWidth * 0.28f, center = Offset(pointEnd.x + armWidth * 0.35f, pointEnd.y))
    }

    else -> {
      // Default natural 3D posture with both arms framing the torso
      drawLeftArmResting(
        centerX = centerX,
        torsoWidth = torsoWidth,
        torsoTopY = torsoTopY,
        armWidth = armWidth,
        armLength = armLength,
        sleeveColor = sleeveColor,
        sleeveShadow = sleeveShadow,
        skinColor = skinColor,
        skinShadow = skinShadow,
        w = w,
        h = h,
        character = character
      )

      drawRightArmResting(
        centerX = centerX,
        torsoWidth = torsoWidth,
        torsoTopY = torsoTopY,
        armWidth = armWidth,
        armLength = armLength,
        sleeveColor = sleeveColor,
        sleeveShadow = sleeveShadow,
        skinColor = skinColor,
        skinShadow = skinShadow,
        w = w,
        h = h,
        character = character
      )
    }
  }
}

private fun DrawScope.drawLeftArmResting(
  centerX: Float,
  torsoWidth: Float,
  torsoTopY: Float,
  armWidth: Float,
  armLength: Float,
  sleeveColor: Color,
  sleeveShadow: Color,
  skinColor: Color,
  skinShadow: Color,
  w: Float,
  h: Float,
  character: CharacterProfile
) {
  val armX = centerX - torsoWidth * 0.5f - armWidth * 0.85f

  // 1. Upper sleeve with 3D cylindrical lighting
  drawRoundRect(
    brush = Brush.horizontalGradient(
      colors = listOf(sleeveColor, sleeveShadow),
      startX = armX,
      endX = armX + armWidth
    ),
    topLeft = Offset(armX, torsoTopY),
    size = Size(armWidth, armLength * 0.45f),
    cornerRadius = CornerRadius(armWidth * 0.4f, armWidth * 0.4f)
  )

  // Sleeve cuff shadow on arm
  drawRoundRect(
    color = Color(0x33000000),
    topLeft = Offset(armX + armWidth * 0.05f, torsoTopY + armLength * 0.42f),
    size = Size(armWidth * 0.90f, h * 0.008f),
    cornerRadius = CornerRadius(armWidth * 0.2f, armWidth * 0.2f)
  )

  // 2. Forearm with 3D gradient
  drawRoundRect(
    brush = Brush.horizontalGradient(
      colors = listOf(skinColor, skinShadow),
      startX = armX,
      endX = armX + armWidth * 0.9f
    ),
    topLeft = Offset(armX + armWidth * 0.05f, torsoTopY + armLength * 0.40f),
    size = Size(armWidth * 0.90f, armLength * 0.58f),
    cornerRadius = CornerRadius(armWidth * 0.4f, armWidth * 0.4f)
  )

  // 3. 3D Hand palm & thumb
  val lHandCenter = Offset(armX + armWidth * 0.48f, torsoTopY + armLength * 0.96f)
  drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(skinColor, skinShadow),
      center = Offset(lHandCenter.x - armWidth * 0.1f, lHandCenter.y - armWidth * 0.1f),
      radius = armWidth * 0.42f
    ),
    radius = armWidth * 0.42f,
    center = lHandCenter
  )
  // Thumb
  drawCircle(skinColor, radius = armWidth * 0.20f, center = Offset(lHandCenter.x + armWidth * 0.26f, lHandCenter.y - armWidth * 0.12f))

  // 4. Left Wrist Accessories (Boy's Smartwatch vs Girl's Beaded Bracelet)
  val lWristY = torsoTopY + armLength * 0.72f
  val lWristX = armX + armWidth * 0.05f

  if (character.gender == CharacterGender.BOY) {
    // Boy's 3D Smartwatch (Dark matte strap + square beveled display + cyan status indicator)
    drawRoundRect(
      color = Color(0xFF1E293B),
      topLeft = Offset(lWristX - armWidth * 0.04f, lWristY),
      size = Size(armWidth * 0.98f, h * 0.022f),
      cornerRadius = CornerRadius(w * 0.008f, w * 0.008f)
    )
    val watchFaceSize = armWidth * 0.68f
    drawRoundRect(
      color = Color(0xFF0F172A),
      topLeft = Offset(lWristX + armWidth * 0.15f, lWristY + (h * 0.022f - watchFaceSize) * 0.5f),
      size = Size(watchFaceSize, watchFaceSize),
      cornerRadius = CornerRadius(w * 0.012f, w * 0.012f)
    )
    // Screen glass gloss reflection
    drawRoundRect(
      color = Color(0x33FFFFFF),
      topLeft = Offset(lWristX + armWidth * 0.18f, lWristY + (h * 0.022f - watchFaceSize) * 0.5f + w * 0.002f),
      size = Size(watchFaceSize * 0.8f, watchFaceSize * 0.4f),
      cornerRadius = CornerRadius(w * 0.008f, w * 0.008f)
    )
    // Glowing cyan active indicator
    drawLine(
      color = Color(0xFF38BDF8),
      start = Offset(lWristX + armWidth * 0.28f, lWristY + h * 0.011f),
      end = Offset(lWristX + armWidth * 0.70f, lWristY + h * 0.011f),
      strokeWidth = w * 0.005f,
      cap = StrokeCap.Round
    )
  } else {
    // Girl's Colorful 3D Beaded Bracelet (Red, Orange, Yellow, Emerald, Cyan beads with individual specular highlights)
    val beadColors = listOf(
      Color(0xFFEF4444),
      Color(0xFFF97316),
      Color(0xFFFBBF24),
      Color(0xFF10B981),
      Color(0xFF06B6D4)
    )
    val beadCount = beadColors.size
    val beadSpacing = (armWidth * 0.9f) / beadCount
    for (b in 0 until beadCount) {
      val bx = lWristX + (b + 0.5f) * beadSpacing
      val by = lWristY + h * 0.010f
      val beadRadius = w * 0.011f
      drawCircle(
        color = beadColors[b],
        radius = beadRadius,
        center = Offset(bx, by)
      )
      // Specular highlight on each bead
      drawCircle(
        color = Color.White.copy(alpha = 0.6f),
        radius = beadRadius * 0.35f,
        center = Offset(bx - beadRadius * 0.25f, by - beadRadius * 0.25f)
      )
    }
  }
}

private fun DrawScope.drawRightArmResting(
  centerX: Float,
  torsoWidth: Float,
  torsoTopY: Float,
  armWidth: Float,
  armLength: Float,
  sleeveColor: Color,
  sleeveShadow: Color,
  skinColor: Color,
  skinShadow: Color,
  w: Float,
  h: Float,
  character: CharacterProfile
) {
  val armX = centerX + torsoWidth * 0.5f - armWidth * 0.05f

  // 1. Upper sleeve
  drawRoundRect(
    brush = Brush.horizontalGradient(
      colors = listOf(sleeveShadow, sleeveColor),
      startX = armX,
      endX = armX + armWidth
    ),
    topLeft = Offset(armX, torsoTopY),
    size = Size(armWidth, armLength * 0.45f),
    cornerRadius = CornerRadius(armWidth * 0.4f, armWidth * 0.4f)
  )

  // 2. Forearm
  drawRoundRect(
    brush = Brush.horizontalGradient(
      colors = listOf(skinShadow, skinColor),
      startX = armX,
      endX = armX + armWidth * 0.9f
    ),
    topLeft = Offset(armX, torsoTopY + armLength * 0.40f),
    size = Size(armWidth * 0.90f, armLength * 0.58f),
    cornerRadius = CornerRadius(armWidth * 0.4f, armWidth * 0.4f)
  )

  // 3. 3D Hand palm & thumb
  val rHandCenter = Offset(armX + armWidth * 0.42f, torsoTopY + armLength * 0.96f)
  drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(skinColor, skinShadow),
      center = Offset(rHandCenter.x - armWidth * 0.1f, rHandCenter.y - armWidth * 0.1f),
      radius = armWidth * 0.42f
    ),
    radius = armWidth * 0.42f,
    center = rHandCenter
  )
  // Thumb
  drawCircle(skinColor, radius = armWidth * 0.20f, center = Offset(rHandCenter.x - armWidth * 0.26f, rHandCenter.y - armWidth * 0.12f))

  // 4. Eco Wristband Accessory on Right Wrist if unlocked
  if (character.accessory == Accessory.WRISTBAND) {
    val rWristY = torsoTopY + armLength * 0.72f
    drawRoundRect(
      color = Color(0xFF10B981),
      topLeft = Offset(armX, rWristY),
      size = Size(armWidth * 0.95f, h * 0.020f),
      cornerRadius = CornerRadius(w * 0.008f, w * 0.008f)
    )
    drawCircle(
      color = Color(0xFFFBBF24),
      radius = w * 0.009f,
      center = Offset(armX + armWidth * 0.48f, rWristY + h * 0.010f)
    )
  }
}
