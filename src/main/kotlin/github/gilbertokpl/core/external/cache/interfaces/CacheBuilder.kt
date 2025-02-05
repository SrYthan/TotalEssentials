package github.gilbertokpl.core.external.cache.interfaces

import org.bukkit.entity.Player

interface CacheBuilder<T> {

    fun update()

    fun load()

    fun getMap(): HashMap<String, T?>

    operator fun get(entity: String): T?

    operator fun get(entity: Player): T?

    operator fun set(entity: String, value: T)

    operator fun set(entity: Player, value: T)

    fun delete(entity: String)

    fun delete(entity: Player)

}