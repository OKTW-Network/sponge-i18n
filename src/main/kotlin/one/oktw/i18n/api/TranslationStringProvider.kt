package one.oktw.i18n.api

interface TranslationStringProvider {
    companion object {
        fun wrap(lambda: (language: String, key: String) -> String): TranslationStringProvider =
                object : TranslationStringProvider {
                    override fun get(language: String, key: String): String = lambda.invoke(language, key)
                }
    }

    // get the translation key from key
    fun get(language: String, key: String): String?
}