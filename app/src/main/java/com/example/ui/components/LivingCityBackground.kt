package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.PI
import kotlin.math.sin

/**
 * High-fidelity, stylized Indian City environment background.
 * Creates depth via distinct environmental layers:
 * Foreground: Paver footpath, tactile tiles, animated diverse NPCs (students, traffic police, elders), public bins, streetlamps.
 * Gameplay/Midground: Asphalt roads with zebra crossing, moving classic yellow-green auto-rickshaw and red city bus, traffic lights.
 * Background: Stylized Indian urban architecture, shops with awnings, residential apartments, lush swaying banyan/neem trees.
 * Distant: Atmospheric gradient sky, floating soft clouds, distant birds.
 */
@Composable
fun LivingCityBackground(
  modifier: Modifier = Modifier,
  showRoad: Boolean = true,
  isParkScene: Boolean = false
) {
  val infiniteTransition = rememberInfiniteTransition(label = "livingCityAnimation")

  // Auto-rickshaw passing by smoothly
  val autoRickshawProgress by infiniteTransition.animateFloat(
    initialValue = -0.3f,
    targetValue = 1.3f,
    animationSpec = infiniteRepeatable(
      animation = tween(8500, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "autoRickshaw"
  )

  // City bus moving in opposite direction
  val busProgress by infiniteTransition.animateFloat(
    initialValue = 1.35f,
    targetValue = -0.45f,
    animationSpec = infiniteRepeatable(
      animation = tween(14000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "cityBus"
  )

  // Cloud gentle drifting
  val cloudProgress by infiniteTransition.animateFloat(
    initialValue = -0.2f,
    targetValue = 1.25f,
    animationSpec = infiniteRepeatable(
      animation = tween(22000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "cloud"
  )

  // Gentle tree leaf sway
  val treeSway by infiniteTransition.animateFloat(
    initialValue = -3.5f,
    targetValue = 3.5f,
    animationSpec = infiniteRepeatable(
      animation = tween(1750, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "treeSway"
  )

  // Ambient pedestrian walk cycle
  val walkCycle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(750, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "pedestrianWalk"
  )

  // Traffic light state cycle (Green, Yellow, Red)
  val trafficLightCycle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 3f,
    animationSpec = infiniteRepeatable(
      animation = tween(6000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "trafficLightCycle"
  )

  Canvas(modifier = modifier.fillMaxSize()) {
    val w = size.width
    val h = size.height
    if (w <= 10f || h <= 10f) return@Canvas

    // 1. Atmospheric Sky Gradient (Warm morning sunlight)
    drawRect(
      brush = Brush.verticalGradient(
        colors = if (isParkScene) listOf(
          Color(0xFFBAE6FD),
          Color(0xFFE0F2FE),
          Color(0xFFDCFCE7)
        ) else listOf(
          Color(0xFF7DD3FC),
          Color(0xFFE0F2FE),
          Color(0xFFFEF3C7)
        )
      )
    )

    // 2. Soft Stylized Clouds
    drawCloud(w * cloudProgress, h * 0.07f, w * 0.18f)
    drawCloud(w * ((cloudProgress + 0.45f) % 1.4f - 0.2f), h * 0.13f, w * 0.14f)
    drawCloud(w * ((cloudProgress + 0.85f) % 1.4f - 0.2f), h * 0.04f, w * 0.22f)

    // 3. Distant Birds in flight
    drawBird(w * ((cloudProgress * 1.4f + 0.1f) % 1.2f), h * 0.10f, w * 0.024f)
    drawBird(w * ((cloudProgress * 1.4f + 0.16f) % 1.2f), h * 0.085f, w * 0.020f)
    drawBird(w * ((cloudProgress * 1.4f + 0.13f) % 1.2f), h * 0.12f, w * 0.016f)

    // 4. Distant Urban Skyline (Layer 1 - Warm pastel silhouettes)
    drawSkyline(w, h, isParkScene)

    // 5. Swaying lush trees (Neem / Ashoka style)
    drawSwayingTree(w * 0.10f, h * 0.58f, w * 0.20f, h * 0.23f, treeSway)
    drawSwayingTree(w * 0.88f, h * 0.59f, w * 0.18f, h * 0.21f, -treeSway)

    if (showRoad) {
      // 6. Gameplay Area: Main Indian City Road
      val roadTop = h * 0.65f
      val roadBottom = h * 0.84f

      // Road Surface with asphalt gradient
      drawRect(
        brush = Brush.verticalGradient(
          colors = listOf(Color(0xFF334155), Color(0xFF1E293B)),
          startY = roadTop,
          endY = roadBottom
        ),
        topLeft = Offset(0f, roadTop),
        size = Size(w, roadBottom - roadTop)
      )

      // Road Curb with yellow-black hazard stripes
      val curbH = h * 0.014f
      drawRect(
        color = Color(0xFFFACC15),
        topLeft = Offset(0f, roadTop),
        size = Size(w, curbH)
      )
      // Curb black segments
      val curbSegW = (w * 0.08f).coerceAtLeast(10f)
      val curbCount = (w / curbSegW).toInt().coerceIn(0, 50)
      for (i in 0 until curbCount) {
        val curbX = i * curbSegW
        drawRect(
          color = Color(0xFF1E293B),
          topLeft = Offset(curbX, roadTop),
          size = Size(curbSegW * 0.5f, curbH)
        )
      }

      // Yellow Dashed Center Lane Line
      val dashWidth = (w * 0.09f).coerceAtLeast(10f)
      val dashGap = (w * 0.06f).coerceAtLeast(5f)
      val dashStep = dashWidth + dashGap
      val dashY = roadTop + (roadBottom - roadTop) * 0.5f
      val dashCount = (w / dashStep).toInt().coerceIn(0, 40)
      for (i in 0 until dashCount) {
        val currentX = i * dashStep
        drawRoundRect(
          color = Color(0xFFFDE047),
          topLeft = Offset(currentX, dashY),
          size = Size(dashWidth, h * 0.009f),
          cornerRadius = CornerRadius(3f, 3f)
        )
      }

      // Zebra Crossing with clean perspective lines
      val zebraStartX = w * 0.62f
      val zebraWidth = w * 0.28f
      val stripeCount = 6
      for (i in 0 until stripeCount) {
        drawRoundRect(
          color = Color(0xFFF8FAFC),
          topLeft = Offset(zebraStartX + (i * zebraWidth / stripeCount), roadTop + curbH + 2f),
          size = Size(zebraWidth / (stripeCount * 1.7f), roadBottom - roadTop - curbH - 4f),
          cornerRadius = CornerRadius(4f, 4f)
        )
      }

      // Vehicles moving with depth and shadow
      // 1. Classic Indian Green & Yellow Auto-Rickshaw
      drawAutoRickshaw(
        x = w * autoRickshawProgress,
        y = roadTop + (roadBottom - roadTop) * 0.10f,
        width = w * 0.23f,
        height = h * 0.095f
      )

      // 2. City Transit Bus
      drawCityBus(
        x = w * busProgress,
        y = roadTop + (roadBottom - roadTop) * 0.44f,
        width = w * 0.40f,
        height = h * 0.12f
      )

      // 7. Foreground: Footpath / Pavement with Tactile Tiles
      drawFootpath(w, h, roadBottom)

      // 8. Diverse Animated Indian City NPCs
      drawPedestrians(w, h, roadBottom, walkCycle)

      // 9. Street Furniture: Segregated Green/Blue bins, Modern Traffic Signal, Bus Stop Shelter
      drawStreetProps(w, h, roadBottom, trafficLightCycle)
    } else {
      // Park green ground with cobblestone pathway
      drawParkGround(w, h)
    }
  }
}

private fun DrawScope.drawCloud(x: Float, y: Float, cloudWidth: Float) {
  val cloudColor = Color(0xEBFFFFFF)
  val r = cloudWidth * 0.26f
  drawCircle(cloudColor, radius = r * 0.88f, center = Offset(x, y))
  drawCircle(cloudColor, radius = r * 1.25f, center = Offset(x + r * 0.82f, y - r * 0.25f))
  drawCircle(cloudColor, radius = r * 0.98f, center = Offset(x + r * 1.85f, y))
  drawRoundRect(
    cloudColor,
    topLeft = Offset(x - r * 0.5f, y),
    size = Size(r * 2.9f, r * 0.85f),
    cornerRadius = CornerRadius(r * 0.45f, r * 0.45f)
  )
}

private fun DrawScope.drawBird(x: Float, y: Float, birdSize: Float) {
  val path = Path().apply {
    moveTo(x - birdSize, y)
    quadraticTo(x - birdSize * 0.5f, y - birdSize * 0.6f, x, y)
    quadraticTo(x + birdSize * 0.5f, y - birdSize * 0.6f, x + birdSize, y)
  }
  drawPath(path, Color(0xFF334155), style = Stroke(width = 2.8f, cap = StrokeCap.Round))
}

private fun DrawScope.drawSkyline(w: Float, h: Float, isParkScene: Boolean) {
  val groundY = h * 0.65f

  if (isParkScene) {
    val hillPath1 = Path().apply {
      moveTo(0f, groundY)
      quadraticTo(w * 0.3f, groundY - h * 0.16f, w * 0.7f, groundY - h * 0.06f)
      quadraticTo(w * 0.85f, groundY - h * 0.12f, w, groundY)
      close()
    }
    drawPath(hillPath1, Color(0xFFBBF7D0))

    val hillPath2 = Path().apply {
      moveTo(0f, groundY)
      quadraticTo(w * 0.4f, groundY - h * 0.08f, w, groundY - h * 0.14f)
      lineTo(w, groundY)
      close()
    }
    drawPath(hillPath2, Color(0xFF86EFAC))
    return
  }

  // Modern Indian Urban Skyline with shops, awnings, and apartment towers
  // Building 1: Mango yellow residential building with balconies
  val b1X = w * 0.03f
  val b1W = w * 0.22f
  val b1H = h * 0.24f
  val b1Top = groundY - b1H
  drawRoundRect(
    brush = Brush.verticalGradient(
      colors = listOf(Color(0xFFFDE68A), Color(0xFFF59E0B)),
      startY = b1Top,
      endY = groundY
    ),
    topLeft = Offset(b1X, b1Top),
    size = Size(b1W, b1H),
    cornerRadius = CornerRadius(8f, 8f)
  )
  // Balcony railings
  for (floor in 1..3) {
    val balconyY = b1Top + floor * (b1H * 0.26f)
    drawRect(
      color = Color(0xFF78350F),
      topLeft = Offset(b1X + b1W * 0.12f, balconyY),
      size = Size(b1W * 0.76f, h * 0.015f)
    )
  }

  // Building 2: Central Tall Modern Civic Center (Sky blue / teal glass)
  val b2X = w * 0.28f
  val b2W = w * 0.24f
  val b2H = h * 0.32f
  val b2Top = groundY - b2H
  drawRoundRect(
    brush = Brush.verticalGradient(
      colors = listOf(Color(0xFF38BDF8), Color(0xFF0284C7)),
      startY = b2Top,
      endY = groundY
    ),
    topLeft = Offset(b2X, b2Top),
    size = Size(b2W, b2H),
    cornerRadius = CornerRadius(10f, 10f)
  )
  // Window Grid
  val winW = b2W * 0.18f
  val winH = b2H * 0.09f
  for (r in 0..4) {
    for (c in 0..2) {
      drawRoundRect(
        color = Color(0xCCFFFFFF),
        topLeft = Offset(b2X + b2W * 0.12f + c * (winW * 1.55f), b2Top + b2H * 0.12f + r * (winH * 1.6f)),
        size = Size(winW, winH),
        cornerRadius = CornerRadius(3f, 3f)
      )
    }
  }

  // Building 3: Indian Corner Kirana / Bookstore with red & white awning
  val b3X = w * 0.55f
  val b3W = w * 0.24f
  val b3H = h * 0.19f
  val b3Top = groundY - b3H
  drawRoundRect(
    brush = Brush.verticalGradient(
      colors = listOf(Color(0xFFFED7AA), Color(0xFFFB923C)),
      startY = b3Top,
      endY = groundY
    ),
    topLeft = Offset(b3X, b3Top),
    size = Size(b3W, b3H),
    cornerRadius = CornerRadius(8f, 8f)
  )
  // Shop sign board
  drawRoundRect(
    color = Color(0xFF00695C),
    topLeft = Offset(b3X + b3W * 0.08f, b3Top + b3H * 0.15f),
    size = Size(b3W * 0.84f, b3H * 0.25f),
    cornerRadius = CornerRadius(4f, 4f)
  )
  // Striped awning
  val awningTop = b3Top + b3H * 0.44f
  val awningW = b3W * 0.90f
  val awningH = b3H * 0.16f
  val stripeW = awningW / 6f
  for (s in 0..5) {
    drawRect(
      color = if (s % 2 == 0) Color(0xFFDC2626) else Color(0xFFFFFFFF),
      topLeft = Offset(b3X + b3W * 0.05f + s * stripeW, awningTop),
      size = Size(stripeW, awningH)
    )
  }

  // Building 4: Community School building with clock dome
  val b4X = w * 0.80f
  val b4W = w * 0.18f
  val b4H = h * 0.26f
  val b4Top = groundY - b4H
  drawRoundRect(
    brush = Brush.verticalGradient(
      colors = listOf(Color(0xFFE2E8F0), Color(0xFF94A3B8)),
      startY = b4Top,
      endY = groundY
    ),
    topLeft = Offset(b4X, b4Top),
    size = Size(b4W, b4H),
    cornerRadius = CornerRadius(8f, 8f)
  )
  // Clock dome
  drawCircle(
    color = Color(0xFF00897B),
    radius = b4W * 0.35f,
    center = Offset(b4X + b4W * 0.5f, b4Top)
  )
  drawCircle(
    color = Color.White,
    radius = b4W * 0.20f,
    center = Offset(b4X + b4W * 0.5f, b4Top)
  )
}

private fun DrawScope.drawSwayingTree(
  trunkBaseX: Float,
  groundY: Float,
  treeWidth: Float,
  treeHeight: Float,
  swayAngle: Float
) {
  val trunkW = treeWidth * 0.18f
  val trunkTop = groundY - treeHeight * 0.55f

  // Trunk
  drawRoundRect(
    brush = Brush.horizontalGradient(
      colors = listOf(Color(0xFF78350F), Color(0xFF451A03)),
      startX = trunkBaseX - trunkW * 0.5f,
      endX = trunkBaseX + trunkW * 0.5f
    ),
    topLeft = Offset(trunkBaseX - trunkW * 0.5f, trunkTop),
    size = Size(trunkW, treeHeight * 0.6f),
    cornerRadius = CornerRadius(6f, 6f)
  )

  // Foliage puffs with gentle sway offset
  val foliageCenterY = trunkTop - treeHeight * 0.18f
  val swayOffset = swayAngle * 2.2f
  val foliageR = treeWidth * 0.42f

  // Deep green background canopy
  drawCircle(Color(0xFF15803D), radius = foliageR * 1.05f, center = Offset(trunkBaseX + swayOffset, foliageCenterY))
  drawCircle(Color(0xFF16A34A), radius = foliageR * 0.95f, center = Offset(trunkBaseX - foliageR * 0.4f + swayOffset, foliageCenterY + foliageR * 0.2f))
  drawCircle(Color(0xFF16A34A), radius = foliageR * 0.95f, center = Offset(trunkBaseX + foliageR * 0.4f + swayOffset, foliageCenterY + foliageR * 0.2f))
  // Highlight top canopy
  drawCircle(Color(0xFF22C55E), radius = foliageR * 0.85f, center = Offset(trunkBaseX + swayOffset * 1.2f, foliageCenterY - foliageR * 0.3f))
}

private fun DrawScope.drawAutoRickshaw(x: Float, y: Float, width: Float, height: Float) {
  val greenColor = Color(0xFF0D9488)
  val yellowRoof = Color(0xFFFACC15)
  val blackWheel = Color(0xFF0F172A)

  // Soft vehicle contact shadow
  drawOval(
    color = Color(0x40000000),
    topLeft = Offset(x - width * 0.05f, y + height * 0.88f),
    size = Size(width * 1.1f, height * 0.22f)
  )

  // Lower chassis (Classic auto green)
  drawRoundRect(
    brush = Brush.verticalGradient(
      colors = listOf(greenColor, Color(0xFF0F766E)),
      startY = y + height * 0.35f,
      endY = y + height * 0.85f
    ),
    topLeft = Offset(x, y + height * 0.35f),
    size = Size(width, height * 0.48f),
    cornerRadius = CornerRadius(width * 0.16f, width * 0.16f)
  )

  // Yellow classic curved canopy roof
  val roofPath = Path().apply {
    moveTo(x + width * 0.14f, y + height * 0.35f)
    lineTo(x + width * 0.26f, y)
    lineTo(x + width * 0.92f, y)
    lineTo(x + width * 0.96f, y + height * 0.35f)
    close()
  }
  drawPath(roofPath, yellowRoof)

  // Windshield glass with reflection
  drawRoundRect(
    brush = Brush.verticalGradient(
      colors = listOf(Color(0xCCBAE6FD), Color(0x660284C7))
    ),
    topLeft = Offset(x + width * 0.28f, y + height * 0.08f),
    size = Size(width * 0.34f, height * 0.28f),
    cornerRadius = CornerRadius(5f, 5f)
  )

  // Front chrome headlight with beam
  drawCircle(Color(0xFFFEF08A), radius = height * 0.14f, center = Offset(x + width * 0.08f, y + height * 0.54f))
  drawCircle(Color(0xFFFFFFFF), radius = height * 0.07f, center = Offset(x + width * 0.08f, y + height * 0.54f))

  // Wheels with silver hubcaps
  val wheelR = height * 0.22f
  // Front wheel
  drawCircle(blackWheel, radius = wheelR, center = Offset(x + width * 0.24f, y + height * 0.84f))
  drawCircle(Color(0xFF94A3B8), radius = wheelR * 0.48f, center = Offset(x + width * 0.24f, y + height * 0.84f))
  // Rear wheel
  drawCircle(blackWheel, radius = wheelR, center = Offset(x + width * 0.80f, y + height * 0.84f))
  drawCircle(Color(0xFF94A3B8), radius = wheelR * 0.48f, center = Offset(x + width * 0.80f, y + height * 0.84f))
}

private fun DrawScope.drawCityBus(x: Float, y: Float, width: Float, height: Float) {
  val busRed = Color(0xFFDC2626)
  val busCream = Color(0xFFFEF3C7)
  val glass = Color(0xCC7DD3FC)

  // Bus floor shadow
  drawOval(
    color = Color(0x44000000),
    topLeft = Offset(x - width * 0.03f, y + height * 0.85f),
    size = Size(width * 1.06f, height * 0.20f)
  )

  // Main bus body with streamlined rounded corners
  drawRoundRect(
    brush = Brush.verticalGradient(
      colors = listOf(busRed, Color(0xFFB91C1C)),
      startY = y,
      endY = y + height * 0.82f
    ),
    topLeft = Offset(x, y),
    size = Size(width, height * 0.82f),
    cornerRadius = CornerRadius(18f, 18f)
  )
  // Cream accent stripe
  drawRect(
    color = busCream,
    topLeft = Offset(x, y + height * 0.44f),
    size = Size(width, height * 0.14f)
  )
  // Panoramic Windows
  for (i in 0..4) {
    drawRoundRect(
      color = glass,
      topLeft = Offset(x + width * 0.05f + i * width * 0.18f, y + height * 0.12f),
      size = Size(width * 0.14f, height * 0.28f),
      cornerRadius = CornerRadius(4f, 4f)
    )
  }
  // Wheels
  val wheelR = height * 0.22f
  drawCircle(Color(0xFF0F172A), radius = wheelR, center = Offset(x + width * 0.22f, y + height * 0.82f))
  drawCircle(Color(0xFFCBD5E1), radius = wheelR * 0.42f, center = Offset(x + width * 0.22f, y + height * 0.82f))
  drawCircle(Color(0xFF0F172A), radius = wheelR, center = Offset(x + width * 0.78f, y + height * 0.82f))
  drawCircle(Color(0xFFCBD5E1), radius = wheelR * 0.42f, center = Offset(x + width * 0.78f, y + height * 0.82f))
}

private fun DrawScope.drawFootpath(w: Float, h: Float, roadBottom: Float) {
  // Interlocking modern paver footpath
  drawRect(
    brush = Brush.verticalGradient(
      colors = listOf(Color(0xFFE2E8F0), Color(0xFFCBD5E1)),
      startY = roadBottom,
      endY = h
    ),
    topLeft = Offset(0f, roadBottom),
    size = Size(w, h - roadBottom)
  )
  // Tactile Yellow Guidance Path (Accessible Indian infrastructure)
  drawRect(
    color = Color(0xFFFBBF24),
    topLeft = Offset(0f, roadBottom + h * 0.012f),
    size = Size(w, h * 0.016f)
  )
  // Pavement grid lines
  val stepX = (w * 0.12f).coerceAtLeast(15f)
  val gridCount = (w / stepX).toInt().coerceIn(0, 40)
  for (i in 0 until gridCount) {
    val cx = i * stepX
    drawLine(
      color = Color(0xFF94A3B8),
      start = Offset(cx, roadBottom),
      end = Offset(cx, h),
      strokeWidth = 1.8f
    )
  }
}

private fun DrawScope.drawPedestrians(w: Float, h: Float, roadBottom: Float, walkCycle: Float) {
  // Diverse Indian city characters performing natural ambient actions
  val bounce = (sin(walkCycle * 2 * PI).toFloat() * 3.5f)

  // NPC 1: Indian School Student walking with backpack
  val studentX = w * 0.18f
  val studentY = roadBottom + h * 0.048f + bounce
  // Head & Hair
  drawCircle(Color(0xFFCF9E76), radius = w * 0.024f, center = Offset(studentX, studentY - h * 0.046f))
  drawArc(
    color = Color(0xFF1E293B),
    startAngle = 180f,
    sweepAngle = 180f,
    useCenter = true,
    topLeft = Offset(studentX - w * 0.024f, studentY - h * 0.068f),
    size = Size(w * 0.048f, h * 0.034f)
  )
  // School Uniform (White shirt + navy shorts)
  drawRoundRect(
    Color(0xFFFFFFFF),
    topLeft = Offset(studentX - w * 0.020f, studentY - h * 0.034f),
    size = Size(w * 0.040f, h * 0.025f),
    cornerRadius = CornerRadius(4f, 4f)
  )
  drawRoundRect(
    Color(0xFF1E3A8A),
    topLeft = Offset(studentX - w * 0.018f, studentY - h * 0.012f),
    size = Size(w * 0.036f, h * 0.022f),
    cornerRadius = CornerRadius(3f, 3f)
  )
  // Orange backpack
  drawRoundRect(
    Color(0xFFF97316),
    topLeft = Offset(studentX - w * 0.034f, studentY - h * 0.032f),
    size = Size(w * 0.016f, h * 0.034f),
    cornerRadius = CornerRadius(4f, 4f)
  )

  // NPC 2: Indian Traffic Police Officer in khakis & white peak cap
  val copX = w * 0.52f
  val copY = roadBottom + h * 0.042f
  // Officer Head & White Cap
  drawCircle(Color(0xFFA97448), radius = w * 0.024f, center = Offset(copX, copY - h * 0.046f))
  drawRoundRect(
    Color(0xFFFFFFFF),
    topLeft = Offset(copX - w * 0.026f, copY - h * 0.068f),
    size = Size(w * 0.052f, h * 0.018f),
    cornerRadius = CornerRadius(3f, 3f)
  )
  // Khaki uniform
  drawRoundRect(
    Color(0xFFB45309),
    topLeft = Offset(copX - w * 0.022f, copY - h * 0.032f),
    size = Size(w * 0.044f, h * 0.044f),
    cornerRadius = CornerRadius(4f, 4f)
  )
  // Reflective neon sash
  drawLine(
    color = Color(0xFF22C55E),
    start = Offset(copX - w * 0.018f, copY - h * 0.032f),
    end = Offset(copX + w * 0.018f, copY + h * 0.012f),
    strokeWidth = 4f
  )

  // NPC 3: Senior Citizen with walking cane waiting safely
  val elderX = w * 0.74f
  val elderY = roadBottom + h * 0.044f
  // Head with silver hair
  drawCircle(Color(0xFFE8BE96), radius = w * 0.024f, center = Offset(elderX, elderY - h * 0.046f))
  drawArc(
    color = Color(0xFFE2E8F0),
    startAngle = 180f,
    sweepAngle = 180f,
    useCenter = true,
    topLeft = Offset(elderX - w * 0.024f, elderY - h * 0.068f),
    size = Size(w * 0.048f, h * 0.030f)
  )
  // Kurta
  drawRoundRect(
    Color(0xFFFEF3C7),
    topLeft = Offset(elderX - w * 0.022f, elderY - h * 0.032f),
    size = Size(w * 0.044f, h * 0.046f),
    cornerRadius = CornerRadius(4f, 4f)
  )
  // Walking Cane
  drawLine(
    color = Color(0xFF78350F),
    start = Offset(elderX + w * 0.026f, elderY - h * 0.026f),
    end = Offset(elderX + w * 0.028f, elderY + h * 0.028f),
    strokeWidth = 3.5f,
    cap = StrokeCap.Round
  )
}

private fun DrawScope.drawStreetProps(
  w: Float,
  h: Float,
  roadBottom: Float,
  trafficCycle: Float
) {
  // Public Segregated Dustbins (Green for Wet/Organic, Blue for Dry/Recyclable)
  val binY = roadBottom + h * 0.032f

  // 1. Wet Waste Bin (Vibrant Green)
  drawRoundRect(
    brush = Brush.verticalGradient(
      colors = listOf(Color(0xFF22C55E), Color(0xFF15803D)),
      startY = binY,
      endY = binY + h * 0.055f
    ),
    topLeft = Offset(w * 0.04f, binY),
    size = Size(w * 0.044f, h * 0.055f),
    cornerRadius = CornerRadius(6f, 6f)
  )
  // Lid
  drawRoundRect(
    Color(0xFF14532D),
    topLeft = Offset(w * 0.036f, binY - h * 0.009f),
    size = Size(w * 0.052f, h * 0.011f),
    cornerRadius = CornerRadius(4f, 4f)
  )

  // 2. Dry Waste Bin (Vibrant Blue)
  drawRoundRect(
    brush = Brush.verticalGradient(
      colors = listOf(Color(0xFF38BDF8), Color(0xFF0369A1)),
      startY = binY,
      endY = binY + h * 0.055f
    ),
    topLeft = Offset(w * 0.098f, binY),
    size = Size(w * 0.044f, h * 0.055f),
    cornerRadius = CornerRadius(6f, 6f)
  )
  // Lid
  drawRoundRect(
    Color(0xFF0C4A6E),
    topLeft = Offset(w * 0.094f, binY - h * 0.009f),
    size = Size(w * 0.052f, h * 0.011f),
    cornerRadius = CornerRadius(4f, 4f)
  )

  // 3. Indian Traffic Light with dynamic glowing bulbs
  val poleX = w * 0.94f
  val poleTop = roadBottom - h * 0.19f
  // Heavy black metal pole
  drawLine(
    color = Color(0xFF334155),
    start = Offset(poleX, roadBottom + h * 0.06f),
    end = Offset(poleX, poleTop),
    strokeWidth = 7f
  )
  // Signal housing box
  drawRoundRect(
    brush = Brush.verticalGradient(
      colors = listOf(Color(0xFF1E293B), Color(0xFF0F172A))
    ),
    topLeft = Offset(poleX - w * 0.026f, poleTop),
    size = Size(w * 0.052f, h * 0.12f),
    cornerRadius = CornerRadius(7f, 7f)
  )

  // Determine active light from trafficCycle (0..<1 Red, 1..<2 Yellow, 2..<3 Green)
  val isRed = trafficCycle < 1f
  val isYellow = trafficCycle in 1f..2f
  val isGreen = trafficCycle >= 2f

  val bulbR = w * 0.017f
  // Red
  drawCircle(
    if (isRed) Color(0xFFEF4444) else Color(0xFF450A0A),
    radius = bulbR,
    center = Offset(poleX, poleTop + h * 0.024f)
  )
  // Yellow
  drawCircle(
    if (isYellow) Color(0xFFFBBF24) else Color(0xFF451A03),
    radius = bulbR,
    center = Offset(poleX, poleTop + h * 0.060f)
  )
  // Green
  drawCircle(
    if (isGreen) Color(0xFF22C55E) else Color(0xFF052E16),
    radius = bulbR,
    center = Offset(poleX, poleTop + h * 0.096f)
  )
}

private fun DrawScope.drawParkGround(w: Float, h: Float) {
  val groundY = h * 0.65f
  drawRect(
    brush = Brush.verticalGradient(
      colors = listOf(Color(0xFF4ADE80), Color(0xFF22C55E), Color(0xFF15803D))
    ),
    topLeft = Offset(0f, groundY),
    size = Size(w, h - groundY)
  )
  // Curving stone garden walkway
  val path = Path().apply {
    moveTo(w * 0.18f, h)
    quadraticTo(w * 0.44f, groundY + h * 0.16f, w * 0.48f, groundY)
    lineTo(w * 0.68f, groundY)
    quadraticTo(w * 0.62f, groundY + h * 0.16f, w * 0.45f, h)
    close()
  }
  drawPath(path, Color(0xFFE2E8F0))
}
