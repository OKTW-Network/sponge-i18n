package one.oktw.i18n.translation

import one.oktw.i18n.api.Registry
import one.oktw.i18n.api.Translation
import com.google.gson.JsonElement
import com.sun.javaws.exceptions.InvalidArgumentException
import one.oktw.i18n.Main.Companion.IDENTIFIER
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

            if (!obj.has("key")) {
                throw InvalidArgumentException(arrayOf("do not have key"))
            }

            val key = obj.getAsJsonPrimitive("key").asString ?: return null

            val values = obj.getAsJsonArray("values")

            val replacements = Array<String>(values.size()) {
                val item = values[it]

                if (item.isJsonObject) {
                    translate(language, item.asJsonObject) ?: ""
                } else if (item.isJsonPrimitive && item.asJsonPrimitive.isString){
                    item.asString
                } else {
                    throw InvalidArgumentException(arrayOf("value is not a Object nor String"))
                }
            }

            val format = getFormat(language, key)

            return format.format(replacements)
        }

        /*
         * Usage:
         * placeholder("tooltip.have_item", "mmis1000", Translation("item.sword"))
         */
        @Suppress("unused")
        fun placeholder(key: String, vararg values: Any): String {
            return IDENTIFIER + Translation(key, *values).toJsonObject().toString()
        }
    }
}