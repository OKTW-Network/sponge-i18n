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
                    lang.ofPlaceHolder(
                            TextColors.AQUA,
                            TextStyles.BOLD,
                            lang.of(
                                    "tooltip.item_owned",
                                    Text.of(
                                        TextColors.DARK_PURPLE,
                                        src.name
                                    ),
                                    Text.of(
                                        TextColors.YELLOW,
                                        lang.of("item.sword")
                                    )
                            )
                    )
            )

            offer(Keys.ITEM_LORE, asList<Text>(
                    lang.ofPlaceHolder("tooltip.description"),
                    lang.ofLiteralPlaceHolder("\\ ", lang.of("tooltip.description"), " /")
            ))
        }

        val swordTranslate = lang.translation("item.sword")
        val hasTranslate = lang.translation("tooltip.item_owned")

        val text1 = Text.of(
                hasTranslate,
                Text.of(TextColors.RED, TextStyles.BOLD, "mmis"),
                Text.of(TextColors.BLUE, TextStyles.ITALIC, swordTranslate)
        )

        fun printComp(player: Player, text: Text, locale: Locale) {
            player.sendMessage(Text.of(
                    ((text as IMixinText).toComponent() as IExtendedMixinTextComponent).getUnformattedComponentText(locale)
            ))
        }

        fun printLegacy(player: Player, text: Text, locale: Locale) {
            player.sendMessage(Text.of(
                    I18nImpl.getLegacySerializer(locale).serialize(text)
            ))
        }

        fun printPlain(player: Player, text: Text, locale: Locale) {
            player.sendMessage(Text.of(
                    I18nImpl.getPlainTextSerializer(locale).serialize(text)
            ))
        }

        printComp(src, text1, Locale.ENGLISH)
        printComp(src, text1, Locale.TAIWAN)
        printPlain(src, text1, Locale.ENGLISH)
        printPlain(src, text1, Locale.TAIWAN)
        printLegacy(src, text1, Locale.ENGLISH)
        printLegacy(src, text1, Locale.TAIWAN)
        src.inventory.offer(item)

        return CommandResult.success()
    }
}