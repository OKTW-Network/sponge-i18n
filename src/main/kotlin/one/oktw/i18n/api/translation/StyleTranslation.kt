package one.oktw.i18n.api.translation

import com.google.gson.JsonElement
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.format.TextColor
import java.time.format.TextStyle
import java.util.*

class StyleTranslation(id: String): Translation{
    constructor(color: TextColor) : this(color.toString()) {

    }

    constructor(style: TextStyle) : this(style.toString()) {

    }

    override fun toJsonObject(): JsonElement {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun fromJsonObject(element: JsonElement): Translation {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun toLocalized(player: Player): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun toLocalized(language: String): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun toLocalized(locale: Locale): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}