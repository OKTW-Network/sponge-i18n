package one.oktw.i18n.impl

import one.oktw.i18n.api.Registry
import one.oktw.i18n.api.provider.TranslationStringProvider
import org.spongepowered.api.entity.living.player.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

val DEFAULT_LANGUAGE = Locale.ENGLISH

/**
 * Registry of player language setting and translation providers
 */
class RegistryImpl: Registry {
    companion object {
        val instance = RegistryImpl()
    }

    private val playerLanguage = ConcurrentHashMap<UUID, Locale>()
    private val cache = ConcurrentHashMap<Pair<String, String>, String>()
    private val providers: ArrayList<TranslationStringProvider> = ArrayList()

    @Suppress("unused")
    override fun register(provider: TranslationStringProvider) {
        providers.add(provider)
    }

    override fun get(locale: Locale, key: String): String {
        val language = locale.toLanguageTag()

        val cached = cache[Pair(language, key)]

        if (cached != null) {
            return cached
        }

        // find translation by locale
        providers.forEach {
            val result =
                    it.get(locale, key)

            if (result != null) {
                cache[Pair(language, key)] = result
                return result
            }
        }

        // fallback ti default language, gave up if we still can't get anything matched
        providers.forEach {
            val result =
                    it.get(DEFAULT_LANGUAGE, key)

            if (result != null) {
                cache[Pair(language, key)] = result
                return result
            }
        }

        // gave up, just return the original key back
        return key
    }

    @Suppress("unused")
    override fun setLanguage(player: Player, locale: Locale) {
        playerLanguage[player.uniqueId] = locale
    }

    override fun getLanguage(player: Player): Locale {
        return playerLanguage[player.uniqueId] ?: DEFAULT_LANGUAGE
    }
}