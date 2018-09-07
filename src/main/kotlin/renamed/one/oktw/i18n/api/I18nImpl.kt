package renamed.one.oktw.i18n.api

import renamed.one.oktw.i18n.api.provider.TranslationStringProvider
import renamed.one.oktw.i18n.api.provider.TranslationStringProvider.Companion.ScopedTranslationStringProvider
import renamed.one.oktw.i18n.api.serializer.LocalizedFomattingCodeSerializer
import renamed.one.oktw.i18n.api.serializer.LocalizedPlainTextSerializer
import renamed.one.oktw.i18n.api.service.TranslationServiceImpl
import org.spongepowered.api.text.serializer.TextSerializer
import java.util.*

object I18nImpl : I18n {
    private val registered = ArrayList<String>()

    override fun getLegacySerializer(locale: Locale): TextSerializer {
        return LocalizedFomattingCodeSerializer(locale, '§')
    }

    override fun getSpongeSerializer(locale: Locale): TextSerializer {
        return LocalizedFomattingCodeSerializer(locale, '&')
    }

    override fun getPlainTextSerializer(locale: Locale): TextSerializer {
        return LocalizedPlainTextSerializer(locale)
    }

    override val registry = Registry.instance

    override fun register(scope: String, provider: TranslationStringProvider, allowSameScopeExist: Boolean): TranslationServiceImpl {
        if (registered.contains(scope) && !allowSameScopeExist) {
            throw Error("already registered provider for $scope")
        }

        if (scope.startsWith("\$")) {
            throw Error("preserved leading \$ in $scope")
        }

        registered += scope

        registry.register(ScopedTranslationStringProvider(scope, provider))

        return TranslationServiceImpl(scope)
    }
}