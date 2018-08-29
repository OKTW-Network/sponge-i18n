package one.oktw.i18n.command

import one.oktw.i18n.Main
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
import org.spongepowered.api.text.format.TextColors
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
                    lang.of(
                            "tooltip.item_owned",
                            src.name,
                            lang.sub("item.sword")
                    )
            )

            offer(Keys.ITEM_LORE, asList<Text>(
                    lang.of("tooltip.description"),
                    lang.ofLiteral("\\ ", lang.sub("tooltip.description"), " /")
            ))
        }

        src.inventory.offer(item)

        src.sendMessage(lang.toLocalizedLiteralText(
                src,
                TextColors.AQUA,
                "gave ",
                src.name,
                " a ",
                lang.sub("item.sword")
        ))

        return CommandResult.success()
    }
}