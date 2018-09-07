package one.oktw.i18n.impl

import one.oktw.i18n.api.I18n
import one.oktw.i18n.api.Registry
import one.oktw.i18n.impl.provider.ScopedTranslationStringProvider
import one.oktw.i18n.api.provider.TranslationStringProvider
import one.oktw.i18n.impl.serializer.LocalizedFomattingCodeSerializer
import one.oktw.i18n.impl.serializer.LocalizedPlainTextSerializer
import one.oktw.i18n.impl.service.TranslationServiceImpl
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

    override fun getRegistry(): Registry {
        return RegistryImpl.instance
    }

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