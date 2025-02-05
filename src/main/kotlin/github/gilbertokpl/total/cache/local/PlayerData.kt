package github.gilbertokpl.total.cache.local

import github.gilbertokpl.core.external.cache.interfaces.CacheBase
import github.gilbertokpl.total.cache.serializer.HomeSerializer
import github.gilbertokpl.total.cache.serializer.KitSerializer
import github.gilbertokpl.total.cache.serializer.LocationSerializer
import github.gilbertokpl.total.cache.sql.PlayerDataSQL
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.MainUtil
import github.gilbertokpl.total.util.PlayerUtil
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object PlayerData : CacheBase {
    override var table: Table = PlayerDataSQL
    override var primaryColumn: Column<String> = PlayerDataSQL.playerTable

    private val ins = github.gilbertokpl.total.TotalEssentials.basePlugin.getCache()


    val kitsCache = ins.stringHashMap(this, PlayerDataSQL.kitsTable, KitSerializer())
    val homeCache = ins.stringHashMap(this, PlayerDataSQL.homeTable, HomeSerializer())
    val nickCache = ins.string(this, PlayerDataSQL.nickTable)
    val gameModeCache = ins.integer(this, PlayerDataSQL.gameModeTable)
    val vanishCache = ins.boolean(this, PlayerDataSQL.vanishTable)
    val lightCache = ins.boolean(this, PlayerDataSQL.lightTable)
    val flyCache = ins.boolean(this, PlayerDataSQL.flyTable)
    val backLocation = ins.location(this, PlayerDataSQL.backTable, LocationSerializer())
    val speedCache = ins.integer(this, PlayerDataSQL.speedTable)
    val inInvsee = ins.simplePlayer()
    val homeLimitCache = ins.simpleInteger()
    val inTeleport = ins.simpleBoolean()

    fun checkIfPlayerExist(entity: String) : Boolean {
        return nickCache[entity.lowercase()] != null
    }

    fun checkIfPlayerExist(entity: Player) : Boolean {
        return nickCache[entity] != null
    }

    fun createNewPlayerData(entity: String) {
        kitsCache[entity] = HashMap()
        homeCache[entity] = HashMap()
        nickCache[entity] = ""
        gameModeCache[entity] = 0
        vanishCache[entity] = false
        lightCache[entity] = false
        flyCache[entity] = false
        backLocation[entity] = SpawnData.spawnLocation["spawn"]
            ?: Location(github.gilbertokpl.total.TotalEssentials.instance.server.getWorld("world"), 1.0, 1.0, 1.0)
        speedCache[entity] = 1
    }

    fun values(p: Player) {

        val nick = nickCache[p]

        if (nick != "" && nick != p.displayName && p.hasPermission("totalessentials.commands.nick")) {
            p.setDisplayName(nick)
        }

        val gameModeName = PlayerUtil.getGamemodeNumber(gameModeCache[p].toString())

        if (p.gameMode != gameModeName) {
            p.gameMode = gameModeName
        }

        if (vanishCache[p]!!) {
            p.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, Int.MAX_VALUE, 1))
            for (it in github.gilbertokpl.total.TotalEssentials.basePlugin.getReflection().getPlayers()) {
                if (it.player!!.hasPermission("totalessentials.commands.vanish")
                    || it.player!!.hasPermission("totalessentials.bypass.vanish")
                ) {
                    continue
                }
                @Suppress("DEPRECATION")
                it.hidePlayer(p)
            }
        }

        if (lightCache[p]!!) {
            p.addPotionEffect(PotionEffect(PotionEffectType.NIGHT_VISION, Int.MAX_VALUE, 1))
        }

        if (flyCache[p]!!) {
            p.allowFlight = true
            p.isFlying = true
        }

        val speed = speedCache[p]!!

        if (speed != 1) {
            p.walkSpeed = (speed * 0.1).toFloat()
            p.flySpeed = (speed * 0.1).toFloat()
        }

        if (MainConfig.vanishActivated) {
            if (p.hasPermission("totalessentials.commands.vanish") ||
                p.hasPermission("totalessentials.bypass.vanish")
            ) return
            for (it in github.gilbertokpl.total.TotalEssentials.basePlugin.getReflection().getPlayers()) {
                if (vanishCache[it] ?: continue) {
                    @Suppress("DEPRECATION")
                    p.hidePlayer(it)
                }
            }
        }

    }
}