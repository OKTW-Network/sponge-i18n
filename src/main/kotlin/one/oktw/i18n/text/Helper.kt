package one.oktw.i18n.text

import org.spongepowered.api.text.Text
import org.spongepowered.api.text.TranslatableText
import org.spongepowered.api.text.format.TextFormat

class Helper {
    companion object {
        // recursively remove all styles while preserve translations
        fun removeStyle(text: Text): Text {
            if (text is TranslatableText) {
                return removeStyle(text)
            }

            val builder = text.toBuilder()

            val newChildren = text.children.map {
                when (it) {
                    is TranslatableText -> removeStyle(it)
                    is Text -> removeStyle(it)
                    else -> it
                }
            }

            builder.removeAll().append(newChildren)

            builder.format(TextFormat.NONE)

            return builder.toText()
        }

        private fun removeStyle(text: TranslatableText): TranslatableText {
            val translation = text.translation

            val arguments = text.arguments.map {
                when (it) {
                    is TranslatableText -> removeStyle(it)
                    is Text -> removeStyle(it)
                    else -> it
                }
            }.toTypedArray()

            val children = text.children.map {
                when (it) {
                    is TranslatableText -> removeStyle(it)
                    is Text -> removeStyle(it)
                    else -> it
                }
            }

            val builder = TranslatableText.builder(translation, *arguments)

            builder.append(children)

            return builder.build()
        }
    }
}