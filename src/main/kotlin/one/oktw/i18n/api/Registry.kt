package one.oktw.i18n.api

import one.oktw.i18n.Main
import one.oktw.i18n.api.provider.TranslationStringProvider
import org.spongepowered.api.entity.living.player.Player
import java.util.*
import java.util.Arrays.asList
import java.util.concurrent.ConcurrentHashMap

val DEFAULT_LANGUAGE = Locale.ENGLISH

/**
 * Registry of player language setting and translation providers
 */
class Registry private constructor() {
    companion object {
        val instance = Registry()
    }

    private val playerLanguage = ConcurrentHashMap<UUID, Locale>()
    private val cache = ConcurrentHashMap<Pair<String, String>, String>()
    private val providers: ArrayList<TranslationStringProvider> = ArrayList()

    @Suppress("unused")
    fun register(provider: TranslationStringProvider) {
        providers.add(provider)
    }

    fun get(locale: Locale, key: String): String {
        val language = locale.toLanguageTag()

        Main.main.logger.info("registry#get $language $key")

        val cached = cache[Pair(language, key)]

        if (cached != null) {
            return cached
        }

        providers.forEach {
            val result =
                    it.get(locale, key)

            if (result != null) {
                cache[Pair(language, key)] = result
                return result
            }
        }

        return key
    }

    @Suppress("unused")
    fun setLanguage(player: Player, locale: Locale) {
        playerLanguage[player.uniqueId] = locale
    }

    fun getLanguage(player: Player): Locale {
        return playerLanguage[player.uniqueId] ?: DEFAULT_LANGUAGE
    }
}