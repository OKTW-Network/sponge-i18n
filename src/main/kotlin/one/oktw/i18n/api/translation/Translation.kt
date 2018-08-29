package one.oktw.i18n.api.translation

import com.google.gson.JsonElement
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text
import java.util.*

interface Translation {
    fun toJsonObject(): JsonElement

    fun toLocalized(player: Player): String

    fun toLocalized(language: String): String

    fun toLocalized(locale: Locale): String

    fun toText(player: Player): Text

    fun toText(language: String): Text

    fun toText(locale: Locale): Text
}

