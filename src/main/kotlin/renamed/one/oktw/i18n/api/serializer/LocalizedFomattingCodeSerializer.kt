package renamed.one.oktw.i18n.api.serializer

import renamed.one.oktw.i18n.text.interfaces.IExtendedMixinTextComponent
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.serializer.TextSerializer
import org.spongepowered.common.interfaces.text.IMixinText
import org.spongepowered.common.text.serializer.SpongeFormattingCodeTextSerializer
import java.util.*

class LocalizedFomattingCodeSerializer(private val locale: Locale, private val code: Char): TextSerializer {
    override fun getName(): String {
        return "localized_serializer";
    }

    override fun getId(): String {
        return "localized_serializer";
    }

    override fun serialize(text: Text): String {
        return ((text as IMixinText).toComponent() as IExtendedMixinTextComponent).toLegacy(locale, code)
    }

    override fun deserialize(input: String): Text {
        return SpongeFormattingCodeTextSerializer(code).deserialize(input)
    }
}