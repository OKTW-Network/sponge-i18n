package one.oktw.i18n.api

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.sun.javaws.exceptions.InvalidArgumentException

class Translation(val key: String, vararg val values: Any) {
    fun toJsonObject(): JsonElement {
        val obj = JsonObject()
        obj.add("key", JsonPrimitive(key))

        val valueArray = JsonArray()
        obj.add("values", valueArray)

        values.forEach { arg ->
            when (arg) {
                is String -> valueArray.add(JsonPrimitive(arg))
                is Translation -> valueArray.add(arg.toJsonObject())
                else -> throw InvalidArgumentException(arrayOf("Expect only String and Translation ar arguments"))
            }
        }

        return obj
    }
}