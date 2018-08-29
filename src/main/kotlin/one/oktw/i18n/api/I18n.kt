package one.oktw.i18n.api

import one.oktw.i18n.api.provider.TranslationStringProvider
import one.oktw.i18n.api.service.TranslationService
import one.oktw.i18n.api.translation.Translation

interface I18n {
    val registry: Registry
    // get non scoped translation string
    fun rawPlaceHolder(key: String, vararg values: Any): String
    // get non scoped translation string
    fun literalPlaceHolder(vararg values: Any): String
    // get non scoped translation
    fun rawTranslation(key: String, vararg values: Any): Translation
    // get literal translation
    fun literalTranslation(vararg values: Any): Translation
    // provide provider to the registry and get own translation service
    fun register(scope: String, provider: TranslationStringProvider): TranslationService
}