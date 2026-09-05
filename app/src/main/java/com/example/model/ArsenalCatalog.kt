package com.example.model

object ArsenalCatalog {

    val defaultItems: List<GameItem> = listOf(
        // Weapons
        GameItem(
            id = "wpn_blade_1",
            name = "Muramasa-X Katana",
            description = "High-frequency cybernetic blade capable of cleaving reinforced chassis.",
            type = ItemType.WEAPON,
            rarity = ItemRarity.RARE,
            attackBonus = 22,
            critBonusPercent = 0.12f,
            priceCredits = 180,
            isEquipped = true,
            upgradeLevel = 1
        ),
        GameItem(
            id = "wpn_plasma_1",
            name = "Hyperion Plasma Rifle",
            description = "Fires compressed superheated ionized plasma bolts that melt titanium plating.",
            type = ItemType.WEAPON,
            rarity = ItemRarity.EPIC,
            attackBonus = 38,
            critBonusPercent = 0.18f,
            priceCredits = 360,
            isEquipped = false,
            upgradeLevel = 1
        ),
        GameItem(
            id = "wpn_emp_1",
            name = "Thor-9 Arc Disrupter",
            description = "Discharges high-voltage electromagnetic surges directly frying shield emitters.",
            type = ItemType.WEAPON,
            rarity = ItemRarity.LEGENDARY,
            attackBonus = 46,
            shieldBonus = 30,
            critBonusPercent = 0.20f,
            priceCredits = 650,
            isEquipped = false,
            upgradeLevel = 1
        ),
        GameItem(
            id = "wpn_graviton_1",
            name = "Singularity Cannon",
            description = "Experimental weapon distorting local gravity fields to crush enemy armor.",
            type = ItemType.WEAPON,
            rarity = ItemRarity.MYTHIC,
            attackBonus = 65,
            critBonusPercent = 0.28f,
            priceCredits = 1200,
            isEquipped = false,
            upgradeLevel = 1
        ),

        // Armors & Exoskeletons
        GameItem(
            id = "arm_exosuit_1",
            name = "Titan Mk.IV Exoskeleton",
            description = "Reinforced carbon-nanotube frame with pneumatic shock absorbers.",
            type = ItemType.ARMOR,
            rarity = ItemRarity.COMMON,
            defenseBonus = 18,
            shieldBonus = 40,
            priceCredits = 120,
            isEquipped = true,
            upgradeLevel = 1
        ),
        GameItem(
            id = "arm_holocloak_1",
            name = "Mirage Holo-Cloak",
            description = "Active light-bending optical camouflage conferring extreme agility and stealth.",
            type = ItemType.ARMOR,
            rarity = ItemRarity.EPIC,
            defenseBonus = 26,
            critBonusPercent = 0.15f,
            priceCredits = 420,
            isEquipped = false,
            upgradeLevel = 1
        ),
        GameItem(
            id = "arm_quantum_1",
            name = "Aegis Prime Quantum Plating",
            description = "Self-regenerating crystalline quantum alloy that reflects hostile projectiles.",
            type = ItemType.ARMOR,
            rarity = ItemRarity.LEGENDARY,
            defenseBonus = 48,
            shieldBonus = 100,
            priceCredits = 850,
            isEquipped = false,
            upgradeLevel = 1
        ),

        // Shields & Defense Modules
        GameItem(
            id = "shd_aegis_1",
            name = "Hex-Grid Deflector Shield",
            description = "Standard deployable energy matrix absorbing kinetic and laser rounds.",
            type = ItemType.SHIELD,
            rarity = ItemRarity.COMMON,
            shieldBonus = 60,
            defenseBonus = 8,
            priceCredits = 90,
            isEquipped = true,
            upgradeLevel = 1
        ),
        GameItem(
            id = "shd_vortex_1",
            name = "Vortex Singularity Shield",
            description = "Creates a micro magnetic vortex that absorbs and converts 30% damage to energy.",
            type = ItemType.SHIELD,
            rarity = ItemRarity.EPIC,
            shieldBonus = 140,
            energyRegenBonus = 15f,
            priceCredits = 520,
            isEquipped = false,
            upgradeLevel = 1
        ),

        // Tech Chips & Stimpacks
        GameItem(
            id = "chip_chronos_1",
            name = "Chronos Overclock Chip",
            description = "Neural coprocessor speeding up reflex latency and accelerating energy capacitors.",
            type = ItemType.TECH_CHIP,
            rarity = ItemRarity.RARE,
            attackBonus = 12,
            energyRegenBonus = 25f,
            priceCredits = 250,
            isEquipped = false,
            upgradeLevel = 1
        ),
        GameItem(
            id = "stim_nanite_1",
            name = "Hyper-Adrenaline Stimpack",
            description = "Military-grade nanite cocktail providing instant combat stabilization.",
            type = ItemType.STIMPACK,
            rarity = ItemRarity.COMMON,
            defenseBonus = 5,
            priceCredits = 60,
            isEquipped = false,
            upgradeLevel = 1
        )
    )
}
