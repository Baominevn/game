package com.example.model

enum class MatchTier(val label: String, val description: String) {
    ROOKIE_BOT("Tân Binh (Bot Gà)", "Huấn luyện cơ bản cho tân thủ, phản xạ chậm"),
    INTERMEDIATE_BOT("Chiến Binh (Bot Trung)", "Tập huấn chiến thuật, biết dùng khiên đỡ"),
    ADVANCED_BOT("Sát Thủ (Bot Mạnh)", "Hệ thần kinh AI tối tân, né tránh và combo dồn dập"),
    LIVE_PVP_1V1("Đấu Trường PVP 1v1", "Ghép trận thời gian thực với đặc vụ người chơi"),
    LIVE_PVP_2V2("Chiến Trường 2v2", "Tổ đội phối hợp 2 người tác chiến thời gian thực")
}

enum class EloRank(val title: String, val minElo: Int, val badgeColor: Long) {
    BRONZE("Đồng I-III", 0, 0xFFCD7F32),
    SILVER("Bạc I-III", 1200, 0xFFC0C0C0),
    GOLD("Vàng I-III", 1500, 0xFFFFD700),
    PLATINUM("Bạch Kim I-III", 1800, 0xFF00E5FF),
    DIAMOND("Kim Cương", 2100, 0xFF7C4DFF),
    QUANTUM_MASTER("Bậc Thầy Lượng Tử", 2500, 0xFFFF007F)
}

data class PvpPlayerProfile(
    val id: String,
    val callsign: String,
    val level: Int,
    val eloRating: Int,
    val eloRank: EloRank,
    val winRatePercent: Int,
    val avatarIcon: String,
    val isOnline: Boolean = true,
    val syndicateName: String = "Ares Syndicate"
)

data class SyndicateGuild(
    val id: String,
    val name: String,
    val tag: String,
    val level: Int,
    val memberCount: Int,
    val maxMembers: Int = 30,
    val totalPower: Long,
    val perkDescription: String,
    val announcement: String
)

data class WorldRaidBoss(
    val id: String,
    val name: String,
    val title: String,
    val maxHp: Long,
    var currentHp: Long,
    val participantsCount: Int,
    val timeRemainingSeconds: Int,
    val topDamagers: List<RaidParticipant>,
    val weaknessElement: String
)

data class RaidParticipant(
    val rank: Int,
    val callsign: String,
    val damageDealt: Long,
    val rewardTier: String
) {
    val damageContributed: Long get() = damageDealt
}

data class ChatMessage(
    val id: String,
    val senderName: String,
    val rankTitle: String,
    val content: String,
    val timestamp: String,
    val isSyndicate: Boolean = false,
    val isSystem: Boolean = false
) {
    val rankBadge: String get() = rankTitle
    val message: String get() = content
}

data class MarketplaceItem(
    val id: String,
    val sellerCallsign: String,
    val item: GameItem,
    val priceCredits: Int,
    val priceVndMomo: Int,
    val momoPhoneTarget: String = "0909120918",
    val isFeatured: Boolean = false
)

data class SpectatorMatch(
    val id: String,
    val player1Name: String,
    val player2Name: String,
    val player1HpPercent: Float,
    val player2HpPercent: Float,
    val spectatorsCount: Int,
    val currentRound: Int,
    val tierLabel: String
) {
    val matchTitle: String get() = "$player1Name vs $player2Name ($tierLabel)"
    val viewersCount: Int get() = spectatorsCount
    val player1Callsign: String get() = player1Name
    val player2Callsign: String get() = player2Name
}
