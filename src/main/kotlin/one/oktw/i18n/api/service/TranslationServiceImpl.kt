package one.oktw.i18n.api.service

import one.oktw.i18n.Main.Companion.IDENTIFIER
import one.oktw.i18n.api.I18nImpl
import one.oktw.i18n.translation.I18nTranslation
import org.spongepowered.api.entity.living.player.Player
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
 * lang.of("tooltip.hasItem", "mmis1000", lang.sub("item.sword"));
 * lang.ofLiteral("mmis1000 has a", lang.sub("item.sword"));
 * lang.sub("item.sword")
 * lang.subText(l"\\", lang.sub(item.sword"),"/");
 * lang.toLocalized(player, "tooltip.hasItem", "mmis1000", lang.sub("item.sword"));
 * lang.toLocalizedLiteral(player, "mmis1000 has a", lang.sub("item.sword"));
 * lang.toLocalizedText(player, "tooltip.hasItem", "mmis1000", lang.sub("item.sword"));
 * lang.toLocalizedLiteralText(player, "mmis1000 has a", lang.sub("item.sword"));
 * ```
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class TranslationServiceImpl(private val scope: String) : TranslationService {
    override fun ofLiteralPlaceHolder(vararg values: Any): Text {
        return ofPlaceHolder(*values)
    }

    override fun ofPlaceHolder(vararg values: Any): Text {
        return Text.of(IDENTIFIER +  TextSerializers.JSON.serialize(Text.of(*values)))
    }

    override fun ofPlaceHolder(key: String, vararg values: Any): Text {
        return Text.of(IDENTIFIER +  TextSerializers.JSON.serialize(of(key, *values)))
    }

    override fun ofPlaceHolderUnscoped(key: String, vararg values: Any): Text {
        return Text.of(IDENTIFIER +  TextSerializers.JSON.serialize(ofUnscoped(key, *values)))
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
}