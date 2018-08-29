package one.oktw.i18n.api.translation

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.sun.javaws.exceptions.InvalidArgumentException
import one.oktw.i18n.api.Registry
import one.oktw.i18n.translation.Helper
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColor
import org.spongepowered.api.text.format.TextStyle
import org.spongepowered.api.text.serializer.TextSerializers
import java.util.*

/**
 * Represent A literal Text
 */
class LiteralTranslation(private vararg val values: Any) : Translation {
    override fun toJsonObject(): JsonElement {
        val obj = JsonObject()
        obj.add("key", JsonPrimitive("\$literal"))

        if (values.isNotEmpty()) {
            val valueArray = JsonArray()
            obj.add("values", valueArray)

            values.forEach { arg ->
                valueArray.add(when (arg) {
                    is Text -> JsonPrimitive(TextSerializers.LEGACY_FORMATTING_CODE.serialize(arg).replace("h", ""))
                    is TextStyle -> JsonPrimitive(TextSerializers.LEGACY_FORMATTING_CODE.serialize(Text.of(arg, "h")).replace("h", ""))
                    is TextColor -> JsonPrimitive(TextSerializers.LEGACY_FORMATTING_CODE.serialize(Text.of(arg, "h")).replace("h", ""))
                    is String -> JsonPrimitive(arg)
                    is Translation -> arg.toJsonObject()
                    else -> throw InvalidArgumentException(arrayOf("Expect only String and Translation ar arguments"))
                })
            }
        }

        return obj
    }

    override fun toLocalized(player: Player): String {
        return Helper.translate(Registry.instance.getLanguage(player), toJsonObject())!!
    }

    override fun toLocalized(language: String): String {
        return Helper.translate(language, toJsonObject())!!
    }

    override fun toLocalized(locale: Locale): String {
        return Helper.translate(locale.toLanguageTag(), toJsonObject())!!
    }

    override fun toText(player: Player): Text = Text.of(toLocalized(player))

    override fun toText(language: String): Text = Text.of(toLocalized(language))

    override fun toText(locale: Locale): Text = Text.of(toLocalized(locale))
}