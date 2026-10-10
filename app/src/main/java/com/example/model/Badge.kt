package com.example.model

data class GameBadge(
  val id: String,
  val title: String,
  val description: String,
  val iconName: String,
  val colorHex: Long,
  val unlockedByLevel: Int
)

object BadgesData {
  val ALL_BADGES = listOf(
    GameBadge(
      id = "clean_city_rookie",
      title = "Clean City Rookie",
      description = "Disposed waste responsibly in the city bin.",
      iconName = "delete",
      colorHex = 0xFF00897B,
      unlockedByLevel = 1
    ),
    GameBadge(
      id = "waste_sorter_pro",
      title = "Waste Segregator",
      description = "Sorted wet, dry, and recyclable waste accurately.",
      iconName = "recycling",
      colorHex = 0xFF43A047,
      unlockedByLevel = 2
    ),
    GameBadge(
      id = "road_safety_rookie",
      title = "Road Safety Rookie",
      description = "Respected traffic signals and waited for the green pedestrian sign.",
      iconName = "traffic",
      colorHex = 0xFFE53935,
      unlockedByLevel = 3
    ),
    GameBadge(
      id = "safe_crosser",
      title = "Zebra Guardian",
      description = "Crossed the busy street safely using the zebra crossing.",
      iconName = "directions_walk",
      colorHex = 0xFF1E88E5,
      unlockedByLevel = 4
    ),
    GameBadge(
      id = "green_guardian",
      title = "Green Guardian",
      description = "Nurtured public greenery and kept park paths clean.",
      iconName = "park",
      colorHex = 0xFF2E7D32,
      unlockedByLevel = 5
    ),
    GameBadge(
      id = "water_saver",
      title = "Water Saver",
      description = "Saved precious gallons by shutting off an unattended running tap.",
      iconName = "water_drop",
      colorHex = 0xFF0288D1,
      unlockedByLevel = 6
    ),
    GameBadge(
      id = "queue_champion",
      title = "Kind Citizen",
      description = "Practiced patience and respected queue discipline.",
      iconName = "groups",
      colorHex = 0xFF8E24AA,
      unlockedByLevel = 7
    ),
    GameBadge(
      id = "public_space_protector",
      title = "Public Space Protector",
      description = "Showed exemplary bus etiquette and cared for community property.",
      iconName = "directions_bus",
      colorHex = 0xFFF57C00,
      unlockedByLevel = 8
    ),
    GameBadge(
      id = "community_helper",
      title = "Community Helper",
      description = "Protected shared public facilities and benches in the city.",
      iconName = "handshake",
      colorHex = 0xFF3949AB,
      unlockedByLevel = 9
    ),
    GameBadge(
      id = "responsible_citizen",
      title = "Responsible Citizen",
      description = "Mastered all city challenges and proved true civic responsibility!",
      iconName = "workspace_premium",
      colorHex = 0xFFFFB300,
      unlockedByLevel = 10
    ),
    GameBadge(
      id = "helping_hand",
      title = "Helping Hand",
      description = "Offered safe, thoughtful assistance to neighbors in the community.",
      iconName = "volunteer_activism",
      colorHex = 0xFFE91E63,
      unlockedByLevel = 11
    ),
    GameBadge(
      id = "respectful_communicator",
      title = "Voice of Respect",
      description = "Communicated with patience, politeness, and dignity in all encounters.",
      iconName = "record_voice_over",
      colorHex = 0xFF7C4DFF,
      unlockedByLevel = 12
    ),
    GameBadge(
      id = "property_protector",
      title = "Property Guardian",
      description = "Protected shared public facilities from damage, markings, and vandalism.",
      iconName = "shield",
      colorHex = 0xFF00897B,
      unlockedByLevel = 13
    ),
    GameBadge(
      id = "quiet_champion",
      title = "Quiet Champion",
      description = "Showed mindful consideration for quiet zones and peace of others.",
      iconName = "volume_down",
      colorHex = 0xFF0288D1,
      unlockedByLevel = 14
    ),
    GameBadge(
      id = "community_fairness",
      title = "Fairness Champion",
      description = "Shared park play areas generously, queued fairly, and gave everyone a turn.",
      iconName = "groups",
      colorHex = 0xFFFB8C00,
      unlockedByLevel = 15
    )
  )
}
