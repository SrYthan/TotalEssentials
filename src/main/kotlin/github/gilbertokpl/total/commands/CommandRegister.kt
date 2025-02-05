package github.gilbertokpl.total.commands

import github.gilbertokpl.core.external.command.CommandTarget
import github.gilbertokpl.core.external.command.annotations.CommandPattern
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.local.LoginData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandRegister : github.gilbertokpl.core.external.command.CommandCreator("register") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("registrar"),
            active = MainConfig.authActivated,
            target = CommandTarget.ALL,
            countdown = 0,
            permission = "totalessentials.commands.register",
            minimumSize = 2,
            maximumSize = 2,
            usage = listOf(
                "P_/registrar <senha> <senha>",
                "totalessentials.commands.register.other_/registrar <player> <senha>",
            )
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {

        val encrypt = TotalEssentials.basePlugin.getEncrypt()

        if (s is Player && !LoginData.checkIfPlayerExist(s)) {
            if (args[0] != args[1]) {
                s.sendMessage(LangConfig.authDifferentPasswords)
                return false
            }

            if (args[0].length >= 16) {
                s.sendMessage(LangConfig.authPasswordLength)
                return false
            }

            var quant = 0

            val playerAddress = s.address?.address.toString()

            for (i in LoginData.ip.getMap().values) {
                if (i == playerAddress) {
                    quant += 1
                }
            }

            if (quant >= MainConfig.authMaxRegister) {
                s.sendMessage(LangConfig.authMaxRegister.replace("%max%", quant.toString()))
                return false
            }

            LoginData.createNewLoginData(s.name.lowercase(), encrypt.encrypt(args[0]), playerAddress)

            s.sendMessage(LangConfig.authRegisterSuccess)

        }
        return false
    }
}