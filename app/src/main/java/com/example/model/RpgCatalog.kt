package com.example.model

object RpgCatalog {

    val defaultSkillNodes: List<SkillNode> = listOf(
        // Branch 1: Cyber Ninja
        SkillNode(
            id = "ninja_1",
            branch = SkillBranch.CYBER_NINJA,
            tier = 1,
            name = "Lưỡi Kiếm Lượng Tử",
            description = "Tăng vĩnh viễn +15 sát thương cơ bản của Cyber Slash",
            bonusAttack = 15,
            bonusCritRate = 0.05f
        ),
        SkillNode(
            id = "ninja_2",
            branch = SkillBranch.CYBER_NINJA,
            tier = 2,
            name = "Bóng Ma Lẩn Khuất",
            description = "Gia tăng +12% tỉ lệ chí mạng và kích hoạt né tránh khi ở vùng tối",
            bonusCritRate = 0.12f,
            specialEffectKey = "SHADOW_DODGE"
        ),
        SkillNode(
            id = "ninja_3",
            branch = SkillBranch.CYBER_NINJA,
            tier = 3,
            name = "Trảm Kích Hư Không",
            description = "Tuyệt kỹ chém xuyên 100% khiên đối phương, gây thêm +40 sát thương",
            bonusAttack = 40,
            bonusCritRate = 0.15f,
            specialEffectKey = "VOID_EXECUTE"
        ),

        // Branch 2: Titan Mecha
        SkillNode(
            id = "titan_1",
            branch = SkillBranch.TITAN_MECHA,
            tier = 1,
            name = "Giáp Carbon Cường Lực",
            description = "Gia cố khung gầm, tăng +20 phòng thủ và +60 khiên tối đa",
            bonusDefense = 20,
            bonusShield = 60
        ),
        SkillNode(
            id = "titan_2",
            branch = SkillBranch.TITAN_MECHA,
            tier = 2,
            name = "Phản Lực Động Năng",
            description = "Chuyển hóa 30% sát thương hấp thụ vào khiên thành xung lực phản lại kẻ địch",
            bonusDefense = 25,
            specialEffectKey = "KINETIC_REFLECT"
        ),
        SkillNode(
            id = "titan_3",
            branch = SkillBranch.TITAN_MECHA,
            tier = 3,
            name = "Pháo Đài Bất Hoại",
            description = "Khi HP dưới 30%, tự động tạo lớp giáp hấp thụ 200 sát thương trong 3 lượt",
            bonusShield = 120,
            bonusDefense = 35,
            specialEffectKey = "FORTRESS_IMMUNITY"
        ),

        // Branch 3: Netrunner
        SkillNode(
            id = "net_1",
            branch = SkillBranch.NETRUNNER,
            tier = 1,
            name = "Ép Xung Neural Core",
            description = "Tăng tốc độ hồi năng lượng Energy +20% mỗi lượt",
            bonusAttack = 10,
            specialEffectKey = "ENERGY_OVERCLOCK"
        ),
        SkillNode(
            id = "net_2",
            branch = SkillBranch.NETRUNNER,
            tier = 2,
            name = "Bão Từ EMP Quá Tải",
            description = "EMP gây thêm hiệu ứng Sốc Điện Tê Liệt (Stun) khiến đối thủ mất lượt",
            bonusAttack = 25,
            specialEffectKey = "EMP_STUN"
        ),
        SkillNode(
            id = "net_3",
            branch = SkillBranch.NETRUNNER,
            tier = 3,
            name = "Mã Độc Hút Nanite",
            description = "Mỗi đòn đánh hút 15% lượng sát thương gây ra hồi phục lại HP bản thân",
            bonusAttack = 35,
            specialEffectKey = "NANITE_VAMPIRISM"
        )
    )

    val defaultSocketChips: List<SocketChip> = listOf(
        SocketChip(
            id = "chip_vamp_1",
            name = "Vi Mạch Huyết Tế Nanite",
            rarity = ItemRarity.EPIC,
            bonusStatDescription = "Hút máu +15% sát thương thành HP",
            attackBonus = 12,
            lifestealPercent = 0.15f
        ),
        SocketChip(
            id = "chip_hyper_1",
            name = "Lõi Lượng Tử Siêu Dẫn",
            rarity = ItemRarity.LEGENDARY,
            bonusStatDescription = "+35 Sát thương Plasma & Bỏ qua 25% giáp",
            attackBonus = 35
        ),
        SocketChip(
            id = "chip_aegis_1",
            name = "Mạch Khiên Deflector V.2",
            rarity = ItemRarity.RARE,
            bonusStatDescription = "+50 Điểm khiên khởi đầu trận & +15 Thủ",
            defenseBonus = 15
        )
    )

    val defaultEquipmentSets: List<EquipmentSet> = listOf(
        EquipmentSet(
            id = "set_phantom",
            setName = "Phantom Muramasa Set",
            piecesRequired2 = "(2 Món) +20% Tỉ lệ bạo kích",
            piecesRequired4 = "(4 Món) Kích hoạt tàng hình tránh đòn đầu tiên",
            activePiecesCount = 2
        ),
        EquipmentSet(
            id = "set_titan",
            setName = "Ares Heavy Titan Set",
            piecesRequired2 = "(2 Món) +80 Khiên năng lượng tối đa",
            piecesRequired4 = "(4 Món) Phản đòn 40% sát thương nhận vào",
            activePiecesCount = 1
        )
    )

