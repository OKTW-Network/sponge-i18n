package one.oktw.i18n.impl.provider

import one.oktw.i18n.api.provider.TranslationStringProvider
import java.util.*

class ScopedTranslationStringProvider(private val scope: String, private val provider: TranslationStringProvider): TranslationStringProvider {
    override fun get(locale: Locale, key: String): String? {
        return if (key.startsWith("$scope:")) {
            provider.get(locale, key.drop(scope.length + 1))
        } else {
            null
        }
    }
}