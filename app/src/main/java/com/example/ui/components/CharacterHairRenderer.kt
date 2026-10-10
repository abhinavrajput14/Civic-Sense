package com.example.ui.components

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.model.Accessory
import com.example.model.CharacterProfile
import com.example.model.HairStyle
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * High-fidelity 3D hair renderer for Civic Quest protagonists.
 * Provides volumetric layered depth, sculpted 3D hair clumps,
 * specular crest highlights, root lift, and zero-clipping skull coverage.
 */
internal fun DrawScope.drawBackHair3D(
  character: CharacterProfile,
  centerX: Float,
  headCenterY: Float,
  headRadius: Float,
  w: Float,
  h: Float,
  hairColor: Color,
  hairHighlight: Color
) {
  val shadowHair = Color(
    red = (hairColor.red * 0.70f).coerceIn(0f, 1f),
    green = (hairColor.green * 0.70f).coerceIn(0f, 1f),
    blue = (hairColor.blue * 0.70f).coerceIn(0f, 1f)
  )

  when (character.hairStyle) {
    HairStyle.PONYTAIL -> {
      // High bouncy ponytail flowing behind the head with high-fidelity 3D volume
      val ponyPlume = Path().apply {
        moveTo(centerX + headRadius * 0.62f, headCenterY - headRadius * 0.65f)
        cubicTo(
          centerX + headRadius * 1.62f, headCenterY - headRadius * 1.05f,
          centerX + headRadius * 1.95f, headCenterY - headRadius * 0.05f,
          centerX + headRadius * 1.38f, headCenterY + headRadius * 0.72f
        )
        cubicTo(
          centerX + headRadius * 1.58f, headCenterY + headRadius * 0.25f,
          centerX + headRadius * 1.25f, headCenterY - headRadius * 0.15f,
          centerX + headRadius * 0.58f, headCenterY - headRadius * 0.38f
        )
        close()
      }
      drawPath(
        ponyPlume,
        brush = Brush.verticalGradient(
          colors = listOf(hairColor, shadowHair),
          startY = headCenterY - headRadius * 1.05f,
          endY = headCenterY + headRadius * 0.72f
        )
      )

      // Inner strand sheen
      val innerStrand = Path().apply {
        moveTo(centerX + headRadius * 0.72f, headCenterY - headRadius * 0.55f)
        quadraticTo(
          centerX + headRadius * 1.6f, headCenterY - headRadius * 0.4f,
          centerX + headRadius * 1.30f, headCenterY + headRadius * 0.5f
        )
        quadraticTo(
          centerX + headRadius * 1.2f, headCenterY,
          centerX + headRadius * 0.65f, headCenterY - headRadius * 0.3f
        )
        close()
      }
      drawPath(innerStrand, hairHighlight.copy(alpha = 0.50f))

      // Stylish hair scrunchie with 3D sphere volume
      drawCircle(
        color = Color(0xFFF43F5E),
        radius = headRadius * 0.25f,
        center = Offset(centerX + headRadius * 0.75f, headCenterY - headRadius * 0.68f)
      )
      drawCircle(
        color = Color(0xFFFDA4AF),
        radius = headRadius * 0.10f,
        center = Offset(centerX + headRadius * 0.71f, headCenterY - headRadius * 0.72f)
      )
    }

    HairStyle.BRAIDS -> {
      // Two long woven braids cascading over shoulders with golden beads
      for (isRight in listOf(false, true)) {
        val sign = if (isRight) 1f else -1f
        val braidX = centerX + sign * (headRadius * 1.02f)
        val startY = headCenterY - headRadius * 0.18f

        for (seg in 0..3) {
          val segY = startY + seg * (headRadius * 0.32f)
          val segWidth = headRadius * (0.38f - seg * 0.035f)
          val segHeight = headRadius * 0.32f
          drawOval(
            brush = Brush.horizontalGradient(
              colors = listOf(shadowHair, hairHighlight, hairColor),
              startX = braidX - segWidth * 0.5f,
              endX = braidX + segWidth * 0.5f
            ),
            topLeft = Offset(braidX - segWidth * 0.5f, segY),
            size = Size(segWidth, segHeight)
          )
        }

        // Braid ribbon tie
        val ribbonY = startY + 4 * (headRadius * 0.32f)
        drawCircle(
          color = Color(0xFFFBBF24),
          radius = headRadius * 0.16f,
          center = Offset(braidX, ribbonY)
        )
        drawCircle(
          color = Color(0xFFFEF08A),
          radius = headRadius * 0.07f,
          center = Offset(braidX - headRadius * 0.04f, ribbonY - headRadius * 0.04f)
        )

        // Braid tassel
        val tassel = Path().apply {
          moveTo(braidX - headRadius * 0.12f, ribbonY)
          lineTo(braidX + headRadius * 0.12f, ribbonY)
          lineTo(braidX, ribbonY + headRadius * 0.26f)
          close()
        }
        drawPath(tassel, shadowHair)
      }
    }

    HairStyle.WAVY_BOB -> {
      // Lush volumetric waves cascading behind shoulders with winged flicked tips (matching Girl reference)
      // Left cascading wing
      val leftWing = Path().apply {
        moveTo(centerX - headRadius * 0.82f, headCenterY - headRadius * 0.40f)
        cubicTo(
          centerX - headRadius * 1.65f, headCenterY + headRadius * 0.10f,
          centerX - headRadius * 1.58f, headCenterY + headRadius * 0.90f,
          centerX - headRadius * 1.35f, headCenterY + headRadius * 1.40f
        )
        // Wing flicked tip
        quadraticTo(
          centerX - headRadius * 1.62f, headCenterY + headRadius * 1.25f,
          centerX - headRadius * 1.45f, headCenterY + headRadius * 0.85f
        )
        cubicTo(
          centerX - headRadius * 1.30f, headCenterY + headRadius * 0.45f,
          centerX - headRadius * 0.95f, headCenterY + headRadius * 0.25f,
          centerX - headRadius * 0.50f, headCenterY + headRadius * 0.65f
        )
        close()
      }
      drawPath(
        leftWing,
        brush = Brush.horizontalGradient(
          colors = listOf(shadowHair, hairColor),
          startX = centerX - headRadius * 1.65f,
          endX = centerX - headRadius * 0.50f
        )
      )

      // Right cascading wing
      val rightWing = Path().apply {
        moveTo(centerX + headRadius * 0.82f, headCenterY - headRadius * 0.40f)
        cubicTo(
          centerX + headRadius * 1.65f, headCenterY + headRadius * 0.10f,
          centerX + headRadius * 1.58f, headCenterY + headRadius * 0.90f,
          centerX + headRadius * 1.35f, headCenterY + headRadius * 1.40f
        )
        // Wing flicked tip
        quadraticTo(
          centerX + headRadius * 1.62f, headCenterY + headRadius * 1.25f,
          centerX + headRadius * 1.45f, headCenterY + headRadius * 0.85f
        )
        cubicTo(
          centerX + headRadius * 1.30f, headCenterY + headRadius * 0.45f,
          centerX + headRadius * 0.95f, headCenterY + headRadius * 0.25f,
          centerX + headRadius * 0.50f, headCenterY + headRadius * 0.65f
        )
        close()
      }
      drawPath(
        rightWing,
        brush = Brush.horizontalGradient(
          colors = listOf(hairColor, shadowHair),
          startX = centerX + headRadius * 0.50f,
          endX = centerX + headRadius * 1.65f
        )
      )

      // Volumetric back hair drape behind neck
      drawRoundRect(
        brush = Brush.verticalGradient(
          colors = listOf(hairColor, shadowHair),
          startY = headCenterY - headRadius * 0.75f,
          endY = headCenterY + headRadius * 1.10f
        ),
        topLeft = Offset(centerX - headRadius * 1.20f, headCenterY - headRadius * 0.75f),
        size = Size(headRadius * 2.40f, headRadius * 1.85f),
        cornerRadius = CornerRadius(headRadius * 0.55f, headRadius * 0.55f)
      )

      // High-sheen strand highlights along wave crests
      for (isRight in listOf(false, true)) {
        val sign = if (isRight) 1f else -1f
        val sheenPath = Path().apply {
          moveTo(centerX + sign * headRadius * 1.15f, headCenterY + headRadius * 0.15f)
          quadraticTo(
            centerX + sign * headRadius * 1.45f, headCenterY + headRadius * 0.65f,
            centerX + sign * headRadius * 1.25f, headCenterY + headRadius * 1.15f
          )
        }
        drawPath(
          sheenPath,
          hairHighlight.copy(alpha = 0.50f),
          style = Stroke(width = w * 0.015f, cap = StrokeCap.Round)
        )
      }
    }

    HairStyle.CURLY_CROP -> {
      // Bubbly curl volume behind ears
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(hairHighlight, hairColor, shadowHair),
          center = Offset(centerX - headRadius * 0.98f, headCenterY),
          radius = headRadius * 0.34f
        ),
        radius = headRadius * 0.34f,
        center = Offset(centerX - headRadius * 0.98f, headCenterY)
      )
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(hairHighlight, hairColor, shadowHair),
          center = Offset(centerX + headRadius * 0.98f, headCenterY),
          radius = headRadius * 0.34f
        ),
        radius = headRadius * 0.34f,
        center = Offset(centerX + headRadius * 0.98f, headCenterY)
      )
    }

    HairStyle.SHORT_SPIKY, HairStyle.SIDE_PART -> {
      // Natural neck taper behind skull with 3D gradient
      drawRoundRect(
        brush = Brush.verticalGradient(
          colors = listOf(hairColor, shadowHair),
          startY = headCenterY - headRadius * 0.2f,
          endY = headCenterY + headRadius * 0.75f
        ),
        topLeft = Offset(centerX - headRadius * 0.95f, headCenterY - headRadius * 0.2f),
        size = Size(headRadius * 1.90f, headRadius * 0.95f),
        cornerRadius = CornerRadius(headRadius * 0.42f, headRadius * 0.42f)
      )
    }
  }
}

