package one.oktw.i18n.command

import one.oktw.i18n.Main
import one.oktw.i18n.text.interfaces.IExtendedMixinTextComponent
import one.oktw.i18n.api.I18nImpl
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandExecutor
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.TranslatableText
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextStyles
import org.spongepowered.api.text.serializer.TextSerializers
import org.spongepowered.api.text.translation.Translation
import org.spongepowered.common.interfaces.text.IMixinText
import java.util.*
import java.util.Arrays.asList

class TestItem : CommandExecutor {
    companion object {
        val spec: CommandSpec = CommandSpec.builder()
                .permission("i18n.command.test")
                .executor(TestItem())
                .description(Text.of("give i18n test translated item"))
                .build()
    }

    val lang = Main.main.languageService

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        if (src !is Player) return CommandResult.empty()

        val item = ItemStack.of(ItemTypes.IRON_SWORD, 1).apply {
            offer(
                    Keys.DISPLAY_NAME,
                    lang.ofLiteral(
                            TextColors.AQUA,
                            TextStyles.BOLD,
                            lang.sub(
                                    "tooltip.item_owned",
                                    src.name,
                                    lang.sub("item.sword")
                            )
                    )
            )

            offer(Keys.ITEM_LORE, asList<Text>(
                    lang.of("tooltip.description"),
                    lang.ofLiteral("\\ ", lang.sub("tooltip.description"), " /")
            ))
        }

        src.sendMessage(lang.toLocalizedLiteralText(
                src,
                "gave ",
                src.name,
                " a ",
                item,
                " ",
                item.translation
        ))

        val swordTranslate = object : Translation {
            override fun getId() = "i18n:item.sword"

            override fun get(locale: Locale) = I18nImpl.registry.get(locale.toLanguageTag(), id)

            override fun get(locale: Locale, vararg args: Any?) = I18nImpl.registry.get(locale.toLanguageTag(), id).format(*args)
        }

        val hasTranslate = object : Translation {
            override fun getId() = "i18n:tooltip.item_owned"

            override fun get(locale: Locale) = I18nImpl.registry.get(locale.toLanguageTag(), id)

            override fun get(locale: Locale, vararg args: Any?) = I18nImpl.registry.get(locale.toLanguageTag(), id).format(*args)
        }


        val str = swordTranslate.get(Locale.US)
        val plain = Text.of(swordTranslate).toPlain()
        val serialized = TextSerializers.JSON.serialize(Text.of(swordTranslate))
        val deserialized = TextSerializers.JSON.deserialize(serialized)
        val text1 = TranslatableText.builder(hasTranslate, arrayOf(Text.of("mmis"), swordTranslate) as Array<Any>).toText()

                //hasTranslate, ImmutableList.of("mmis", swordTranslate)).toText()

        src.inventory.offer(item)

        src.sendMessage(Text.of(
                str
        ))

        src.sendMessage(Text.of(
                plain
        ))

        src.sendMessage(Text.of(
                serialized
        ))

        src.sendMessage(Text.of(
                ((deserialized as IMixinText).toComponent() as IExtendedMixinTextComponent).getUnformattedComponentText(Locale.ENGLISH)
        ))

        src.sendMessage(Text.of(
                ((deserialized as IMixinText).toComponent() as IExtendedMixinTextComponent).getUnformattedComponentText(Locale.TAIWAN)
        ))

        src.sendMessage(Text.of(
                ((text1 as IMixinText).toComponent() as IExtendedMixinTextComponent).getUnformattedComponentText(Locale.ENGLISH)
        ))

        src.sendMessage(Text.of(
                ((text1 as IMixinText).toComponent() as IExtendedMixinTextComponent).getUnformattedComponentText(Locale.TAIWAN)
        ))

        src.sendMessage(
                Text.of(
                        TextSerializers.FORMATTING_CODE.serialize(deserialized)
                )
        )

        return CommandResult.success()
    }
}