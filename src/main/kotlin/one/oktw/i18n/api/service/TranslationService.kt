package one.oktw.i18n.api.service

import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.translation.Translation
import java.util.*

interface TranslationService {
    // get real item name of an ItemStack
    fun fromItem(item: ItemStack): Text

    // place holders
    fun ofLiteralPlaceHolder(vararg values: Any): Text
    fun ofPlaceHolder(vararg values: Any): Text
    fun ofPlaceHolder(key: String, vararg values: Any): Text
    fun ofPlaceHolderUnscoped(key: String, vararg values: Any): Text

    // normal Texts
    fun of(key: String, vararg values: Any): Text
    fun ofUnscoped(key: String, vararg values: Any): Text

    // translations
    fun translation(key: String): Translation
    fun translationUnscoped(key: String): Translation

    // serializers
    fun toPlain(player: Player, text: Text): String
    fun toPlain(locale: Locale, text: Text): String
    fun toLegacy(player: Player, text: Text): String
    fun toLegacy(locale: Locale, text: Text): String
    fun toSponge(player: Player, text: Text): String
    fun toSponge(locale: Locale, text: Text): String
}