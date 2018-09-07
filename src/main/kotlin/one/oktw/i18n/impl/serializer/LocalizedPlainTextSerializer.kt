package one.oktw.i18n.impl.serializer

import one.oktw.i18n.text.interfaces.IExtendedMixinTextComponent
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.serializer.FormattingCodeTextSerializer
import org.spongepowered.api.text.serializer.TextSerializer
import org.spongepowered.api.text.serializer.TextSerializers
import org.spongepowered.common.interfaces.text.IMixinText
import org.spongepowered.common.text.serializer.SpongeFormattingCodeTextSerializer
import java.util.*

class LocalizedPlainTextSerializer(private val locale: Locale): TextSerializer {
    override fun getName(): String {
        return "localized_serializer";
    }

    override fun getId(): String {
        return "localized_serializer";
    }

    override fun serialize(text: Text): String {
        return ((text as IMixinText).toComponent() as IExtendedMixinTextComponent).toPlain(locale)
    }

    override fun deserialize(input: String): Text {
        return TextSerializers.PLAIN.deserialize(input)
    }
}