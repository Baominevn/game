package com.example.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "player_profile")
data class PlayerEntity(
    @PrimaryKey val id: Int = 1,
    val level: Int = 1,
    val currentXp: Int = 0,
    val credits: Int = 350,
    val nanites: Int = 25,
    val maxHp: Int = 500,
    val currentHp: Int = 500,
    val maxShield: Int = 200,
    val currentShield: Int = 200,
    val baseAttack: Int = 45,
    val baseDefense: Int = 20,
    val equippedWeaponId: String = "wpn_blade_1",
    val equippedArmorId: String = "arm_exosuit_1",
    val equippedShieldId: String = "shd_aegis_1",
    val stateSignature: String = "",
    val lastSyncTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "inventory_items")
data class InventoryItemEntity(
    @PrimaryKey val itemId: String,
    val name: String,
    val type: String,
    val rarity: String,
    val attackBonus: Int,
    val defenseBonus: Int,
    val shieldBonus: Int,
    val critBonusPercent: Float,
    val upgradeLevel: Int,
    val isEquipped: Boolean
)

@Entity(tableName = "battle_history")
data class BattleHistoryEntity(
    @PrimaryKey(autoGenerate = true) val battleId: Long = 0,
    val enemyName: String,
    val outcome: String, // "VICTORY" or "DEFEAT"
    val creditsGained: Int,
    val xpGained: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface CyberDao {
    @Query("SELECT * FROM player_profile WHERE id = 1 LIMIT 1")
    fun getPlayerProfile(): Flow<PlayerEntity?>

    @Query("SELECT * FROM player_profile WHERE id = 1 LIMIT 1")
    suspend fun getPlayerProfileSync(): PlayerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePlayerProfile(profile: PlayerEntity)

    @Query("SELECT * FROM inventory_items")
    fun getAllInventory(): Flow<List<InventoryItemEntity>>

    @Query("SELECT * FROM inventory_items")
    suspend fun getAllInventorySync(): List<InventoryItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveInventoryItem(item: InventoryItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveInventoryItems(items: List<InventoryItemEntity>)

    @Query("UPDATE inventory_items SET isEquipped = :isEquipped WHERE itemId = :itemId")
    suspend fun updateEquippedStatus(itemId: String, isEquipped: Boolean)

    @Query("SELECT * FROM battle_history ORDER BY timestamp DESC LIMIT 20")
    fun getRecentBattles(): Flow<List<BattleHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBattleRecord(record: BattleHistoryEntity)
}

@Database(
    entities = [PlayerEntity::class, InventoryItemEntity::class, BattleHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CyberDatabase : RoomDatabase() {
    abstract fun cyberDao(): CyberDao
}