    val defaultDailyBounties: List<DailyBounty> = listOf(
        DailyBounty(
            id = "bounty_1",
            title = "Tập Huấn Tân Binh",
            description = "Chiến thắng 2 trận đấu bất kỳ trong Đấu Trường",
            currentProgress = 1,
            maxProgress = 2,
            rewardCredits = 150,
            rewardNanites = 20
        ),
        DailyBounty(
            id = "bounty_2",
            title = "Khai Thác Động Năng",
            description = "Lắc điện thoại tích lũy trên 50% Kinetic Charge",
            currentProgress = 35,
            maxProgress = 50,
            rewardCredits = 200,
            rewardNanites = 30
        ),
        DailyBounty(
            id = "bounty_3",
            title = "Bậc Thầy Bạo Kích",
            description = "Thực hiện 3 đòn Critical Strike chuẩn xác",
            currentProgress = 2,
            maxProgress = 3,
            rewardCredits = 300,
            rewardNanites = 40
        )
    )

    val defaultAchievements: List<MasteryAchievement> = listOf(
        MasteryAchievement(
            id = "ach_first_blood",
            title = "Chiến Tích Đầu Tiên",
            description = "Hạ gục thành công mục tiêu tập sự đầu tiên",
            rewardTitle = "Tân Binh Cyber",
            isUnlocked = true
        ),
        MasteryAchievement(
            id = "ach_berserk_slayer",
            title = "Kẻ Diệt Berserk",
            description = "Hạ gục một AI Boss khi đang trong trạng thái Cuồng Nộ Berserk",
            rewardTitle = "Chiến Thần Phá Lôi",
            isUnlocked = false
        ),
        MasteryAchievement(
            id = "ach_momo_trader",
            title = "Thương Nhân Công Nghệ",
            description = "Thực hiện giao dịch chợ đen P2P đầu tiên",
            rewardTitle = "Đại Gia Chợ Đen",
            isUnlocked = false
        ),
        MasteryAchievement(
            id = "ach_tower_climber",
            title = "Chinh Phục Tháp Thần Kinh",
            description = "Vượt qua tầng 10 của Infinite Cyber Tower",
            rewardTitle = "Bậc Thầy Tháp Thần Kinh",
            isUnlocked = false
        )
    )

    val defaultMarketplaceItems: List<MarketplaceItem> = listOf(
        MarketplaceItem(
            id = "mkt_1",
            sellerCallsign = "Shadow_Hunter99",
            item = ArsenalCatalog.defaultItems[1], // Plasma rifle
            priceCredits = 420,
            priceVndMomo = 20000,
            momoPhoneTarget = "0909120918",
            isFeatured = true
        ),
        MarketplaceItem(
            id = "mkt_2",
            sellerCallsign = "Netrunner_V",
            item = ArsenalCatalog.defaultItems[2], // Arc disrupter
            priceCredits = 750,
            priceVndMomo = 50000,
            momoPhoneTarget = "0909120918",
            isFeatured = true
        ),
        MarketplaceItem(
            id = "mkt_3",
            sellerCallsign = "CyberValkyrie",
            item = ArsenalCatalog.defaultItems[3], // Singularity Cannon
            priceCredits = 1500,
            priceVndMomo = 100000,
            momoPhoneTarget = "0909120918",
            isFeatured = false
        )
    )

    val defaultSyndicate: SyndicateGuild = SyndicateGuild(
        id = "syn_ares_01",
        name = "Ares Cyber Vanguard",
        tag = "ARES",
        level = 8,
        memberCount = 24,
        maxMembers = 30,
        totalPower = 845000L,
        perkDescription = "+15% Tốc độ hồi khiên & +10% Credits từ mọi trận thắng",
        announcement = "Tập trung lực lượng săn Boss Thế Giới ARES-TITAN lúc 20:00 tối nay!"
    )

    val defaultRaidBoss: WorldRaidBoss = WorldRaidBoss(
        id = "raid_titan_omega",
        name = "ARES-TITAN OMEGA",
        title = "Siêu Trùm Lượng Tử Thế Giới (Co-op 4 Người)",
        maxHp = 5000000L,
        currentHp = 3420000L,
        participantsCount = 48,
        timeRemainingSeconds = 7200,
        weaknessElement = "Yếu trước vũ khí Plasma & Quá tải xung điện EMP",
        topDamagers = listOf(
            RaidParticipant(1, "Neon_Dragon", 820000L, "Hòm Thần Thoại Mythic"),
            RaidParticipant(2, "Cipher_Zero", 640000L, "Hòm Huyền Thoại Legendary"),
            RaidParticipant(3, "Ghost_K9", 510000L, "Hòm Sử Thi Epic"),
            RaidParticipant(4, "Bạn (Operative)", 390000L, "Hòm Sử Thi Epic")
        )
    )
}
