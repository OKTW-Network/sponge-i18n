package one.oktw.i18n.api

import org.spongepowered.api.entity.living.player.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

const val DEFAULT_LANGUAGE = "en"

class Registry private constructor() {
    companion object {
        val instance = Registry()
    }

    private val playerLanguage = ConcurrentHashMap<UUID, String>()
    private val cache = ConcurrentHashMap<Pair<String, String>, String>()
    private val providers: ArrayList<TranslationStringProvider> = ArrayList()

    @Suppress("unused")
    fun register(provider: TranslationStringProvider) {
        providers.add(provider)
    }

    fun get(language: String, key: String): String {
        val cached = cache[Pair(language, key)]

        if (cached != null) {
            return cached
        }

        val locale = Locale.forLanguageTag(language)

        val partial = locale.language + '-' + locale.variant
        val minimal = locale.language

        providers.forEach {
            val result = it.get(language, key) ?: it.get(partial, key) ?: it.get(minimal, key)

            if (result != null) {
                cache[Pair(language, key)] = result
                return result
            }
        }

        return key
    }

    @Suppress("unused")
    fun setLanguage(player: Player, language: String) {
        playerLanguage[player.uniqueId] = language
    }

    fun getLanguage(player: Player): String {
        return playerLanguage[player.uniqueId] ?: DEFAULT_LANGUAGE
    }
}