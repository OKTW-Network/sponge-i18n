package one.oktw.i18n.api.provider

interface TranslationStringProvider {
    companion object {
        class ScopedTranslationStringProvider(private val scope: String, private val provider: TranslationStringProvider): TranslationStringProvider {
            override fun get(language: String, key: String): String? {
                return if (key.startsWith("$scope:")) {
                    provider.get(language, key.drop(scope.length + 1))
                } else {
                    null
                }
            }
        }
    }

    // get the translation key from key
    fun get(language: String, key: String): String?
}