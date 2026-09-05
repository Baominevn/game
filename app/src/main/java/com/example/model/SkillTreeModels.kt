package com.example.model

enum class SkillBranch(val title: String, val description: String, val themeColorHex: Long) {
    CYBER_NINJA("Cyber Ninja", "Chuyên tốc độ, nhát chém chí mạng và né tránh lẩn khuất", 0xFF00E5FF),
    TITAN_MECHA("Titan Mecha", "Gia cố giáp hạng nặng, hấp thụ xung lực và phản đòn khiên", 0xFFFF9100),
    NETRUNNER("Netrunner", "Hack tần số điện từ, sốc điện EMP và hút cạn năng lượng đối thủ", 0xFF7C4DFF);

    val branchName: String get() = title
}

data class SkillNode(
    val id: String,
    val branch: SkillBranch,
    val tier: Int,
    val name: String,
    val description: String,
    val costSkillPoints: Int = 1,
    val isUnlocked: Boolean = false,
    val bonusAttack: Int = 0,
    val bonusDefense: Int = 0,
    val bonusShield: Int = 0,
    val bonusCritRate: Float = 0f,
    val specialEffectKey: String = ""
)
