package one.oktw.i18n.translation

import com.google.gson.*
import com.sun.javaws.exceptions.InvalidArgumentException
import one.oktw.i18n.Main.Companion.IDENTIFIER
import one.oktw.i18n.api.Registry
import one.oktw.i18n.api.translation.KeyTranslation
import one.oktw.i18n.api.translation.LiteralTranslation
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.text.translation.Translatable
import org.spongepowered.api.text.translation.Translation
import java.text.MessageFormat

class Helper {
    companion object {
        private val cache = object : ThreadLocal<HashMap<Pair<String, String>, MessageFormat>>() {
            override fun initialValue(): HashMap<Pair<String, String>, MessageFormat> {
                return HashMap()
            }
        }

        private fun getFormat(language: String, key: String) = cache.get().computeIfAbsent(Pair(language, key)) { (language, key) ->
            MessageFormat(Registry.instance.get(language, key))
        }

        fun translate(language: String, element: JsonElement): String? {
            if (!element.isJsonObject) {
                throw InvalidArgumentException(arrayOf("not an object"))
            }

            val obj = element.asJsonObject

            when {
                obj.has("key") -> {
                    val key = obj.getAsJsonPrimitive("key").asString ?: return null

                    val values = if (obj.has("values")) {
                        obj.getAsJsonArray("values")
                    } else {
                        JsonArray()
                    }

                    val replacements = Array<String>(values.size()) {
                        val item = values[it]

                        if (item.isJsonObject) {
                            translate(language, item.asJsonObject) ?: ""
                        } else if (item.isJsonPrimitive && item.asJsonPrimitive.isString) {
                            item.asString
                        } else {
                            throw InvalidArgumentException(arrayOf("value is not a Object nor String"))
                        }
                    }

                    val format = if (key == "\$literal") {
                        MessageFormat(replacements.mapIndexed { index, _ -> "{$index}" }.joinToString(""))
                    } else {
                        getFormat(language, key)
                    }

                    return format.format(replacements)
                }
                else -> throw InvalidArgumentException(arrayOf("do not have a key"))
            }
        }

        /*
         * Usage:
         * placeholder("tooltip.have_item", "mmis1000", Translation("item.sword"))
         */
        @Suppress("unused")
        fun placeholder(key: String, vararg values: Any): String {
            return IDENTIFIER + KeyTranslation(key, *values).toJsonObject().toString()
        }

        /*
         * Usage:
         * literalPlaceHolder("{0} get a {1}", "mmis1000", Translation("item.sword"))
         */
        @Suppress("unused")
        fun literalPlaceHolder(vararg values: Any): String {
            return IDENTIFIER + LiteralTranslation(*values).toJsonObject().toString()
        }

        fun convertTranslation(itemStack: ItemStack): JsonElement {
            @Suppress("CAST_NEVER_SUCCEEDS")
            val nativeStack = itemStack as net.minecraft.item.ItemStack

            val display = nativeStack.getSubCompound("display")

            if (display == null || !display.hasKey("Name")) {
                return convertTranslation(itemStack as Translatable)
            }

            val name = display.getString("Name")

            if (!name.startsWith(IDENTIFIER)) {
                return JsonPrimitive(name)
            }

            val parser = JsonParser()

            return try {
                parser.parse(name.drop(IDENTIFIER.length))
            } catch (err: Throwable) {
                JsonPrimitive(name)
            }
        }

        fun convertTranslation(translatable: Translatable): JsonElement {
                return convertTranslation(translatable.translation)
        }

        fun convertTranslation(translation: Translation): JsonElement {
            val obj = JsonObject()
            obj.addProperty("key", "minecraft:${translation.id}")
            return obj
        }
    }
}