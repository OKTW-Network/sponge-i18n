package one.oktw.i18n.text

import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemShulkerBox
import net.minecraft.item.ItemStack
import net.minecraft.nbt.JsonToNBT
import net.minecraft.nbt.NBTTagList
import net.minecraft.nbt.NBTTagString
import net.minecraft.util.NonNullList
import net.minecraft.util.text.*
import net.minecraft.util.text.event.HoverEvent
import one.oktw.i18n.Main
import one.oktw.i18n.impl.I18nImpl
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.TranslatableText
import org.spongepowered.api.text.format.TextFormat
import org.spongepowered.api.text.serializer.TextSerializers
import java.util.*

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

        fun rewriteItem(item: ItemStack, player: Player): ItemStack {
            val language = I18nImpl.registry.getLanguage(player)
            return rewriteItem(item, language)
        }

        fun rewriteItem(item: ItemStack, language: Locale): ItemStack {
            val translated = ArrayList<Pair<Int, String>>()

            val newItem = item.copy()

            newItem.getSubCompound("display")?.let { nbt ->
                if (nbt.hasKey("Name", 8)) {
                    val originalName = nbt.getString("Name")
                    if (originalName.startsWith(Main.IDENTIFIER)) {
                        try {
                            val json = originalName.drop(Main.IDENTIFIER.length)
                            val text = TextSerializers.JSON.deserialize(json)
                            val result = I18nImpl.getLegacySerializer(language).serialize(text)

                            nbt.setString("Name", result)
                            translated.add(Pair(-1, json))
                        } catch (err: Throwable) {
                            Main.main.logger.error(err.localizedMessage)
                            Main.main.logger.error(err.stackTrace.joinToString(""))
                        }
                    }
                }

                if (nbt.getTagId("Lore") == 9.toByte()) {
                    val list = nbt.getTagList("Lore", 8)

                    for (i in 0 until list.tagCount()) {
                        val originalName = list.getStringTagAt(i)

                        if (originalName.startsWith(Main.IDENTIFIER)) {
                            try {
                                val json = originalName.drop(Main.IDENTIFIER.length)
                                val text = TextSerializers.JSON.deserialize(json)
                                val result = I18nImpl.getLegacySerializer(language).serialize(text)
                                list.set(i, NBTTagString(result))

                                translated.add(Pair(i, json))
                            } catch (err: Throwable) {
                                Main.main.logger.error(err.localizedMessage)
                                Main.main.logger.error(err.stackTrace.joinToString(""))
                            }
                        }
                    }
                }
            }

            var shulkerTranslated = false
            if (newItem.item is ItemShulkerBox) {
                newItem.getSubCompound("BlockEntityTag")?.let { blockEntityTag ->

                    shulkerTranslated = true
                    if (blockEntityTag.hasKey("Items", 9)) {
                        val itemList = NonNullList.withSize(27, ItemStack.EMPTY)
                        ItemStackHelper.loadAllItems(blockEntityTag, itemList)

                        (0 until 27).map {
                            itemList[it] = rewriteItem(itemList[it], language)
                        }

                        ItemStackHelper.saveAllItems(blockEntityTag, itemList)
                    }
                }
            }

            if (shulkerTranslated) {
                translated.add(Pair(-2, item.getSubCompound("BlockEntityTag").toString()))
            }

            if (translated.size > 0) {
                val translatedList = NBTTagList()
                val i18n = newItem.getOrCreateSubCompound("i18n")
                i18n.setTag("list", translatedList)

                translated.forEach { (index, json) ->
                    val obj = JsonObject()
                    obj.addProperty("index", index)
                    obj.add("translation", JsonPrimitive(json))

                    translatedList.appendTag(NBTTagString(obj.toString()))
                }
            }

            return newItem
        }

        private fun cloneStyle(style: Style, lang: Locale): Style {
            return style.createShallowCopy().apply {
                if (this.hoverEvent != null && this.hoverEvent.action === HoverEvent.Action.SHOW_ITEM) {
                    try {
                        val nbt = JsonToNBT.getTagFromJson(this.hoverEvent.value.unformattedText)
                        val item = ItemStack(nbt)
                        val rewrittenItem = rewriteItem(item, lang)

                        this.hoverEvent = HoverEvent(
                                this.hoverEvent.action,
                                TextComponentString(rewrittenItem.serializeNBT().toString())
                        )
                    } catch (err: Throwable) {
                    }
                }
            }
        }

        private fun applyStyleAndSibling(from: ITextComponent, to: ITextComponent, lang: Locale) {
            to.apply {
                style = cloneStyle(from.style, lang)
                from.siblings.forEach {
                    this.appendSibling(
                            fixTextComponentServerSide(it, lang)
                    )
                }
            }
        }

        // replace all special marker in text component with actual translated content
        fun fixTextComponentServerSide(text: ITextComponent, lang: Locale): ITextComponent {
            return when (text) {
                is TextComponentString -> {
                    if (text.text.startsWith(Main.IDENTIFIER)) {
                        val unwrapped = text.text.drop(Main.IDENTIFIER.length)
                        try {
                            val parsed = TextSerializers.JSON.deserialize(unwrapped)
                            val legacy = I18nImpl.getLegacySerializer(lang).serialize(parsed)
                            TextComponentString(legacy)
                        } catch (err: Throwable) {
                            TextComponentString(text.text)
                        }
                    } else {
                        TextComponentString(text.text)
                    }.apply {
                        applyStyleAndSibling(text, this, lang)
                    }
                }
                is TextComponentTranslation -> {
                    TextComponentTranslation(text.key, *(text.formatArgs.map {
                        if (it is ITextComponent) {
                            fixTextComponentServerSide(it, lang)
                        } else {
                            it
                        }
                    }.toTypedArray())).apply {
                        applyStyleAndSibling(text, this, lang)
                    }
                }
                is TextComponentKeybind -> {
                    TextComponentKeybind(text.keybind).apply {
                        applyStyleAndSibling(text, this, lang)
                    }
                }
                is TextComponentScore -> {
                    TextComponentScore(text.name, text.objective).apply {
                        applyStyleAndSibling(text, this, lang)
                    }
                }
                is TextComponentSelector -> {
                    TextComponentSelector(text.selector).apply {
                        applyStyleAndSibling(text, this, lang)
                    }
                }
                else -> text
            }
        }
    }
}