package one.oktw.i18n.translation

import one.oktw.i18n.api.I18nImpl
import org.spongepowered.api.text.translation.Translation
import java.util.*

class I18nTranslation(private var id: String) : Translation {
    override fun getId(): String {
        return this.id
    }

    override fun get(locale: Locale) = I18nImpl.registry.get(locale, id)

    override fun get(locale: Locale, vararg args: Any?) = I18nImpl.registry.get(locale, id).format(*args)
}