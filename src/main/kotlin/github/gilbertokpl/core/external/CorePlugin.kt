package github.gilbertokpl.core.external

import github.gilbertokpl.core.external.cache.Cache
import github.gilbertokpl.core.external.config.Config
import github.gilbertokpl.core.external.config.values.Value
import github.gilbertokpl.core.external.task.Task
import github.gilbertokpl.core.external.utils.*
import github.gilbertokpl.core.internal.events.Events
import github.gilbertokpl.core.internal.serializator.AES
import org.bukkit.plugin.Plugin
import org.jetbrains.exposed.sql.Table

class CorePlugin(pl: Plugin) {

    val plugin = pl

    lateinit var sql: org.jetbrains.exposed.sql.Database

    private lateinit var configPackageReload: String

    lateinit var mainPath: String

    lateinit var langPath: String

    var online = System.currentTimeMillis()

    var serverPrefix = ""

    //obf
    private val timeInstance = Time(this)

    //obf
    private val colorInstance = Color(this)

    //obf
    private val reflectionInstance = Reflection(this)

    //obf
    private val databaseInstance = Database(this)

    //obf
    private val configInstance = Config(this)

    //deobf
    private val valueInstance = Value(this)

    //deobf
    private val taskInstance = Task(this)

    //obf
    private val hostInstance = Host(this)

    //deobf
    private val cacheInstance = Cache(this)

    //obf
    private val inventoryInstance = Inventory(this)

    //obf
    private val encryptInstance = Encrypt(this)

    fun start(
        commandPackage: String,
        ListenerPackage: String,
        configPackage: String,
        cachePackage: String,
        databaseTable: List<Table>
    ) {
        mainPath = plugin.dataFolder.path
        langPath = plugin.dataFolder.path + "/lang/"
        configPackageReload = configPackage
        getConfig().start(configPackage)
        getDatabase().start(databaseTable)
        getReflection().registerCommandByPackage(commandPackage)
        Events(this).start(ListenerPackage)
        getCache().start(cachePackage)
        getHost().start()
    }

    fun reloadConfig(): Boolean {
        return try {
            getConfig().start(configPackageReload)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun stop() {
        getCache().stop()
    }

    fun getTime(): Time {
        return timeInstance
    }

    fun getColor(): Color {
        return colorInstance
    }

    fun getReflection(): Reflection {
        return reflectionInstance
    }

    fun getDatabase(): Database {
        return databaseInstance
    }

    fun getConfig(): Config {
        return configInstance
    }

    fun getValue(): Value {
        return valueInstance
    }

    fun getTask(): Task {
        return taskInstance
    }

    fun getHost(): Host {
        return hostInstance
    }

    fun getCache(): Cache {
        return cacheInstance
    }

    fun getInventory(): Inventory {
        return inventoryInstance
    }

    fun getEncrypt(): Encrypt {
        return encryptInstance
    }


}