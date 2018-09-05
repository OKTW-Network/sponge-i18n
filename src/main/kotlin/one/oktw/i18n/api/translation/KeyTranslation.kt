package one.oktw.i18n.api.translation

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.sun.javaws.exceptions.InvalidArgumentException
import one.oktw.i18n.api.Registry
import one.oktw.i18n.translation.Helper
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColor
import org.spongepowered.api.text.format.TextStyle
import org.spongepowered.api.text.serializer.TextSerializers
import org.spongepowered.api.text.translation.Translatable
import java.util.*

/**
 * Represent A nested translatable Text
 */
class KeyTranslation(private val key: String, private vararg val values: Any) : Translation {
    override fun fromJsonObject(element: JsonElement): Translation {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun toJsonObject(): JsonElement {
        val obj = JsonObject()
        obj.add("key", JsonPrimitive(key))

        if (values.isNotEmpty()) {
            val valueArray = JsonArray()
            obj.add("values", valueArray)

            values.forEach { arg ->
                valueArray.add(when (arg) {
                    is ItemStack -> Helper.convertTranslation(arg)
                    is Translatable -> Helper.convertTranslation(arg)
                    is org.spongepowered.api.text.translation.Translation -> Helper.convertTranslation(arg)
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
}