package one.oktw.i18n.test

import one.oktw.i18n.api.provider.TranslationStringProvider
import java.util.*

class TestTranslationProvider private constructor(): TranslationStringProvider {
    companion object {
        val instance = TestTranslationProvider()
    }

    override fun get(language: String, key: String): String? {
        val languageName = Locale.forLanguageTag(language).language

        return when (languageName) {
            "en"-> when (key) {
                "tooltip.item_owned"-> "%s's %s"
                "item.sword"-> "Sword"
                "tooltip.description"->"Description"
                else -> null
            }
            "zh"-> when (key) {
                "tooltip.item_owned"-> "%s 的 %s"
                "item.sword"-> "劍"
                "tooltip.description"->"描述"
                else -> null
            }
            else -> {
                null
            }
        }
    }
}