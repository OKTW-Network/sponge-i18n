package one.oktw.i18n.api

import one.oktw.i18n.api.provider.TranslationStringProvider
import one.oktw.i18n.api.provider.TranslationStringProvider.Companion.ScopedTranslationStringProvider
import one.oktw.i18n.api.service.TranslationService
import one.oktw.i18n.api.translation.KeyTranslation
import one.oktw.i18n.api.translation.LiteralTranslation
import one.oktw.i18n.translation.Helper

object I18nImpl : I18n {
    private val registered = ArrayList<String>()

    override val registry = Registry.instance
    override fun rawPlaceHolder(key: String, vararg values: Any) = Helper.placeholder(key, *values)
    override fun literalPlaceHolder(vararg values: Any) = Helper.literalPlaceHolder(*values)
    override fun rawTranslation(key: String, vararg values: Any) = KeyTranslation(key, *values)
    override fun literalTranslation(vararg values: Any) = LiteralTranslation(*values)
    override fun register(scope: String, provider: TranslationStringProvider): TranslationService {
        if (registered.contains(scope)) {
            throw Error("already registered provider for $scope")
        }

        if (scope.startsWith("\$")) {
            throw Error("preserved leading \$ in $scope")
        }

        registered += scope

        registry.register(ScopedTranslationStringProvider(scope, provider))

        return TranslationService(scope)
    }
}