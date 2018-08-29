package one.oktw.i18n.api.service

import one.oktw.i18n.api.translation.KeyTranslation
import one.oktw.i18n.api.translation.LiteralTranslation
import one.oktw.i18n.api.translation.Translation
import one.oktw.i18n.translation.Helper
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text

/**
 * A simple wrapper that allow other plugin to use
 * Usage:
 * ```java
 * TranslationService lang;
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
@Suppress("unused")
class TranslationService(private val scope: String) {
    fun of(key: String, vararg values: Any): Text {
        return Text.of(Helper.placeholder("$scope:$key", *values))
    }

    fun ofLiteral(vararg values: Any): Text {
        return Text.of(Helper.literalPlaceHolder(*values))
    }

    fun sub(key: String, vararg values: Any): Translation {
        return KeyTranslation("$scope:$key", *values)
    }

    fun subText(vararg values: Any): Translation {
        return LiteralTranslation(*values)
    }

    fun toLocalized(player: Player, key: String, vararg values: Any): String {
        return KeyTranslation("$scope:$key", *values).toLocalized(player)
    }

    fun toLocalizedLiteral(player: Player, vararg values: Any): String {
        return LiteralTranslation(*values).toLocalized(player)
    }

    fun toLocalizedText(player: Player, key: String, vararg values: Any): Text {
        return Text.of(KeyTranslation("$scope:$key", *values).toLocalized(player))
    }

    fun toLocalizedLiteralText(player: Player, vararg values: Any): Text {
        return Text.of(LiteralTranslation(*values).toLocalized(player))
    }
}