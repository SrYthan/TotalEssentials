package github.gilbertokpl.total.commands

import github.gilbertokpl.core.external.command.CommandTarget
import github.gilbertokpl.core.external.command.annotations.CommandPattern
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.cache.local.PlayerData
import github.gilbertokpl.total.cache.local.WarpData
import github.gilbertokpl.total.util.TaskUtil
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandWarp : github.gilbertokpl.core.external.command.CommandCreator("warp") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("warps"),
            active = MainConfig.warpsActivated,
            target = CommandTarget.ALL,
            countdown = 0,
            permission = "totalessentials.commands.warp",
            minimumSize = 0,
            maximumSize = 2,
            usage = listOf(
                "P_/warp <warpName>",
                "totalessentials.commands.warp.other_/warp <playerName> <warpName>"
            )
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {

        val p = if (s is Player) {
            s
        } else {
            null
        }

        if (args.isEmpty()) {
            s.sendMessage(
                LangConfig.warpsList.replace(
                    "%list%",
                    WarpData.getList(p).toString()
                )
            )
            return false
        }

        if (p == null || (args.size == 2 && p.hasPermission("totalessentials.commands.warp.other"))) {
            val newPlayer = Bukkit.getPlayer(args[0].lowercase()) ?: return true

            val warpName = args[1].lowercase()

            //check if not exist
            if (!WarpData.checkIfWarpExist(warpName)) {
                s.sendMessage(
                    LangConfig.warpsList.replace(
                        "%list%",
                        WarpData.getList(null).toString()
                    )
                )
                return false
            }

            newPlayer.teleport(WarpData.warpLocation[warpName]!!)

            newPlayer.sendMessage(
                LangConfig.warpsTeleportedOtherMessage.replace(
                    "%warp%",
                    warpName
                )
            )
            s.sendMessage(
                LangConfig.warpsTeleportedOtherSuccess
                    .replace("%warp%", warpName)
                    .replace("%player%", newPlayer.name.lowercase())
            )

            return false
        }

        //check length of warp name
        if (args[0].length > 16) {
            s.sendMessage(LangConfig.warpsNameLength)
            return false
        }

        val warpName = args[0].lowercase()

        if (!WarpData.checkIfWarpExist(warpName)) {
            p.sendMessage(
                LangConfig.warpsList.replace(
                    "%list%",
                    WarpData.getList(p).toString()
                )
            )
            return false
        }


        if (!p.hasPermission("totalessentials.commands.warp.$warpName")) {
            p.sendMessage(LangConfig.generalNotPerm)
            return false
        }

        val time = MainConfig.warpsTimeToTeleport

        if (p.hasPermission("totalessentials.bypass.teleport") || time == 0) {
            p.teleport(WarpData.warpLocation[warpName]!!)
            p.sendMessage(
                LangConfig.warpsTeleported.replace(
                    "%warp%",
                    warpName
                )
            )
            return false
        }

        val inTeleport = PlayerData.inTeleport[p]
        if (inTeleport != null && inTeleport) {
            p.sendMessage(LangConfig.homesInTeleport)
            return false
        }

        val exe = TaskUtil.teleportExecutor(time)

        PlayerData.inTeleport[p] = true

        exe {
            PlayerData.inTeleport[p] = false
            val loc = WarpData.warpLocation[warpName] ?: return@exe
            p.teleport(loc)
            s.sendMessage(
                LangConfig.warpsTeleported.replace(
                    "%warp%",
                    warpName
                )
            )
        }

        s.sendMessage(
            LangConfig.warpsTimeToTeleport.replace("%warp%", warpName)
                .replace("%time%", time.toString())
        )

        return false
    }
}
