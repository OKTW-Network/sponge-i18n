package renamed.one.oktw.i18n.api

import renamed.one.oktw.i18n.api.provider.TranslationStringProvider
import renamed.one.oktw.i18n.api.service.TranslationService
import org.spongepowered.api.text.serializer.TextSerializer
import java.util.*

interface I18n {
    val registry: Registry

    // get locale aware legacy formatting code serializer
    fun getLegacySerializer(locale: Locale): TextSerializer
    // get locale aware sponge formatting code text serializer
    fun getSpongeSerializer(locale: Locale): TextSerializer
    // get locale aware plain text serializer
    fun getPlainTextSerializer(locale: Locale): TextSerializer

    fun register(scope: String, provider: TranslationStringProvider, allowSameScopeExist: Boolean = false): TranslationService
}