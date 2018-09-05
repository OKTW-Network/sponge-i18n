package one.oktw.i18n.api.provider

import java.util.*

interface TranslationStringProvider {
    companion object {
        class ScopedTranslationStringProvider(private val scope: String, private val provider: TranslationStringProvider): TranslationStringProvider {
            override fun get(locale: Locale, key: String): String? {
                return if (key.startsWith("$scope:")) {
                    provider.get(locale, key.drop(scope.length + 1))
                } else {
                    null
                }
            }
        }
    }

    // get the translation key from key
    fun get(locale: Locale, key: String): String?
}