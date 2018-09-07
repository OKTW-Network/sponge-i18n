package one.oktw.i18n.impl.service

import one.oktw.i18n.Main.Companion.IDENTIFIER
import one.oktw.i18n.api.service.TranslationService
import one.oktw.i18n.impl.I18nImpl
import one.oktw.i18n.text.Helper
import one.oktw.i18n.translation.I18nTranslation
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.TranslatableText
import org.spongepowered.api.text.serializer.TextSerializers
import org.spongepowered.api.text.translation.Translation
import java.util.*

/**
 * A simple wrapper that allow other plugin to use
 * Usage:
 * ```java
 * TranslationServiceImpl lang;
 * Player player
 *
 * lang.ofPlaceHolder("tooltip.hasItem", "mmis1000", lang.of("item.sword"));
 * lang.ofLiteralPlaceHolder("mmis1000 has a", lang.of("item.sword"));
 * lang.of("item.sword")
 * lang.translation("item.sword")
 * lang.toPlain(player, lang.of("tooltip.hasItem", "mmis1000", lang.of("item.sword")));
 * lang.toLegacy(player, Text.of("mmis1000 has a", lang.of("item.sword")));
 * lang.toSponge(player, Text.of("tooltip.hasItem", "mmis1000", lang.of("item.sword")));
 * ```
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class TranslationServiceImpl(private val scope: String) : TranslationService {
    override fun fromItem(item: ItemStack): Text {
        return item.get(Keys.DISPLAY_NAME).orElse(null)?.let {
            val plain = it.toPlain()

            if (plain.startsWith(IDENTIFIER)) {
                val json = plain.drop(IDENTIFIER.length)

                try {
                    TextSerializers.JSON.deserialize(json)
                } catch (err: Throwable) {
                    it
                }
            } else {
                it
            }
        } ?: let {
            Text.of(item.translation)
        }
    }

    override fun ofLiteralPlaceHolder(vararg values: Any): Text {
        return ofPlaceHolder(*values)
    }

    override fun ofPlaceHolder(vararg values: Any): Text {
        return Text.of(IDENTIFIER + TextSerializers.JSON.serialize(Text.of(*values)))
    }

    override fun ofPlaceHolder(key: String, vararg values: Any): Text {
        return Text.of(IDENTIFIER + TextSerializers.JSON.serialize(of(key, *values)))
    }

    override fun ofPlaceHolderUnscoped(key: String, vararg values: Any): Text {
        return Text.of(IDENTIFIER + TextSerializers.JSON.serialize(ofUnscoped(key, *values)))
    }

    override fun of(key: String, vararg values: Any): Text {
        return TranslatableText.of(I18nTranslation("$scope:$key"), *values)
    }

    override fun ofUnscoped(key: String, vararg values: Any): Text {
        return TranslatableText.of(I18nTranslation(key), *values)
    }

    override fun translation(key: String): Translation {
        return I18nTranslation("$scope:$key")
    }

    override fun translationUnscoped(key: String): Translation {
        return I18nTranslation(key)
    }

    override fun toPlain(player: Player, text: Text): String {
        return toPlain(I18nImpl.registry.getLanguage(player), text)
    }

    override fun toPlain(locale: Locale, text: Text): String {
        return I18nImpl.getPlainTextSerializer(locale).serialize(text)
    }

    override fun toLegacy(player: Player, text: Text): String {
        return toLegacy(I18nImpl.registry.getLanguage(player), text)
    }

    override fun toLegacy(locale: Locale, text: Text): String {
        return I18nImpl.getLegacySerializer(locale).serialize(text)
    }

    override fun toSponge(player: Player, text: Text): String {
        return toSponge(I18nImpl.registry.getLanguage(player), text)
    }

    override fun toSponge(locale: Locale, text: Text): String {
        return I18nImpl.getSpongeSerializer(locale).serialize(text)
    }

    override fun removeStyle(text: Text): Text {
        return Helper.removeStyle(text)
    }
}