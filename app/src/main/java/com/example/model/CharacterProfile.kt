package com.example.model

enum class CharacterGender {
  BOY,
  GIRL
}

enum class CharacterExpression {
  IDLE,
  HAPPY,
  CELEBRATING,
  CONFUSED,
  SAD,
  WAVING,
  POINTING
}

enum class SkinTone(val label: String, val hex: Long) {
  FAIR("Fair", 0xFFFFDCB1),
  MEDIUM_LIGHT("Warm Wheat", 0xFFE8BE96),
  MEDIUM("Sun-Kissed", 0xFFCF9E76),
  DEEP_WARM("Rich Caramel", 0xFFA97448),
  DEEP("Deep Cocoa", 0xFF7D4E2D)
}

enum class HairColor(val label: String, val hex: Long) {
  BLACK("Jet Black", 0xFF212121),
  DARK_BROWN("Deep Brown", 0xFF4E342E),
  CHESTNUT("Chestnut", 0xFF6D4C41),
  GOLDEN("Honey", 0xFFD4A373),
  AUBURN("Auburn", 0xFF8D5B4C)
}

enum class HairStyle(val label: String) {
  SHORT_SPIKY("Short Spiky"),
  CURLY_CROP("Curly Crop"),
  PONYTAIL("High Ponytail"),
  BRAIDS("Two Braids"),
  SIDE_PART("Side Part"),
  WAVY_BOB("Wavy Bob")
}

enum class OutfitStyle(val label: String, val primaryHex: Long, val secondaryHex: Long) {
  SCHOOL_HERO("School Outfit", 0xFFFFFFFF, 0xFF1A1A1A),
  EXPLORER("Eco Explorer", 0xFF00897B, 0xFFFFA000),
  STREET_CASUAL("City Casual", 0xFF1E88E5, 0xFFFF5722),
  PARK_RANGER("Green Ranger", 0xFF43A047, 0xFFFFD54F),
  SPORTS("Active Runner", 0xFFE53935, 0xFFFFFFFF)
}

enum class TShirtColor(
  val label: String,
  val hex: Long,
  val shadeHex: Long,
  val collarHex: Long
) {
  WHITE("White T-Shirt", 0xFFFFFFFF, 0xFFE0E0E0, 0xFFF5F5F5),
  BLACK("Black T-Shirt", 0xFF212121, 0xFF121212, 0xFF2E2E2E),
  RED("Red T-Shirt", 0xFFE53935, 0xFFC62828, 0xFFD32F2F),
  BLUE("Blue T-Shirt", 0xFF1E88E5, 0xFF1565C0, 0xFF1976D2),
  GREEN("Green T-Shirt", 0xFF43A047, 0xFF2E7D32, 0xFF388E3C),
  YELLOW("Yellow T-Shirt", 0xFFFDD835, 0xFFFBC02D, 0xFFF9A825),
  ORANGE("Orange T-Shirt", 0xFFFB8C00, 0xFFEF6C00, 0xFFF57C00),
  PURPLE("Purple T-Shirt", 0xFF8E24AA, 0xFF6A1B9A, 0xFF7B1FA2),
  SKY_BLUE("Sky Blue T-Shirt", 0xFF29B6F6, 0xFF0288D1, 0xFF039BE5),
  PINK("Pink T-Shirt", 0xFFEC407A, 0xFFD81B60, 0xFFC2185B),
  GREY("Grey T-Shirt", 0xFF757575, 0xFF616161, 0xFF9E9E9E),
  DARK_BLUE("Dark Blue T-Shirt", 0xFF1A237E, 0xFF0D1642, 0xFF283593)
}

enum class PantsStyle(val label: String, val hex: Long) {
  DENIM_JEANS("Classic Denim", 0xFF1976D2),
  KHAKI_CARGO("Khaki Cargo", 0xFF8D6E63),
  DARK_JOGGERS("Sport Joggers", 0xFF37474F),
  NAVY_SHORTS("Navy Shorts", 0xFF283593)
}

enum class ShoesStyle(val label: String, val hex: Long) {
  WHITE_SNEAKERS("White Kicks", 0xFFECEFF1),
  RED_RUNNERS("Red Runners", 0xFFD32F2F),
  YELLOW_CANVAS("Gold Canvas", 0xFFFFA000),
  BLUE_SPEED("Blue Sprint", 0xFF0288D1)
}

enum class Accessory(val label: String) {
  NONE("None"),
  COOL_CAP("Hero Cap"),
  BADGE_PIN("Civic Star Pin"),
  GLASSES("Smart Glasses"),
  WRISTBAND("Eco Wristband")
}

data class CharacterProfile(
  val name: String = "Spidyy",
  val gender: CharacterGender = CharacterGender.BOY,
  val skinTone: SkinTone = SkinTone.MEDIUM,
  val hairStyle: HairStyle = HairStyle.SHORT_SPIKY,
  val hairColor: HairColor = HairColor.BLACK,
  val outfitStyle: OutfitStyle = OutfitStyle.SCHOOL_HERO,
  val selectedTShirt: TShirtColor? = null,
  val pantsStyle: PantsStyle = PantsStyle.DENIM_JEANS,
  val shoesStyle: ShoesStyle = ShoesStyle.WHITE_SNEAKERS,
  val accessory: Accessory = Accessory.BADGE_PIN,
  val backpackHex: Long = 0xFFFF7043
)
