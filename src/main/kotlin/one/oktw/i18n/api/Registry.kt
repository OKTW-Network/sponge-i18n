package one.oktw.i18n.api

import one.oktw.i18n.api.provider.TranslationStringProvider
import org.spongepowered.api.entity.living.player.Player
import java.util.*
import java.util.Arrays.asList
import java.util.concurrent.ConcurrentHashMap

const val DEFAULT_LANGUAGE = "en"

/**
 * Registry of player language setting and translation providers
 */
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

        val partial = locale.language + '-' + locale.country
        val minimal = locale.language

        asList(language, partial, minimal, DEFAULT_LANGUAGE).forEach {current->
            providers.forEach {
                val result =
                        it.get(current, key)

                if (result != null) {
                    cache[Pair(current, key)] = result
                    return result
                }
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