/**
 * 3D Front hair and headgear renderer.
 * Features sculpted clumps, piecey forehead bangs, root volume, and specular sheens.
 */
internal fun DrawScope.drawHairAndHeadgear3D(
  character: CharacterProfile,
  centerX: Float,
  headCenterY: Float,
  headRadius: Float,
  w: Float,
  h: Float,
  hairColor: Color,
  hairHighlight: Color
) {
  val shadowHair = Color(
    red = (hairColor.red * 0.70f).coerceIn(0f, 1f),
    green = (hairColor.green * 0.70f).coerceIn(0f, 1f),
    blue = (hairColor.blue * 0.70f).coerceIn(0f, 1f)
  )

  if (character.accessory == Accessory.COOL_CAP) {
    // Premium 3D Hero Cap (Dome, curved visor, gold star crest)
    val capColor = Color(0xFFF97316)
    val capVisor = Color(0xFFEA580C)

    // Base hair visible beneath cap on temples
    drawArc(
      color = hairColor,
      startAngle = 180f,
      sweepAngle = 180f,
      useCenter = true,
      topLeft = Offset(centerX - headRadius * 1.06f, headCenterY - headRadius * 1.18f),
      size = Size(headRadius * 2.12f, headRadius * 1.48f)
    )

    // Cap crown dome with 3D spherical gradient
    drawArc(
      brush = Brush.radialGradient(
        colors = listOf(Color(0xFFFDBA74), capColor, Color(0xFF9A3412)),
        center = Offset(centerX - headRadius * 0.2f, headCenterY - headRadius * 0.9f),
        radius = headRadius * 1.2f
      ),
      startAngle = 180f,
      sweepAngle = 180f,
      useCenter = true,
      topLeft = Offset(centerX - headRadius * 1.10f, headCenterY - headRadius * 1.28f),
      size = Size(headRadius * 2.20f, headRadius * 1.48f)
    )
    // Dynamic curved visor with 3D drop shadow
    drawRoundRect(
      color = Color(0x33000000),
      topLeft = Offset(centerX - headRadius * 0.50f, headCenterY - headRadius * 0.54f),
      size = Size(headRadius * 1.72f, h * 0.038f),
      cornerRadius = CornerRadius(w * 0.02f, w * 0.02f)
    )
    drawRoundRect(
      brush = Brush.verticalGradient(
        colors = listOf(Color(0xFFFB923C), capVisor),
        startY = headCenterY - headRadius * 0.58f,
        endY = headCenterY - headRadius * 0.58f + h * 0.034f
      ),
      topLeft = Offset(centerX - headRadius * 0.50f, headCenterY - headRadius * 0.58f),
      size = Size(headRadius * 1.72f, h * 0.034f),
      cornerRadius = CornerRadius(w * 0.02f, w * 0.02f)
    )
    // Star crest
    drawCircle(
      color = Color(0xFFFDE047),
      radius = w * 0.024f,
      center = Offset(centerX - headRadius * 0.18f, headCenterY - headRadius * 0.88f)
    )
    return
  }

  // Front hair styles with complete scalp coverage, volumetric layering, and reference likeness
  when (character.hairStyle) {
    HairStyle.SHORT_SPIKY -> {
      // Solid crown dome covering skull completely with 3D volume (matching Boy reference)
      drawArc(
        brush = Brush.verticalGradient(
          colors = listOf(
            hairHighlight.copy(alpha = 0.85f),
            hairColor,
            shadowHair
          ),
          startY = headCenterY - headRadius * 1.30f,
          endY = headCenterY - headRadius * 0.50f
        ),
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(centerX - headRadius * 1.10f, headCenterY - headRadius * 1.25f),
        size = Size(headRadius * 2.20f, headRadius * 1.62f)
      )

      // Spiky crown tufts (layered clumpy texture sweeping right/up)
      val crownTufts = Path().apply {
        moveTo(centerX - headRadius * 1.05f, headCenterY - headRadius * 0.25f)
        cubicTo(
          centerX - headRadius * 0.95f, headCenterY - headRadius * 1.35f,
          centerX - headRadius * 0.55f, headCenterY - headRadius * 1.48f,
          centerX - headRadius * 0.35f, headCenterY - headRadius * 1.58f
        )
        // Crest spike 1
        lineTo(centerX - headRadius * 0.15f, headCenterY - headRadius * 1.70f)
        lineTo(centerX + headRadius * 0.05f, headCenterY - headRadius * 1.45f)
        // Crest spike 2 (dominant quiff)
        lineTo(centerX + headRadius * 0.35f, headCenterY - headRadius * 1.68f)
        lineTo(centerX + headRadius * 0.48f, headCenterY - headRadius * 1.42f)
        // Crest spike 3
        lineTo(centerX + headRadius * 0.72f, headCenterY - headRadius * 1.55f)
        cubicTo(
          centerX + headRadius * 1.02f, headCenterY - headRadius * 1.28f,
          centerX + headRadius * 1.08f, headCenterY - headRadius * 0.65f,
          centerX + headRadius * 1.05f, headCenterY - headRadius * 0.25f
        )
        quadraticTo(centerX, headCenterY - headRadius * 0.55f, centerX - headRadius * 1.05f, headCenterY - headRadius * 0.25f)
        close()
      }
      drawPath(
        crownTufts,
        brush = Brush.verticalGradient(
          colors = listOf(hairHighlight.copy(alpha = 0.95f), hairColor, shadowHair),
          startY = headCenterY - headRadius * 1.70f,
          endY = headCenterY - headRadius * 0.25f
        )
      )

      // Layered piecey fringe/bangs sweeping across forehead with soft cast drop shadows underneath
      // Forehead ambient shadow for fringe
      drawArc(
        color = Color(0x22000000),
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(centerX - headRadius * 0.90f, headCenterY - headRadius * 0.75f),
        size = Size(headRadius * 1.80f, headRadius * 1.10f)
      )

      // Clump 1: Left temple lock
      val lockLeft = Path().apply {
        moveTo(centerX - headRadius * 1.02f, headCenterY - headRadius * 0.25f)
        quadraticTo(centerX - headRadius * 0.85f, headCenterY - headRadius * 0.80f, centerX - headRadius * 0.65f, headCenterY - headRadius * 0.75f)
        lineTo(centerX - headRadius * 0.60f, headCenterY - headRadius * 0.20f)
        quadraticTo(centerX - headRadius * 0.82f, headCenterY - headRadius * 0.40f, centerX - headRadius * 1.02f, headCenterY - headRadius * 0.25f)
        close()
      }
      drawPath(lockLeft, hairColor)

      // Clump 2: Mid-left sweeping clump
      val lockMidLeft = Path().apply {
        moveTo(centerX - headRadius * 0.65f, headCenterY - headRadius * 0.85f)
        quadraticTo(centerX - headRadius * 0.45f, headCenterY - headRadius * 0.85f, centerX - headRadius * 0.35f, headCenterY - headRadius * 0.82f)
        lineTo(centerX - headRadius * 0.20f, headCenterY - headRadius * 0.15f)
        lineTo(centerX - headRadius * 0.38f, headCenterY - headRadius * 0.50f)
        close()
      }
      drawPath(lockMidLeft, hairColor)

      // Clump 3: Center prominent swept fringe (reaches down over brow line)
      val lockCenter = Path().apply {
        moveTo(centerX - headRadius * 0.35f, headCenterY - headRadius * 0.90f)
        quadraticTo(centerX + headRadius * 0.05f, headCenterY - headRadius * 0.90f, centerX + headRadius * 0.25f, headCenterY - headRadius * 0.85f)
        lineTo(centerX + headRadius * 0.22f, headCenterY - headRadius * 0.08f)
        lineTo(centerX - headRadius * 0.02f, headCenterY - headRadius * 0.55f)
        close()
      }
      drawPath(
        lockCenter,
        brush = Brush.verticalGradient(
          colors = listOf(hairHighlight.copy(alpha = 0.9f), hairColor),
          startY = headCenterY - headRadius * 0.90f,
          endY = headCenterY - headRadius * 0.08f
        )
      )

      // Clump 4: Right temple lock
      val lockRight = Path().apply {
        moveTo(centerX + headRadius * 0.22f, headCenterY - headRadius * 0.85f)
        quadraticTo(centerX + headRadius * 0.65f, headCenterY - headRadius * 0.82f, centerX + headRadius * 1.02f, headCenterY - headRadius * 0.25f)
        quadraticTo(centerX + headRadius * 0.80f, headCenterY - headRadius * 0.40f, centerX + headRadius * 0.52f, headCenterY - headRadius * 0.22f)
        close()
      }
      drawPath(lockRight, hairColor)

      // Sideburns framing ears
      drawRoundRect(
        color = hairColor,
        topLeft = Offset(centerX - headRadius * 1.02f, headCenterY - headRadius * 0.15f),
        size = Size(headRadius * 0.14f, headRadius * 0.42f),
        cornerRadius = CornerRadius(headRadius * 0.07f, headRadius * 0.07f)
      )
      drawRoundRect(
        color = hairColor,
        topLeft = Offset(centerX + headRadius * 0.88f, headCenterY - headRadius * 0.15f),
        size = Size(headRadius * 0.14f, headRadius * 0.42f),
        cornerRadius = CornerRadius(headRadius * 0.07f, headRadius * 0.07f)
      )

      // Highlight sheens on the ridge of the clumps
      drawArc(
        color = hairHighlight.copy(alpha = 0.55f),
        startAngle = 205f,
        sweepAngle = 55f,
        useCenter = false,
        topLeft = Offset(centerX - headRadius * 0.7f, headCenterY - headRadius * 1.45f),
        size = Size(headRadius * 1.4f, headRadius * 0.9f),
        style = Stroke(width = w * 0.020f, cap = StrokeCap.Round)
      )
      drawArc(
        color = hairHighlight.copy(alpha = 0.40f),
        startAngle = 270f,
        sweepAngle = 40f,
        useCenter = false,
        topLeft = Offset(centerX - headRadius * 0.2f, headCenterY - headRadius * 1.55f),
        size = Size(headRadius * 0.9f, headRadius * 0.8f),
        style = Stroke(width = w * 0.016f, cap = StrokeCap.Round)
      )
    }

    HairStyle.CURLY_CROP -> {
      // Solid crown base
      drawArc(
        brush = Brush.verticalGradient(
          colors = listOf(hairHighlight.copy(alpha = 0.8f), hairColor, shadowHair),
          startY = headCenterY - headRadius * 1.20f,
          endY = headCenterY - headRadius * 0.40f
        ),
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(centerX - headRadius * 1.08f, headCenterY - headRadius * 1.18f),
        size = Size(headRadius * 2.16f, headRadius * 1.54f)
      )

      // Rich 3D curls around crown and forehead
      val curlRadius = headRadius * 0.42f
      for (i in -4..4) {
        val angle = (i * 22f) * (PI / 180f).toFloat()
        val cx = centerX + (headRadius * 0.98f) * sin(angle)
        val cy = headCenterY - (headRadius * 0.94f) * cos(angle)
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(hairHighlight, hairColor, shadowHair),
            center = Offset(cx - curlRadius * 0.2f, cy - curlRadius * 0.2f),
            radius = curlRadius
          ),
          radius = curlRadius,
          center = Offset(cx, cy)
        )
      }
      // Inner curls for dense texture
      drawCircle(hairColor, radius = curlRadius * 0.9f, center = Offset(centerX - headRadius * 0.35f, headCenterY - headRadius * 0.88f))
      drawCircle(hairColor, radius = curlRadius * 0.9f, center = Offset(centerX + headRadius * 0.35f, headCenterY - headRadius * 0.88f))
      drawCircle(hairColor, radius = curlRadius * 0.9f, center = Offset(centerX, headCenterY - headRadius * 0.98f))
    }

    HairStyle.PONYTAIL -> {
      // Solid sleek crown dome with 3D gradient
      drawArc(
        brush = Brush.verticalGradient(
          colors = listOf(hairHighlight.copy(alpha = 0.85f), hairColor, shadowHair),
          startY = headCenterY - headRadius * 1.20f,
          endY = headCenterY - headRadius * 0.40f
        ),
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(centerX - headRadius * 1.08f, headCenterY - headRadius * 1.18f),
        size = Size(headRadius * 2.16f, headRadius * 1.52f)
      )

      // Stylish side-swept bangs across forehead
      val frontBangs = Path().apply {
        moveTo(centerX - headRadius * 1.05f, headCenterY - headRadius * 0.1f)
        quadraticTo(centerX - headRadius * 0.5f, headCenterY - headRadius * 1.28f, centerX + headRadius * 0.82f, headCenterY - headRadius * 1.18f)
        lineTo(centerX + headRadius * 1.05f, headCenterY - headRadius * 0.1f)
        quadraticTo(centerX + headRadius * 0.5f, headCenterY - headRadius * 0.45f, centerX, headCenterY - headRadius * 0.48f)
        quadraticTo(centerX - headRadius * 0.6f, headCenterY - headRadius * 0.52f, centerX - headRadius * 1.05f, headCenterY - headRadius * 0.1f)
        close()
      }
      drawPath(frontBangs, hairColor)

      // Temple locks
      drawRoundRect(
        color = hairColor,
        topLeft = Offset(centerX - headRadius * 1.06f, headCenterY - headRadius * 0.2f),
        size = Size(headRadius * 0.20f, headRadius * 0.48f),
        cornerRadius = CornerRadius(headRadius * 0.1f, headRadius * 0.1f)
      )
      drawRoundRect(
        color = hairColor,
        topLeft = Offset(centerX + headRadius * 0.86f, headCenterY - headRadius * 0.2f),
        size = Size(headRadius * 0.20f, headRadius * 0.48f),
        cornerRadius = CornerRadius(headRadius * 0.1f, headRadius * 0.1f)
      )
    }

    HairStyle.BRAIDS -> {
      // Center-parted crown dome
      drawArc(
        brush = Brush.verticalGradient(
          colors = listOf(hairHighlight.copy(alpha = 0.85f), hairColor, shadowHair),
          startY = headCenterY - headRadius * 1.20f,
          endY = headCenterY - headRadius * 0.40f
        ),
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(centerX - headRadius * 1.08f, headCenterY - headRadius * 1.18f),
        size = Size(headRadius * 2.16f, headRadius * 1.54f)
      )

      // Left parted bang
      val leftBang = Path().apply {
        moveTo(centerX, headCenterY - headRadius * 1.12f)
        quadraticTo(centerX - headRadius * 0.52f, headCenterY - headRadius * 0.98f, centerX - headRadius * 1.05f, headCenterY - headRadius * 0.15f)
        quadraticTo(centerX - headRadius * 0.52f, headCenterY - headRadius * 0.55f, centerX, headCenterY - headRadius * 0.85f)
        close()
      }
      drawPath(leftBang, hairColor)

      // Right parted bang
      val rightBang = Path().apply {
        moveTo(centerX, headCenterY - headRadius * 1.12f)
        quadraticTo(centerX + headRadius * 0.52f, headCenterY - headRadius * 0.98f, centerX + headRadius * 1.05f, headCenterY - headRadius * 0.15f)
        quadraticTo(centerX + headRadius * 0.52f, headCenterY - headRadius * 0.55f, centerX, headCenterY - headRadius * 0.85f)
        close()
      }
      drawPath(rightBang, hairColor)
    }

    HairStyle.SIDE_PART -> {
      // Solid crown
      drawArc(
        brush = Brush.verticalGradient(
          colors = listOf(hairHighlight.copy(alpha = 0.85f), hairColor, shadowHair),
          startY = headCenterY - headRadius * 1.20f,
          endY = headCenterY - headRadius * 0.40f
        ),
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(centerX - headRadius * 1.08f, headCenterY - headRadius * 1.20f),
        size = Size(headRadius * 2.16f, headRadius * 1.55f)
      )

      // Sweeping voluminous side fringe
      val sidePath = Path().apply {
        moveTo(centerX - headRadius * 1.05f, headCenterY - headRadius * 0.15f)
        quadraticTo(centerX - headRadius * 0.62f, headCenterY - headRadius * 1.38f, centerX + headRadius * 0.48f, headCenterY - headRadius * 1.28f)
        lineTo(centerX + headRadius * 1.05f, headCenterY - headRadius * 0.15f)
        quadraticTo(centerX + headRadius * 0.25f, headCenterY - headRadius * 0.52f, centerX - headRadius * 1.05f, headCenterY - headRadius * 0.15f)
        close()
      }
      drawPath(sidePath, hairColor)

      // Specular highlight
      drawArc(
        color = hairHighlight.copy(alpha = 0.5f),
        startAngle = 210f,
        sweepAngle = 65f,
        useCenter = false,
        topLeft = Offset(centerX - headRadius * 0.7f, headCenterY - headRadius * 1.22f),
        size = Size(headRadius * 1.4f, headRadius * 0.8f),
        style = Stroke(width = w * 0.018f, cap = StrokeCap.Round)
      )
    }

    HairStyle.WAVY_BOB -> {
      // Solid crown dome with high natural root lift (matching Girl reference)
      drawArc(
        brush = Brush.verticalGradient(
          colors = listOf(
            hairHighlight.copy(alpha = 0.85f),
            hairColor,
            shadowHair
          ),
          startY = headCenterY - headRadius * 1.30f,
          endY = headCenterY - headRadius * 0.50f
        ),
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(centerX - headRadius * 1.12f, headCenterY - headRadius * 1.25f),
        size = Size(headRadius * 2.24f, headRadius * 1.62f)
      )

      // Center-parted feathered curtain bangs (signature Girl reference look)
      // Left curtain wing
      val leftCurtain = Path().apply {
        moveTo(centerX, headCenterY - headRadius * 1.15f)
        cubicTo(
          centerX - headRadius * 0.35f, headCenterY - headRadius * 1.10f,
          centerX - headRadius * 0.75f, headCenterY - headRadius * 0.75f,
          centerX - headRadius * 1.25f, headCenterY - headRadius * 0.05f
        )
        // Wing flick at cheek level
        quadraticTo(
          centerX - headRadius * 1.38f, headCenterY + headRadius * 0.28f,
          centerX - headRadius * 1.18f, headCenterY + headRadius * 0.48f
        )
        // Inner curve framing the face
        cubicTo(
          centerX - headRadius * 0.95f, headCenterY + headRadius * 0.15f,
          centerX - headRadius * 0.68f, headCenterY - headRadius * 0.45f,
          centerX, headCenterY - headRadius * 0.80f
        )
        close()
      }
      drawPath(
        leftCurtain,
        brush = Brush.verticalGradient(
          colors = listOf(hairHighlight.copy(alpha = 0.90f), hairColor, shadowHair),
          startY = headCenterY - headRadius * 1.15f,
          endY = headCenterY + headRadius * 0.48f
        )
      )

      // Right curtain wing
      val rightCurtain = Path().apply {
        moveTo(centerX, headCenterY - headRadius * 1.15f)
        cubicTo(
          centerX + headRadius * 0.35f, headCenterY - headRadius * 1.10f,
          centerX + headRadius * 0.75f, headCenterY - headRadius * 0.75f,
          centerX + headRadius * 1.25f, headCenterY - headRadius * 0.05f
        )
        // Wing flick at cheek level
        quadraticTo(
          centerX + headRadius * 1.38f, headCenterY + headRadius * 0.28f,
          centerX + headRadius * 1.18f, headCenterY + headRadius * 0.48f
        )
        // Inner curve framing the face
        cubicTo(
          centerX + headRadius * 0.95f, headCenterY + headRadius * 0.15f,
          centerX + headRadius * 0.68f, headCenterY - headRadius * 0.45f,
          centerX, headCenterY - headRadius * 0.80f
        )
        close()
      }
      drawPath(
        rightCurtain,
        brush = Brush.verticalGradient(
          colors = listOf(hairHighlight.copy(alpha = 0.90f), hairColor, shadowHair),
          startY = headCenterY - headRadius * 1.15f,
          endY = headCenterY + headRadius * 0.48f
        )
      )

      // Cheek framing wave locks cascading down
      val leftCheekLock = Path().apply {
        moveTo(centerX - headRadius * 1.05f, headCenterY - headRadius * 0.10f)
        quadraticTo(centerX - headRadius * 1.22f, headCenterY + headRadius * 0.50f, centerX - headRadius * 0.88f, headCenterY + headRadius * 0.85f)
        quadraticTo(centerX - headRadius * 0.80f, headCenterY + headRadius * 0.45f, centerX - headRadius * 0.95f, headCenterY + headRadius * 0.10f)
        close()
      }
      drawPath(leftCheekLock, hairColor)

      val rightCheekLock = Path().apply {
        moveTo(centerX + headRadius * 1.05f, headCenterY - headRadius * 0.10f)
        quadraticTo(centerX + headRadius * 1.22f, headCenterY + headRadius * 0.50f, centerX + headRadius * 0.88f, headCenterY + headRadius * 0.85f)
        quadraticTo(centerX + headRadius * 0.80f, headCenterY + headRadius * 0.45f, centerX + headRadius * 0.95f, headCenterY + headRadius * 0.10f)
        close()
      }
      drawPath(rightCheekLock, hairColor)

      // Wave crest highlights
      drawArc(
        color = hairHighlight.copy(alpha = 0.50f),
        startAngle = 200f,
        sweepAngle = 60f,
        useCenter = false,
        topLeft = Offset(centerX - headRadius * 0.75f, headCenterY - headRadius * 1.25f),
        size = Size(headRadius * 1.50f, headRadius * 0.95f),
        style = Stroke(width = w * 0.016f, cap = StrokeCap.Round)
      )
      drawArc(
        color = hairHighlight.copy(alpha = 0.50f),
        startAngle = 280f,
        sweepAngle = 60f,
        useCenter = false,
        topLeft = Offset(centerX - headRadius * 0.75f, headCenterY - headRadius * 1.25f),
        size = Size(headRadius * 1.50f, headRadius * 0.95f),
        style = Stroke(width = w * 0.016f, cap = StrokeCap.Round)
      )
    }
  }
}
