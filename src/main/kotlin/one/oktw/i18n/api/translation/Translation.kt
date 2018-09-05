package one.oktw.i18n.api.translation

import com.google.gson.JsonElement
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text
import java.util.*

@Suppress("unused")
interface Translation {
    fun toJsonObject(): JsonElement

    fun fromJsonObject(element: JsonElement): Translation

    fun toLocalized(player: Player): String

    fun toLocalized(language: String): String

    fun toLocalized(locale: Locale): String

    fun toText(player: Player): Text = Text.of(toLocalized(player))

    fun toText(language: String): Text = Text.of(toLocalized(language))

    fun toText(locale: Locale): Text = Text.of(toLocalized(locale))
}

